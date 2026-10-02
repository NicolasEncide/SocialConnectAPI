package br.com.socialconnect.api.beneficiarios.repository;

import br.com.socialconnect.api.beneficiarios.model.Beneficiario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeneficiarioRepository
        extends JpaRepository<Beneficiario, Long> {

    // Paginado: busca por CPF exato
    Page<Beneficiario> findByCpf(String cpf, Pageable pageable);

    // Paginado: busca parcial por nome (case-insensitive)
    Page<Beneficiario> findByNomeContainingIgnoreCase(
            String nome, Pageable pageable);

    // Verificação de unicidade (usado no POST)
    boolean existsByCpf(String cpf);
}