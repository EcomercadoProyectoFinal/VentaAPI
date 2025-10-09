package org.esfe.repositorios;

import org.esfe.modelos.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IMetodoPagoRepository extends JpaRepository<MetodoPago, Integer> {
    List<MetodoPago> findByMetodoPagoContainingIgnoreCaseOrDescripcionContainingIgnoreCaseOrderByIdDesc(
        String metodoPago,
        String descripcion
    );

    List<MetodoPago> findByMetodoPagoContainingIgnoreCaseOrderByIdDesc(String metodoPago);

    List<MetodoPago> findByDescripcionContainingIgnoreCaseOrderByIdDesc(String descripcion);
}