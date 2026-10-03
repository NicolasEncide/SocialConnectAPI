package br.com.socialconnect.api.beneficiarios.service;

import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioPatchDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioRequestDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioResponseDTO;
import br.com.socialconnect.api.beneficiarios.model.Beneficiario;
import br.com.socialconnect.api.beneficiarios.repository.BeneficiarioRepository;
import br.com.socialconnect.api.exception.CpfDuplicadoException;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BeneficiarioServiceTest {

    @Mock
    private BeneficiarioRepository beneficiarioRepository;

    @InjectMocks
    private BeneficiarioService beneficiarioService;

    @Test
    @DisplayName("Deve criar beneficiário quando dados válidos")
    void deveCriarBeneficiarioQuandoDadosValidos() {
        BeneficiarioRequestDTO dto = new BeneficiarioRequestDTO(
                "Carlos Silva",
                "52998224725",
                "11988887777",
                "Rua Nova, 123",
                "Renda baixa"
        );

        Mockito.when(beneficiarioRepository.existsByCpf(dto.cpf())).thenReturn(false);

        Beneficiario beneficiarioSalvo = Beneficiario.builder()
                .idBeneficiario(1L)
                .nome(dto.nome())
                .cpf(dto.cpf())
                .telefone(dto.telefone())
                .endereco(dto.endereco())
                .situacaoVulnerabilidade(dto.situacaoVulnerabilidade())
                .dataCadastro(LocalDate.now())
                .build();

        Mockito.when(beneficiarioRepository.save(Mockito.any(Beneficiario.class))).thenReturn(beneficiarioSalvo);

        BeneficiarioResponseDTO resultado = beneficiarioService.criar(dto);

        Assertions.assertNotNull(resultado.idBeneficiario(), "ID do beneficiário não deve ser nulo");
        Assertions.assertEquals("Carlos Silva", resultado.nome());
        Assertions.assertEquals("52998224725", resultado.cpf());
        Mockito.verify(beneficiarioRepository, Mockito.times(1)).existsByCpf(dto.cpf());
        Mockito.verify(beneficiarioRepository, Mockito.times(1)).save(Mockito.any(Beneficiario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF já estiver cadastrado")
    void deveLancarExcecaoQuandoCpfJaExistir() {
        BeneficiarioRequestDTO dto = new BeneficiarioRequestDTO(
                "Carlos Silva",
                "52998224725",
                "11988887777",
                "Rua Nova, 123",
                "Renda baixa"
        );

        Mockito.when(beneficiarioRepository.existsByCpf(dto.cpf())).thenReturn(true);

        Assertions.assertThrows(CpfDuplicadoException.class, () -> beneficiarioService.criar(dto));
        Mockito.verify(beneficiarioRepository, Mockito.times(1)).existsByCpf(dto.cpf());
        Mockito.verify(beneficiarioRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve buscar beneficiário por ID com sucesso")
    void deveBuscarPorIdQuandoExistir() {
        Beneficiario beneficiario = Beneficiario.builder()
                .idBeneficiario(1L)
                .nome("Maria Silva")
                .cpf("52998224725")
                .dataCadastro(LocalDate.now())
                .build();

        Mockito.when(beneficiarioRepository.findById(1L)).thenReturn(Optional.of(beneficiario));

        BeneficiarioResponseDTO resultado = beneficiarioService.buscarPorId(1L);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(1L, resultado.idBeneficiario());
        Assertions.assertEquals("Maria Silva", resultado.nome());
        Mockito.verify(beneficiarioRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar ID inexistente")
    void deveLancarExcecaoQuandoBeneficiarioNaoExistir() {
        Mockito.when(beneficiarioRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> beneficiarioService.buscarPorId(999L));
        Mockito.verify(beneficiarioRepository, Mockito.times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve listar beneficiários com paginação")
    void deveListarBeneficiariosPaginados() {
        Pageable pageable = PageRequest.of(0, 10);
        Beneficiario beneficiario = Beneficiario.builder()
                .idBeneficiario(1L)
                .nome("Ana Souza")
                .cpf("52998224725")
                .dataCadastro(LocalDate.now())
                .build();

        Page<Beneficiario> pagina = new PageImpl<>(List.of(beneficiario), pageable, 1);
        Mockito.when(beneficiarioRepository.findAll(pageable)).thenReturn(pagina);

        Page<BeneficiarioResponseDTO> resultado = beneficiarioService.listar(null, null, pageable);

        Assertions.assertEquals(1, resultado.getTotalElements());
        Assertions.assertEquals("Ana Souza", resultado.getContent().get(0).nome());
        Mockito.verify(beneficiarioRepository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve atualizar beneficiário quando dados válidos")
    void deveAtualizarBeneficiarioQuandoDadosValidos() {
        Beneficiario existente = Beneficiario.builder()
                .idBeneficiario(1L)
                .nome("Maria Silva")
                .cpf("52998224725")
                .telefone("11999998888")
                .dataCadastro(LocalDate.now())
                .build();

        BeneficiarioRequestDTO dto = new BeneficiarioRequestDTO(
                "Maria Silva Santos",
                "52998224725",
                "11977776666",
                "Rua Nova, 500",
                "Vulnerabilidade moderada"
        );

        Mockito.when(beneficiarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        Mockito.when(beneficiarioRepository.save(Mockito.any(Beneficiario.class))).thenReturn(existente);

        BeneficiarioResponseDTO resultado = beneficiarioService.atualizar(1L, dto);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals("Maria Silva Santos", resultado.nome());
        Assertions.assertEquals("11977776666", resultado.telefone());
        Mockito.verify(beneficiarioRepository, Mockito.times(1)).save(existente);
    }

    @Test
    @DisplayName("Deve atualizar parcialmente um beneficiário")
    void deveAtualizarParcialmenteBeneficiario() {
        Beneficiario existente = Beneficiario.builder()
                .idBeneficiario(1L)
                .nome("Maria Silva")
                .cpf("52998224725")
                .telefone("11999998888")
                .dataCadastro(LocalDate.now())
                .build();

        BeneficiarioPatchDTO dto = new BeneficiarioPatchDTO(
                null,
                "11911112222",
                "Novo Endereço, 10",
                null
        );

        Mockito.when(beneficiarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        Mockito.when(beneficiarioRepository.save(Mockito.any(Beneficiario.class))).thenReturn(existente);

        BeneficiarioResponseDTO resultado = beneficiarioService.atualizarParcial(1L, dto);

        Assertions.assertEquals("11911112222", resultado.telefone());
        Assertions.assertEquals("Novo Endereço, 10", resultado.endereco());
        Mockito.verify(beneficiarioRepository, Mockito.times(1)).save(existente);
    }

    @Test
    @DisplayName("Deve deletar beneficiário quando ID existir")
    void deveDeletarBeneficiarioQuandoExistir() {
        Mockito.when(beneficiarioRepository.existsById(1L)).thenReturn(true);

        beneficiarioService.deletar(1L);

        Mockito.verify(beneficiarioRepository, Mockito.times(1)).existsById(1L);
        Mockito.verify(beneficiarioRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar ID inexistente")
    void deveLancarExcecaoAoDeletarInexistente() {
        Mockito.when(beneficiarioRepository.existsById(999L)).thenReturn(false);

        Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> beneficiarioService.deletar(999L));
        Mockito.verify(beneficiarioRepository, Mockito.never()).deleteById(999L);
    }
}
