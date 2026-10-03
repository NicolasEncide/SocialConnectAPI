package br.com.socialconnect.api.produtos.repository;

import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long>, JpaSpecificationExecutor<Produto> {
    boolean existsByNome(String nome);
    boolean existsByNomeIgnoreCase(String nome);
    Optional<Produto> findByNomeIgnoreCase(String nome);
    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Produto> findByCategoria(CategoriaProduto categoria, Pageable pageable);
    Page<Produto> findByNomeContainingIgnoreCaseAndCategoria(String nome, CategoriaProduto categoria, Pageable pageable);
}
