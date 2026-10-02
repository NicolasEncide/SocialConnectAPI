package br.com.socialconnect.api.doadores.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doadores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_doador")
    private Long idDoador;

    @Column(nullable = false, length = 150)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoDoador tipo;
}
