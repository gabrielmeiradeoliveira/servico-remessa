package com.desafio.remessa.transferencia.dto;

import com.desafio.remessa.transferencia.domain.Remessa;
import com.desafio.remessa.transferencia.domain.StatusRemessa;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RemessaResponse(
    Long id,
    Long remetenteId,
    Long destinatarioId,
    BigDecimal valorReal,
    BigDecimal valorDolar,
    BigDecimal cotacao,
    LocalDateTime dataHora,
    StatusRemessa status) {

  public static RemessaResponse from(Remessa remessa) {
    return new RemessaResponse(
        remessa.getId(),
        remessa.getRemetenteId(),
        remessa.getDestinatarioId(),
        remessa.getValorReal(),
        remessa.getValorDolar(),
        remessa.getCotacao(),
        remessa.getDataHora(),
        remessa.getStatus());
  }
}
