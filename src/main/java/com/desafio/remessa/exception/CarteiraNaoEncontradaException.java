package com.desafio.remessa.exception;

public class CarteiraNaoEncontradaException extends RuntimeException {

  public CarteiraNaoEncontradaException(String mensagem) {
    super(mensagem);
  }
}
