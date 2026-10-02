package br.com.socialconnect.api.doadores.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipo de personalidade jurídica do doador")
public enum TipoDoador {
    PESSOA_FISICA,
    PESSOA_JURIDICA
}
