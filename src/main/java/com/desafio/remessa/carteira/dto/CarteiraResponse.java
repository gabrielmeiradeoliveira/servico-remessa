package com.desafio.remessa.carteira.dto;

import com.desafio.remessa.carteira.domain.Carteira;
import java.math.BigDecimal;

public record CarteiraResponse(
    Long id, Long usuarioId, BigDecimal saldoReal, BigDecimal saldoDolar) {

  public static CarteiraResponse from(Carteira carteira) {
    return new CarteiraResponse(
        carteira.getId(),
        carteira.getUsuarioId(),
        carteira.getSaldoReal(),
        carteira.getSaldoDolar());
  }
}
