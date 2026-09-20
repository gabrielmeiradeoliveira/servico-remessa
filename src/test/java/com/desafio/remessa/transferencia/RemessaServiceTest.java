package com.desafio.remessa.transferencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.desafio.remessa.carteira.CarteiraRepository;
import com.desafio.remessa.carteira.domain.Carteira;
import com.desafio.remessa.cotacao.CotacaoService;
import com.desafio.remessa.exception.RemessaInvalidaException;
import com.desafio.remessa.transferencia.domain.Remessa;
import com.desafio.remessa.transferencia.domain.StatusRemessa;
import com.desafio.remessa.transferencia.dto.CriarRemessaRequest;
import com.desafio.remessa.usuario.UsuarioRepository;
import com.desafio.remessa.usuario.domain.TipoUsuario;
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
class RemessaServiceTest {

  @Mock private UsuarioRepository usuarioRepository;

  @Mock private CarteiraRepository carteiraRepository;

  @Mock private RemessaRepository remessaRepository;

  @Mock private CotacaoService cotacaoService;

  @InjectMocks private RemessaService remessaService;

  @Test
  void deveCriarRemessaComValorConvertido() {
    CriarRemessaRequest requisicao = new CriarRemessaRequest(1L, 2L, new BigDecimal("100.00"));
    prepararRemessaValida(requisicao, new BigDecimal("500.00"));
    when(remessaRepository.buscarTotalEnviadoNoDia(any(), any())).thenReturn(BigDecimal.ZERO);
    when(cotacaoService.buscarCotacaoCompra(any())).thenReturn(new BigDecimal("5.00"));
    when(remessaRepository.realizarTransferencia(any(Remessa.class))).thenReturn(10L);

    Remessa resultado = remessaService.criar(requisicao);

    ArgumentCaptor<Remessa> remessaCaptor = ArgumentCaptor.forClass(Remessa.class);
    verify(remessaRepository).realizarTransferencia(remessaCaptor.capture());
    Remessa remessaPersistida = remessaCaptor.getValue();
    assertEquals(10L, resultado.getId());
    assertEquals(new BigDecimal("20.00"), resultado.getValorDolar());
    assertEquals(StatusRemessa.CONCLUIDA, resultado.getStatus());
    assertEquals(new BigDecimal("100.00"), remessaPersistida.getValorReal());
    assertEquals(new BigDecimal("20.00"), remessaPersistida.getValorDolar());
  }

  @Test
  void deveImpedirRemessaParaOMesmoUsuario() {
    CriarRemessaRequest requisicao = new CriarRemessaRequest(1L, 1L, new BigDecimal("10.00"));

    assertThrows(RemessaInvalidaException.class, () -> remessaService.criar(requisicao));

    verifyNoInteractions(usuarioRepository, carteiraRepository, remessaRepository, cotacaoService);
  }

  @Test
  void deveImpedirRemessaSemSaldoSuficiente() {
    CriarRemessaRequest requisicao = new CriarRemessaRequest(1L, 2L, new BigDecimal("100.00"));
    prepararRemessaValida(requisicao, new BigDecimal("99.99"));

    assertThrows(RemessaInvalidaException.class, () -> remessaService.criar(requisicao));

    verifyNoInteractions(cotacaoService);
  }

  @Test
  void deveImpedirRemessaQueUltrapassaLimiteDiario() {
    CriarRemessaRequest requisicao = new CriarRemessaRequest(1L, 2L, new BigDecimal("100.01"));
    prepararRemessaValida(requisicao, new BigDecimal("500.00"));
    when(remessaRepository.buscarTotalEnviadoNoDia(any(), any()))
        .thenReturn(new BigDecimal("9900.00"));

    assertThrows(RemessaInvalidaException.class, () -> remessaService.criar(requisicao));

    verifyNoInteractions(cotacaoService);
  }

  private void prepararRemessaValida(CriarRemessaRequest requisicao, BigDecimal saldoRemetente) {
    when(usuarioRepository.buscarPorId(requisicao.remetenteId()))
        .thenReturn(
            Optional.of(
                Usuario.builder().id(requisicao.remetenteId()).tipo(TipoUsuario.PF).build()));
    when(usuarioRepository.buscarPorId(requisicao.destinatarioId()))
        .thenReturn(
            Optional.of(
                Usuario.builder().id(requisicao.destinatarioId()).tipo(TipoUsuario.PF).build()));
    when(carteiraRepository.buscarPorUsuarioId(requisicao.remetenteId()))
        .thenReturn(
            Optional.of(
                Carteira.builder()
                    .usuarioId(requisicao.remetenteId())
                    .saldoReal(saldoRemetente)
                    .build()));
    when(carteiraRepository.buscarPorUsuarioId(requisicao.destinatarioId()))
        .thenReturn(Optional.of(Carteira.builder().usuarioId(requisicao.destinatarioId()).build()));
  }
}
