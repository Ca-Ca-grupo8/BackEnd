package com.caeca.estoque.dto.joia;

import com.caeca.estoque.database.model.arquivo.Arquivo;
import com.caeca.estoque.database.model.categoria.Categoria;
import com.caeca.estoque.dto.arquivo.ArquivoResponseDto;
import com.caeca.estoque.dto.banho.BanhoResponseDto;
import com.caeca.estoque.dto.categoria.CategoriaSaidaDTO;
import com.caeca.estoque.dto.fornecedor.FornecedorResponseDTO;
import com.caeca.estoque.dto.material.MaterialResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public class JoiaSaidaDTO {

    private Long id;

    private String nome;

    private Double preco;

    private Integer quantidade;

    private Integer quantidadeMinima;

    private CategoriaSaidaDTO categoria;

    private BanhoResponseDto banho;

    private MaterialResponseDto material;

    private Boolean isAtivo;

    private JoiaStatus status;

    private LocalDateTime dataCadastro;

    private LocalDateTime dataAtualizacao;

    private ArquivoResponseDto arquivo;

    private List<FornecedorResponseDTO> fornecedores;

    public CategoriaSaidaDTO getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaSaidaDTO categoria) {
        this.categoria = categoria;
    }

    public BanhoResponseDto getBanho() {
        return banho;
    }

    public void setBanho(BanhoResponseDto banho) {
        this.banho = banho;
    }

    public MaterialResponseDto getMaterial() {
        return material;
    }

    public void setMaterial(MaterialResponseDto material) {
        this.material = material;
    }

    public JoiaSaidaDTO(Long id, String nome, Double preco, Integer quantidade, Integer quantidadeMinima, Boolean isAtivo, LocalDateTime dataCadastro, ArquivoResponseDto arquivo) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.quantidade = quantidade;
        this.quantidadeMinima = quantidadeMinima;
        this.isAtivo = isAtivo;
        this.dataCadastro = dataCadastro;
        this.arquivo = arquivo;
    }

    public JoiaSaidaDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Boolean getAtivo() {
        return isAtivo;
    }

    public void setAtivo(Boolean ativo) {
        isAtivo = ativo;
    }

    public JoiaStatus getStatus() {
        return status;
    }

    public void setStatus(JoiaStatus status) {
        this.status = status;
    }

    public Integer getQuantidadeMinima() {
        return quantidadeMinima;
    }

    public void setQuantidadeMinima(Integer quantidadeMinima) {
        this.quantidadeMinima = quantidadeMinima;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public ArquivoResponseDto getArquivo() {
        return arquivo;
    }

    public void setArquivo(ArquivoResponseDto arquivo) {
        this.arquivo = arquivo;
    }

    public List<FornecedorResponseDTO> getFornecedores() {
        return fornecedores;
    }

    public void setFornecedores(List<FornecedorResponseDTO> fornecedores) {
        this.fornecedores = fornecedores;
    }
}
