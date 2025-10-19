package org.esfe.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import org.esfe.servicios.interfaces.IMetodoPagoService;
import org.esfe.dtos.metodopago.MetodoPagoGuardar;
import org.esfe.dtos.metodopago.MetodoPagoModificar;
import org.esfe.dtos.metodopago.MetodoPagoSalida;
import org.esfe.modelos.MetodoPago;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;

@RestController
@RequestMapping("/api/metodospago")
public class MetodoPagoController {

    @Autowired
    private IMetodoPagoService metodoPagoService;

    @Autowired
    private ModelMapper modelMapper; 

    // OPERACIONES DE LECTURA - Públicas

    /**
     * Obtiene todos los métodos de pago con paginación.
     * Si no hay contenido, devuelve 404 (manejado en el controlador).
     */
    @GetMapping
    public ResponseEntity<Page<MetodoPagoSalida>> mostrarTodosPaginados(Pageable pageable) {
        Page<MetodoPagoSalida> metodosPago = metodoPagoService.obtenerTodosPaginados(pageable);
        if (metodosPago.hasContent()) {
            return ResponseEntity.ok(metodosPago);
        }
        // Devolvemos 404 si la página está vacía, para la lista paginada.
        return ResponseEntity.notFound().build(); 
    }

    /**
     * Obtiene todos los métodos de pago en una lista sin paginación.
     */
    @GetMapping("/lista")
    public ResponseEntity<List<MetodoPagoSalida>> mostrarTodos() {
        List<MetodoPagoSalida> metodosPago = metodoPagoService.obtenerTodos();
        if (!metodosPago.isEmpty()) {
            return ResponseEntity.ok(metodosPago);
        }
        // Devolvemos 404 si la lista está vacía.
        return ResponseEntity.notFound().build(); 
    }

    /**
     * Obtiene un método de pago por su ID.
     * Si no se encuentra, el servicio lanza RecursoNoEncontradoException,
     * y el GlobalExceptionHandler devuelve el 404 con mensaje.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MetodoPagoSalida> mostrarPorId(@PathVariable Integer id) {
        MetodoPagoSalida metodoPago = metodoPagoService.obtenerPorId(id);
        return ResponseEntity.ok(metodoPago);
    }

    // OPERACIONES DE ESCRITURA - Privadas

    /**
     * Crea un nuevo método de pago.
     */
    @PostMapping
    public ResponseEntity<MetodoPagoSalida> crear(@Valid @RequestBody MetodoPagoGuardar metodoPagoGuardar) {
        MetodoPagoSalida nuevoMetodoPago = metodoPagoService.crear(metodoPagoGuardar);
        // Retorna 201 Created
        return new ResponseEntity<>(nuevoMetodoPago, HttpStatus.CREATED);
    }

    /**
     * Edita un método de pago existente.
     * Si no se encuentra el ID, el servicio lanza la excepción y el Global Handler se encarga.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MetodoPagoSalida> editar(
            @PathVariable Integer id,
            @Valid @RequestBody MetodoPagoModificar metodoPagoModificar) {

        // Establecer el ID del path en el DTO para el servicio
        metodoPagoModificar.setId(id);
        
        MetodoPagoSalida metodoPagoEditado = metodoPagoService.editar(metodoPagoModificar);
        return ResponseEntity.ok(metodoPagoEditado);
    }

    /**
     * Elimina un método de pago por su ID.
     * Si no se encuentra el ID, el servicio lanza la excepción y el Global Handler se encarga.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPorId(@PathVariable Integer id) {
        metodoPagoService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    // OPERACIONES DE BÚSQUEDA - Públicas

    /**
     * Busca métodos de pago por nombre o descripción.
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<MetodoPagoSalida>> buscarMetodosPago(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String descripcion) {

        List<MetodoPago> metodosPago = metodoPagoService.findByMetodoPagoContainingIgnoreCaseOrDescripcionContainingIgnoreCaseOrderByIdDesc(
                nombre,
                descripcion
        );

        List<MetodoPagoSalida> metodosPagoSalida = metodosPago.stream()
                .map(metodoPago -> modelMapper.map(metodoPago, MetodoPagoSalida.class))
                .collect(Collectors.toList());

        if (!metodosPagoSalida.isEmpty()) {
            return ResponseEntity.ok(metodosPagoSalida);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}