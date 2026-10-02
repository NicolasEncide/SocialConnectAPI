package br.com.socialconnect.api.doacoes.service;

import br.com.socialconnect.api.doacoes.dto.DoacaoPatchDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoRequestDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoResponseDTO;
import br.com.socialconnect.api.doacoes.model.Doacao;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import br.com.socialconnect.api.doacoes.repository.DoacaoRepository;
import br.com.socialconnect.api.doadores.model.Doador;
import br.com.socialconnect.api.doadores.service.DoadorService;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import jakarta.persistence.criteria.JoinType;
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
public class DoacaoService {

    private final DoacaoRepository repository;
    private final DoadorService doadorService;

    public DoacaoService(DoacaoRepository repository, DoadorService doadorService) {
        this.repository = repository;
        this.doadorService = doadorService;
    }

    @Transactional(readOnly = true)
    public Page<DoacaoResponseDTO> listar(
            LocalDate dataInicio, LocalDate dataFim, TipoDoacao tipo, Pageable pageable) {

        Specification<Doacao> spec = (root, query, cb) -> {
            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("doador", JoinType.LEFT);
            }
            List<Predicate> predicates = new ArrayList<>();
            if (dataInicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataDoacao"), dataInicio));
            }
            if (dataFim != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dataDoacao"), dataFim));
            }
            if (tipo != null) {
                predicates.add(cb.equal(root.get("tipo"), tipo));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return repository.findAll(spec, pageable)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public DoacaoResponseDTO buscarPorId(Long idDoacao) {
        return repository.findById(idDoacao)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Doação não encontrada com o ID: " + idDoacao));
    }

    @Transactional
    public DoacaoResponseDTO criar(DoacaoRequestDTO dto) {
        Doador doador = doadorService.buscarEntidadePorId(dto.idDoador());

        Doacao entity = Doacao.builder()
                .doador(doador)
                .dataDoacao(dto.dataDoacao())
                .valor(dto.valor())
                .tipo(dto.tipo())
                .descricao(dto.descricao())
                .build();

        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public DoacaoResponseDTO atualizar(Long idDoacao, DoacaoRequestDTO dto) {
        Doacao entity = repository.findById(idDoacao)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Doação não encontrada com o ID: " + idDoacao));

        Doador doador = doadorService.buscarEntidadePorId(dto.idDoador());

        entity.setDoador(doador);
        entity.setDataDoacao(dto.dataDoacao());
        entity.setValor(dto.valor());
        entity.setTipo(dto.tipo());
        entity.setDescricao(dto.descricao());

        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public DoacaoResponseDTO atualizarParcial(Long idDoacao, DoacaoPatchDTO dto) {
        Doacao entity = repository.findById(idDoacao)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Doação não encontrada com o ID: " + idDoacao));

        if (dto.idDoador() != null) {
            Doador doador = doadorService.buscarEntidadePorId(dto.idDoador());
            entity.setDoador(doador);
        }
        if (dto.dataDoacao() != null) {
            entity.setDataDoacao(dto.dataDoacao());
        }
        if (dto.valor() != null) {
            entity.setValor(dto.valor());
        }
        if (dto.tipo() != null) {
            entity.setTipo(dto.tipo());
        }
        if (dto.descricao() != null) {
            entity.setDescricao(dto.descricao());
        }

        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public void deletar(Long idDoacao) {
        if (!repository.existsById(idDoacao)) {
            throw new RecursoNaoEncontradoException(
                    "Doação não encontrada com o ID: " + idDoacao);
        }
        repository.deleteById(idDoacao);
    }

    private DoacaoResponseDTO toResponseDTO(Doacao entity) {
        return new DoacaoResponseDTO(
                entity.getIdDoacao(),
                entity.getDoador().getIdDoador(),
                entity.getDoador().getNome(),
                entity.getDataDoacao(),
                entity.getValor(),
                entity.getTipo(),
                entity.getDescricao()
        );
    }
}
