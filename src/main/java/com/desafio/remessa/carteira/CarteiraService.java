package com.desafio.remessa.carteira;

import com.desafio.remessa.carteira.domain.Carteira;
import com.desafio.remessa.exception.CarteiraJaExisteException;
import com.desafio.remessa.exception.CarteiraNaoEncontradaException;
import com.desafio.remessa.exception.UsuarioNaoEncontradoException;
import com.desafio.remessa.usuario.UsuarioRepository;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarteiraService {

  private final CarteiraRepository carteiraRepository;
  private final UsuarioRepository usuarioRepository;

  public Optional<Carteira> buscarPorUsuarioId(Long usuarioId) {
    log.debug("Buscando carteira do usuário: usuarioId={}", usuarioId);

    return carteiraRepository.buscarPorUsuarioId(usuarioId);
  }

  public Long inserir(Long usuarioId) {
    log.info("Criando carteira para usuário: usuarioId={}", usuarioId);

    validarUsuarioExistente(usuarioId);
    validarCarteiraExistente(usuarioId);

    var carteira =
        Carteira.builder()
            .usuarioId(usuarioId)
            .saldoReal(BigDecimal.ZERO)
            .saldoDolar(BigDecimal.ZERO)
            .build();

    Long id = carteiraRepository.inserir(carteira);

    log.info("Carteira criada com sucesso: id={}, usuarioId={}", id, usuarioId);

    return id;
  }

  public boolean atualizarSaldos(Carteira carteira) {
    log.debug("Atualizando saldos da carteira: usuarioId={}", carteira.getUsuarioId());

    return carteiraRepository.atualizarSaldos(carteira);
  }

  public void creditarSaldoReal(Long usuarioId, BigDecimal valor) {
    validarUsuarioExistente(usuarioId);

    if (!carteiraRepository.creditarSaldoReal(usuarioId, valor)) {
      throw new CarteiraNaoEncontradaException("Carteira não encontrada");
    }

    log.info("Saldo em reais creditado: usuarioId={}, valor={}", usuarioId, valor);
  }

  public void excluirPorUsuarioId(Long usuarioId) {
    carteiraRepository.excluirPorUsuarioId(usuarioId);
  }

  private void validarUsuarioExistente(Long usuarioId) {
    if (usuarioRepository.buscarPorId(usuarioId).isEmpty()) {
      throw new UsuarioNaoEncontradoException("Usuário não encontrado");
    }
  }

  private void validarCarteiraExistente(Long usuarioId) {
    if (carteiraRepository.buscarPorUsuarioId(usuarioId).isPresent()) {
      throw new CarteiraJaExisteException("Usuário já possui uma carteira");
    }
  }
}
