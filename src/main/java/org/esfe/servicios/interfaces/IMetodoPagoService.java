package org.esfe.servicios.interfaces;

import org.esfe.dtos.metodopago.MetodoPagoGuardar; // Asumiendo que existe
import org.esfe.dtos.metodopago.MetodoPagoModificar;
import org.esfe.dtos.metodopago.MetodoPagoSalida; // Asumiendo que existe
import org.esfe.modelos.MetodoPago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IMetodoPagoService {
    List<MetodoPagoSalida> obtenerTodos();

    Page<MetodoPagoSalida> obtenerTodosPaginados(Pageable pageable);

    MetodoPagoSalida obtenerPorId(Integer id);

    MetodoPagoSalida crear(MetodoPagoGuardar metodoPagoGuardar);

    MetodoPagoSalida editar(MetodoPagoModificar metodoPagoModificar);

    void eliminarPorId(Integer id);

    List<MetodoPago> findByMetodoPagoContainingIgnoreCaseOrDescripcionContainingIgnoreCaseOrderByIdDesc(
        String metodoPago,
        String descripcion
    );
}