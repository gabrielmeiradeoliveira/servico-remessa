package com.desafio.remessa.transferencia;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.desafio.remessa.carteira.CarteiraRepository;
import com.desafio.remessa.carteira.domain.Carteira;
import com.desafio.remessa.transferencia.domain.Remessa;
import com.desafio.remessa.transferencia.domain.StatusRemessa;
import com.desafio.remessa.usuario.UsuarioRepository;
import com.desafio.remessa.usuario.domain.TipoUsuario;
import com.desafio.remessa.usuario.domain.Usuario;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RemessaRepositoryIT {

  @Autowired private RemessaRepository remessaRepository;

  @Autowired private CarteiraRepository carteiraRepository;

  @Autowired private UsuarioRepository usuarioRepository;

  @Autowired private Jdbi jdbi;

  @BeforeEach
  void limparBanco() {
    jdbi.useHandle(
        handle -> {
          handle.execute("DELETE FROM remessa");
          handle.execute("DELETE FROM carteira");
          handle.execute("DELETE FROM usuario");
        });
  }

  @AfterEach
  void limparBancoAposTeste() {
    limparBanco();
  }

  @Test
  void deveRealizarTransferenciaEAtualizarSaldos() {
    Long remetenteId = criarUsuario("ana.silva@example.com", "12345678901");
    Long destinatarioId = criarUsuario("bruno.silva@example.com", "98765432100");
    criarCarteira(remetenteId, new BigDecimal("100.00"), BigDecimal.ZERO);
    criarCarteira(destinatarioId, BigDecimal.ZERO, BigDecimal.ZERO);

    Long remessaId = remessaRepository.realizarTransferencia(remessa(remetenteId, destinatarioId));

    var carteiraRemetente = carteiraRepository.buscarPorUsuarioId(remetenteId).orElseThrow();
    var carteiraDestinatario = carteiraRepository.buscarPorUsuarioId(destinatarioId).orElseThrow();
    assertEquals(1L, remessaId);
    assertEquals(new BigDecimal("80.00"), carteiraRemetente.getSaldoReal());
    assertEquals(new BigDecimal("4.00"), carteiraDestinatario.getSaldoDolar());
    BigDecimal totalEnviado =
        remessaRepository.buscarTotalEnviadoNoDia(remetenteId, LocalDate.now());
    assertEquals(0, new BigDecimal("20.00").compareTo(totalEnviado));
  }

  private Long criarUsuario(String email, String cpf) {
    return usuarioRepository.inserir(
        Usuario.builder()
            .nome("Usuário de teste")
            .email(email)
            .senha("hash-de-teste")
            .tipo(TipoUsuario.PF)
            .cpf(cpf)
            .build());
  }

  private void criarCarteira(Long usuarioId, BigDecimal saldoReal, BigDecimal saldoDolar) {
    carteiraRepository.inserir(
        Carteira.builder()
            .usuarioId(usuarioId)
            .saldoReal(saldoReal)
            .saldoDolar(saldoDolar)
            .build());
  }

  private Remessa remessa(Long remetenteId, Long destinatarioId) {
    return Remessa.builder()
        .remetenteId(remetenteId)
        .destinatarioId(destinatarioId)
        .valorReal(new BigDecimal("20.00"))
        .valorDolar(new BigDecimal("4.00"))
        .cotacao(new BigDecimal("5.00"))
        .dataHora(LocalDateTime.now())
        .status(StatusRemessa.CONCLUIDA)
        .build();
  }
}
