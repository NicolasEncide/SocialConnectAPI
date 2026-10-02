package br.com.socialconnect.api.doacoes.repository;

import br.com.socialconnect.api.doacoes.model.Doacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DoacaoRepository extends JpaRepository<Doacao, Long>, JpaSpecificationExecutor<Doacao> {
}
