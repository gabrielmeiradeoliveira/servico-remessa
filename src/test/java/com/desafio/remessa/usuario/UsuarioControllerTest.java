package com.desafio.remessa.usuario;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.desafio.remessa.exception.GlobalExceptionHandler;
import com.desafio.remessa.usuario.domain.TipoUsuario;
import com.desafio.remessa.usuario.domain.Usuario;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

  @Mock private UsuarioService usuarioService;

  private MockMvc mockMvc;

  @BeforeEach
  void configurarMockMvc() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(new UsuarioController(usuarioService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void deveBuscarUsuarioPorEmail() throws Exception {
    when(usuarioService.buscarPorEmail("ana.silva@example.com"))
        .thenReturn(Optional.of(usuarioPessoaFisica()));

    mockMvc
        .perform(get("/usuarios/email/{email}", "ana.silva@example.com"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L));
  }

  @Test
  void deveRetornarNotFoundAoBuscarUsuarioPorCpfInexistente() throws Exception {
    when(usuarioService.buscarPorCpf("99999999999")).thenReturn(Optional.empty());

    mockMvc
        .perform(get("/usuarios/cpf/{cpf}", "99999999999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.mensagem").value("Usuário não encontrado"));
  }

  @Test
  void deveBuscarUsuarioPorCnpj() throws Exception {
    when(usuarioService.buscarPorCnpj("12345678000190"))
        .thenReturn(
            Optional.of(
                Usuario.builder().id(2L).tipo(TipoUsuario.PJ).cnpj("12345678000190").build()));

    mockMvc
        .perform(get("/usuarios/cnpj/{cnpj}", "12345678000190"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(2L));
  }

  private Usuario usuarioPessoaFisica() {
    return Usuario.builder().id(1L).tipo(TipoUsuario.PF).cpf("12345678901").build();
  }
}
