package com.caeca.estoque.dto.endereco;

public class EnderecoResponseDto {
    private Long id;
    private String cep;
    private String logradouro;
    private Integer numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;

    public EnderecoResponseDto() {
    }

    public EnderecoResponseDto(Long id, String cep, String logradouro, Integer numero, String complemento, String bairro, String cidade, String uf) {
        this.id = id;
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String cep;
        private String logradouro;
        private Integer numero;
        private String complemento;
        private String bairro;
        private String cidade;
        private String uf;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder cep(String cep) { this.cep = cep; return this; }
        public Builder logradouro(String logradouro) { this.logradouro = logradouro; return this; }
        public Builder numero(Integer numero) { this.numero = numero; return this; }
        public Builder complemento(String complemento) { this.complemento = complemento; return this; }
        public Builder bairro(String bairro) { this.bairro = bairro; return this; }
        public Builder cidade(String cidade) { this.cidade = cidade; return this; }
        public Builder uf(String uf) { this.uf = uf; return this; }
        public EnderecoResponseDto build() {
            return new EnderecoResponseDto(id, cep, logradouro, numero, complemento, bairro, cidade, uf);
        }
    }
}
