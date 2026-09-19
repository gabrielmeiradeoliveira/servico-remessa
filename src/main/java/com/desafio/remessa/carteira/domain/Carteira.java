package com.desafio.remessa.carteira.domain;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Carteira {

  private Long id;
  private Long usuarioId;
  private BigDecimal saldoReal;
  private BigDecimal saldoDolar;
}
