package br.com.socialconnect.api.produtos.service;

import br.com.socialconnect.api.exception.EstoqueMenorQueZeroException;
import br.com.socialconnect.api.exception.NomeDuplicadoException;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    @Test
    @DisplayName("Deve criar produto com dados válidos e calcular estoqueBaixo como false")
    void deveCriarProdutoQuandoDadosValidosEstoqueNormal() {
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Arroz Tipo 1 - 5kg",
                CategoriaProduto.ALIMENTO,
                20,
                5,
                "PACOTE"
        );

        Mockito.when(produtoRepository.existsByNomeIgnoreCase(dto.nome().trim())).thenReturn(false);

        Produto salvo = Produto.builder()
                .idProduto(1L)
                .nome(dto.nome())
                .categoria(dto.categoria())
                .estoqueAtual(20)
                .estoqueMinimo(5)
                .unidadeMedida(dto.unidadeMedida())
                .dataCadastro(LocalDate.now())
                .build();

        Mockito.when(produtoRepository.save(Mockito.any(Produto.class))).thenReturn(salvo);

        ProdutoResponseDTO resultado = produtoService.criar(dto);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(1L, resultado.idProduto());
        Assertions.assertEquals("Arroz Tipo 1 - 5kg", resultado.nome());
        Assertions.assertEquals(CategoriaProduto.ALIMENTO, resultado.categoria());
        Assertions.assertEquals(20, resultado.estoqueAtual());
        Assertions.assertEquals(5, resultado.estoqueMinimo());
        Assertions.assertFalse(resultado.estoqueBaixo(), "estoqueBaixo deve ser false quando estoqueAtual >= estoqueMinimo");
    }

    @Test
    @DisplayName("Deve criar produto e calcular estoqueBaixo como true quando estoqueAtual < estoqueMinimo")
    void deveCriarProdutoComEstoqueBaixoTrue() {
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Feijão Preto 1kg",
                CategoriaProduto.ALIMENTO,
                3,
                10,
                "KG"
        );

        Mockito.when(produtoRepository.existsByNomeIgnoreCase(dto.nome().trim())).thenReturn(false);

        Produto salvo = Produto.builder()
                .idProduto(2L)
                .nome(dto.nome())
                .categoria(dto.categoria())
                .estoqueAtual(3)
                .estoqueMinimo(10)
                .unidadeMedida(dto.unidadeMedida())
                .dataCadastro(LocalDate.now())
                .build();

        Mockito.when(produtoRepository.save(Mockito.any(Produto.class))).thenReturn(salvo);

        ProdutoResponseDTO resultado = produtoService.criar(dto);

        Assertions.assertNotNull(resultado);
        Assertions.assertTrue(resultado.estoqueBaixo(), "estoqueBaixo deve ser true quando estoqueAtual < estoqueMinimo");
    }

    @Test
    @DisplayName("Deve lançar NomeDuplicadoException quando já existir produto com o mesmo nome")
    void deveLancarExcecaoQuandoNomeJaExistir() {
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Arroz 5kg",
                CategoriaProduto.ALIMENTO,
                10,
                2,
                "PCT"
        );

        Mockito.when(produtoRepository.existsByNomeIgnoreCase(dto.nome().trim())).thenReturn(true);

        Assertions.assertThrows(NomeDuplicadoException.class, () -> produtoService.criar(dto));
        Mockito.verify(produtoRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve lançar EstoqueMenorQueZeroException quando estoqueAtual for negativo")
    void deveLancarExcecaoQuandoEstoqueAtualNegativo() {
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Leite Integral",
                CategoriaProduto.ALIMENTO,
                -5,
                2,
                "L"
        );

        Mockito.when(produtoRepository.existsByNomeIgnoreCase(dto.nome().trim())).thenReturn(false);

        Assertions.assertThrows(EstoqueMenorQueZeroException.class, () -> produtoService.criar(dto));
        Mockito.verify(produtoRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve buscar produto por ID com sucesso")
    void deveBuscarPorIdQuandoExistir() {
        Produto produto = Produto.builder()
                .idProduto(1L)
                .nome("Sabonete")
                .categoria(CategoriaProduto.HIGIENE)
                .estoqueAtual(15)
                .estoqueMinimo(5)
                .unidadeMedida("UN")
                .dataCadastro(LocalDate.now())
                .build();

        Mockito.when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        ProdutoResponseDTO resultado = produtoService.buscarPorId(1L);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(1L, resultado.idProduto());
        Assertions.assertEquals("Sabonete", resultado.nome());
        Assertions.assertEquals(CategoriaProduto.HIGIENE, resultado.categoria());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando ID não existir")
    void deveLancarExcecaoQuandoProdutoNaoExistir() {
        Mockito.when(produtoRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> produtoService.buscarPorId(999L));
    }

    @Test
    @DisplayName("Deve atualizar produto totalmente")
    void deveAtualizarProdutoQuandoValido() {
        Produto existente = Produto.builder()
                .idProduto(1L)
                .nome("Camiseta P")
                .categoria(CategoriaProduto.ROUPA)
                .estoqueAtual(10)
                .estoqueMinimo(5)
                .unidadeMedida("UN")
                .dataCadastro(LocalDate.of(2026, 1, 1))
                .build();

        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Camiseta M",
                CategoriaProduto.ROUPA,
                8,
                3,
                "UN"
        );

        Mockito.when(produtoRepository.findById(1L)).thenReturn(Optional.of(existente));
        Mockito.when(produtoRepository.existsByNomeIgnoreCase(dto.nome().trim())).thenReturn(false);
        Mockito.when(produtoRepository.save(Mockito.any(Produto.class))).thenReturn(existente);

        ProdutoResponseDTO resultado = produtoService.atualizar(1L, dto);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals("Camiseta M", resultado.nome());
        Assertions.assertEquals(8, resultado.estoqueAtual());
        Assertions.assertEquals(LocalDate.of(2026, 1, 1), resultado.dataCadastro(), "Data de cadastro original deve ser mantida");
    }

    @Test
    @DisplayName("Deve deletar produto quando ID existir")
    void deveDeletarProdutoQuandoExistir() {
        Mockito.when(produtoRepository.existsById(1L)).thenReturn(true);

        produtoService.deletar(1L);

        Mockito.verify(produtoRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar produto inexistente")
    void deveLancarExcecaoAoDeletarInexistente() {
        Mockito.when(produtoRepository.existsById(999L)).thenReturn(false);

        Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> produtoService.deletar(999L));
        Mockito.verify(produtoRepository, Mockito.never()).deleteById(999L);
    }

    @Test
    @DisplayName("Deve listar produtos com paginação")
    @SuppressWarnings("unchecked")
    void deveListarProdutosComPaginacao() {
        Pageable pageable = PageRequest.of(0, 10);
        Produto p = Produto.builder()
                .idProduto(1L)
                .nome("Arroz")
                .categoria(CategoriaProduto.ALIMENTO)
                .estoqueAtual(5)
                .estoqueMinimo(10)
                .unidadeMedida("KG")
                .dataCadastro(LocalDate.now())
                .build();

        Page<Produto> page = new PageImpl<>(List.of(p), pageable, 1);
        Mockito.when(produtoRepository.findAll(Mockito.any(Specification.class), Mockito.eq(pageable)))
                .thenReturn(page);

        Page<ProdutoResponseDTO> resultado = produtoService.listar("Arroz", CategoriaProduto.ALIMENTO, pageable);

        Assertions.assertEquals(1, resultado.getTotalElements());
        Assertions.assertEquals("Arroz", resultado.getContent().get(0).nome());
        Assertions.assertTrue(resultado.getContent().get(0).estoqueBaixo());
    }
}
