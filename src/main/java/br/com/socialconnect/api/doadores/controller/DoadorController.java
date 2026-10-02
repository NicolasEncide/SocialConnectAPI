package br.com.socialconnect.api.doadores.controller;

import br.com.socialconnect.api.doadores.dto.DoadorRequestDTO;
import br.com.socialconnect.api.doadores.dto.DoadorResponseDTO;
import br.com.socialconnect.api.doadores.service.DoadorService;
import br.com.socialconnect.api.exception.ProblemDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import java.util.List;

@RestController
@RequestMapping("/api/v1/doadores")
@Tag(name = "Doadores", description = "API para gestão de doadores (pessoas físicas e jurídicas)")
public class DoadorController {

    private final DoadorService service;

    public DoadorController(DoadorService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "Lista todos os doadores",
            description = "Retorna lista de doadores com suporte a paginação e filtro opcional por nome."
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<Page<DoadorResponseDTO>> listar(
            @Parameter(description = "Nome para filtrar (parcial)")
            @RequestParam(required = false) String nome,

            @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listar(nome, pageable));
    }

    @GetMapping("/todos")
    @Operation(summary = "Lista completa de doadores", description = "Retorna todos os doadores sem paginação.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<DoadorResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{idDoador}")
    @Operation(summary = "Busca doador por ID", description = "Retorna os detalhes de um doador específico.")
    @ApiResponse(responseCode = "200", description = "Doador encontrado")
    @ApiResponse(responseCode = "404", description = "Doador não encontrado",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<DoadorResponseDTO> buscarPorId(
            @Parameter(description = "Identificador do doador", example = "1")
            @PathVariable Long idDoador) {
        return ResponseEntity.ok(service.buscarPorId(idDoador));
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo doador", description = "Cria um novo doador (pessoa física ou jurídica).")
    @ApiResponse(responseCode = "201", description = "Doador criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<DoadorResponseDTO> criar(
            @Valid @RequestBody DoadorRequestDTO dto) {
        DoadorResponseDTO salvo = service.criar(dto);
        URI location = URI.create("/api/v1/doadores/" + salvo.idDoador());
        return ResponseEntity.created(location).body(salvo);
    }

    @PutMapping("/{idDoador}")
    @Operation(summary = "Atualiza um doador", description = "Substitui os dados de um doador existente.")
    @ApiResponse(responseCode = "200", description = "Doador atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Doador não encontrado",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<DoadorResponseDTO> atualizar(
            @Parameter(description = "Identificador do doador", example = "1")
            @PathVariable Long idDoador,

            @Valid @RequestBody DoadorRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(idDoador, dto));
    }

    @DeleteMapping("/{idDoador}")
    @Operation(summary = "Remove um doador", description = "Exclui um doador pelo identificador único.")
    @ApiResponse(responseCode = "204", description = "Doador removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Doador não encontrado",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> deletar(
            @Parameter(description = "Identificador do doador", example = "1")
            @PathVariable Long idDoador) {
        service.deletar(idDoador);
        return ResponseEntity.noContent().build();
    }
}
