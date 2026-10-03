package br.com.socialconnect.api.doacoes;

import br.com.socialconnect.api.doacoes.dto.DoacaoRequestDTO;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import br.com.socialconnect.api.doacoes.repository.DoacaoRepository;
import br.com.socialconnect.api.doadores.dto.DoadorRequestDTO;
import br.com.socialconnect.api.doadores.model.TipoDoador;
import br.com.socialconnect.api.doadores.repository.DoadorRepository;
import br.com.socialconnect.api.config.SortParameterSanitizerFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class DoacaoIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SortParameterSanitizerFilter sortParameterSanitizerFilter;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private DoacaoRepository doacaoRepository;

    @Autowired
    private DoadorRepository doadorRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(sortParameterSanitizerFilter)
                .build();
        doacaoRepository.deleteAll();
        doadorRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve registrar um doador e uma doação com sucesso")
    void deveRegistrarDoadorEDoacaoComSucesso() throws Exception {
        DoadorRequestDTO doadorDTO = new DoadorRequestDTO("Instituto Solidário", TipoDoador.PESSOA_JURIDICA);

        String doadorResponse = mockMvc.perform(post("/api/v1/doadores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doadorDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.idDoador", notNullValue()))
                .andReturn().getResponse().getContentAsString();

        Long idDoador = objectMapper.readTree(doadorResponse).get("idDoador").asLong();

        DoacaoRequestDTO doacaoDTO = new DoacaoRequestDTO(
                idDoador,
                LocalDate.now(),
                BigDecimal.valueOf(1500.00),
                TipoDoacao.FINANCEIRA,
                "Doação para reforma da sala de convivência"
        );

        mockMvc.perform(post("/api/v1/doacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doacaoDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.idDoacao", notNullValue()))
                .andExpect(jsonPath("$.idDoador", is(idDoador.intValue())))
                .andExpect(jsonPath("$.tipo", is("FINANCEIRA")))
                .andExpect(jsonPath("$.nomeDoador", is("Instituto Solidário")));
    }

    @Test
    @DisplayName("Deve filtrar doações por tipo e intervalo de datas com paginação")
    void deveFiltrarDoacoesComSucesso() throws Exception {
        DoadorRequestDTO doadorDTO = new DoadorRequestDTO("João da Silva", TipoDoador.PESSOA_FISICA);
        String doadorResponse = mockMvc.perform(post("/api/v1/doadores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doadorDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long idDoador = objectMapper.readTree(doadorResponse).get("idDoador").asLong();

        DoacaoRequestDTO d1 = new DoacaoRequestDTO(
                idDoador,
                LocalDate.of(2026, 2, 10),
                BigDecimal.valueOf(200.00),
                TipoDoacao.ALIMENTO,
                "Cestas de alimentos"
        );
        mockMvc.perform(post("/api/v1/doacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(d1)))
                .andExpect(status().isCreated());

        DoacaoRequestDTO d2 = new DoacaoRequestDTO(
                idDoador,
                LocalDate.of(2026, 3, 15),
                BigDecimal.valueOf(100.00),
                TipoDoacao.ROUPA,
                "Agasalhos de inverno"
        );
        mockMvc.perform(post("/api/v1/doacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(d2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/doacoes")
                        .param("tipo", "ALIMENTO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].tipo", is("ALIMENTO")));

        mockMvc.perform(get("/api/v1/doacoes")
                        .param("dataInicio", "2026-03-01")
                        .param("dataFim", "2026-03-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].tipo", is("ROUPA")));
    }

    @Test
    @DisplayName("Deve listar doações com sort contendo colchetes ou placeholder string sem lançar erro 500")
    void deveListarDoacoesComSortComColchetesSemErro500() throws Exception {
        mockMvc.perform(get("/api/v1/doacoes")
                        .param("sort", "[\"valor,desc\"]"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/doacoes")
                        .param("sort", "[ \"string\" ]"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/doacoes")
                        .param("sort", "string"))
                .andExpect(status().isOk());
    }
}
