package com.biblioteca.exception;

public class LibroInvalidoException extends RuntimeException {
    public LibroInvalidoException(String mensaje) {
        super(mensaje);
    }
}