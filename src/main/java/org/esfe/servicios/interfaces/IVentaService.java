package org.esfe.servicios.interfaces;

import org.esfe.dtos.venta.VentaGuardar;
import org.esfe.dtos.venta.VentaConfirmacionPago;
import org.esfe.dtos.venta.VentaSalida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IVentaService {

    /** Obtiene todas las ventas. */
    List<VentaSalida> obtenerTodas();

    /** Obtiene todas las ventas con paginación. */
    Page<VentaSalida> obtenerTodasPaginadas(Pageable pageable);

    /** Obtiene una venta por su ID. */
    VentaSalida obtenerPorId(Integer id);

    /** Crea una nueva venta. */
    VentaSalida crear(VentaGuardar ventaGuardar);

    // --- Operaciones de Búsqueda por Rol (Para Vistas) ---

    /** Obtiene el historial de compras de un cliente específico. */
    List<VentaSalida> obtenerVentasPorCliente(Long usuarioId);

    /** Obtiene todas las ventas realizadas por una empresa específica. */
    List<VentaSalida> obtenerVentasPorEmpresa(Long idEmpresaVendedora);

    /** Obtiene las ventas gestionadas por un broker específico. */
    List<VentaSalida> obtenerVentasPorBroker(Long idBroker);

    /**
     * Busca una venta por su correlativo (número de factura).
     */
    VentaSalida obtenerPorCorrelativo(String correlativo);

    /**
     * Marca o desmarca la confirmación de pago por parte de la empresa.
     * @param id Venta ID.
     * @param confirmacion DTO que contiene el estado de confirmación.
     */
    VentaSalida confirmarPagoEmpresa(Integer id, VentaConfirmacionPago confirmacion);

    /**
     * Actualiza el estado de la venta a CANCELADA, ENVIADA, etc.
     * @param id Venta ID.
     * @param nuevoEstado El nuevo estado (e.g., "CANCELADA").
     */
    VentaSalida actualizarEstadoVenta(Integer id, String nuevoEstado);

}