package br.com.socialconnect.api.produtos.service;

import br.com.socialconnect.api.exception.EstoqueMenorQueZeroException;
import br.com.socialconnect.api.exception.NomeDuplicadoException;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> listar(String nome, CategoriaProduto categoria, Pageable pageable) {
        Specification<Produto> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (nome != null && !nome.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("nome")), "%" + nome.trim().toLowerCase() + "%"));
            }
            if (categoria != null) {
                predicates.add(cb.equal(root.get("categoria"), categoria));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return produtoRepository.findAll(spec, pageable).map(ProdutoResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarPorId(Long idProduto) {
        return produtoRepository.findById(idProduto)
                .map(ProdutoResponseDTO::fromEntity)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Produto não encontrado com o ID: " + idProduto));
    }

    @Transactional
    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
        if (produtoRepository.existsByNomeIgnoreCase(dto.nome().trim())) {
            throw new NomeDuplicadoException(dto.nome().trim());
        }
        if ((dto.estoqueAtual() != null && dto.estoqueAtual() < 0) || (dto.estoqueMinimo() != null && dto.estoqueMinimo() < 0)) {
            throw new EstoqueMenorQueZeroException();
        }

        Produto entity = Produto.builder()
                .nome(dto.nome().trim())
                .categoria(dto.categoria())
                .estoqueAtual(dto.estoqueAtual())
                .estoqueMinimo(dto.estoqueMinimo())
                .unidadeMedida(dto.unidadeMedida().trim())
                .dataCadastro(LocalDate.now())
                .build();

        return ProdutoResponseDTO.fromEntity(produtoRepository.save(entity));
    }

    @Transactional
    public ProdutoResponseDTO atualizar(Long idProduto, ProdutoRequestDTO dto) {
        Produto entity = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Produto não encontrado com o ID: " + idProduto));

        if (!entity.getNome().equalsIgnoreCase(dto.nome().trim()) 
                && produtoRepository.existsByNomeIgnoreCase(dto.nome().trim())) {
            throw new NomeDuplicadoException(dto.nome().trim());
        }

        if ((dto.estoqueAtual() != null && dto.estoqueAtual() < 0) || (dto.estoqueMinimo() != null && dto.estoqueMinimo() < 0)) {
            throw new EstoqueMenorQueZeroException();
        }

        entity.setNome(dto.nome().trim());
        entity.setCategoria(dto.categoria());
        entity.setEstoqueAtual(dto.estoqueAtual());
        entity.setEstoqueMinimo(dto.estoqueMinimo());
        entity.setUnidadeMedida(dto.unidadeMedida().trim());

        return ProdutoResponseDTO.fromEntity(produtoRepository.save(entity));
    }

    @Transactional
    public void deletar(Long idProduto) {
        if (!produtoRepository.existsById(idProduto)) {
            throw new RecursoNaoEncontradoException(
                    "Produto não encontrado com o ID: " + idProduto);
        }
        produtoRepository.deleteById(idProduto);
    }
}
