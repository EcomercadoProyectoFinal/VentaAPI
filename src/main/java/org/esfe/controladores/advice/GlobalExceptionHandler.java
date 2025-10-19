package org.esfe.controladores.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import org.esfe.excepciones.RecursoNoEncontradoException;

import java.util.stream.Collectors;

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
    /**
     * Maneja el 400 Bad Request cuando el cuerpo de la petición está mal formado o falta (como en tu imagen).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, WebRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "La solicitud no es válida o el cuerpo JSON está ausente/mal formado.";

        ErrorResponse errorResponse = new ErrorResponse(
            status,
            message,
            request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, status);
    }
    
    /**
     * Maneja errores de validación (@Valid) y devuelve 400 con los detalles de los campos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        // Recolectar todos los mensajes de error de campo
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponse errorResponse = new ErrorResponse(
            status,
            "Error de Validación de Datos: " + errors,
            request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, status);
    }

}