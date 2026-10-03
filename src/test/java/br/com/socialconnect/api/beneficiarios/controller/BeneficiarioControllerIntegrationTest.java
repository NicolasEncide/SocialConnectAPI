package br.com.socialconnect.api.beneficiarios.controller;

import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioRequestDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioResponseDTO;
import br.com.socialconnect.api.beneficiarios.repository.BeneficiarioRepository;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class BeneficiarioControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    private String getUrl() {
        return "http://localhost:" + port + "/api/v1/beneficiarios";
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
        beneficiarioRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar beneficiário quando dados válidos")
    void deveCriarBeneficiarioQuandoDadosValidos() {
        BeneficiarioRequestDTO dto = new BeneficiarioRequestDTO(
                "Lucas Mendes",
                "52998224725",
                "11988887777",
                "Rua do Sol, 42",
                "Vulnerabilidade alimentar"
        );

        ResponseEntity<BeneficiarioResponseDTO> response = restTemplate.postForEntity(
                getUrl(), dto, BeneficiarioResponseDTO.class
        );

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody(), "O corpo da resposta não deve ser nulo");
        Assertions.assertNotNull(response.getBody().idBeneficiario(), "ID do beneficiário não deve ser nulo");
        Assertions.assertEquals("Lucas Mendes", response.getBody().nome());
        Assertions.assertEquals("52998224725", response.getBody().cpf());
    }

    @Test
    @DisplayName("Deve retornar 400 quando CPF for inválido")
    void deveRetornar400QuandoCpfInvalido() {
        BeneficiarioRequestDTO dto = new BeneficiarioRequestDTO(
                "Lucas Mendes",
                "00011122233",
                "11988887777",
                "Rua do Sol, 42",
                "Vulnerabilidade"
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                getUrl(), dto, String.class
        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody(), "O corpo da resposta de erro não deve ser nulo");
    }
}
