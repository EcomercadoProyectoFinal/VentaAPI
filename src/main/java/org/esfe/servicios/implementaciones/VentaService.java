package org.esfe.servicios.implementaciones;

import org.esfe.dtos.venta.VentaConfirmacionPago;
import org.esfe.dtos.venta.VentaGuardar;
import org.esfe.dtos.venta.VentaSalida;
import org.esfe.modelos.DetalleVenta;
import org.esfe.modelos.MetodoPago;
import org.esfe.modelos.Venta;
import org.esfe.repositorios.IMetodoPagoRepository;
import org.esfe.repositorios.IVentaRepository;
import org.esfe.servicios.interfaces.IVentaService;

// Importar la excepción personalizada (Asumiendo que está en org.esfe.excepciones)
import org.esfe.excepciones.RecursoNoEncontradoException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class VentaService implements IVentaService {

    @Autowired
    private IVentaRepository ventaRepository;

    @Autowired
    private IMetodoPagoRepository metodoPagoRepository;

    @Autowired
    private ModelMapper modelMapper;

    /** Crea un Pageable que asegura la ordenación DESC por ID. */
    private Pageable createSortedPageable(Pageable pageable) {
        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public List<VentaSalida> obtenerTodas() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        List<Venta> ventas = ventaRepository.findAll(sort);

        return ventas.stream()
                .map(venta -> modelMapper.map(venta, VentaSalida.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<VentaSalida> obtenerTodasPaginadas(Pageable pageable) {
        Pageable sortedPageable = createSortedPageable(pageable);
        Page<Venta> page = ventaRepository.findAll(sortedPageable);
        return page.map(venta -> modelMapper.map(venta, VentaSalida.class));
    }

    @Override
    public VentaSalida obtenerPorId(Integer id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        String.format("Venta no encontrada con ID: %d", id)));
        return modelMapper.map(venta, VentaSalida.class);
    }

    @Override
    @Transactional
    public VentaSalida crear(VentaGuardar ventaGuardar) {
        // 1. Mapear DTO a Entidad Venta principal
        Venta venta = modelMapper.map(ventaGuardar, Venta.class);
        venta.setId(null);

        // 2. Obtener MetodoPago y asignarlo
        MetodoPago metodoPago = metodoPagoRepository.findById(ventaGuardar.getIdMetodoPago())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        String.format("Método de Pago no encontrado con ID: %d", ventaGuardar.getIdMetodoPago())));
        venta.setMetodoPago(metodoPago);

        // 3. Asignar DATOS AUTOMÁTICOS
        venta.setFecha(LocalDateTime.now());
        venta.setEstado("PENDIENTE");
        venta.setPagoConfirmadoEmpresa(false);
        venta.setCorrelativo("VEN-" + System.currentTimeMillis());

        AtomicReference<BigDecimal> totalCalculado = new AtomicReference<>(BigDecimal.ZERO);

        List<DetalleVenta> detallesVenta = ventaGuardar.getDetalles().stream()
                .map(detalleDto -> {
                    DetalleVenta detalle = modelMapper.map(detalleDto, DetalleVenta.class);
                    detalle.setId(null);

                    detalle.setVenta(venta); 
                    BigDecimal subtotal = detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad()));
                    detalle.setSubtotal(subtotal);
                    totalCalculado.updateAndGet(current -> current.add(subtotal));

                    return detalle;
                })
                .collect(Collectors.toList());

        venta.setTotal(totalCalculado.get());

        venta.setDetalles(detallesVenta);

        Venta ventaGuardada = ventaRepository.save(venta);

        return modelMapper.map(ventaGuardada, VentaSalida.class);
    }

    @Override
    public Page<VentaSalida> obtenerVentasPorClientePaginado(Long usuarioId, Pageable pageable) {
        Pageable sortedPageable = createSortedPageable(pageable);
        Page<Venta> page = ventaRepository.findByUsuarioId(usuarioId, sortedPageable);
        return page.map(venta -> modelMapper.map(venta, VentaSalida.class));
    }

    @Override
    public Page<VentaSalida> obtenerVentasPorEmpresaPaginado(Long idEmpresaVendedora, Pageable pageable) {
        Pageable sortedPageable = createSortedPageable(pageable);
        Page<Venta> page = ventaRepository.findByIdEmpresaVendedora(idEmpresaVendedora, sortedPageable);
        return page.map(venta -> modelMapper.map(venta, VentaSalida.class));
    }

    @Override
    public Page<VentaSalida> obtenerVentasPorBrokerPaginado(Long idBroker, Pageable pageable) {
        Pageable sortedPageable = createSortedPageable(pageable);
        Page<Venta> page = ventaRepository.findByIdBroker(idBroker, sortedPageable);
        return page.map(venta -> modelMapper.map(venta, VentaSalida.class));
    }

    @Override
    public VentaSalida obtenerPorCorrelativo(String correlativo) {
        Venta venta = ventaRepository.findByCorrelativo(correlativo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        String.format("Venta no encontrada con Correlativo: %s", correlativo)));
        return modelMapper.map(venta, VentaSalida.class);
    }

    @Override
    @Transactional
    public VentaSalida confirmarPagoEmpresa(Integer id, VentaConfirmacionPago confirmacion) {
        if (!ventaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException(
                    String.format("Venta no encontrada con ID: %d", id));
        }

        ventaRepository.actualizarConfirmacionPagoEmpresa(
                id,
                confirmacion.getPagoConfirmadoEmpresa());

        Venta ventaActualizada = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Error al recargar la venta después de actualizar. Posible fallo de concurrencia/DB."));
        return modelMapper.map(ventaActualizada, VentaSalida.class);
    }

    @Override
    @Transactional
    public VentaSalida actualizarEstadoVenta(Integer id, String nuevoEstado) {
        if (!ventaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException(
                    String.format("Venta no encontrada con ID: %d", id));
        }

        ventaRepository.actualizarEstadoVenta(id, nuevoEstado);

        Venta ventaActualizada = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Error al recargar la venta después de actualizar el estado. Posible fallo de concurrencia/DB."));
        return modelMapper.map(ventaActualizada, VentaSalida.class);
    }
}