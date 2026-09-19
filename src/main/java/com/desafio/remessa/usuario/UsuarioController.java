package com.desafio.remessa.usuario;

import com.desafio.remessa.exception.UsuarioNaoEncontradoException;
import com.desafio.remessa.usuario.domain.Usuario;
import com.desafio.remessa.usuario.dto.AtualizarUsuarioRequest;
import com.desafio.remessa.usuario.dto.CriarUsuarioRequest;
import com.desafio.remessa.usuario.dto.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuários", description = "Operações relacionadas aos usuários")
public class UsuarioController {

  private final UsuarioService usuarioService;

  @PostMapping
  @Operation(summary = "Cadastra um novo usuário")
  public ResponseEntity<Void> inserir(@Valid @RequestBody CriarUsuarioRequest requisicao) {
    log.info("Solicitação recebida para criar usuário: tipo={}", requisicao.getTipo());
    Long id = usuarioService.inserir(requisicao);
    log.info("Usuário criado com sucesso: id={}", id);
    return ResponseEntity.created(URI.create("/usuarios/" + id)).build();
  }

  @GetMapping
  @Operation(summary = "Lista todos os usuários")
  public ResponseEntity<List<UsuarioResponse>> buscarTodos() {
    var usuarios = usuarioService.buscarTodos().stream().map(UsuarioResponse::from).toList();
    log.debug("Consulta de usuários concluída: quantidade={}", usuarios.size());
    return ResponseEntity.ok(usuarios);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Busca um usuário pelo ID")
  public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {
    return ResponseEntity.ok(
        UsuarioResponse.from(obterUsuario(usuarioService.buscarPorId(id), "ID")));
  }

  @GetMapping("/email/{email}")
  @Operation(summary = "Busca um usuário pelo e-mail")
  public ResponseEntity<UsuarioResponse> buscarPorEmail(@PathVariable String email) {
    return ResponseEntity.ok(
        UsuarioResponse.from(obterUsuario(usuarioService.buscarPorEmail(email), "e-mail")));
  }

  @GetMapping("/cpf/{cpf}")
  @Operation(summary = "Busca um usuário pelo CPF")
  public ResponseEntity<UsuarioResponse> buscarPorCpf(@PathVariable String cpf) {
    return ResponseEntity.ok(
        UsuarioResponse.from(obterUsuario(usuarioService.buscarPorCpf(cpf), "CPF")));
  }

  @GetMapping("/cnpj/{cnpj}")
  @Operation(summary = "Busca um usuário pelo CNPJ")
  public ResponseEntity<UsuarioResponse> buscarPorCnpj(@PathVariable String cnpj) {
    return ResponseEntity.ok(
        UsuarioResponse.from(obterUsuario(usuarioService.buscarPorCnpj(cnpj), "CNPJ")));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Atualiza um usuário")
  public ResponseEntity<Void> atualizar(
      @PathVariable Long id, @Valid @RequestBody AtualizarUsuarioRequest requisicao) {

    if (!usuarioService.atualizar(id, requisicao)) {
      throw new UsuarioNaoEncontradoException("Usuário não encontrado");
    }

    log.info("Usuário atualizado com sucesso: id={}", id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Exclui um usuário")
  public ResponseEntity<Void> excluir(@PathVariable Long id) {
    if (!usuarioService.excluir(id)) {
      throw new UsuarioNaoEncontradoException("Usuário não encontrado");
    }

    log.info("Usuário excluído com sucesso: id={}", id);
    return ResponseEntity.noContent().build();
  }

  private Usuario obterUsuario(Optional<Usuario> usuario, String criterioBusca) {
    if (usuario.isEmpty()) {
      log.debug("Usuário não encontrado para busca por {}", criterioBusca);
      throw new UsuarioNaoEncontradoException("Usuário não encontrado");
    }

    log.debug("Usuário encontrado em busca por {}: id={}", criterioBusca, usuario.get().getId());
    return usuario.get();
  }
}
