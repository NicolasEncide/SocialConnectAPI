package br.com.socialconnect.api.doacoes.service;

import br.com.socialconnect.api.doacoes.dto.DoacaoRequestDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoResponseDTO;
import br.com.socialconnect.api.doacoes.model.Doacao;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import br.com.socialconnect.api.doacoes.repository.DoacaoRepository;
import br.com.socialconnect.api.doadores.model.Doador;
import br.com.socialconnect.api.doadores.model.TipoDoador;
import br.com.socialconnect.api.doadores.service.DoadorService;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class DoacaoServiceTest {

    @Mock
    private DoacaoRepository doacaoRepository;

    @Mock
    private DoadorService doadorService;

    @InjectMocks
    private DoacaoService doacaoService;

    @Test
    @DisplayName("Deve criar doação quando dados válidos")
    void deveCriarDoacaoQuandoDadosValidos() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        DoacaoRequestDTO dto = new DoacaoRequestDTO(
                1L,
                LocalDate.of(2026, 9, 11),
                new BigDecimal("100.00"),
                TipoDoacao.ALIMENTO,
                "Doação de teste"
        );

        Doador doador = Doador.builder()
                .idDoador(1L)
                .nome("Doador Teste")
                .tipo(TipoDoador.PESSOA_FISICA)
                .build();
        Mockito.when(doadorService.buscarEntidadePorId(1L)).thenReturn(doador);

        Doacao doacaoSalva = Doacao.builder()
                .idDoacao(1L)
                .doador(doador)
                .dataDoacao(dto.dataDoacao())
                .valor(dto.valor())
                .tipo(dto.tipo())
                .descricao(dto.descricao())
                .build();
        Mockito.when(doacaoRepository.save(Mockito.any(Doacao.class))).thenReturn(doacaoSalva);

        // ==========================================
        // ACT: Executar a ação
        // ==========================================
        DoacaoResponseDTO resultado = doacaoService.criar(dto);

        // ==========================================
        // ASSERT: Verificar o resultado
        // ==========================================
        Assertions.assertNotNull(resultado.idDoacao(), "ID da doação não deve ser nulo");
        Assertions.assertEquals(new BigDecimal("100.00"), resultado.valor());
        Mockito.verify(doadorService, Mockito.times(1)).buscarEntidadePorId(1L);
        Mockito.verify(doacaoRepository, Mockito.times(1)).save(Mockito.any(Doacao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando doador não existir")
    void deveLancarExcecaoQuandoDoadorNaoExistir() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        DoacaoRequestDTO dto = new DoacaoRequestDTO(
                999L,
                LocalDate.of(2026, 9, 11),
                new BigDecimal("100.00"),
                TipoDoacao.ALIMENTO,
                "Doação para doador inexistente"
        );

        Mockito.when(doadorService.buscarEntidadePorId(999L))
                .thenThrow(new RecursoNaoEncontradoException("Doador não encontrado com o ID: 999"));

        // ==========================================
        // ACT & ASSERT: Executar e verificar exceção
        // ==========================================
        Assertions.assertThrows(RuntimeException.class, () -> doacaoService.criar(dto));
        Mockito.verify(doadorService, Mockito.times(1)).buscarEntidadePorId(999L);
        Mockito.verify(doacaoRepository, Mockito.never()).save(Mockito.any());
    }
}
