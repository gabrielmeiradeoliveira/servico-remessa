package com.desafio.remessa.transferencia;

import com.desafio.remessa.transferencia.dto.CriarRemessaRequest;
import com.desafio.remessa.transferencia.dto.RemessaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transferencias")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transferências", description = "Operações de remessa entre usuários")
public class RemessaController {

  private final RemessaService remessaService;

  @PostMapping
  @Operation(summary = "Realiza uma remessa")
  public ResponseEntity<RemessaResponse> criar(@Valid @RequestBody CriarRemessaRequest request) {

    log.info(
        "Solicitação de remessa: remetenteId={}, destinatarioId={}, valorReal={}",
        request.remetenteId(),
        request.destinatarioId(),
        request.valorReal());

    var remessa = remessaService.criar(request);

    return ResponseEntity.created(URI.create("/transferencias/" + remessa.getId()))
        .body(RemessaResponse.from(remessa));
  }
}
