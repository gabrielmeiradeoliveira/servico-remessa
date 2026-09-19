package com.desafio.remessa.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioInvalidoException.class)
    public ResponseEntity<Map<String, String>> tratarUsuarioInvalido(
            UsuarioInvalidoException exception) {

        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "mensagem",
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(UsuarioJaExisteException.class)
    public ResponseEntity<Map<String, String>> tratarUsuarioJaExiste(
            UsuarioJaExisteException exception) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "mensagem",
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> tratarUsuarioNaoEncontrado(
            UsuarioNaoEncontradoException exception) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "mensagem",
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> tratarValidacao(
            MethodArgumentNotValidException exception) {

        var erro = exception.getBindingResult()
                .getFieldErrors()
                .getFirst();

        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "campo",
                        erro.getField(),
                        "mensagem",
                        erro.getDefaultMessage()
                ));
    }

    @ExceptionHandler(CarteiraJaExisteException.class)
    public ResponseEntity<Map<String, String>> tratarCarteiraJaExiste(
            CarteiraJaExisteException exception) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "mensagem",
                        exception.getMessage()
                ));
    }
}
