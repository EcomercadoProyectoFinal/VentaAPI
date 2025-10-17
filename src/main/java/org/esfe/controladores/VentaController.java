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
     * GET /api/ventas?page=0&size=10
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
     */
    @GetMapping("/{id}")
    public ResponseEntity<VentaSalida> mostrarPorId(@PathVariable Integer id) {
        try {
            VentaSalida venta = ventaService.obtenerPorId(id);
            return ResponseEntity.ok(venta);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crea una nueva venta (realizada por un cliente).
     */
    @PostMapping
    public ResponseEntity<VentaSalida> crear(@Valid @RequestBody VentaGuardar ventaGuardar) {
        try {
            VentaSalida nuevaVenta = ventaService.crear(ventaGuardar);
            // Retorna 201 Created
            return new ResponseEntity<>(nuevaVenta, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Manejar errores de negocio (ej: MétodoPago no encontrado)
            return ResponseEntity.badRequest().build();
        }
    }

    //  BÚSQUEDAS POR ROL (Utilizando IDs de la sesión) 

    /**
     * CLIENTE: Obtiene las compras de un usuario/cliente específico.
     */
    @GetMapping("/cliente/{usuarioId}")
    public ResponseEntity<Page<VentaSalida>> mostrarVentasPorClientePaginado(
            @PathVariable Long usuarioId,
            Pageable pageable) { // Spring lo inyecta automáticamente de los Query Params
        
        Page<VentaSalida> ventas = ventaService.obtenerVentasPorClientePaginado(usuarioId, pageable);
        if (ventas.hasContent()) {
            return ResponseEntity.ok(ventas);
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene las ventas realizadas por una empresa específica.
     * GET /api/ventas/empresa/{idEmpresaVendedora}?page=0&size=10
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
     * GET /api/ventas/broker/{idBroker}?page=0&size=10
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

    //  OPERACIONES DE NEGOCIO (EMPRESA) 

    /**
     * Búsqueda de Venta por Correlativo (para confirmación de pago).
     */
    @GetMapping("/correlativo/{correlativo}")
    public ResponseEntity<VentaSalida> mostrarPorCorrelativo(@PathVariable String correlativo) {
        try {
            VentaSalida venta = ventaService.obtenerPorCorrelativo(correlativo);
            return ResponseEntity.ok(venta);
        } catch (RuntimeException e) {
            // Se lanza RecursoNoEncontradoException en el servicio
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Confirma o revierte el pago de una venta (Solo para Empresa).
     * PUT /api/ventas/{id}/confirmarpago
     */
    @PutMapping("/{id}/confirmarpago")
    public ResponseEntity<VentaSalida> confirmarPagoEmpresa(
            @PathVariable Integer id,
            @Valid @RequestBody VentaConfirmacionPago confirmacion) {
        try {
            VentaSalida ventaEditada = ventaService.confirmarPagoEmpresa(id, confirmacion);
            return ResponseEntity.ok(ventaEditada);
        } catch (RuntimeException e) {
            // Maneja el caso de Venta no encontrada u otros errores de negocio
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Actualiza el estado de la venta (e.g., "ENVIADA", "CANCELADA") (Solo para Empresa).
     */
    @PutMapping("/{id}/estado/{nuevoEstado}")
    public ResponseEntity<VentaSalida> actualizarEstadoVenta(
            @PathVariable Integer id,
            @PathVariable String nuevoEstado) {
        try {
            VentaSalida ventaEditada = ventaService.actualizarEstadoVenta(id, nuevoEstado);
            return ResponseEntity.ok(ventaEditada);
        } catch (RuntimeException e) {
            // Maneja el caso de Venta no encontrada u otros errores
            return ResponseEntity.notFound().build();
        }
    }
}