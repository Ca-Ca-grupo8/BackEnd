package com.caeca.estoque.handler;

import com.caeca.estoque.dto.endereco.ErroResponseDto;
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

    @ExceptionHandler(QtdInsuficienteException.class)
    public ResponseEntity<ErrorResponse> handlerQtdInsuficiente(QtdInsuficienteException exception){
        ErrorResponse response = new ErrorResponse(
                409, exception.getMessage()
        );
        return ResponseEntity.status(409).body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handlerBadRequest(BadRequestException exception){
        ErrorResponse response = new ErrorResponse(
                400, exception.getMessage()
        );
        return ResponseEntity.status(400).body(response);
    }

    @ExceptionHandler(EnderecoNotFoundException.class)
    public ResponseEntity<ErroResponseDto> handleEnderecoNotFound(EnderecoNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErroResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(CepNotFoundException.class)
    public ResponseEntity<ErroResponseDto> handleCepNotFound(CepNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErroResponseDto(ex.getMessage()));
    }

    // Erros de validação retorna um erro por campo
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErroResponseDto>> handleValidation(MethodArgumentNotValidException ex) {
        List<ErroResponseDto> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(this::buildErroFromField)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErroResponseDto> handleGeneric(RuntimeException ex) {
        System.err.println("Erro inesperado no servidor.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErroResponseDto("Erro inesperado no servidor."));
    }

    private ErroResponseDto buildErroFromField(FieldError fe) {
        return new ErroResponseDto(fe.getDefaultMessage(), fe.getField());
    }

}
