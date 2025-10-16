package org.esfe.servicios.implementaciones;

import org.esfe.dtos.venta.VentaConfirmacionPago;
import org.esfe.dtos.venta.VentaGuardar;
import org.esfe.dtos.venta.VentaSalida;
import org.esfe.modelos.MetodoPago;
import org.esfe.modelos.Venta;
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
        // Para obtener todas y ordenar DESC, usamos Sort
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        List<Venta> ventas = ventaRepository.findAll(sort); 
        
        return ventas.stream()
                .map(venta -> modelMapper.map(venta, VentaSalida.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<VentaSalida> obtenerTodasPaginadas(Pageable pageable) {
        // Aplicamos el ordenamiento DESC al Pageable recibido
        Pageable sortedPageable = createSortedPageable(pageable);
        
        Page<Venta> page = ventaRepository.findAll(sortedPageable);
        
        return page.map(venta -> modelMapper.map(venta, VentaSalida.class));
        // Usamos page.map() que es más eficiente que crear un PageImpl
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
        // 1. Mapear DTO a Entidad
        Venta venta = modelMapper.map(ventaGuardar, Venta.class);
        venta.setId(null);

        // 2. Obtener la entidad MetodoPago y asignarla
        MetodoPago metodoPago = metodoPagoRepository.findById(ventaGuardar.getIdMetodoPago())
                .orElseThrow(() -> new RecursoNoEncontradoException("MétodoPago", "id", ventaGuardar.getIdMetodoPago().toString()));
        venta.setMetodoPago(metodoPago);

        // 3. Asignar DATOS AUTOMÁTICOS
        venta.setFecha(LocalDateTime.now());
        venta.setEstado("PENDIENTE"); // Estado inicial
        venta.setPagoConfirmadoEmpresa(false); // Por defecto
        
        // TODO: Lógica para calcular total (sumando detalles) y generar correlativo único
        venta.setTotal(new BigDecimal("0.00")); 
        venta.setCorrelativo("VEN-" + System.currentTimeMillis()); 

        // 4. Guardar y mapear la salida
        Venta ventaGuardada = ventaRepository.save(venta);
        return modelMapper.map(ventaGuardada, VentaSalida.class);
    }

    //  Operaciones de Búsqueda por Rol (Paginadas y Ordenadas) 

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

    //  Operaciones Críticas del Negocio (Empresa) 

    @Override
    @Transactional
    public VentaSalida confirmarPagoEmpresa(Integer id, VentaConfirmacionPago confirmacion) {
        if (!ventaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Venta", "id", id.toString());
        }

        // Ejecutar la actualización eficiente con @Modifying
        ventaRepository.actualizarConfirmacionPagoEmpresa(
            id, 
            confirmacion.getPagoConfirmadoEmpresa()
        );

        // Recargar y devolver el estado actual de la venta
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

        // Ejecutar la actualización eficiente con @Modifying
        ventaRepository.actualizarEstadoVenta(id, nuevoEstado);
        
        // Recargar y devolver el estado actual de la venta
        Venta ventaActualizada = ventaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Error al recargar la venta después de actualizar el estado."));
        return modelMapper.map(ventaActualizada, VentaSalida.class);
    }
}