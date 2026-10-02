package br.com.socialconnect.api.doacoes.dto;

import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import br.com.socialconnect.api.validation.DataNaoFutura;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Dados para cadastro de uma nova doação")
public record DoacaoRequestDTO(
        @Schema(description = "ID do doador que realizou a doação", example = "1")
        @NotNull(message = "ID do doador é obrigatório")
        Long idDoador,

        @Schema(description = "Data em que a doação foi realizada", example = "2026-08-20")
        @NotNull(message = "Data da doação é obrigatória")
        @DataNaoFutura
        LocalDate dataDoacao,

        @Schema(description = "Valor financeiro estimado ou doado (obrigatório se financeira)", example = "250.00")
        @Positive(message = "O valor deve ser positivo")
        BigDecimal valor,

        @Schema(description = "Tipo da doação: ALIMENTO, ROUPA ou FINANCEIRA", example = "ALIMENTO")
        @NotNull(message = "Tipo da doação é obrigatório")
        TipoDoacao tipo,

        @Schema(description = "Descrição detalhada dos itens ou propósito da doação", example = "10 cestas básicas")
        @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
        String descricao
) {}
