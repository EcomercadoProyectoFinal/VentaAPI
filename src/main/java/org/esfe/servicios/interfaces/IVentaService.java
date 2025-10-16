package org.esfe.servicios.interfaces;

import org.esfe.dtos.venta.VentaGuardar;
import org.esfe.dtos.venta.VentaConfirmacionPago;
import org.esfe.dtos.venta.VentaSalida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IVentaService {

    List<VentaSalida> obtenerTodas();

    /** Obtiene todas las ventas con paginación y orden DESC por ID. */
    Page<VentaSalida> obtenerTodasPaginadas(Pageable pageable);

    /** Obtiene una venta por su ID. */
    VentaSalida obtenerPorId(Integer id);

    /** Crea una nueva venta. */
    VentaSalida crear(VentaGuardar ventaGuardar);

    /** Obtiene el historial de compras de un cliente específico con paginación. */
    Page<VentaSalida> obtenerVentasPorClientePaginado(Long usuarioId, Pageable pageable);

    /** Obtiene todas las ventas realizadas por una empresa específica con paginación. */
    Page<VentaSalida> obtenerVentasPorEmpresaPaginado(Long idEmpresaVendedora, Pageable pageable);

    /** Obtiene las ventas gestionadas por un broker específico con paginación. */
    Page<VentaSalida> obtenerVentasPorBrokerPaginado(Long idBroker, Pageable pageable);

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