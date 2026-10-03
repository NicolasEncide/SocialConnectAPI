package br.com.socialconnect.api.produtos.controller;

import br.com.socialconnect.api.exception.EstoqueMenorQueZeroException;
import br.com.socialconnect.api.exception.GlobalExceptionHandler;
import br.com.socialconnect.api.exception.NomeDuplicadoException;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.service.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProdutoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProdutoService produtoService;

    @InjectMocks
    private ProdutoController produtoController;

    @BeforeEach
    void setUp() {
        com.fasterxml.jackson.databind.ObjectMapper mapper = com.fasterxml.jackson.databind.json.JsonMapper.builder()
                .addModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
                .disable(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .build();

        mockMvc = MockMvcBuilders.standaloneSetup(produtoController)
                .setMessageConverters(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(mapper))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/produtos - Deve retornar 200 com lista paginada")
    void deveListarProdutosComSucesso() throws Exception {
        ProdutoResponseDTO item = new ProdutoResponseDTO(
                1L, "Arroz 5kg", CategoriaProduto.ALIMENTO, 20, 5, "PCT", false, LocalDate.now()
        );
        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        Page<ProdutoResponseDTO> page = new PageImpl<>(List.of(item), pageable, 1);

        Mockito.when(produtoService.listar(Mockito.any(), Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/produtos")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "nome,asc")
                        .param("categoria", "ALIMENTO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nome", is("Arroz 5kg")))
                .andExpect(jsonPath("$.content[0].categoria", is("ALIMENTO")))
                .andExpect(jsonPath("$.content[0].estoqueBaixo", is(false)));
    }

    @Test
    @DisplayName("GET /api/v1/produtos/{id_produto} - Deve retornar 200 quando produto existir")
    void deveBuscarPorIdComSucesso() throws Exception {
        ProdutoResponseDTO item = new ProdutoResponseDTO(
                1L, "Sabonete", CategoriaProduto.HIGIENE, 2, 5, "UN", true, LocalDate.now()
        );

        Mockito.when(produtoService.buscarPorId(1L)).thenReturn(item);

        mockMvc.perform(get("/api/v1/produtos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProduto", is(1)))
                .andExpect(jsonPath("$.nome", is("Sabonete")))
                .andExpect(jsonPath("$.estoqueBaixo", is(true)));
    }

    @Test
    @DisplayName("GET /api/v1/produtos/{id_produto} - Deve retornar 404 com Problem Details quando não existir")
    void deveRetornar404AoBuscarPorIdInexistente() throws Exception {
        Mockito.when(produtoService.buscarPorId(999L))
                .thenThrow(new RecursoNaoEncontradoException("Produto não encontrado com o ID: 999"));

        mockMvc.perform(get("/api/v1/produtos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.title", is("Recurso não encontrado")));
    }

    @Test
    @DisplayName("POST /api/v1/produtos - Deve retornar 201 com Location no cabeçalho")
    void deveCriarProdutoComSucesso() throws Exception {
        String json = """
                {
                    "nome": "Feijão Preto 1kg",
                    "categoria": "ALIMENTO",
                    "estoqueAtual": 15,
                    "estoqueMinimo": 5,
                    "unidadeMedida": "KG"
                }
                """;

        ProdutoResponseDTO salvo = new ProdutoResponseDTO(
                10L, "Feijão Preto 1kg", CategoriaProduto.ALIMENTO, 15, 5, "KG", false, LocalDate.now()
        );

        Mockito.when(produtoService.criar(Mockito.any(ProdutoRequestDTO.class))).thenReturn(salvo);

        mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/produtos/10"))
                .andExpect(jsonPath("$.idProduto", is(10)))
                .andExpect(jsonPath("$.nome", is("Feijão Preto 1kg")));
    }

    @Test
    @DisplayName("POST /api/v1/produtos - Deve retornar 400 Bad Request quando campo obrigatório for inválido")
    void deveRetornar400AoCriarComDadosInvalidos() throws Exception {
        String jsonInvalido = """
                {
                    "nome": "",
                    "categoria": null,
                    "estoqueAtual": 10,
                    "estoqueMinimo": 5,
                    "unidadeMedida": ""
                }
                """;

        mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.title", is("Erro de validação")));
    }

    @Test
    @DisplayName("POST /api/v1/produtos - Deve retornar 409 Conflict quando nome for duplicado")
    void deveRetornar409AoCriarComNomeDuplicado() throws Exception {
        String json = """
                {
                    "nome": "Arroz 5kg",
                    "categoria": "ALIMENTO",
                    "estoqueAtual": 10,
                    "estoqueMinimo": 2,
                    "unidadeMedida": "PCT"
                }
                """;

        Mockito.when(produtoService.criar(Mockito.any(ProdutoRequestDTO.class)))
                .thenThrow(new NomeDuplicadoException("Arroz 5kg"));

        mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.title", is("Nome já cadastrado")));
    }

    @Test
    @DisplayName("POST /api/v1/produtos - Deve retornar 422 Unprocessable Entity quando estoque for negativo")
    void deveRetornar422AoCriarComEstoqueNegativo() throws Exception {
        String json = """
                {
                    "nome": "Leite",
                    "categoria": "ALIMENTO",
                    "estoqueAtual": -1,
                    "estoqueMinimo": 2,
                    "unidadeMedida": "L"
                }
                """;

        Mockito.when(produtoService.criar(Mockito.any(ProdutoRequestDTO.class)))
                .thenThrow(new EstoqueMenorQueZeroException());

        mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status", is(422)))
                .andExpect(jsonPath("$.title", is("Estoque não pode ser negativo")));
    }

    @Test
    @DisplayName("PUT /api/v1/produtos/{id_produto} - Deve retornar 200 OK na atualização total")
    void deveAtualizarProdutoComSucesso() throws Exception {
        String json = """
                {
                    "nome": "Arroz Integral 5kg",
                    "categoria": "ALIMENTO",
                    "estoqueAtual": 30,
                    "estoqueMinimo": 10,
                    "unidadeMedida": "PCT"
                }
                """;

        ProdutoResponseDTO atualizado = new ProdutoResponseDTO(
                1L, "Arroz Integral 5kg", CategoriaProduto.ALIMENTO, 30, 10, "PCT", false, LocalDate.now()
        );

        Mockito.when(produtoService.atualizar(Mockito.eq(1L), Mockito.any(ProdutoRequestDTO.class)))
                .thenReturn(atualizado);

        mockMvc.perform(put("/api/v1/produtos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProduto", is(1)))
                .andExpect(jsonPath("$.nome", is("Arroz Integral 5kg")));
    }

    @Test
    @DisplayName("PUT /api/v1/produtos/{id_produto} - Deve retornar 404 quando ID não existir")
    void deveRetornar404AoAtualizarProdutoInexistente() throws Exception {
        String json = """
                {
                    "nome": "Arroz Integral 5kg",
                    "categoria": "ALIMENTO",
                    "estoqueAtual": 30,
                    "estoqueMinimo": 10,
                    "unidadeMedida": "PCT"
                }
                """;

        Mockito.when(produtoService.atualizar(Mockito.eq(999L), Mockito.any(ProdutoRequestDTO.class)))
                .thenThrow(new RecursoNaoEncontradoException("Produto não encontrado com o ID: 999"));

        mockMvc.perform(put("/api/v1/produtos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @DisplayName("PUT /api/v1/produtos/{id_produto} - Deve retornar 409 quando nome for duplicado")
    void deveRetornar409AoAtualizarComNomeDuplicado() throws Exception {
        String json = """
                {
                    "nome": "Arroz Existente",
                    "categoria": "ALIMENTO",
                    "estoqueAtual": 30,
                    "estoqueMinimo": 10,
                    "unidadeMedida": "PCT"
                }
                """;

        Mockito.when(produtoService.atualizar(Mockito.eq(1L), Mockito.any(ProdutoRequestDTO.class)))
                .thenThrow(new NomeDuplicadoException("Arroz Existente"));

        mockMvc.perform(put("/api/v1/produtos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));
    }

    @Test
    @DisplayName("PUT /api/v1/produtos/{id_produto} - Deve retornar 422 quando estoque for negativo")
    void deveRetornar422AoAtualizarComEstoqueNegativo() throws Exception {
        String json = """
                {
                    "nome": "Arroz",
                    "categoria": "ALIMENTO",
                    "estoqueAtual": -3,
                    "estoqueMinimo": 5,
                    "unidadeMedida": "PCT"
                }
                """;

        Mockito.when(produtoService.atualizar(Mockito.eq(1L), Mockito.any(ProdutoRequestDTO.class)))
                .thenThrow(new EstoqueMenorQueZeroException());

        mockMvc.perform(put("/api/v1/produtos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status", is(422)));
    }

    @Test
    @DisplayName("DELETE /api/v1/produtos/{id_produto} - Deve retornar 204 No Content quando sucesso")
    void deveDeletarProdutoComSucesso() throws Exception {
        Mockito.doNothing().when(produtoService).deletar(1L);

        mockMvc.perform(delete("/api/v1/produtos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/produtos/{id_produto} - Deve retornar 404 quando produto não existir")
    void deveRetornar404AoDeletarProdutoInexistente() throws Exception {
        Mockito.doThrow(new RecursoNaoEncontradoException("Produto não encontrado com o ID: 999"))
                .when(produtoService).deletar(999L);

        mockMvc.perform(delete("/api/v1/produtos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }
}
