package com.desafio.remessa.usuario;

import com.desafio.remessa.usuario.domain.TipoUsuario;
import com.desafio.remessa.usuario.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

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

    private Usuario usuarioDeTeste() {
        return Usuario.builder().id(1L).tipo(TipoUsuario.PF).build();
    }
}
