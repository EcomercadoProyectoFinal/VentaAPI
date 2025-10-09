package org.esfe.servicios.implementaciones;

import org.esfe.dtos.metodopago.MetodoPagoGuardar;
import org.esfe.dtos.metodopago.MetodoPagoModificar;
import org.esfe.dtos.metodopago.MetodoPagoSalida;
import org.esfe.modelos.MetodoPago;
import org.esfe.repositorios.IMetodoPagoRepository;
import org.esfe.servicios.interfaces.IMetodoPagoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MetodoPagoService implements IMetodoPagoService {

    @Autowired
    private IMetodoPagoRepository metodoPagoRepository;

    @Autowired
    private ModelMapper modelMapper;
    
    @Override
    public MetodoPagoSalida crear(MetodoPagoGuardar metodoPagoGuardar) {
        MetodoPago metodoPago = modelMapper.map(metodoPagoGuardar, MetodoPago.class);
        metodoPago.setId(null); // Asegurar que es una nueva entidad
        metodoPago = metodoPagoRepository.save(metodoPago);
        return modelMapper.map(metodoPago, MetodoPagoSalida.class);
    }

    @Override
    public MetodoPagoSalida editar(MetodoPagoModificar metodoPagoModificar) {
        MetodoPago metodoPagoExistente = metodoPagoRepository.findById(metodoPagoModificar.getId())
                .orElseThrow(() -> new RuntimeException("Método de Pago no encontrado con ID: " + metodoPagoModificar.getId()));

        modelMapper.map(metodoPagoModificar, metodoPagoExistente);
        metodoPagoExistente = metodoPagoRepository.save(metodoPagoExistente);

        return modelMapper.map(metodoPagoExistente, MetodoPagoSalida.class);
    }

    @Override
    public void eliminarPorId(Integer id) {
        if (!metodoPagoRepository.existsById(id)) {
            throw new RuntimeException("Método de Pago no encontrado con ID: " + id);
        }
        metodoPagoRepository.deleteById(id);
    }

    @Override
    public MetodoPagoSalida obtenerPorId(Integer id) {
        MetodoPago metodoPago = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Método de Pago no encontrado"));

        return modelMapper.map(metodoPago, MetodoPagoSalida.class);
    }

    @Override
    public List<MetodoPagoSalida> obtenerTodos() {
        List<MetodoPago> metodosPago = metodoPagoRepository.findAll();

        return metodosPago.stream()
                .map(metodoPago -> modelMapper.map(metodoPago, MetodoPagoSalida.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<MetodoPagoSalida> obtenerTodosPaginados(Pageable pageable) {
        Page<MetodoPago> page = metodoPagoRepository.findAll(pageable);

        List<MetodoPagoSalida> metodosPagoDto = page.stream()
                .map(metodoPago -> modelMapper.map(metodoPago, MetodoPagoSalida.class))
                .collect(Collectors.toList());

        return new PageImpl<>(metodosPagoDto, page.getPageable(), page.getTotalElements());
    }

    @Override
    public List<MetodoPago> findByMetodoPagoContainingIgnoreCaseOrDescripcionContainingIgnoreCaseOrderByIdDesc(
            String metodoPago,
            String descripcion) {

        return metodoPagoRepository.findByMetodoPagoContainingIgnoreCaseOrDescripcionContainingIgnoreCaseOrderByIdDesc(
                metodoPago,
                descripcion
        );
    }
}