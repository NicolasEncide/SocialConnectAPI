package br.com.socialconnect.api.produtos.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "produtos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private Long idProduto;

    @Column(nullable = false, length = 150, unique = true)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoriaProduto categoria;

    @Column(name = "estoque_atual", nullable = false)
    private Integer estoqueAtual;

    @Column(name = "estoque_minimo", nullable = false)
    private Integer estoqueMinimo;

    @Column(name = "unidade_medida", nullable = false, length = 20)
    private String unidadeMedida;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro;
}
