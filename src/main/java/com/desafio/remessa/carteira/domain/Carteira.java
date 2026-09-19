package com.desafio.remessa.carteira.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

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
