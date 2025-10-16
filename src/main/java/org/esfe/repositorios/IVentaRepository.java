package org.esfe.repositorios;

import org.esfe.modelos.Venta;
import org.springframework.data.domain.Page; // Importar Page
import org.springframework.data.domain.Pageable; // Importar Pageable
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional; // Importar Optional (para búsquedas por ID o Correlativo)

public interface IVentaRepository extends JpaRepository<Venta, Integer> {
    /** Obtiene las ventas de un cliente (usuario) con paginación. */
    Page<Venta> findByUsuarioId(Long usuarioId, Pageable pageable);

    /** Obtiene las ventas realizadas por una empresa con paginación. */
    Page<Venta> findByIdEmpresaVendedora(Long idEmpresaVendedora, Pageable pageable);

    /** Obtiene las ventas gestionadas por un broker con paginación. */
    Page<Venta> findByIdBroker(Long idBroker, Pageable pageable);

    // 2. BÚSQUEDA COMBINADA PAGINADA
    /** Obtiene las ventas de una empresa filtradas por un estado y con paginación. */
    Page<Venta> findByIdEmpresaVendedoraAndEstado(Long idEmpresaVendedora, String estado, Pageable pageable);

    // 3. BÚSQUEDA POR CORRELATIVO (ÚNICO)
    /** Busca una venta por su correlativo*/
    Optional<Venta> findByCorrelativo(String correlativo);

    // 4. MÉTODOS DE ACTUALIZACIÓN
    /**
     * Confirma o revierte el estado de pago por parte de la empresa.
     * @param id El ID de la venta.
     * @param pagoConfirmadoEmpresa El estado de confirmación (true o false).
     * @return El número de registros actualizados.
     */
    @Modifying
    @Query("UPDATE Venta v SET v.pagoConfirmadoEmpresa = :pagoConfirmadoEmpresa WHERE v.id = :id")
    int actualizarConfirmacionPagoEmpresa(@Param("id") Integer id, @Param("pagoConfirmadoEmpresa") Boolean pagoConfirmadoEmpresa);

    /**
     * Cancela la venta, actualizando su estado.
     * @param id El ID de la venta.
     * @param nuevoEstado El nuevo estado (e.g., "CANCELADA").
     * @return El número de registros actualizados.
     */
    @Modifying
    @Query("UPDATE Venta v SET v.estado = :nuevoEstado WHERE v.id = :id")
    int actualizarEstadoVenta(@Param("id") Integer id, @Param("nuevoEstado") String nuevoEstado);

}