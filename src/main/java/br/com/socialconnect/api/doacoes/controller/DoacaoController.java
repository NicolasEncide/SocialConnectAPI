package br.com.socialconnect.api.doacoes.controller;

import br.com.socialconnect.api.doacoes.dto.DoacaoPatchDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoRequestDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoResponseDTO;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import br.com.socialconnect.api.doacoes.service.DoacaoService;
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
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/doacoes")
@Tag(name = "Doações", description = "API para gestão e registro de doações recebidas")
public class DoacaoController {

    private final DoacaoService service;

    public DoacaoController(DoacaoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "Lista doações com filtros dinâmicos e paginação",
            description = "Consulta doações recebidas com suporte a filtros opcionais por intervalo de datas e tipo de doação."
    )
    @ApiResponse(responseCode = "200", description = "Lista de doações retornada com sucesso")
    public ResponseEntity<Page<DoacaoResponseDTO>> listar(
            @Parameter(description = "Data inicial para o filtro (YYYY-MM-DD)", example = "2026-01-01")
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate dataInicio,

            @Parameter(description = "Data final para o filtro (YYYY-MM-DD)", example = "2026-12-31")
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate dataFim,

            @Parameter(description = "Tipo da doação (ALIMENTO, ROUPA, FINANCEIRA)", example = "ALIMENTO")
            @RequestParam(required = false) TipoDoacao tipo,

            @ParameterObject @PageableDefault(size = 20, sort = "dataDoacao") Pageable pageable) {
        return ResponseEntity.ok(service.listar(dataInicio, dataFim, tipo, pageable));
    }

    @GetMapping("/{idDoacao}")
    @Operation(summary = "Busca doação por ID", description = "Retorna os dados detalhados de uma doação pelo identificador.")
    @ApiResponse(responseCode = "200", description = "Doação encontrada")
    @ApiResponse(responseCode = "404", description = "Doação não encontrada",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<DoacaoResponseDTO> buscarPorId(
            @Parameter(description = "Identificador da doação", example = "1")
            @PathVariable Long idDoacao) {
        return ResponseEntity.ok(service.buscarPorId(idDoacao));
    }

    @PostMapping
    @Operation(summary = "Registra uma nova doação", description = "Cadastra uma doação vinculada a um doador existente.")
    @ApiResponse(responseCode = "201", description = "Doação cadastrada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Doador informado não encontrado",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<DoacaoResponseDTO> criar(
            @Valid @RequestBody DoacaoRequestDTO dto) {
        DoacaoResponseDTO salvo = service.criar(dto);
        URI location = URI.create("/api/v1/doacoes/" + salvo.idDoacao());
        return ResponseEntity.created(location).body(salvo);
    }

    @PutMapping("/{idDoacao}")
    @Operation(summary = "Atualiza totalmente uma doação", description = "Substitui todos os dados de uma doação existente (PUT).")
    @ApiResponse(responseCode = "200", description = "Doação atualizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Doação ou doador não encontrado",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<DoacaoResponseDTO> atualizar(
            @Parameter(description = "Identificador da doação", example = "1")
            @PathVariable Long idDoacao,

            @Valid @RequestBody DoacaoRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(idDoacao, dto));
    }

    @PatchMapping("/{idDoacao}")
    @Operation(summary = "Atualiza parcialmente uma doação", description = "Altera campos específicos de uma doação (PATCH).")
    @ApiResponse(responseCode = "200", description = "Doação atualizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Doação ou doador não encontrado",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<DoacaoResponseDTO> atualizarParcial(
            @Parameter(description = "Identificador da doação", example = "1")
            @PathVariable Long idDoacao,

            @Valid @RequestBody DoacaoPatchDTO dto) {
        return ResponseEntity.ok(service.atualizarParcial(idDoacao, dto));
    }

    @DeleteMapping("/{idDoacao}")
    @Operation(summary = "Remove uma doação", description = "Exclui um registro de doação pelo identificador.")
    @ApiResponse(responseCode = "204", description = "Doação removida com sucesso")
    @ApiResponse(responseCode = "404", description = "Doação não encontrada",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> deletar(
            @Parameter(description = "Identificador da doação", example = "1")
            @PathVariable Long idDoacao) {
        service.deletar(idDoacao);
        return ResponseEntity.noContent().build();
    }
}
