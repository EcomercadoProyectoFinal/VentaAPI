package org.esfe.repositorios;

import org.esfe.modelos.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IVentaRepository extends JpaRepository<Venta, Interger> {
    List<Venta> findByUsuarioId(Long usuarioId);

    // Para que la empresa vea sus ventas
    List<Venta> findByIdEmpresaVendedora(Long idEmpresaVendedora);

    // Para que el broker vea las ventas que gestionó
    List<Venta> findByIdBroker(Long idBroker);

    Optional<Venta> findByIdEmpresaVendedoraAndEstado(Long idEmpresaVendedora, String estado);

    Optional<Venta> findByCorrelativo(String correlativo);
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
