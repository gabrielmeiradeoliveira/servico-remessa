package com.desafio.remessa.transferencia;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.desafio.remessa.exception.GlobalExceptionHandler;
import com.desafio.remessa.exception.RemessaInvalidaException;
import com.desafio.remessa.transferencia.domain.Remessa;
import com.desafio.remessa.transferencia.domain.StatusRemessa;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class RemessaControllerTest {

  @Mock private RemessaService remessaService;

  private MockMvc mockMvc;

  @BeforeEach
  void configurarMockMvc() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(new RemessaController(remessaService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void deveCriarRemessa() throws Exception {
    when(remessaService.criar(any())).thenReturn(remessaDeTeste());

    mockMvc
        .perform(
            post("/transferencias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requisicaoJson()))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/transferencias/10"))
        .andExpect(jsonPath("$.id").value(10L))
        .andExpect(jsonPath("$.valorDolar").value(20.00))
        .andExpect(jsonPath("$.status").value("CONCLUIDA"));
  }

  @Test
  void deveRetornarBadRequestParaRemessaInvalida() throws Exception {
    when(remessaService.criar(any())).thenThrow(new RemessaInvalidaException("Saldo insuficiente"));

    mockMvc
        .perform(
            post("/transferencias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requisicaoJson()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.mensagem").value("Saldo insuficiente"));
  }

  private String requisicaoJson() {
    return """
        {
          "remetenteId": 1,
          "destinatarioId": 2,
          "valorReal": 100.00
        }
        """;
  }

  private Remessa remessaDeTeste() {
    return Remessa.builder()
        .id(10L)
        .remetenteId(1L)
        .destinatarioId(2L)
        .valorReal(new BigDecimal("100.00"))
        .valorDolar(new BigDecimal("20.00"))
        .cotacao(new BigDecimal("5.00"))
        .dataHora(LocalDateTime.of(2026, 9, 19, 12, 0))
        .status(StatusRemessa.CONCLUIDA)
        .build();
  }
}
