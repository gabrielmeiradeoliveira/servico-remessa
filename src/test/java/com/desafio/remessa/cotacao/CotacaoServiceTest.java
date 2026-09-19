package com.desafio.remessa.cotacao;

import com.desafio.remessa.cotacao.dto.CotacaoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CotacaoServiceTest {

    @Mock
    private BancoCentralClient bancoCentralClient;

    private CotacaoService cotacaoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cotacaoService = new CotacaoService(bancoCentralClient);
    }

    @Test
    void deveRetornarCotacaoDoDia() {

        LocalDate data = LocalDate.of(2026, 9, 18);

        var item = new CotacaoResponse.CotacaoItem(
                new BigDecimal("5.25"),
                new BigDecimal("5.26"),
                "2026-09-18 13:10:00.000"
        );

        var response = new CotacaoResponse(
                List.of(item)
        );

        when(bancoCentralClient.buscarCotacao(data))
                .thenReturn(response);

        BigDecimal resultado =
                cotacaoService.buscarCotacaoCompra(data);

        assertEquals(
                new BigDecimal("5.25"),
                resultado
        );

        verify(bancoCentralClient).buscarCotacao(data);
    }

    @Test
    void deveBuscarCotacaoDoDiaAnteriorQuandoNaoExistirCotacao() {

        LocalDate sabado = LocalDate.of(2026, 9, 19);
        LocalDate sexta = LocalDate.of(2026, 9, 18);

        var respostaVazia = new CotacaoResponse(
                List.of()
        );

        var itemSexta = new CotacaoResponse.CotacaoItem(
                new BigDecimal("5.25"),
                new BigDecimal("5.26"),
                "2026-09-18 13:10:00.000"
        );

        var respostaSexta = new CotacaoResponse(
                List.of(itemSexta)
        );

        when(bancoCentralClient.buscarCotacao(sabado))
                .thenReturn(respostaVazia);

        when(bancoCentralClient.buscarCotacao(sexta))
                .thenReturn(respostaSexta);

        BigDecimal resultado =
                cotacaoService.buscarCotacaoCompra(sabado);

        assertEquals(
                new BigDecimal("5.25"),
                resultado
        );

        verify(bancoCentralClient).buscarCotacao(sabado);
        verify(bancoCentralClient).buscarCotacao(sexta);
    }

    @Test
    void deveBuscarUltimaCotacaoDisponivelNoDomingo() {

        LocalDate domingo = LocalDate.of(2026, 9, 20);
        LocalDate sabado = LocalDate.of(2026, 9, 19);
        LocalDate sexta = LocalDate.of(2026, 9, 18);

        var respostaVazia = new CotacaoResponse(
                List.of()
        );

        var itemSexta = new CotacaoResponse.CotacaoItem(
                new BigDecimal("5.25"),
                new BigDecimal("5.26"),
                "2026-09-18 13:10:00.000"
        );

        var respostaSexta = new CotacaoResponse(
                List.of(itemSexta)
        );

        when(bancoCentralClient.buscarCotacao(domingo))
                .thenReturn(respostaVazia);

        when(bancoCentralClient.buscarCotacao(sabado))
                .thenReturn(respostaVazia);

        when(bancoCentralClient.buscarCotacao(sexta))
                .thenReturn(respostaSexta);

        BigDecimal resultado =
                cotacaoService.buscarCotacaoCompra(domingo);

        assertEquals(
                new BigDecimal("5.25"),
                resultado
        );

        verify(bancoCentralClient).buscarCotacao(domingo);
        verify(bancoCentralClient).buscarCotacao(sabado);
        verify(bancoCentralClient).buscarCotacao(sexta);
    }

    @Test
    void deveLancarErroQuandoNaoEncontrarCotacao() {

        LocalDate data = LocalDate.of(2026, 9, 19);

        when(bancoCentralClient.buscarCotacao(any()))
                .thenReturn(new CotacaoResponse(List.of()));

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalStateException.class,
                () -> cotacaoService.buscarCotacaoCompra(data)
        );

        verify(
                bancoCentralClient,
                times(7)
        ).buscarCotacao(any());
    }
}