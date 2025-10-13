package org.esfe.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ventas")
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String correlativo;

    private LocalDateTime fecha;
    // El total de la venta, calculado en el backend
    // Usar BigDecimal para un cálculo preciso del dinero
    private BigDecimal total;

    // El estado de la venta: pendiente, pagada, enviada, etc.
    private String estado;

    // El ID de la transacción de Stripe para referencia
    // Este es el dato que enlaza tu venta con el pago real en Stripe
    private String stripePaymentIntentId;

    @Column(nullable = false)
    private Boolean pagoConfirmadoEmpresa = false;

    @ManyToOne
    @JoinColumn(name = "idMetodoPago", nullable = false)
    private MetodoPago metodoPago;

    private Long usuarioId;

    private Long idEmpresaVendedora;

    private Long idBroker;
}
