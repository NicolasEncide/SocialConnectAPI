package br.com.socialconnect.api.doacoes.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipo ou categoria da doação recebida")
public enum TipoDoacao {
    ALIMENTO,
    ROUPA,
    FINANCEIRA
}
