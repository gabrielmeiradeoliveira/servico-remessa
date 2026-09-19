package com.desafio.remessa.usuario.dto;

import com.desafio.remessa.usuario.domain.TipoUsuario;

public record AtualizarUsuarioRequest(
        String nome,
        String email,
        String senha,
        TipoUsuario tipo,
        String cpf,
        String cnpj
) {
}