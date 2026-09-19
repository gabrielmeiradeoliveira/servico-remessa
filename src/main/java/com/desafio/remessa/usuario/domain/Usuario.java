package com.desafio.remessa.usuario.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

  private Long id;
  private String nome;
  private String email;
  private String senha;
  private TipoUsuario tipo;
  private String cpf;
  private String cnpj;
}
