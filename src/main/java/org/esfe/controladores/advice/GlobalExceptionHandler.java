package org.esfe.controladores.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import org.esfe.excepciones.RecursoNoEncontradoException;
import org.esfe.dtos.errores.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleRecursoNoEncontrado(
            RecursoNoEncontradoException ex, WebRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        
        ErrorResponse errorResponse = new ErrorResponse(
            status,
            ex.getMessage(), // El mensaje que se lanzó desde el servicio
            request.getDescription(false).replace("uri=", "") // Obtiene la ruta de la petición
        );

        return new ResponseEntity<>(errorResponse, status);
    }

}