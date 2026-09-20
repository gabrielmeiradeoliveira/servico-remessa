package com.desafio.remessa.exception;

public class RemessaNaoProcessadaException extends RuntimeException {

  public RemessaNaoProcessadaException(String mensagem) {
    super(mensagem);
  }
}
