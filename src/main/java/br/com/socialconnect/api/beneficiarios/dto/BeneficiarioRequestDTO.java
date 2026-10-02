package br.com.socialconnect.api.beneficiarios.dto;

import br.com.socialconnect.api.validation.CPF;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criação ou atualização de beneficiário")
public record BeneficiarioRequestDTO(

        @Schema(description = "Nome completo do beneficiário", example = "Maria da Silva")
        @NotBlank(message = "{NotBlank.nome}")
        @Size(max = 150, message = "{Size.nome}")
        String nome,

        @Schema(description = "CPF válido (com ou sem pontuação)", example = "12345678900")
        @NotBlank(message = "{NotBlank.cpf}")
        @CPF
        String cpf,

        @Schema(description = "Telefone para contato", example = "11999999999")
        @Size(max = 20)
        String telefone,

        @Schema(description = "Endereço de residência", example = "Rua das Flores, 123")
        @Size(max = 255)
        String endereco,

        @Schema(description = "Descrição da situação de vulnerabilidade", example = "Renda familiar baixa")
        @Size(max = 500)
        String situacaoVulnerabilidade

) {}