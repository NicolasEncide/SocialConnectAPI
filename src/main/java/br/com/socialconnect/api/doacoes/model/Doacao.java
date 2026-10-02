package br.com.socialconnect.api.doacoes.model;

import br.com.socialconnect.api.doadores.model.Doador;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "doacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_doacao")
    private Long idDoacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_doador", nullable = false)
    private Doador doador;

    @Column(name = "data_doacao", nullable = false)
    private LocalDate dataDoacao;

    @Column(precision = 10, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoDoacao tipo;

    @Column(length = 500)
    private String descricao;
}
