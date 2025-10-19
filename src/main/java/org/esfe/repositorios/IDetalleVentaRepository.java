package org.esfe.repositorios;

import org.esfe.modelos.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {
    
}