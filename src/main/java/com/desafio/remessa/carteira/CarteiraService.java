package com.desafio.remessa.carteira;

import com.desafio.remessa.carteira.domain.Carteira;
import com.desafio.remessa.exception.CarteiraJaExisteException;
import com.desafio.remessa.exception.UsuarioNaoEncontradoException;
import com.desafio.remessa.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarteiraService {

    private final CarteiraRepository carteiraRepository;
    private final UsuarioRepository usuarioRepository;

    public Optional<Carteira> buscarPorUsuarioId(Long usuarioId) {
        log.debug(
                "Buscando carteira do usuário: usuarioId={}",
                usuarioId
        );

        return carteiraRepository.buscarPorUsuarioId(usuarioId);
    }

    public Long inserir(Long usuarioId) {
        log.info(
                "Criando carteira para usuário: usuarioId={}",
                usuarioId
        );

        validarUsuarioExistente(usuarioId);
        validarCarteiraExistente(usuarioId);

        var carteira = Carteira.builder()
                .usuarioId(usuarioId)
                .saldoReal(BigDecimal.ZERO)
                .saldoDolar(BigDecimal.ZERO)
                .build();

        Long id = carteiraRepository.inserir(carteira);

        log.info(
                "Carteira criada com sucesso: id={}, usuarioId={}",
                id,
                usuarioId
        );

        return id;
    }

    public boolean atualizarSaldos(Carteira carteira) {
        log.debug(
                "Atualizando saldos da carteira: usuarioId={}",
                carteira.getUsuarioId()
        );

        return carteiraRepository.atualizarSaldos(carteira);
    }

    private void validarUsuarioExistente(Long usuarioId) {
        if (usuarioRepository.buscarPorId(usuarioId).isEmpty()) {
            throw new UsuarioNaoEncontradoException(
                    "Usuário não encontrado"
            );
        }
    }

    private void validarCarteiraExistente(Long usuarioId) {
        if (carteiraRepository.buscarPorUsuarioId(usuarioId).isPresent()) {
            throw new CarteiraJaExisteException(
                    "Usuário já possui uma carteira"
            );
        }
    }
}