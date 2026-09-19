package com.desafio.remessa.carteira;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.desafio.remessa.carteira.domain.Carteira;
import com.desafio.remessa.usuario.UsuarioRepository;
import com.desafio.remessa.usuario.domain.TipoUsuario;
import com.desafio.remessa.usuario.domain.Usuario;
import java.math.BigDecimal;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CarteiraRepositoryIT {

  @Autowired private CarteiraRepository carteiraRepository;

  @Autowired private UsuarioRepository usuarioRepository;

  @Autowired private Jdbi jdbi;

  @BeforeEach
  void limparBanco() {
    limparTabelas();
  }

  @AfterEach
  void limparBancoAposTeste() {
    limparTabelas();
  }

  private void limparTabelas() {
    jdbi.useHandle(
        handle -> {
          handle.execute("DELETE FROM carteira");
          handle.execute("DELETE FROM usuario");
        });
  }

  @Test
  void deveInserirEBuscarCarteiraPorUsuarioId() {
    Long usuarioId = criarUsuario();
    Long carteiraId =
        carteiraRepository.inserir(
            Carteira.builder()
                .usuarioId(usuarioId)
                .saldoReal(new BigDecimal("100.50"))
                .saldoDolar(new BigDecimal("20.25"))
                .build());

    var carteira = carteiraRepository.buscarPorUsuarioId(usuarioId);

    assertTrue(carteira.isPresent());
    assertEquals(carteiraId, carteira.orElseThrow().getId());
    assertEquals(new BigDecimal("100.50"), carteira.orElseThrow().getSaldoReal());
    assertEquals(new BigDecimal("20.25"), carteira.orElseThrow().getSaldoDolar());
  }

  @Test
  void deveAtualizarSaldosDaCarteira() {
    Long usuarioId = criarUsuario();
    carteiraRepository.inserir(
        Carteira.builder()
            .usuarioId(usuarioId)
            .saldoReal(BigDecimal.ZERO)
            .saldoDolar(BigDecimal.ZERO)
            .build());

    boolean atualizado =
        carteiraRepository.atualizarSaldos(
            Carteira.builder()
                .usuarioId(usuarioId)
                .saldoReal(new BigDecimal("250.75"))
                .saldoDolar(new BigDecimal("50.15"))
                .build());

    var carteira = carteiraRepository.buscarPorUsuarioId(usuarioId).orElseThrow();
    assertTrue(atualizado);
    assertEquals(new BigDecimal("250.75"), carteira.getSaldoReal());
    assertEquals(new BigDecimal("50.15"), carteira.getSaldoDolar());
  }

  @Test
  void deveRetornarFalsoAoAtualizarCarteiraInexistente() {
    boolean atualizado =
        carteiraRepository.atualizarSaldos(
            Carteira.builder()
                .usuarioId(999L)
                .saldoReal(BigDecimal.ONE)
                .saldoDolar(BigDecimal.ONE)
                .build());

    assertFalse(atualizado);
  }

  private Long criarUsuario() {
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
