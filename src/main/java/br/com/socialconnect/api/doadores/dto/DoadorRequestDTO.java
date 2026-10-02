package br.com.socialconnect.api.doadores.dto;

import br.com.socialconnect.api.doadores.model.TipoDoador;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastro ou atualização de doador")
public record DoadorRequestDTO(
        @Schema(description = "Nome do doador (pessoa física ou razão social)", example = "Fundação Esperança")
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
        String nome,

        @Schema(description = "Tipo do doador: PESSOA_FISICA ou PESSOA_JURIDICA", example = "PESSOA_JURIDICA")
        @NotNull(message = "Tipo do doador é obrigatório")
        TipoDoador tipo
) {}
