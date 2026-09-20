package com.desafio.remessa.transferencia;

import com.desafio.remessa.exception.RemessaNaoProcessadaException;
import com.desafio.remessa.transferencia.domain.Remessa;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RemessaRepository {

  private final Jdbi jdbi;

  public BigDecimal buscarTotalEnviadoNoDia(Long remetenteId, LocalDate data) {

    LocalDateTime inicio = data.atStartOfDay();
    LocalDateTime fim = data.plusDays(1).atStartOfDay();

    log.debug("Buscando total enviado no dia: remetenteId={}, data={}", remetenteId, data);

    return jdbi.withHandle(
        handle ->
            handle
                .createQuery(
                    """
                        SELECT COALESCE(SUM(valor_real), 0)
                        FROM remessa
                        WHERE remetente_id = :remetenteId
                          AND data_hora >= :inicio
                          AND data_hora < :fim
                          AND status = 'CONCLUIDA'
                        """)
                .bind("remetenteId", remetenteId)
                .bind("inicio", inicio)
                .bind("fim", fim)
                .mapTo(BigDecimal.class)
                .one());
  }

  public Long realizarTransferencia(Remessa remessa) {

    log.info(
        "Realizando transferência: remetenteId={}, destinatarioId={}, valorReal={}",
        remessa.getRemetenteId(),
        remessa.getDestinatarioId(),
        remessa.getValorReal());

    return jdbi.inTransaction(
        handle -> {
          int debitos =
              handle
                  .createUpdate(
                      """
                    UPDATE carteira
                    SET saldo_real = saldo_real - :valor
                    WHERE usuario_id = :usuarioId
                      AND saldo_real >= :valor
                    """)
                  .bind("valor", remessa.getValorReal())
                  .bind("usuarioId", remessa.getRemetenteId())
                  .execute();

          if (debitos == 0) {
            throw new RemessaNaoProcessadaException(
                "Não foi possível debitar o saldo do remetente");
          }

          int creditos =
              handle
                  .createUpdate(
                      """
                    UPDATE carteira
                    SET saldo_dolar = saldo_dolar + :valor
                    WHERE usuario_id = :usuarioId
                    """)
                  .bind("valor", remessa.getValorDolar())
                  .bind("usuarioId", remessa.getDestinatarioId())
                  .execute();

          if (creditos == 0) {
            throw new RemessaNaoProcessadaException(
                "Não foi possível creditar o saldo do destinatário");
          }

          Long id =
              handle
                  .createUpdate(
                      """
                    INSERT INTO remessa (
                        remetente_id,
                        destinatario_id,
                        valor_real,
                        valor_dolar,
                        cotacao,
                        data_hora,
                        status
                    )
                    VALUES (
                        :remetenteId,
                        :destinatarioId,
                        :valorReal,
                        :valorDolar,
                        :cotacao,
                        :dataHora,
                        :status
                    )
                    """)
                  .bind("remetenteId", remessa.getRemetenteId())
                  .bind("destinatarioId", remessa.getDestinatarioId())
                  .bind("valorReal", remessa.getValorReal())
                  .bind("valorDolar", remessa.getValorDolar())
                  .bind("cotacao", remessa.getCotacao())
                  .bind("dataHora", remessa.getDataHora())
                  .bind("status", remessa.getStatus().name())
                  .executeAndReturnGeneratedKeys("id")
                  .mapTo(Long.class)
                  .one();

          log.info(
              "Transferência concluída: remessaId={}, remetenteId={}, destinatarioId={}",
              id,
              remessa.getRemetenteId(),
              remessa.getDestinatarioId());

          return id;
        });
  }
}
