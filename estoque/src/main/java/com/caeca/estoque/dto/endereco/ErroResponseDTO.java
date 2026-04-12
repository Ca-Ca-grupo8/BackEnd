package com.caeca.estoque.dto.endereco;

import com.fasterxml.jackson.annotation.JsonInclude;



@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErroResponseDTO {
    private String erro;
    private String campo;

    public ErroResponseDTO() {
    }

    public ErroResponseDTO(String campo, String erro) {
        this.campo = campo;
        this.erro = erro;
    }

    public ErroResponseDTO(String message) {
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
