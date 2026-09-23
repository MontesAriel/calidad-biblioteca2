package com.biblioteca.steps;

import com.biblioteca.CucumberSpringConfiguration;
import com.biblioteca.model.Libro;
import com.biblioteca.model.Prestamo;
import com.biblioteca.model.Usuario;
import com.biblioteca.repository.LibroRepository;
import com.biblioteca.repository.PrestamoRepository;
import com.biblioteca.repository.UsuarioRepository;
import com.biblioteca.service.PrestamoService;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PrestamoServiceSteps extends CucumberSpringConfiguration {

    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private PrestamoRepository prestamoRepository;

    private Exception excepcionCapturada;
    private Prestamo prestamoCreado;
    private double resultadoCalculo;
    private List<Prestamo> listaPrestamos;

    private final Map<Long, Long> idsUsuarios = new HashMap<>();
    private final Map<Long, Long> idsLibros = new HashMap<>();
    private final Map<Long, Long> idsPrestamos = new HashMap<>();

    private long resolverUsuario(Long idFeature) {
        return idsUsuarios.getOrDefault(idFeature, idFeature);
    }

    private long resolverLibro(Long idFeature) {
        return idsLibros.getOrDefault(idFeature, idFeature);
    }

    private long resolverPrestamo(Long idFeature) {
        return idsPrestamos.getOrDefault(idFeature, idFeature);
    }

    @Dado("que existe un usuario activo de id {long}")
    public void crearUsuarioActivo(Long idFeature) {
        Usuario usuario = new Usuario("Usuario Activo", "activo" + idFeature + "@test.com");
        usuario.setActivo(true);
        usuario.setMoroso(false);
        Usuario guardado = usuarioRepository.save(usuario);
        idsUsuarios.put(idFeature, guardado.getId());
    }

    @Dado("que existe un usuario inactivo de id {long}")
    public void crearUsuarioInactivo(Long idFeature) {
        Usuario usuario = new Usuario("Usuario Inactivo", "inactivo" + idFeature + "@test.com");
        usuario.setActivo(false);
        usuario.setMoroso(false);
        Usuario guardado = usuarioRepository.save(usuario);
        idsUsuarios.put(idFeature, guardado.getId());
    }

    @Dado("que existe un usuario moroso de id {long}")
    public void crearUsuarioMoroso(Long idFeature) {
        Usuario usuario = new Usuario("Usuario Moroso", "moroso" + idFeature + "@test.com");
        usuario.setActivo(true);
        usuario.setMoroso(true);
        Usuario guardado = usuarioRepository.save(usuario);
        idsUsuarios.put(idFeature, guardado.getId());
    }

    @Dado("que existe un usuario activo con tres prestamos de id {long}")
    public void crearUsuarioConMaximoPrestamos(Long idFeature) {
        Usuario usuario = new Usuario("Usuario Con Maximo", "maximo" + idFeature + "@test.com");
        usuario.setActivo(true);
        usuario.setMoroso(false);
        Usuario guardado = usuarioRepository.save(usuario);
        idsUsuarios.put(idFeature, guardado.getId());

        List<Prestamo> prestamos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Prestamo p = new Prestamo();
            p.setUsuario(guardado);
            p.setFechaPrestamo(LocalDate.now());
            p.setDevuelto(false);
            prestamoRepository.save(p);
            prestamos.add(p);
        }
        guardado.setPrestamos(prestamos);
        usuarioRepository.save(guardado);
    }

    @Dado("que existe un libro disponible de id {long}")
    public void crearLibroDisponible(Long idFeature) {
        Libro libro = new Libro("Libro Disponible " + idFeature, "Autor de Prueba");
        libro.setPrestado(false);
        Libro guardado = libroRepository.save(libro);
        idsLibros.put(idFeature, guardado.getId());
    }

    @Dado("que existe un libro ya prestado de id {long}")
    public void crearLibroPrestado(Long idFeature) {
        Libro libro = new Libro("Libro Prestado " + idFeature, "Autor de Prueba");
        libro.setPrestado(true);
        Libro guardado = libroRepository.save(libro);
        idsLibros.put(idFeature, guardado.getId());
    }

    @Dado("que existe un préstamo activo de id {long}")
    public void crearPrestamoActivo(Long idFeature) {
        Libro libro = new Libro("Libro de Prestamo " + idFeature, "Autor de Prueba");
        libro.setPrestado(true);
        Libro libroGuardado = libroRepository.save(libro);

        Prestamo prestamo = new Prestamo();
        prestamo.setLibro(libroGuardado);
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setDevuelto(false);
        Prestamo guardado = prestamoRepository.save(prestamo);
        idsPrestamos.put(idFeature, guardado.getId());
    }

    @Dado("que existe un préstamo devuelto de id {long}")
    public void crearPrestamoDevuelto(Long idFeature) {
        Libro libro = new Libro("Libro Devuelto " + idFeature, "Autor de Prueba");
        libro.setPrestado(false);
        Libro libroGuardado = libroRepository.save(libro);

        Prestamo prestamo = new Prestamo();
        prestamo.setLibro(libroGuardado);
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setDevuelto(true);
        Prestamo guardado = prestamoRepository.save(prestamo);
        idsPrestamos.put(idFeature, guardado.getId());
    }

    @Cuando("intento prestar un libro con usuario id {long} y libro id {long}")
    public void intentoPrestarLibro(Long usuarioIdFeature, Long libroIdFeature) {
        try {
            prestamoService.prestarLibro(resolverUsuario(usuarioIdFeature), resolverLibro(libroIdFeature));
        } catch (Exception e) {
            excepcionCapturada = e;
        }
    }

    @Cuando("presto el libro con usuario id {long} y libro id {long}")
    public void prestarLibroExito(Long usuarioIdFeature, Long libroIdFeature) {
        prestamoCreado = prestamoService.prestarLibro(resolverUsuario(usuarioIdFeature), resolverLibro(libroIdFeature));
    }

    @Cuando("calculo el recargo para un préstamo nulo")
    public void calcularRecargoANulo() {
        resultadoCalculo = prestamoService.calcularRecargo(null);
    }

    @Cuando("calculo el recargo para un préstamo de hace {int} días")
    public void calcularRecargoAConDias(int dias) {
        Prestamo p = new Prestamo();
        p.setFechaPrestamo(LocalDate.now().minusDays(dias));
        resultadoCalculo = prestamoService.calcularRecargo(p);
    }

    @Cuando("intento devolver el préstamo con id {long}")
    public void intentoDevolverLibro(Long idFeature) {
        try {
            prestamoService.devolverLibro(resolverPrestamo(idFeature));
        } catch (Exception e) {
            excepcionCapturada = e;
        }
    }

    @Cuando("devuelvo el préstamo con id {long}")
    public void devolverLibroExito(Long idFeature) {
        prestamoService.devolverLibro(resolverPrestamo(idFeature));
    }

    @Cuando("calculo la multa para un préstamo nulo")
    public void calcularMultaNulo() {
        resultadoCalculo = prestamoService.calcularMulta(null);
    }

    @Cuando("calculo la multa no devuelto de hace {int} días")
    public void calcularMultaNoDevuelto(int dias) {
        Prestamo p = new Prestamo();
        p.setDevuelto(false);
        p.setFechaPrestamo(LocalDate.now().minusDays(dias));
        resultadoCalculo = prestamoService.calcularMulta(p);
    }

    @Cuando("calculo la multa devuelto con diferencia de {int} días")
    public void calcularMultaDevuelto(int dias) {
        Prestamo p = new Prestamo();
        p.setDevuelto(true);
        LocalDate inicio = LocalDate.now().minusDays(dias);
        p.setFechaPrestamo(inicio);
        p.setFechaDevolucion(LocalDate.now());
        resultadoCalculo = prestamoService.calcularMulta(p);
    }

    @Cuando("solicito la lista de todos los préstamos")
    public void listarPrestamos() {
        listaPrestamos = prestamoService.listarPrestamos();
    }

    @Entonces("se lanza una excepción en prestamo con mensaje {string}")
    public void verificarExcepcion(String mensajeEsperado) {
        assertNotNull(excepcionCapturada, "Se esperaba una excepción pero no ocurrió.");
        assertEquals(mensajeEsperado, excepcionCapturada.getMessage());
        excepcionCapturada = null;
    }

    @Entonces("el préstamo creado no es nulo y el libro queda prestado")
    public void verificarPrestamoCreado() {
        assertNotNull(prestamoCreado);
        assertTrue(prestamoCreado.getLibro().isPrestado());
    }

    @Entonces("el recargo devuelto es {string}")
    public void verificarRecargo(String esperadoTexto) {
        verificarValorEsperado(esperadoTexto);
    }

    @Entonces("la multa devuelta es {string}")
    public void verificarMulta(String esperadoTexto) {
        verificarValorEsperado(esperadoTexto);
    }

    private void verificarValorEsperado(String esperadoTexto) {
        double esperado = Double.parseDouble(esperadoTexto);
        assertEquals(esperado, resultadoCalculo, 0.01);
    }

    @Entonces("la devolución finaliza correctamente")
    public void verificarDevolucionExito() {
        assertNull(excepcionCapturada);
    }


    @Entonces("la lista de préstamos devuelta no es nula")
    public void verificarListaPrestamos() {
        assertNotNull(listaPrestamos);
    }
}