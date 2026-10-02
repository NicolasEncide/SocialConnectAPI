package br.com.socialconnect.api.doadores.dto;

import br.com.socialconnect.api.doadores.model.TipoDoador;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados retornados de um doador cadastrado")
public record DoadorResponseDTO(
        @Schema(description = "Identificador único do doador", example = "1")
        Long idDoador,

        @Schema(description = "Nome do doador", example = "Fundação Esperança")
        String nome,

        @Schema(description = "Tipo do doador", example = "PESSOA_JURIDICA")
        TipoDoador tipo
) {}
