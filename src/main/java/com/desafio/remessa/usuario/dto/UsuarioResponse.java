package com.desafio.remessa.usuario.dto;

import com.desafio.remessa.usuario.domain.TipoUsuario;
import com.desafio.remessa.usuario.domain.Usuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        TipoUsuario tipo,
        String cpf,
        String cnpj
) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipo(),
                usuario.getCpf(),
                usuario.getCnpj()
        );
    }
}