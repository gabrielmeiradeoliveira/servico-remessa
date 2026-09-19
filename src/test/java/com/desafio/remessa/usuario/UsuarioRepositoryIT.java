package com.desafio.remessa.usuario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.desafio.remessa.usuario.domain.TipoUsuario;
import com.desafio.remessa.usuario.domain.Usuario;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UsuarioRepositoryIT {

  @Autowired private UsuarioRepository usuarioRepository;

  @Autowired private Jdbi jdbi;

  @BeforeEach
  void limparBanco() {
    jdbi.useHandle(handle -> handle.execute("DELETE FROM usuario"));
  }

  @Test
  void deveBuscarUsuarioPorEmail() {
    Long id = criarPessoaFisica();

    var usuario = usuarioRepository.buscarPorEmail("ana.silva@example.com");

    assertTrue(usuario.isPresent());
    assertEquals(id, usuario.get().getId());
  }

  @Test
  void deveBuscarUsuarioPorCpf() {
    Long id = criarPessoaFisica();

    var usuario = usuarioRepository.buscarPorCpf("12345678901");

    assertTrue(usuario.isPresent());
    assertEquals(id, usuario.get().getId());
  }

  @Test
  void deveBuscarUsuarioPorCnpj() {
    Long id =
        usuarioRepository.inserir(
            Usuario.builder()
                .nome("Empresa Exemplo")
                .email("contato@empresa.com")
                .senha("hash-de-teste")
                .tipo(TipoUsuario.PJ)
                .cnpj("12345678000190")
                .build());

    var usuario = usuarioRepository.buscarPorCnpj("12345678000190");

    assertTrue(usuario.isPresent());
    assertEquals(id, usuario.get().getId());
  }

  private Long criarPessoaFisica() {
    return usuarioRepository.inserir(
        Usuario.builder()
            .nome("Ana Silva")
            .email("ana.silva@example.com")
            .senha("hash-de-teste")
            .tipo(TipoUsuario.PF)
            .cpf("12345678901")
            .build());
  }
}
