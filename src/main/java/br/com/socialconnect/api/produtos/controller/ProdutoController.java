package br.com.socialconnect.api.produtos.controller;

import br.com.socialconnect.api.exception.ProblemDetail;
import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/produtos")
@Tag(name = "Produtos", description = "API para gestão de produtos da instituição")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    @Operation(
            summary = "Lista todos os produtos",
            description = "Retorna uma lista paginada de produtos, com filtros opcionais por nome (parcial) e categoria."
    )
    @ApiResponse(responseCode = "200", description = "Lista paginada retornada com sucesso")
    @ApiResponse(responseCode = "400", description = "Parâmetros de consulta ou ordenação inválidos",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Page<ProdutoResponseDTO>> listar(
            @Parameter(description = "Nome para filtrar (parcial, sem distinção de maiúsculas/minúsculas)", example = "Arroz")
            @RequestParam(required = false) String nome,

            @Parameter(description = "Categoria do produto para filtrar", example = "ALIMENTO")
            @RequestParam(required = false) CategoriaProduto categoria,

            @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(produtoService.listar(nome, categoria, pageable));
    }

    @GetMapping("/{id_produto}")
    @Operation(
            summary = "Busca produto por ID",
            description = "Retorna os detalhes de um produto específico através de seu identificador único."
    )
    @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(
            @Parameter(description = "Identificador único do produto", example = "1")
            @PathVariable("id_produto") Long idProduto) {
        return ResponseEntity.ok(produtoService.buscarPorId(idProduto));
    }

    @PostMapping
    @Operation(
            summary = "Cria um novo produto",
            description = "Cadastra um novo produto no estoque. Retorna 201 Created com o cabeçalho Location apontando para o recurso criado."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Produto criado com sucesso",
            headers = @Header(name = "Location", description = "URI do produto recém-criado", schema = @Schema(type = "string"))
    )
    @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Nome já cadastrado no sistema",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "422", description = "Regra de negócio violada (estoque não pode ser negativo)",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<ProdutoResponseDTO> criar(
            @Valid @RequestBody ProdutoRequestDTO dto) {
        ProdutoResponseDTO salvo = produtoService.criar(dto);
        URI location = URI.create("/api/v1/produtos/" + salvo.idProduto());
        return ResponseEntity.created(location).body(salvo);
    }

    @PutMapping("/{id_produto}")
    @Operation(
            summary = "Atualização total do produto",
            description = "Substitui integralmente as informações de um produto existente mantendo a data de cadastro original."
    )
    @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Produto não encontrado",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Nome já cadastrado em outro produto",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "422", description = "Regra de negócio violada (estoque não pode ser negativo)",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @Parameter(description = "Identificador único do produto", example = "1")
            @PathVariable("id_produto") Long idProduto,

            @Valid @RequestBody ProdutoRequestDTO dto) {
        return ResponseEntity.ok(produtoService.atualizar(idProduto, dto));
    }

    @DeleteMapping("/{id_produto}")
    @Operation(
            summary = "Remove um produto",
            description = "Exclui um produto do sistema através de seu identificador único."
    )
    @ApiResponse(responseCode = "204", description = "Produto removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> deletar(
            @Parameter(description = "Identificador único do produto", example = "1")
            @PathVariable("id_produto") Long idProduto) {
        produtoService.deletar(idProduto);
        return ResponseEntity.noContent().build();
    }
}
