package com.biblioteca.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LibroTest {

    @Test
    void prestarLibroValido_debeQuedarPrestado() {
        Libro libro = new Libro("El Aleph", "Jorge Luis Borges");

        String resultado = libro.prestar();

        assertEquals("Libro prestado correctamente", resultado);
        assertTrue(libro.isPrestado());
    }

    @Test
    void prestarConTituloInvalido_debeDevolverMensajeDeError() {
        Libro libro = new Libro("   ", "Jorge Luis Borges");

        String resultado = libro.prestar();

        assertEquals("No se puede prestar", resultado);
        assertFalse(libro.isPrestado());
    }

    @Test
    void prestarConAutorInvalido_debeDevolverMensajeDeError() {
        Libro libro = new Libro("El Aleph", null);

        String resultado = libro.prestar();

        assertEquals("No se puede prestar", resultado);
        assertFalse(libro.isPrestado());
    }

    @Test
    void prestarLibroYaPrestado_debeDevolverMensajeDeYaPrestado() {
        Libro libro = new Libro("El Aleph", "Jorge Luis Borges");
        libro.setPrestado(true);

        String resultado = libro.prestar();

        assertEquals("El libro ya está prestado", resultado);
    }
}