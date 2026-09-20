package com.desafio.remessa.carteira;

import com.desafio.remessa.carteira.dto.CarteiraResponse;
import com.desafio.remessa.carteira.dto.CreditarSaldoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carteiras")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Carteiras", description = "Operações relacionadas às carteiras dos usuários")
public class CarteiraController {

  private final CarteiraService carteiraService;

  @PostMapping("/usuario/{usuarioId}")
  @Operation(summary = "Cria uma carteira para o usuário")
  public ResponseEntity<Void> inserir(@PathVariable Long usuarioId) {

    log.info("Solicitação recebida para criar carteira: usuarioId={}", usuarioId);

    Long id = carteiraService.inserir(usuarioId);

    log.info("Carteira criada com sucesso: id={}, usuarioId={}", id, usuarioId);

    return ResponseEntity.created(URI.create("/carteiras/usuario/" + usuarioId)).build();
  }

  @GetMapping("/usuario/{usuarioId}")
  @Operation(summary = "Busca a carteira de um usuário")
  public ResponseEntity<CarteiraResponse> buscarPorUsuarioId(@PathVariable Long usuarioId) {

    var carteira = carteiraService.buscarPorUsuarioId(usuarioId);

    if (carteira.isEmpty()) {
      log.warn("Carteira não encontrada: usuarioId={}", usuarioId);

      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(CarteiraResponse.from(carteira.get()));
  }

  @PostMapping("/usuario/{usuarioId}/creditos")
  @Operation(summary = "Credita saldo em reais na carteira de um usuário")
  public ResponseEntity<Void> creditarSaldoReal(
      @PathVariable Long usuarioId, @Valid @RequestBody CreditarSaldoRequest requisicao) {

    carteiraService.creditarSaldoReal(usuarioId, requisicao.valorReal());
    return ResponseEntity.noContent().build();
  }
}
