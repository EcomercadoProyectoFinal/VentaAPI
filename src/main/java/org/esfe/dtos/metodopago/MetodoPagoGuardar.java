package org.esfe.dtos.metodopago;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetodoPagoGuardar implements Serializable {
    @NotBlank(message = "El metodo de pago no puede estar vacío")
    @Size(min = 5, max = 100, message = "El metodo de pago debe tener entre 5 y 100 caracteres")
    private String metodoPago;

    private String descripcion;
}
