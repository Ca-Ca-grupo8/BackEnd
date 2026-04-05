package com.caeca.estoque.exception;

public class ErrorResponse {
    public Integer status;
    public String mensagem;

    public ErrorResponse() {
    }

    public ErrorResponse(Integer status, String mensagem) {
        this.status = status;
        this.mensagem = mensagem;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}