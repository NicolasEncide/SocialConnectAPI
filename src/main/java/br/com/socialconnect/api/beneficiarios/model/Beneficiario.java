package br.com.socialconnect.api.beneficiarios.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "beneficiarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Beneficiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_beneficiario")
    private Long idBeneficiario;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(length = 20)
    private String telefone;

    @Column(length = 255)
    private String endereco;

    @Column(name = "situacao_vulnerabilidade", length = 500)
    private String situacaoVulnerabilidade;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro;
}