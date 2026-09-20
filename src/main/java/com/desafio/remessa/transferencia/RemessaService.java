package com.desafio.remessa.transferencia;

import com.desafio.remessa.carteira.CarteiraRepository;
import com.desafio.remessa.cotacao.CotacaoService;
import com.desafio.remessa.exception.CarteiraNaoEncontradaException;
import com.desafio.remessa.exception.RemessaInvalidaException;
import com.desafio.remessa.exception.UsuarioNaoEncontradoException;
import com.desafio.remessa.transferencia.domain.Remessa;
import com.desafio.remessa.transferencia.domain.StatusRemessa;
import com.desafio.remessa.transferencia.dto.CriarRemessaRequest;
import com.desafio.remessa.usuario.UsuarioRepository;
import com.desafio.remessa.usuario.domain.TipoUsuario;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RemessaService {

  private static final BigDecimal LIMITE_PF = new BigDecimal("10000.00");

  private static final BigDecimal LIMITE_PJ = new BigDecimal("50000.00");

  private final UsuarioRepository usuarioRepository;
  private final CarteiraRepository carteiraRepository;
  private final RemessaRepository remessaRepository;
  private final CotacaoService cotacaoService;

  public Remessa criar(CriarRemessaRequest request) {

    log.info(
        "Iniciando remessa: remetenteId={}, destinatarioId={}, valorReal={}",
        request.remetenteId(),
        request.destinatarioId(),
        request.valorReal());

    validarUsuariosDiferentes(request);

    var remetente =
        usuarioRepository
            .buscarPorId(request.remetenteId())
            .orElseThrow(() -> new UsuarioNaoEncontradoException("Remetente não encontrado"));

    usuarioRepository
        .buscarPorId(request.destinatarioId())
        .orElseThrow(() -> new UsuarioNaoEncontradoException("Destinatário não encontrado"));

    var carteiraRemetente =
        carteiraRepository
            .buscarPorUsuarioId(request.remetenteId())
            .orElseThrow(
                () -> new CarteiraNaoEncontradaException("Carteira do remetente não encontrada"));

    carteiraRepository
        .buscarPorUsuarioId(request.destinatarioId())
        .orElseThrow(
            () -> new CarteiraNaoEncontradaException("Carteira do destinatário não encontrada"));

    validarSaldo(carteiraRemetente.getSaldoReal(), request.valorReal());

    LocalDate dataAtual = LocalDate.now();

    BigDecimal totalHoje =
        remessaRepository.buscarTotalEnviadoNoDia(request.remetenteId(), dataAtual);

    validarLimiteDiario(remetente.getTipo(), totalHoje, request.valorReal());

    BigDecimal cotacao = cotacaoService.buscarCotacaoCompra(dataAtual);

    BigDecimal valorDolar = calcularValorDolar(request.valorReal(), cotacao);

    Remessa remessa =
        Remessa.builder()
            .remetenteId(request.remetenteId())
            .destinatarioId(request.destinatarioId())
            .valorReal(request.valorReal())
            .valorDolar(valorDolar)
            .cotacao(cotacao)
            .dataHora(LocalDateTime.now())
            .status(StatusRemessa.CONCLUIDA)
            .build();

    Long id = remessaRepository.realizarTransferencia(remessa);

    log.info(
        "Remessa concluída: id={}, remetenteId={}, destinatarioId={}",
        id,
        request.remetenteId(),
        request.destinatarioId());

    return Remessa.builder()
        .id(id)
        .remetenteId(remessa.getRemetenteId())
        .destinatarioId(remessa.getDestinatarioId())
        .valorReal(remessa.getValorReal())
        .valorDolar(remessa.getValorDolar())
        .cotacao(remessa.getCotacao())
        .dataHora(remessa.getDataHora())
        .status(remessa.getStatus())
        .build();
  }

  private BigDecimal calcularValorDolar(BigDecimal valorReal, BigDecimal cotacao) {

    return valorReal.divide(cotacao, 2, RoundingMode.HALF_UP);
  }

  private void validarUsuariosDiferentes(CriarRemessaRequest request) {

    if (request.remetenteId().equals(request.destinatarioId())) {

      throw new RemessaInvalidaException("Remetente e destinatário devem ser diferentes");
    }
  }

  private void validarSaldo(BigDecimal saldo, BigDecimal valor) {

    if (saldo.compareTo(valor) < 0) {
      throw new RemessaInvalidaException("Saldo insuficiente");
    }
  }

  private void validarLimiteDiario(TipoUsuario tipo, BigDecimal totalHoje, BigDecimal valor) {

    BigDecimal limite = tipo == TipoUsuario.PF ? LIMITE_PF : LIMITE_PJ;

    BigDecimal totalComNovaRemessa = totalHoje.add(valor);

    if (totalComNovaRemessa.compareTo(limite) > 0) {
      throw new RemessaInvalidaException("Limite diário de remessa excedido");
    }
  }
}
