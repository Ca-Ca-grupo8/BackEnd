package com.caeca.estoque.dto.endereco;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;


@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErroResponseDto {
    private String erro;
    private String campo;

    public ErroResponseDto() {
    }

    public ErroResponseDto(String campo, String erro) {
        this.campo = campo;
        this.erro = erro;
    }

    public ErroResponseDto(String message) {
        this.erro = message;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }


}
