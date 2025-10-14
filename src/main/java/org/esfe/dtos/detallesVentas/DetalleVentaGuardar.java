package org.esfe.dtos.detallesVentas;

import java.io.Serializable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetalleVentaGuardar implements Serializable {
    @NotNull(message = "El ID del producto es obligatorio.")
    @Min(value = 1, message = "El ID del producto debe ser mayor a 0.")
    private Long idProducto; 

    @NotNull(message = "La cantidad es obligatoria.")
    @Min(value = 1, message = "La cantidad debe ser al menos 1.")
    private Integer cantidad;
}
