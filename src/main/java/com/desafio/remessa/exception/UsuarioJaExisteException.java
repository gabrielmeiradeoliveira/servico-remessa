package com.desafio.remessa.exception;

public class UsuarioJaExisteException extends RuntimeException {

  public UsuarioJaExisteException(String mensagem) {
    super(mensagem);
  }
}
