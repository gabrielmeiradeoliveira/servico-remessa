package com.desafio.remessa.cotacao;

import com.desafio.remessa.cotacao.dto.CotacaoResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CotacaoService {

  private static final int MAX_DIAS_BUSCA = 7;

  private final BancoCentralClient bancoCentralClient;

  public BigDecimal buscarCotacaoCompra(LocalDate data) {

    LocalDate dataConsulta = data;

    for (int tentativa = 0; tentativa < MAX_DIAS_BUSCA; tentativa++) {

      CotacaoResponse response = bancoCentralClient.buscarCotacao(dataConsulta);

      if (possuiCotacao(response)) {

        BigDecimal cotacaoCompra = response.value().getLast().cotacaoCompra();

        log.info(
            "Cotação encontrada: dataSolicitada={}, dataCotacao={}, cotacaoCompra={}",
            data,
            dataConsulta,
            cotacaoCompra);

        return cotacaoCompra;
      }

      log.debug("Cotação não encontrada para {}. Buscando dia anterior.", dataConsulta);

      dataConsulta = dataConsulta.minusDays(1);
    }

    throw new IllegalStateException("Não foi possível encontrar uma cotação do dólar");
  }

  private boolean possuiCotacao(CotacaoResponse response) {
    return response != null && response.value() != null && !response.value().isEmpty();
  }
}
