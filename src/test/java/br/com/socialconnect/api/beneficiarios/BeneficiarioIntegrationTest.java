package br.com.socialconnect.api.beneficiarios;

import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioPatchDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioRequestDTO;
import br.com.socialconnect.api.beneficiarios.repository.BeneficiarioRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class BeneficiarioIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private BeneficiarioRepository repository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        repository.deleteAll();
    }

    @Test
    @DisplayName("Deve cadastrar um beneficiário com sucesso e retornar 201 com Location")
    void deveCadastrarBeneficiarioComSucesso() throws Exception {
        BeneficiarioRequestDTO dto = new BeneficiarioRequestDTO(
                "Maria da Silva",
                "52998224725",
                "11999998888",
                "Rua das Flores, 123",
                "Vulnerabilidade social moderada"
        );

        mockMvc.perform(post("/api/v1/beneficiarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.idBeneficiario", notNullValue()))
                .andExpect(jsonPath("$.nome", is("Maria da Silva")))
                .andExpect(jsonPath("$.cpf", is("52998224725")));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request com RFC 7807 quando o CPF for inválido")
    void deveRetornarErro400QuandoCpfInvalido() throws Exception {
        BeneficiarioRequestDTO dto = new BeneficiarioRequestDTO(
                "Maria da Silva",
                "11122233344",
                "11999998888",
                "Rua das Flores, 123",
                "Vulnerabilidade social"
        );

        mockMvc.perform(post("/api/v1/beneficiarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.title", is("Erro de validação")))
                .andExpect(jsonPath("$.type", containsString("errors/validacao")))
                .andExpect(jsonPath("$.errors", not(empty())));
    }

    @Test
    @DisplayName("Deve retornar 409 Conflict com RFC 7807 quando o CPF já estiver cadastrado")
    void deveRetornarErro409QuandoCpfDuplicado() throws Exception {
        BeneficiarioRequestDTO dto = new BeneficiarioRequestDTO(
                "Maria da Silva",
                "52998224725",
                "11999998888",
                "Rua das Flores, 123",
                "Vulnerabilidade"
        );

        mockMvc.perform(post("/api/v1/beneficiarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/beneficiarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.title", is("CPF já cadastrado")))
                .andExpect(jsonPath("$.type", containsString("errors/cpf-duplicado")));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found com RFC 7807 para ID inexistente")
    void deveRetornarErro404ParaIdInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/beneficiarios/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.title", is("Recurso não encontrado")))
                .andExpect(jsonPath("$.type", containsString("errors/nao-encontrado")));
    }

    @Test
    @DisplayName("Deve atualizar parcialmente um beneficiário com PATCH")
    void deveAtualizarBeneficiarioComPatch() throws Exception {
        BeneficiarioRequestDTO dto = new BeneficiarioRequestDTO(
                "Maria da Silva",
                "52998224725",
                "11999998888",
                "Rua Antiga, 1",
                "Vulnerabilidade"
        );

        String response = mockMvc.perform(post("/api/v1/beneficiarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("idBeneficiario").asLong();

        BeneficiarioPatchDTO patchDTO = new BeneficiarioPatchDTO(
                null,
                "11988887777",
                "Rua Nova, 456",
                null
        );

        mockMvc.perform(patch("/api/v1/beneficiarios/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Maria da Silva")))
                .andExpect(jsonPath("$.telefone", is("11988887777")))
                .andExpect(jsonPath("$.endereco", is("Rua Nova, 456")));
    }

    @Test
    @DisplayName("Deve remover beneficiário com DELETE retornando 204 No Content")
    void deveRemoverBeneficiarioComSucesso() throws Exception {
        BeneficiarioRequestDTO dto = new BeneficiarioRequestDTO(
                "Maria da Silva",
                "52998224725",
                "11999998888",
                "Rua das Flores",
                "Vulnerabilidade"
        );

        String response = mockMvc.perform(post("/api/v1/beneficiarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("idBeneficiario").asLong();

        mockMvc.perform(delete("/api/v1/beneficiarios/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/beneficiarios/" + id))
                .andExpect(status().isNotFound());
    }
}
