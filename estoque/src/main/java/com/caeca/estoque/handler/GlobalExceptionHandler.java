package com.caeca.estoque.handler;

import com.caeca.estoque.dto.endereco.ErroResponseDTO;
import com.caeca.estoque.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerNotFoundHandler(NotFoundException exception){
        ErrorResponse response = new ErrorResponse(
                404, exception.getMessage()
        );
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handlerConflict(ConflictException exception){
        ErrorResponse response = new ErrorResponse(
                409, exception.getMessage()
        );
        return ResponseEntity.status(409).body(response);
    }

    @ExceptionHandler(EnderecoNotFoundException.class)
    public ResponseEntity<ErroResponseDTO> handleEnderecoNotFound(EnderecoNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErroResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(CepNotFoundException.class)
    public ResponseEntity<ErroResponseDTO> handleCepNotFound(CepNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErroResponseDTO(ex.getMessage()));
    }

    // Erros de validação (@Valid): retorna um erro por campo
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErroResponseDTO>> handleValidation(MethodArgumentNotValidException ex) {
        List<ErroResponseDTO> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(this::buildErroFromField)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErroResponseDTO> handleGeneric(RuntimeException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErroResponseDTO("Erro inesperado no servidor."));
    }

    private ErroResponseDTO buildErroFromField(FieldError fe) {
        return new ErroResponseDTO(fe.getDefaultMessage(), fe.getField());
    }

}
