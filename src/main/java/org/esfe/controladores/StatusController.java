package org.esfe.controladores;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class StatusController {

    @GetMapping("/")
    public String welcome() {
        return "Servidor VentaAPI activo y listo.";
    }
}
