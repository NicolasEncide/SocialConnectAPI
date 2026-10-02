package br.com.socialconnect.api.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Estrutura padronizada de erro RFC 7807 (Problem Details)")
public record ProblemDetail(
        @Schema(description = "URI de referência para o tipo de erro", example = "https://socialconnect.api/errors/validacao")
        String type,

        @Schema(description = "Título legível do erro", example = "Erro de validação")
        String title,

        @Schema(description = "Código de status HTTP", example = "400")
        int status,

        @Schema(description = "Descrição detalhada do erro", example = "Um ou mais campos são inválidos.")
        String detail,

        @Schema(description = "URI da requisição que gerou o erro", example = "/api/v1/beneficiarios")
        String instance,

        @Schema(description = "Data e hora da ocorrência", example = "2026-08-29T14:30:00")
        LocalDateTime timestamp,

        @Schema(description = "Lista de erros por campo (em caso de falha de validação)")
        List<FieldError> errors
) {
    @Schema(description = "Detalhe do erro de um campo específico")
    public record FieldError(
            @Schema(description = "Nome do campo que falhou", example = "cpf")
            String field,

            @Schema(description = "Mensagem descrevendo a falha no campo", example = "CPF inválido")
            String message
    ) {}
}
