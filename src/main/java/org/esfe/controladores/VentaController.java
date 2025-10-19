package org.esfe.controladores;

import org.esfe.dtos.venta.VentaConfirmacionPago;
import org.esfe.dtos.venta.VentaGuardar;
import org.esfe.dtos.venta.VentaSalida;
import org.esfe.servicios.interfaces.IVentaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private IVentaService ventaService;

    /**
     * Obtiene todas las ventas con paginación y orden DESC por ID.
     */
    @GetMapping
    public ResponseEntity<Page<VentaSalida>> mostrarTodasPaginadas(Pageable pageable) {
        Page<VentaSalida> ventas = ventaService.obtenerTodasPaginadas(pageable);
        if (ventas.hasContent()) {
            return ResponseEntity.ok(ventas);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Obtiene una venta por su ID.
     * Si no se encuentra, el servicio lanza RecursoNoEncontradoException (404 Global Handler).
     */
    @GetMapping("/{id}")
    public ResponseEntity<VentaSalida> mostrarPorId(@PathVariable Integer id) {
        VentaSalida venta = ventaService.obtenerPorId(id);
        return ResponseEntity.ok(venta);
    }

    /**
     * Crea una nueva venta (realizada por un cliente).
     * Los errores de validación (@Valid) o RecursoNoEncontrado (Método de Pago) 
     * son manejados por el GlobalExceptionHandler.
     */
    @PostMapping
    public ResponseEntity<VentaSalida> crear(@Valid @RequestBody VentaGuardar ventaGuardar) {
        VentaSalida nuevaVenta = ventaService.crear(ventaGuardar);
        return new ResponseEntity<>(nuevaVenta, HttpStatus.CREATED);
    }

    // BÚSQUEDAS POR ROL

    /**
     * CLIENTE: Obtiene las compras de un usuario/cliente específico.
     */
    @GetMapping("/cliente/{usuarioId}")
    public ResponseEntity<Page<VentaSalida>> mostrarVentasPorClientePaginado(
            @PathVariable Long usuarioId,
            Pageable pageable) { 

        Page<VentaSalida> ventas = ventaService.obtenerVentasPorClientePaginado(usuarioId, pageable);
        if (ventas.hasContent()) {
            return ResponseEntity.ok(ventas);
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene las ventas realizadas por una empresa específica.
     */
    @GetMapping("/empresa/{idEmpresaVendedora}")
    public ResponseEntity<Page<VentaSalida>> mostrarVentasPorEmpresaPaginado(
            @PathVariable Long idEmpresaVendedora,
            Pageable pageable) {

        Page<VentaSalida> ventas = ventaService.obtenerVentasPorEmpresaPaginado(idEmpresaVendedora, pageable);
        if (ventas.hasContent()) {
            return ResponseEntity.ok(ventas);
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene las ventas gestionadas por un broker específico.
     */
    @GetMapping("/broker/{idBroker}")
    public ResponseEntity<Page<VentaSalida>> mostrarVentasPorBrokerPaginado(
            @PathVariable Long idBroker,
            Pageable pageable) {

        Page<VentaSalida> ventas = ventaService.obtenerVentasPorBrokerPaginado(idBroker, pageable);
        if (ventas.hasContent()) {
            return ResponseEntity.ok(ventas);
        }
        return ResponseEntity.noContent().build();
    }

    // OPERACIONES DE NEGOCIO (EMPRESA)

    /**
     * Búsqueda de Venta por Correlativo (para confirmación de pago).
     * Si no se encuentra, el servicio lanza RecursoNoEncontradoException (404 Global Handler).
     */
    @GetMapping("/correlativo/{correlativo}")
    public ResponseEntity<VentaSalida> mostrarPorCorrelativo(@PathVariable String correlativo) {
        VentaSalida venta = ventaService.obtenerPorCorrelativo(correlativo);
        return ResponseEntity.ok(venta);
    }

    /**
     * Confirma o revierte el pago de una venta (Solo para Empresa).
     * Si no se encuentra, el servicio lanza RecursoNoEncontradoException (404 Global Handler).
     */
    @PutMapping("/{id}/confirmarpago")
    public ResponseEntity<VentaSalida> confirmarPagoEmpresa(
            @PathVariable Integer id,
            @Valid @RequestBody VentaConfirmacionPago confirmacion) {
        
        VentaSalida ventaEditada = ventaService.confirmarPagoEmpresa(id, confirmacion);
        return ResponseEntity.ok(ventaEditada);
    }

    /**
     * Actualiza el estado de la venta.
     */
    @PutMapping("/{id}/estado/{nuevoEstado}")
    public ResponseEntity<VentaSalida> actualizarEstadoVenta(
            @PathVariable Integer id,
            @PathVariable String nuevoEstado) {
        
        VentaSalida ventaEditada = ventaService.actualizarEstadoVenta(id, nuevoEstado);
        return ResponseEntity.ok(ventaEditada);
    }
}