package com.desafio.remessa.cotacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desafio.remessa.cotacao.dto.CotacaoResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CotacaoServiceTest {

  @Mock private BancoCentralClient bancoCentralClient;

  @org.mockito.InjectMocks private CotacaoService cotacaoService;

  @Test
  void deveRetornarCotacaoDoDia() {

    LocalDate data = LocalDate.of(2026, 9, 18);

    var item =
        new CotacaoResponse.CotacaoItem(
            new BigDecimal("5.25"), new BigDecimal("5.26"), "2026-09-18 13:10:00.000");

    var response = new CotacaoResponse(List.of(item));

    when(bancoCentralClient.buscarCotacao(data)).thenReturn(response);

    BigDecimal resultado = cotacaoService.buscarCotacaoCompra(data);

    assertEquals(new BigDecimal("5.25"), resultado);

    verify(bancoCentralClient).buscarCotacao(data);
  }

  @Test
  void deveBuscarCotacaoDoDiaAnteriorQuandoNaoExistirCotacao() {

    LocalDate sabado = LocalDate.of(2026, 9, 19);
    LocalDate sexta = LocalDate.of(2026, 9, 18);

    var respostaVazia = new CotacaoResponse(List.of());

    var itemSexta =
        new CotacaoResponse.CotacaoItem(
            new BigDecimal("5.25"), new BigDecimal("5.26"), "2026-09-18 13:10:00.000");

    var respostaSexta = new CotacaoResponse(List.of(itemSexta));

    when(bancoCentralClient.buscarCotacao(sabado)).thenReturn(respostaVazia);

    when(bancoCentralClient.buscarCotacao(sexta)).thenReturn(respostaSexta);

    BigDecimal resultado = cotacaoService.buscarCotacaoCompra(sabado);

    assertEquals(new BigDecimal("5.25"), resultado);

    verify(bancoCentralClient).buscarCotacao(sabado);
    verify(bancoCentralClient).buscarCotacao(sexta);
  }

  @Test
  void deveBuscarUltimaCotacaoDisponivelNoDomingo() {

    LocalDate domingo = LocalDate.of(2026, 9, 20);
    LocalDate sabado = LocalDate.of(2026, 9, 19);
    LocalDate sexta = LocalDate.of(2026, 9, 18);

    var respostaVazia = new CotacaoResponse(List.of());

    var itemSexta =
        new CotacaoResponse.CotacaoItem(
            new BigDecimal("5.25"), new BigDecimal("5.26"), "2026-09-18 13:10:00.000");

    var respostaSexta = new CotacaoResponse(List.of(itemSexta));

    when(bancoCentralClient.buscarCotacao(domingo)).thenReturn(respostaVazia);

    when(bancoCentralClient.buscarCotacao(sabado)).thenReturn(respostaVazia);

    when(bancoCentralClient.buscarCotacao(sexta)).thenReturn(respostaSexta);

    BigDecimal resultado = cotacaoService.buscarCotacaoCompra(domingo);

    assertEquals(new BigDecimal("5.25"), resultado);

    verify(bancoCentralClient).buscarCotacao(domingo);
    verify(bancoCentralClient).buscarCotacao(sabado);
    verify(bancoCentralClient).buscarCotacao(sexta);
  }

  @Test
  void deveLancarErroQuandoNaoEncontrarCotacao() {

    LocalDate data = LocalDate.of(2026, 9, 19);

    when(bancoCentralClient.buscarCotacao(any())).thenReturn(new CotacaoResponse(List.of()));

    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalStateException.class, () -> cotacaoService.buscarCotacaoCompra(data));

    verify(bancoCentralClient, times(7)).buscarCotacao(any());
  }

  @Test
  void deveBuscarDiaAnteriorQuandoClienteRetornarRespostaNula() {
    LocalDate data = LocalDate.of(2026, 9, 19);
    LocalDate diaAnterior = data.minusDays(1);
    var respostaAnterior =
        new CotacaoResponse(
            List.of(
                new CotacaoResponse.CotacaoItem(
                    new BigDecimal("5.25"), new BigDecimal("5.26"), "2026-09-18 13:10:00.000")));
    when(bancoCentralClient.buscarCotacao(data)).thenReturn(null);
    when(bancoCentralClient.buscarCotacao(diaAnterior)).thenReturn(respostaAnterior);

    BigDecimal resultado = cotacaoService.buscarCotacaoCompra(data);

    assertEquals(new BigDecimal("5.25"), resultado);
    verify(bancoCentralClient).buscarCotacao(data);
    verify(bancoCentralClient).buscarCotacao(diaAnterior);
  }
}
