package org.esfe.servicios.implementaciones;

import org.esfe.dtos.venta.VentaConfirmacionPago;
import org.esfe.dtos.venta.VentaGuardar;
import org.esfe.dtos.venta.VentaSalida;
import org.esfe.modelos.DetalleVenta; // Importar DetalleVenta
import org.esfe.modelos.MetodoPago;
import org.esfe.modelos.Venta;
import org.esfe.repositorios.IDetalleVentaRepository; // Importar Repositorio DetalleVenta
import org.esfe.repositorios.IMetodoPagoRepository;
import org.esfe.repositorios.IVentaRepository;
import org.esfe.servicios.interfaces.IVentaService;

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
import java.util.concurrent.atomic.AtomicReference; // Utilidad para el cálculo
import java.util.stream.Collectors;

// Simulación de una excepción para mantener la coherencia
class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String recurso, String campo, String valor) {
        super(String.format("%s no encontrado con %s : '%s'", recurso, campo, valor));
    }
}

@Service
public class VentaService implements IVentaService {

    @Autowired
    private IVentaRepository ventaRepository;

    @Autowired
    private IMetodoPagoRepository metodoPagoRepository;

    @Autowired
    private IDetalleVentaRepository detalleVentaRepository;

    @Autowired
    private ModelMapper modelMapper;

    /** Crea un Pageable que asegura la ordenación DESC por ID. */
    private Pageable createSortedPageable(Pageable pageable) {
        return PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by(Sort.Direction.DESC, "id")
        );
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
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta", "id", id.toString()));
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
                .orElseThrow(() -> new RecursoNoEncontradoException("MétodoPago", "id", ventaGuardar.getIdMetodoPago().toString()));
        venta.setMetodoPago(metodoPago);

        // 3. Asignar DATOS AUTOMÁTICOS
        venta.setFecha(LocalDateTime.now());
        venta.setEstado("PENDIENTE");
        venta.setPagoConfirmadoEmpresa(false);
        venta.setCorrelativo("VEN-" + System.currentTimeMillis()); 

        // 4. Procesamiento de DetalleVentas y Cálculo del Total
        AtomicReference<BigDecimal> totalCalculado = new AtomicReference<>(BigDecimal.ZERO);

        List<DetalleVenta> detallesVenta = ventaGuardar.getDetalles().stream()
            .map(detalleDto -> {
                DetalleVenta detalle = modelMapper.map(detalleDto, DetalleVenta.class);

                detalle.setId(null);
                
                // Cálculo del subtotal: cantidad * precioUnitario
                BigDecimal subtotal = detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad()));
                detalle.setSubtotal(subtotal);
                
                // Acumular el total
                totalCalculado.updateAndGet(current -> current.add(subtotal));

                return detalle;
            })
            .collect(Collectors.toList());
            
        // Asignar el total calculado a la Venta principal
        venta.setTotal(totalCalculado.get());
        
        // 5. Guardar Venta principal para obtener su ID
        Venta ventaGuardada = ventaRepository.save(venta); 
        
        // 6. Asignar la referencia de Venta a cada detalle y guardarlos
        detallesVenta.forEach(detalle -> detalle.setVenta(ventaGuardada));
        detalleVentaRepository.saveAll(detallesVenta); // Guardar todos los detalles en lote

        // 7. Mapear la salida (ModelMapper mapeará VentaGuardada a VentaSalida, incluyendo la lista de detalles)
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
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta", "correlativo", correlativo));
        return modelMapper.map(venta, VentaSalida.class);
    }

    @Override
    @Transactional
    public VentaSalida confirmarPagoEmpresa(Integer id, VentaConfirmacionPago confirmacion) {
        if (!ventaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Venta", "id", id.toString());
        }

        ventaRepository.actualizarConfirmacionPagoEmpresa(
            id, 
            confirmacion.getPagoConfirmadoEmpresa()
        );

        Venta ventaActualizada = ventaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Error al recargar la venta después de actualizar.")); 
        return modelMapper.map(ventaActualizada, VentaSalida.class);
    }

    @Override
    @Transactional
    public VentaSalida actualizarEstadoVenta(Integer id, String nuevoEstado) {
        if (!ventaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Venta", "id", id.toString());
        }

        ventaRepository.actualizarEstadoVenta(id, nuevoEstado);
        
        Venta ventaActualizada = ventaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Error al recargar la venta después de actualizar el estado."));
        return modelMapper.map(ventaActualizada, VentaSalida.class);
    }
}