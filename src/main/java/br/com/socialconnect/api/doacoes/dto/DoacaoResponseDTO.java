package br.com.socialconnect.api.doacoes.dto;

import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Dados retornados de uma doação")
public record DoacaoResponseDTO(
        @Schema(description = "Identificador único da doação", example = "1")
        Long idDoacao,

        @Schema(description = "Identificador do doador", example = "1")
        Long idDoador,

        @Schema(description = "Nome do doador", example = "Fundação Esperança")
        String nomeDoador,

        @Schema(description = "Data da doação", example = "2026-08-20")
        LocalDate dataDoacao,

        @Schema(description = "Valor da doação", example = "250.00")
        BigDecimal valor,

        @Schema(description = "Tipo da doação", example = "ALIMENTO")
        TipoDoacao tipo,

        @Schema(description = "Descrição da doação", example = "10 cestas básicas")
        String descricao
) {}
