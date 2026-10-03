package br.com.socialconnect.api.produtos.dto;

import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Dados retornados de um produto")
public record ProdutoResponseDTO(
        @Schema(description = "Identificador único do produto", example = "1")
        Long idProduto,

        @Schema(description = "Nome do produto", example = "Arroz 5kg")
        String nome,

        @Schema(description = "Categoria do produto", example = "ALIMENTO")
        CategoriaProduto categoria,

        @Schema(description = "Quantidade atual em estoque", example = "3")
        Integer estoqueAtual,

        @Schema(description = "Quantidade mínima de segurança para o estoque", example = "5")
        Integer estoqueMinimo,

        @Schema(description = "Unidade de medida", example = "KG")
        String unidadeMedida,

        @Schema(description = "Indica se o estoque atual está abaixo do estoque mínimo (estoqueAtual < estoqueMinimo)", example = "true")
        boolean estoqueBaixo,

        @Schema(description = "Data de cadastro do produto", example = "2026-10-02")
        LocalDate dataCadastro
) {
    public static ProdutoResponseDTO fromEntity(Produto p) {
        boolean estoqueBaixo = p.getEstoqueAtual() != null
                && p.getEstoqueMinimo() != null
                && p.getEstoqueAtual() < p.getEstoqueMinimo();

        return new ProdutoResponseDTO(
                p.getIdProduto(),
                p.getNome(),
                p.getCategoria(),
                p.getEstoqueAtual(),
                p.getEstoqueMinimo(),
                p.getUnidadeMedida(),
                estoqueBaixo,
                p.getDataCadastro()
        );
    }
}
