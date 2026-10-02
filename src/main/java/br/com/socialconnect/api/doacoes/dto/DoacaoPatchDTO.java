package br.com.socialconnect.api.doacoes.dto;

import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Dados para atualização parcial de doação")
public record DoacaoPatchDTO(
        @Schema(description = "ID do doador", example = "1")
        Long idDoador,

        @Schema(description = "Data da doação", example = "2026-08-20")
        LocalDate dataDoacao,

        @Schema(description = "Valor estimado ou doado", example = "300.00")
        @Positive(message = "O valor deve ser positivo")
        BigDecimal valor,

        @Schema(description = "Tipo da doação", example = "ROUPA")
        TipoDoacao tipo,

        @Schema(description = "Descrição da doação", example = "Agasalhos e cobertores")
        @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
        String descricao
) {}
