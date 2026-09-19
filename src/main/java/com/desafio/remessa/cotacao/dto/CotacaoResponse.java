package com.desafio.remessa.cotacao.dto;

import java.math.BigDecimal;
import java.util.List;

public record CotacaoResponse(
        List<CotacaoItem> value
) {

    public record CotacaoItem(
            BigDecimal cotacaoCompra,
            BigDecimal cotacaoVenda,
            String dataHoraCotacao
    ) {
    }
}