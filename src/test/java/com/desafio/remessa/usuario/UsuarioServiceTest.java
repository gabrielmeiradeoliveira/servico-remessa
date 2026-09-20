package com.desafio.remessa.usuario;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desafio.remessa.carteira.CarteiraService;
import com.desafio.remessa.transferencia.RemessaRepository;
import com.desafio.remessa.usuario.domain.TipoUsuario;
import com.desafio.remessa.usuario.domain.Usuario;
import com.desafio.remessa.usuario.dto.CriarUsuarioRequest;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

  @Mock private UsuarioRepository usuarioRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private CarteiraService carteiraService;

  @Mock private RemessaRepository remessaRepository;

  @InjectMocks private UsuarioService usuarioService;

  @Test
  void deveBuscarUsuarioPorEmail() {
    Usuario usuario = usuarioDeTeste();
    when(usuarioRepository.buscarPorEmail("teste@example.com")).thenReturn(Optional.of(usuario));

    var resultado = usuarioService.buscarPorEmail("teste@example.com");

    assertSame(usuario, resultado.orElseThrow());
    verify(usuarioRepository).buscarPorEmail("teste@example.com");
  }

  @Test
  void deveBuscarUsuarioPorCpf() {
    Usuario usuario = usuarioDeTeste();
    when(usuarioRepository.buscarPorCpf("12345678901")).thenReturn(Optional.of(usuario));

    var resultado = usuarioService.buscarPorCpf("12345678901");

    assertSame(usuario, resultado.orElseThrow());
    verify(usuarioRepository).buscarPorCpf("12345678901");
  }

  @Test
  void deveBuscarUsuarioPorCnpj() {
    Usuario usuario = Usuario.builder().id(2L).tipo(TipoUsuario.PJ).build();
    when(usuarioRepository.buscarPorCnpj("12345678000190")).thenReturn(Optional.of(usuario));

    var resultado = usuarioService.buscarPorCnpj("12345678000190");

    assertSame(usuario, resultado.orElseThrow());
    verify(usuarioRepository).buscarPorCnpj("12345678000190");
  }

  @Test
  void deveCriarCarteiraAoCadastrarUsuario() {
    CriarUsuarioRequest requisicao =
        new CriarUsuarioRequest(
            "Ana Silva", "ana@example.com", "senha123", TipoUsuario.PF, "12345678901", null);
    when(usuarioRepository.buscarPorEmail(requisicao.email())).thenReturn(Optional.empty());
    when(usuarioRepository.buscarPorCpf(requisicao.cpf())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(requisicao.senha())).thenReturn("senha-hash");
    when(usuarioRepository.inserirComCarteira(any(Usuario.class))).thenReturn(10L);

    usuarioService.inserir(requisicao);

    verify(usuarioRepository).inserirComCarteira(any(Usuario.class));
  }

  private Usuario usuarioDeTeste() {
    return Usuario.builder().id(1L).tipo(TipoUsuario.PF).build();
  }
}
