package com.desafio.remessa.cotacao;

import com.desafio.remessa.cotacao.dto.CotacaoResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class BancoCentralClient {

  private static final String URL_BASE =
      "https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata";

  private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("MM-dd-yyyy");

  private final RestClient restClient;

  public BancoCentralClient(RestClient.Builder builder) {
    this.restClient = builder.baseUrl(URL_BASE).build();
  }

  public CotacaoResponse buscarCotacao(LocalDate data) {

    String dataFormatada = data.format(FORMATADOR);

    log.debug("Consultando cotação do dólar no Banco Central: data={}", data);

    return restClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/CotacaoDolarDia(dataCotacao=@dataCotacao)")
                    .queryParam("@dataCotacao", "'" + dataFormatada + "'")
                    .queryParam("$format", "json")
                    .build())
        .retrieve()
        .body(CotacaoResponse.class);
  }
}
