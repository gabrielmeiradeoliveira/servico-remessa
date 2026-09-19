package com.desafio.remessa.usuario;

import com.desafio.remessa.exception.UsuarioInvalidoException;
import com.desafio.remessa.exception.UsuarioJaExisteException;
import com.desafio.remessa.usuario.domain.TipoUsuario;
import com.desafio.remessa.usuario.domain.Usuario;
import com.desafio.remessa.usuario.dto.AtualizarUsuarioRequest;
import com.desafio.remessa.usuario.dto.CriarUsuarioRequest;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

  private final UsuarioRepository usuarioRepository;
  private final PasswordEncoder passwordEncoder;

  public Optional<Usuario> buscarPorId(Long id) {
    log.debug("Buscando usuário por id={}", id);
    return usuarioRepository.buscarPorId(id);
  }

  public List<Usuario> buscarTodos() {
    log.debug("Buscando todos os usuários");
    return usuarioRepository.buscarTodos();
  }

  public Optional<Usuario> buscarPorEmail(String email) {
    return usuarioRepository.buscarPorEmail(email);
  }

  public Optional<Usuario> buscarPorCpf(String cpf) {
    return usuarioRepository.buscarPorCpf(cpf);
  }

  public Optional<Usuario> buscarPorCnpj(String cnpj) {
    return usuarioRepository.buscarPorCnpj(cnpj);
  }

  public Long inserir(CriarUsuarioRequest requisicao) {
    log.info("Criando usuário: tipo={}", requisicao.getTipo());

    validarTipoEDocumento(requisicao.getTipo(), requisicao.getCpf(), requisicao.getCnpj());

    validarDuplicidadeCadastro(requisicao.getEmail(), requisicao.getCpf(), requisicao.getCnpj());

    String senhaHash = passwordEncoder.encode(requisicao.getSenha());

    var usuario =
        Usuario.builder()
            .nome(requisicao.getNome())
            .email(requisicao.getEmail())
            .senha(senhaHash)
            .tipo(requisicao.getTipo())
            .cpf(requisicao.getCpf())
            .cnpj(requisicao.getCnpj())
            .build();

    Long id = usuarioRepository.inserir(usuario);

    log.info("Usuário persistido: id={}", id);

    return id;
  }

  public boolean atualizar(Long id, AtualizarUsuarioRequest requisicao) {
    var usuarioExistente = usuarioRepository.buscarPorId(id);

    if (usuarioExistente.isEmpty()) {
      log.warn("Atualização ignorada; usuário não encontrado: id={}", id);
      return false;
    }

    validarTipoEDocumento(requisicao.tipo(), requisicao.cpf(), requisicao.cnpj());

    validarDuplicidadeAtualizacao(id, requisicao.email(), requisicao.cpf(), requisicao.cnpj());

    var usuarioAtual = usuarioExistente.get();

    String senhaHash = usuarioAtual.getSenha();

    if (requisicao.senha() != null && !requisicao.senha().isBlank()) {
      senhaHash = passwordEncoder.encode(requisicao.senha());
    }

    var usuario =
        Usuario.builder()
            .id(id)
            .nome(requisicao.nome())
            .email(requisicao.email())
            .senha(senhaHash)
            .tipo(requisicao.tipo())
            .cpf(requisicao.cpf())
            .cnpj(requisicao.cnpj())
            .build();

    boolean atualizado = usuarioRepository.atualizar(usuario);

    log.info("Atualização de usuário concluída: id={}, atualizado={}", id, atualizado);

    return atualizado;
  }

  public boolean excluir(Long id) {
    boolean excluido = usuarioRepository.excluir(id);

    log.info("Exclusão de usuário concluída: id={}, excluído={}", id, excluido);

    return excluido;
  }

  private void validarTipoEDocumento(TipoUsuario tipo, String cpf, String cnpj) {

    if (tipo == null) {
      throw new UsuarioInvalidoException("Tipo do usuário é obrigatório");
    }

    if (tipo == TipoUsuario.PF) {

      if (cpf == null || cpf.isBlank()) {
        throw new UsuarioInvalidoException("CPF é obrigatório para usuário PF");
      }

      if (cnpj != null && !cnpj.isBlank()) {
        throw new UsuarioInvalidoException("Usuário PF não pode possuir CNPJ");
      }
    }

    if (tipo == TipoUsuario.PJ) {

      if (cnpj == null || cnpj.isBlank()) {
        throw new UsuarioInvalidoException("CNPJ é obrigatório para usuário PJ");
      }

      if (cpf != null && !cpf.isBlank()) {
        throw new UsuarioInvalidoException("Usuário PJ não pode possuir CPF");
      }
    }
  }

  private void validarDuplicidadeCadastro(String email, String cpf, String cnpj) {

    if (usuarioRepository.buscarPorEmail(email).isPresent()) {
      throw new UsuarioJaExisteException("E-mail já cadastrado");
    }

    if (cpf != null && !cpf.isBlank() && usuarioRepository.buscarPorCpf(cpf).isPresent()) {

      throw new UsuarioJaExisteException("CPF já cadastrado");
    }

    if (cnpj != null && !cnpj.isBlank() && usuarioRepository.buscarPorCnpj(cnpj).isPresent()) {

      throw new UsuarioJaExisteException("CNPJ já cadastrado");
    }
  }

  private void validarDuplicidadeAtualizacao(Long id, String email, String cpf, String cnpj) {

    usuarioRepository
        .buscarPorEmail(email)
        .filter(usuario -> !usuario.getId().equals(id))
        .ifPresent(
            usuario -> {
              throw new UsuarioJaExisteException("E-mail já cadastrado");
            });

    if (cpf != null && !cpf.isBlank()) {
      usuarioRepository
          .buscarPorCpf(cpf)
          .filter(usuario -> !usuario.getId().equals(id))
          .ifPresent(
              usuario -> {
                throw new UsuarioJaExisteException("CPF já cadastrado");
              });
    }

    if (cnpj != null && !cnpj.isBlank()) {
      usuarioRepository
          .buscarPorCnpj(cnpj)
          .filter(usuario -> !usuario.getId().equals(id))
          .ifPresent(
              usuario -> {
                throw new UsuarioJaExisteException("CNPJ já cadastrado");
              });
    }
  }
}
