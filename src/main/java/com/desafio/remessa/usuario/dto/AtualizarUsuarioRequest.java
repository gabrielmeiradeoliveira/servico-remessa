package com.desafio.remessa.usuario.dto;

import com.desafio.remessa.usuario.domain.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AtualizarUsuarioRequest(
    @NotBlank(message = "Nome é obrigatório") String nome,
    @NotBlank(message = "E-mail é obrigatório") @Email(message = "E-mail inválido") String email,
    @Size(min = 6, message = "Senha deve possuir no mínimo 6 caracteres") String senha,
    @NotNull(message = "Tipo do usuário é obrigatório") TipoUsuario tipo,
    String cpf,
    String cnpj) {}
