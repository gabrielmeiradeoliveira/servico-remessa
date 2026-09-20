package com.desafio.remessa.transferencia.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Remessa {

  private Long id;
  private Long remetenteId;
  private Long destinatarioId;
  private BigDecimal valorReal;
  private BigDecimal valorDolar;
  private BigDecimal cotacao;
  private LocalDateTime dataHora;
  private StatusRemessa status;
}
