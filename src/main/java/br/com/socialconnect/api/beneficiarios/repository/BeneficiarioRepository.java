package br.com.socialconnect.api.beneficiarios.repository;

import br.com.socialconnect.api.beneficiarios.model.Beneficiario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeneficiarioRepository
        extends JpaRepository<Beneficiario, Long> {

    Page<Beneficiario> findByCpf(String cpf, Pageable pageable);

    Page<Beneficiario> findByNomeContainingIgnoreCase(
            String nome, Pageable pageable);

    boolean existsByCpf(String cpf);
}