package com.caeca.estoque.database.model.arquivo;

import com.caeca.estoque.database.model.joia.Joia;
import jakarta.activation.MimeType;
import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
public class Arquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeOriginal;

    @Column(nullable = false)
    private String s3Key;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String mimeType;

    @Column(nullable = false)
    private Long tamanho;

    @ManyToOne
    @JoinColumn(name = "fk_joia")
    private Joia joia;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime dataUpload;

    public Arquivo() {
    }

    public Arquivo(Long id, String nomeOriginal, String s3Key, String url, String mimeType, Long tamanho, Joia joia, LocalDateTime dataUpload) {
        this.id = id;
        this.nomeOriginal = nomeOriginal;
        this.s3Key = s3Key;
        this.url = url;
        this.mimeType = mimeType;
        this.tamanho = tamanho;
        this.joia = joia;
        this.dataUpload = dataUpload;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeOriginal() {
        return nomeOriginal;
    }

    public void setNomeOriginal(String nomeOriginal) {
        this.nomeOriginal = nomeOriginal;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Joia getJoia() {
        return joia;
    }

    public void setJoia(Joia joia) {
        this.joia = joia;
    }

    public LocalDateTime getDataUpload() {
        return dataUpload;
    }

    public void setDataUpload(LocalDateTime dataUpload) {
        this.dataUpload = dataUpload;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getTamanho() {
        return tamanho;
    }

    public void setTamanho(Long tamanho) {
        this.tamanho = tamanho;
    }
}
