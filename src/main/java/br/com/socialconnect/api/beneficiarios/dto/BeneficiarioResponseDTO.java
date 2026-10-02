package br.com.socialconnect.api.beneficiarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Dados retornados de um beneficiário")
public record BeneficiarioResponseDTO(
        @Schema(description = "Identificador único do beneficiário", example = "1")
        Long idBeneficiario,

        @Schema(description = "Nome completo do beneficiário", example = "Maria da Silva")
        String nome,

        @Schema(description = "CPF do beneficiário", example = "12345678900")
        String cpf,

        @Schema(description = "Telefone para contato", example = "11999999999")
        String telefone,

        @Schema(description = "Endereço de residência", example = "Rua das Flores, 123")
        String endereco,

        @Schema(description = "Descrição da situação de vulnerabilidade", example = "Renda familiar baixa")
        String situacaoVulnerabilidade,

        @Schema(description = "Data de cadastro no sistema", example = "2026-08-29")
        LocalDate dataCadastro
) {}