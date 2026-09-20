package com.desafio.remessa.carteira;

import com.desafio.remessa.carteira.domain.Carteira;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CarteiraRepository {

  private final Jdbi jdbi;

  public Optional<Carteira> buscarPorUsuarioId(Long usuarioId) {
    log.debug("Buscando carteira por usuarioId={}", usuarioId);

    return jdbi.withHandle(
        handle ->
            handle
                .createQuery(
                    """
                SELECT
                    id,
                    usuario_id AS usuarioId,
                    saldo_real AS saldoReal,
                    saldo_dolar AS saldoDolar
                FROM carteira
                WHERE usuario_id = :usuarioId
                """)
                .bind("usuarioId", usuarioId)
                .mapToBean(Carteira.class)
                .findOne());
  }

  public Long inserir(Carteira carteira) {
    log.debug("Inserindo carteira para usuarioId={}", carteira.getUsuarioId());

    return jdbi.withHandle(
        handle ->
            handle
                .createUpdate(
                    """
                INSERT INTO carteira (
                    usuario_id,
                    saldo_real,
                    saldo_dolar
                )
                VALUES (
                    :usuarioId,
                    :saldoReal,
                    :saldoDolar
                )
                """)
                .bind("usuarioId", carteira.getUsuarioId())
                .bind("saldoReal", carteira.getSaldoReal())
                .bind("saldoDolar", carteira.getSaldoDolar())
                .executeAndReturnGeneratedKeys("id")
                .mapTo(Long.class)
                .one());
  }

  public boolean atualizarSaldos(Carteira carteira) {
    log.debug("Atualizando saldos da carteira do usuarioId={}", carteira.getUsuarioId());

    int linhasAfetadas =
        jdbi.withHandle(
            handle ->
                handle
                    .createUpdate(
                        """
                UPDATE carteira
                SET
                    saldo_real = :saldoReal,
                    saldo_dolar = :saldoDolar
                WHERE usuario_id = :usuarioId
                """)
                    .bind("saldoReal", carteira.getSaldoReal())
                    .bind("saldoDolar", carteira.getSaldoDolar())
                    .bind("usuarioId", carteira.getUsuarioId())
                    .execute());

    return linhasAfetadas > 0;
  }

  public boolean creditarSaldoReal(Long usuarioId, BigDecimal valor) {
    log.debug("Creditando saldo em reais: usuarioId={}, valor={}", usuarioId, valor);

    int linhasAfetadas =
        jdbi.withHandle(
            handle ->
                handle
                    .createUpdate(
                        """
                        UPDATE carteira
                        SET saldo_real = saldo_real + :valor
                        WHERE usuario_id = :usuarioId
                        """)
                    .bind("usuarioId", usuarioId)
                    .bind("valor", valor)
                    .execute());

    return linhasAfetadas > 0;
  }

  public boolean excluirPorUsuarioId(Long usuarioId) {
    log.debug("Excluindo carteira: usuarioId={}", usuarioId);

    int linhasAfetadas =
        jdbi.withHandle(
            handle ->
                handle
                    .createUpdate("DELETE FROM carteira WHERE usuario_id = :usuarioId")
                    .bind("usuarioId", usuarioId)
                    .execute());

    return linhasAfetadas > 0;
  }
}
