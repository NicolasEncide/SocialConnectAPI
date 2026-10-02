package br.com.socialconnect.api.beneficiarios.controller;

import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioPatchDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioRequestDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioResponseDTO;
import br.com.socialconnect.api.beneficiarios.service.BeneficiarioService;
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

@RestController
@RequestMapping("/api/v1/beneficiarios")
@Tag(name = "Beneficiários", description = "API para gestão de beneficiários atendidos pela instituição")
public class BeneficiarioController {

    private final BeneficiarioService service;

    public BeneficiarioController(BeneficiarioService service) {
        this.service = service;
    }

    // ✅ GET com paginação e filtros
    @GetMapping
    @Operation(
            summary = "Lista todos os beneficiários",
            description = "Retorna uma lista paginada de beneficiários, permitindo filtros opcionais por nome (parcial) e CPF (exato)."
    )
    @ApiResponse(responseCode = "200", description = "Lista paginada retornada com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Page<BeneficiarioResponseDTO>> listar(
            @Parameter(description = "Nome para filtrar (parcial, sem distinção de maiúsculas/minúsculas)")
            @RequestParam(required = false) String nome,

            @Parameter(description = "CPF exato para filtrar")
            @RequestParam(required = false) String cpf,

            @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listar(nome, cpf, pageable));
    }

    // ✅ GET por ID
    @GetMapping("/{idBeneficiario}")
    @Operation(
            summary = "Busca um beneficiário por ID",
            description = "Retorna os detalhes de um beneficiário específico através de seu identificador único."
    )
    @ApiResponse(responseCode = "200", description = "Beneficiário encontrado")
    @ApiResponse(responseCode = "404", description = "Beneficiário não encontrado",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<BeneficiarioResponseDTO> buscarPorId(
            @Parameter(description = "Identificador único do beneficiário", example = "1")
            @PathVariable Long idBeneficiario) {
        return ResponseEntity.ok(service.buscarPorId(idBeneficiario));
    }

    // ✅ POST com validação e 201 Created
    @PostMapping
    @Operation(
            summary = "Cria um novo beneficiário",
            description = "Cadastra um novo beneficiário no sistema com validação de dados obrigatórios e CPF."
    )
    @ApiResponse(responseCode = "201", description = "Beneficiário cadastrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "CPF já cadastrado no sistema",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<BeneficiarioResponseDTO> criar(
            @Valid @RequestBody BeneficiarioRequestDTO dto) {
        BeneficiarioResponseDTO salvo = service.criar(dto);
        URI location = URI.create("/api/v1/beneficiarios/" + salvo.idBeneficiario());
        return ResponseEntity.created(location).body(salvo);
    }

    // ✅ PUT (substituição total)
    @PutMapping("/{idBeneficiario}")
    @Operation(
            summary = "Substitui os dados de um beneficiário",
            description = "Atualiza integralmente os dados de um beneficiário cadastrado (PUT)."
    )
    @ApiResponse(responseCode = "200", description = "Beneficiário atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Beneficiário não encontrado",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Conflito de CPF com outro registro",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<BeneficiarioResponseDTO> atualizar(
            @Parameter(description = "Identificador único do beneficiário", example = "1")
            @PathVariable Long idBeneficiario,

            @Valid @RequestBody BeneficiarioRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(idBeneficiario, dto));
    }

    // ✅ PATCH (atualização parcial)
    @PatchMapping("/{idBeneficiario}")
    @Operation(
            summary = "Atualiza parcialmente um beneficiário",
            description = "Atualiza apenas os campos enviados no corpo da requisição (PATCH)."
    )
    @ApiResponse(responseCode = "200", description = "Beneficiário atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Beneficiário não encontrado",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<BeneficiarioResponseDTO> atualizarParcial(
            @Parameter(description = "Identificador único do beneficiário", example = "1")
            @PathVariable Long idBeneficiario,

            @Valid @RequestBody BeneficiarioPatchDTO dto) {
        return ResponseEntity.ok(service.atualizarParcial(idBeneficiario, dto));
    }

    // ✅ DELETE com 204 No Content
    @DeleteMapping("/{idBeneficiario}")
    @Operation(
            summary = "Remove um beneficiário",
            description = "Exclui um beneficiário cadastrado através de seu identificador único."
    )
    @ApiResponse(responseCode = "204", description = "Beneficiário removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Beneficiário não encontrado",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> deletar(
            @Parameter(description = "Identificador único do beneficiário", example = "1")
            @PathVariable Long idBeneficiario) {
        service.deletar(idBeneficiario);
        return ResponseEntity.noContent().build();
    }
}