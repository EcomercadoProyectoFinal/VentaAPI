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
    private ModelMapper modelMapper; // Aunque no se usa directamente en este controller, se mantiene por consistencia.

    // ✅ OPERACIONES DE LECTURA - Públicas

    /**
     * Obtiene todos los métodos de pago con paginación.
     * GET /api/metodos-pago?page=0&size=10&sort=id,desc
     */
    @GetMapping
    public ResponseEntity<Page<MetodoPagoSalida>> mostrarTodosPaginados(Pageable pageable) {
        Page<MetodoPagoSalida> metodosPago = metodoPagoService.obtenerTodosPaginados(pageable);
        if (metodosPago.hasContent()) {
            return ResponseEntity.ok(metodosPago);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Obtiene todos los métodos de pago en una lista sin paginación.
     * GET /api/metodos-pago/lista
     */
    @GetMapping("/lista")
    public ResponseEntity<List<MetodoPagoSalida>> mostrarTodos() {
        List<MetodoPagoSalida> metodosPago = metodoPagoService.obtenerTodos();
        if (!metodosPago.isEmpty()) {
            return ResponseEntity.ok(metodosPago);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Obtiene un método de pago por su ID.
     * GET /api/metodos-pago/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<MetodoPagoSalida> mostrarPorId(@PathVariable Integer id) {
        try {
            MetodoPagoSalida metodoPago = metodoPagoService.obtenerPorId(id);
            return ResponseEntity.ok(metodoPago);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ---

    // ✅ OPERACIONES DE ESCRITURA - Privadas (requieren autenticación y autorización)

    /**
     * Crea un nuevo método de pago.
     * POST /api/metodos-pago
     */
    @PostMapping
    public ResponseEntity<MetodoPagoSalida> crear(@Valid @RequestBody MetodoPagoGuardar metodoPagoGuardar) {
        MetodoPagoSalida nuevoMetodoPago = metodoPagoService.crear(metodoPagoGuardar);
        // Retorna 201 Created
        return new ResponseEntity<>(nuevoMetodoPago, HttpStatus.CREATED);
    }

    /**
     * Edita un método de pago existente.
     * PUT /api/metodos-pago
     */
    /**
     * Edita un método de pago existente.
     * PUT /api/metodos-pago/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<MetodoPagoSalida> editar(
            @PathVariable Integer id,
            @Valid @RequestBody MetodoPagoModificar metodoPagoModificar) {

        try {
            // Establecer el ID del path en el DTO para el servicio
            metodoPagoModificar.setId(id);

            MetodoPagoSalida metodoPagoEditado = metodoPagoService.editar(metodoPagoModificar);
            return ResponseEntity.ok(metodoPagoEditado);
        } catch (RuntimeException e) {
            // Se lanza una RuntimeException si el ID no existe en el servicio.
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina un método de pago por su ID.
     * DELETE /api/metodos-pago/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPorId(@PathVariable Integer id) {
        try {
            metodoPagoService.eliminarPorId(id);
            // Retorna 204 No Content
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            // Se lanza una RuntimeException si el ID no existe en el servicio.
            return ResponseEntity.notFound().build();
        }
    }

    // ---

    // ✅ OPERACIONES DE BÚSQUEDA - Públicas

    /**
     * Busca métodos de pago por nombre o descripción.
     * GET /api/metodos-pago/buscar?nombre=Efectivo&descripcion=Pago%20en%20mano
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<MetodoPagoSalida>> buscarMetodosPago(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String descripcion) {

        // Llamamos al método de servicio que implementa la lógica de búsqueda
        List<MetodoPago> metodosPago = metodoPagoService.findByMetodoPagoContainingIgnoreCaseOrDescripcionContainingIgnoreCaseOrderByIdDesc(
                nombre,
                descripcion
        );

        // Convertir la lista de entidades a DTOs de salida
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