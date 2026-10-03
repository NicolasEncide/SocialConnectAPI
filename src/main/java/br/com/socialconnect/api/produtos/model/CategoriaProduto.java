package br.com.socialconnect.api.produtos.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Categorias disponíveis para classificação de produtos")
public enum CategoriaProduto {
    ALIMENTO,
    ROUPA,
    HIGIENE,
    OUTROS
}
