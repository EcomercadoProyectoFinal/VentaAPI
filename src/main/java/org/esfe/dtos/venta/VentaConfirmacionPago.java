package org.esfe.dtos.venta;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(description = "DTO para actualizar el estado de confirmación de pago por parte de la empresa.")
public class VentaConfirmacionPago {
    @NotNull(message = "El estado de confirmación de pago no puede ser nulo.")
    @Schema(description = "Estado de confirmación de pago (true si el pago ha sido recibido).", example = "true")
    private Boolean pagoConfirmadoEmpresa;
}
