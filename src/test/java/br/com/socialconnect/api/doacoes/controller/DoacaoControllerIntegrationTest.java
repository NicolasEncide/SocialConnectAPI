package br.com.socialconnect.api.doacoes.controller;

import br.com.socialconnect.api.doacoes.dto.DoacaoRequestDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoResponseDTO;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import br.com.socialconnect.api.doacoes.repository.DoacaoRepository;
import br.com.socialconnect.api.doadores.model.Doador;
import br.com.socialconnect.api.doadores.model.TipoDoador;
import br.com.socialconnect.api.doadores.repository.DoadorRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class DoacaoControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    @Autowired
    private DoadorRepository doadorRepository;

    @Autowired
    private DoacaoRepository doacaoRepository;

    private String getUrl() {
        return "http://localhost:" + port + "/api/v1/doacoes";
    }

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(HttpStatusCode statusCode) {
                return false;
            }
        });
        doacaoRepository.deleteAll();
        doadorRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar doação quando dados válidos")
    void deveCriarDoacaoQuandoDadosValidos() {
        Doador doador = doadorRepository.save(Doador.builder()
                .nome("Instituto Solidário")
                .tipo(TipoDoador.PESSOA_JURIDICA)
                .build());

        DoacaoRequestDTO dto = new DoacaoRequestDTO(
                doador.getIdDoador(),
                LocalDate.now(),
                new BigDecimal("150.00"),
                TipoDoacao.FINANCEIRA,
                "Doação de teste para projeto social"
        );

        ResponseEntity<DoacaoResponseDTO> response = restTemplate.postForEntity(
                getUrl(), dto, DoacaoResponseDTO.class
        );

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody(), "O corpo da resposta não deve ser nulo");
        Assertions.assertNotNull(response.getBody().idDoacao(), "ID da doação não deve ser nulo");
        Assertions.assertEquals(doador.getIdDoador(), response.getBody().idDoador());
        Assertions.assertEquals(new BigDecimal("150.00"), response.getBody().valor());
    }

    @Test
    @DisplayName("Deve retornar 400 quando data da doação for futura")
    void deveRetornar400QuandoDataFutura() {
        Doador doador = doadorRepository.save(Doador.builder()
                .nome("Doador Futuro")
                .tipo(TipoDoador.PESSOA_FISICA)
                .build());

        DoacaoRequestDTO dto = new DoacaoRequestDTO(
                doador.getIdDoador(),
                LocalDate.now().plusDays(10),
                new BigDecimal("200.00"),
                TipoDoacao.ALIMENTO,
                "Doação com data futura inválida"
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                getUrl(), dto, String.class
        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody(), "O corpo da resposta com erro não deve ser nulo");
        Assertions.assertTrue(response.getBody().toLowerCase().contains("futuro"),
                "A mensagem de erro deve conter a palavra 'futuro'");
    }
}
