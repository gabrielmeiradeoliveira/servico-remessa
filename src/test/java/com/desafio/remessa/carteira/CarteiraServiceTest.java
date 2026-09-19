package com.desafio.remessa.carteira;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desafio.remessa.carteira.domain.Carteira;
import com.desafio.remessa.exception.CarteiraJaExisteException;
import com.desafio.remessa.exception.UsuarioNaoEncontradoException;
import com.desafio.remessa.usuario.UsuarioRepository;
import com.desafio.remessa.usuario.domain.Usuario;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarteiraServiceTest {

  @Mock private CarteiraRepository carteiraRepository;

  @Mock private UsuarioRepository usuarioRepository;

  @InjectMocks private CarteiraService carteiraService;

  @Test
  void deveBuscarCarteiraPorUsuarioId() {
    Carteira carteira = carteiraDeTeste();
    when(carteiraRepository.buscarPorUsuarioId(1L)).thenReturn(Optional.of(carteira));

    var resultado = carteiraService.buscarPorUsuarioId(1L);

    assertSame(carteira, resultado.orElseThrow());
    verify(carteiraRepository).buscarPorUsuarioId(1L);
  }

  @Test
  void deveInserirCarteiraComSaldosZerados() {
    when(usuarioRepository.buscarPorId(1L))
        .thenReturn(Optional.of(Usuario.builder().id(1L).build()));
    when(carteiraRepository.buscarPorUsuarioId(1L)).thenReturn(Optional.empty());
    when(carteiraRepository.inserir(org.mockito.ArgumentMatchers.any(Carteira.class)))
        .thenReturn(10L);

    Long id = carteiraService.inserir(1L);

    ArgumentCaptor<Carteira> carteiraCaptor = ArgumentCaptor.forClass(Carteira.class);
    verify(carteiraRepository).inserir(carteiraCaptor.capture());
    Carteira carteira = carteiraCaptor.getValue();
    assertEquals(10L, id);
    assertEquals(1L, carteira.getUsuarioId());
    assertEquals(BigDecimal.ZERO, carteira.getSaldoReal());
    assertEquals(BigDecimal.ZERO, carteira.getSaldoDolar());
  }

  @Test
  void deveImpedirCriacaoParaUsuarioInexistente() {
    when(usuarioRepository.buscarPorId(1L)).thenReturn(Optional.empty());

    assertThrows(UsuarioNaoEncontradoException.class, () -> carteiraService.inserir(1L));
    verify(usuarioRepository).buscarPorId(1L);
  }

  @Test
  void deveImpedirCriacaoDeSegundaCarteiraParaMesmoUsuario() {
    when(usuarioRepository.buscarPorId(1L))
        .thenReturn(Optional.of(Usuario.builder().id(1L).build()));
    when(carteiraRepository.buscarPorUsuarioId(1L)).thenReturn(Optional.of(carteiraDeTeste()));

    assertThrows(CarteiraJaExisteException.class, () -> carteiraService.inserir(1L));
    verify(carteiraRepository).buscarPorUsuarioId(1L);
  }

  @Test
  void deveAtualizarSaldos() {
    Carteira carteira = carteiraDeTeste();
    when(carteiraRepository.atualizarSaldos(carteira)).thenReturn(true);

    boolean atualizado = carteiraService.atualizarSaldos(carteira);

    assertTrue(atualizado);
    verify(carteiraRepository).atualizarSaldos(carteira);
  }

  private Carteira carteiraDeTeste() {
    return Carteira.builder()
        .id(10L)
        .usuarioId(1L)
        .saldoReal(new BigDecimal("100.00"))
        .saldoDolar(new BigDecimal("20.00"))
        .build();
  }
}
