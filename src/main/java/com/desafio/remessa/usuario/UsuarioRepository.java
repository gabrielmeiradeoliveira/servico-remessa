package com.desafio.remessa.usuario;

import com.desafio.remessa.usuario.domain.Usuario;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UsuarioRepository {

  private final Jdbi jdbi;

  public Optional<Usuario> buscarPorId(Long id) {
    log.debug("Executando consulta de usuário por id={}", id);
    return jdbi.withHandle(
        handle ->
            handle
                .createQuery(
                    """
                        SELECT
                            id,
                            nome,
                            email,
                            tipo,
                            cpf,
                            cnpj
                        FROM usuario
                        WHERE id = :id
                        """)
                .bind("id", id)
                .mapToBean(Usuario.class)
                .findOne());
  }

  public List<Usuario> buscarTodos() {
    log.debug("Executando consulta de todos os usuários");
    return jdbi.withHandle(
        handle ->
            handle
                .createQuery(
                    """
                        SELECT
                            id,
                            nome,
                            email,
                            tipo,
                            cpf,
                            cnpj
                        FROM usuario
                        """)
                .mapToBean(Usuario.class)
                .list());
  }

  public Optional<Usuario> buscarPorEmail(String email) {
    return jdbi.withHandle(
        handle ->
            handle
                .createQuery(
                    """
                        SELECT
                            id,
                            nome,
                            email,
                            tipo,
                            cpf,
                            cnpj
                        FROM usuario
                        WHERE email = :email
                        """)
                .bind("email", email)
                .mapToBean(Usuario.class)
                .findOne());
  }

  public Optional<Usuario> buscarPorCpf(String cpf) {
    return jdbi.withHandle(
        handle ->
            handle
                .createQuery(
                    """
                        SELECT
                            id,
                            nome,
                            email,
                            tipo,
                            cpf,
                            cnpj
                        FROM usuario
                        WHERE cpf = :cpf
                        """)
                .bind("cpf", cpf)
                .mapToBean(Usuario.class)
                .findOne());
  }

  public Optional<Usuario> buscarPorCnpj(String cnpj) {
    return jdbi.withHandle(
        handle ->
            handle
                .createQuery(
                    """
                        SELECT
                            id,
                            nome,
                            email,
                            tipo,
                            cpf,
                            cnpj
                        FROM usuario
                        WHERE cnpj = :cnpj
                        """)
                .bind("cnpj", cnpj)
                .mapToBean(Usuario.class)
                .findOne());
  }

  public Long inserir(Usuario usuario) {
    return jdbi.withHandle(handle -> inserir(handle, usuario));
  }

  public Long inserirComCarteira(Usuario usuario) {
    return jdbi.inTransaction(
        handle -> {
          Long usuarioId = inserir(handle, usuario);

          handle
              .createUpdate(
                  """
                  INSERT INTO carteira (usuario_id, saldo_real, saldo_dolar)
                  VALUES (:usuarioId, :saldoReal, :saldoDolar)
                  """)
              .bind("usuarioId", usuarioId)
              .bind("saldoReal", BigDecimal.ZERO)
              .bind("saldoDolar", BigDecimal.ZERO)
              .execute();

          return usuarioId;
        });
  }

  private Long inserir(Handle handle, Usuario usuario) {
    Long id =
        handle
            .createUpdate(
                """
                        INSERT INTO usuario (
                            nome,
                            email,
                            senha,
                            tipo,
                            cpf,
                            cnpj
                        )
                        VALUES (
                            :nome,
                            :email,
                            :senha,
                            :tipo,
                            :cpf,
                            :cnpj
                        )
                        """)
            .bind("nome", usuario.getNome())
            .bind("email", usuario.getEmail())
            .bind("senha", usuario.getSenha())
            .bind("tipo", usuario.getTipo().name())
            .bind("cpf", usuario.getCpf())
            .bind("cnpj", usuario.getCnpj())
            .executeAndReturnGeneratedKeys("id")
            .mapTo(Long.class)
            .one();
    log.debug("Inserção de usuário concluída: id={}", id);
    return id;
  }

  public boolean atualizar(Usuario usuario) {
    int linhasAfetadas =
        jdbi.withHandle(
            handle ->
                handle
                    .createUpdate(
                        """
                        UPDATE usuario
                        SET
                            nome = :nome,
                            email = :email,
                            senha = :senha,
                            tipo = :tipo,
                            cpf = :cpf,
                            cnpj = :cnpj
                        WHERE id = :id
                        """)
                    .bind("id", usuario.getId())
                    .bind("nome", usuario.getNome())
                    .bind("email", usuario.getEmail())
                    .bind("senha", usuario.getSenha())
                    .bind("tipo", usuario.getTipo().name())
                    .bind("cpf", usuario.getCpf())
                    .bind("cnpj", usuario.getCnpj())
                    .execute());

    boolean atualizado = linhasAfetadas > 0;
    log.debug(
        "Atualização de usuário no banco: id={}, linhasAfetadas={}",
        usuario.getId(),
        linhasAfetadas);
    return atualizado;
  }

  public boolean excluir(Long id) {
    int linhasAfetadas =
        jdbi.withHandle(
            handle ->
                handle
                    .createUpdate(
                        """
                    DELETE FROM usuario
                    WHERE id = :id
                    """)
                    .bind("id", id)
                    .execute());

    boolean excluido = linhasAfetadas > 0;
    log.debug("Exclusão de usuário no banco: id={}, linhasAfetadas={}", id, linhasAfetadas);
    return excluido;
  }
}
