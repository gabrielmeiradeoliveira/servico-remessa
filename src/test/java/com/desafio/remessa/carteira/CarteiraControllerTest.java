package com.desafio.remessa.carteira;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.desafio.remessa.carteira.domain.Carteira;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class CarteiraControllerTest {

  @Mock private CarteiraService carteiraService;

  private MockMvc mockMvc;

  @BeforeEach
  void configurarMockMvc() {
    mockMvc = MockMvcBuilders.standaloneSetup(new CarteiraController(carteiraService)).build();
  }

  @Test
  void deveCriarCarteira() throws Exception {
    when(carteiraService.inserir(1L)).thenReturn(10L);

    mockMvc
        .perform(post("/carteiras/usuario/{usuarioId}", 1L))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/carteiras/usuario/1"));

    verify(carteiraService).inserir(1L);
  }

  @Test
  void deveBuscarCarteiraPorUsuarioId() throws Exception {
    when(carteiraService.buscarPorUsuarioId(1L)).thenReturn(Optional.of(carteiraDeTeste()));

    mockMvc
        .perform(get("/carteiras/usuario/{usuarioId}", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(10L))
        .andExpect(jsonPath("$.usuarioId").value(1L))
        .andExpect(jsonPath("$.saldoReal").value(100.00))
        .andExpect(jsonPath("$.saldoDolar").value(20.00));
  }

  @Test
  void deveRetornarNotFoundQuandoCarteiraNaoExiste() throws Exception {
    when(carteiraService.buscarPorUsuarioId(999L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/carteiras/usuario/{usuarioId}", 999L)).andExpect(status().isNotFound());
  }

  private Carteira carteiraDeTeste() {
    return Carteira.builder()
        .id(10L)
        .usuarioId(1L)
        .saldoReal(new BigDecimal("100.00"))
        .saldoDolar(new BigDecimal("20.00"))
        .build();
  }
}
