package br.com.socialconnect.api.beneficiarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para atualização parcial (PATCH) de beneficiário")
public record BeneficiarioPatchDTO(
        @Schema(description = "Nome completo do beneficiário", example = "Maria da Silva Santos")
        @Size(max = 150, message = "{Size.nome}")
        String nome,

        @Schema(description = "Telefone para contato", example = "11988887777")
        @Size(max = 20)
        String telefone,

        @Schema(description = "Endereço atualizado", example = "Rua Nova, 456")
        @Size(max = 255)
        String endereco,

        @Schema(description = "Descrição atualizada da vulnerabilidade", example = "Renda familiar baixa")
        @Size(max = 500)
        String situacaoVulnerabilidade
) {}