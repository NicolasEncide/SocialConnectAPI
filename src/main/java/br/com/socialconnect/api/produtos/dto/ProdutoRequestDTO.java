package br.com.socialconnect.api.produtos.dto;

import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criação ou atualização de produto")
public record ProdutoRequestDTO(
        @Schema(description = "Nome do produto", example = "Arroz 5kg")
        @NotBlank(message = "O nome do produto é obrigatório")
        @Size(max = 150, message = "O nome do produto deve ter no máximo 150 caracteres")
        String nome,

        @Schema(description = "Categoria do produto", example = "ALIMENTO")
        @NotNull(message = "A categoria do produto é obrigatória")
        CategoriaProduto categoria,

        @Schema(description = "Quantidade atual em estoque", example = "20")
        @NotNull(message = "O estoque atual é obrigatório")
        Integer estoqueAtual,

        @Schema(description = "Quantidade mínima de segurança para o estoque", example = "5")
        @NotNull(message = "O estoque mínimo é obrigatório")
        Integer estoqueMinimo,

        @Schema(description = "Unidade de medida do produto", example = "KG")
        @NotBlank(message = "A unidade de medida é obrigatória")
        @Size(max = 20, message = "A unidade de medida deve ter no máximo 20 caracteres")
        String unidadeMedida
) {}