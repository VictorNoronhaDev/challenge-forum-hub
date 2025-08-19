package br.com.forumhub.domain.topico;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Entity
@Table(name = "topicos")
public class Topico {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, length = 180)
    private String titulo;


    @Column(nullable = false, columnDefinition = "text")
    private String mensagem;


    @Column(name = "data_criacao", nullable = false, columnDefinition = "datetime(6)")
    private LocalDateTime dataCriacao;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusTopico status;


    @Column(nullable = false, length = 100)
    private String autor;


    @Column(nullable = false, length = 120)
    private String curso;


    public Topico() {}


    public Topico(String titulo, String mensagem, String autor, String curso) {
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.autor = autor;
        this.curso = curso;
        this.status = StatusTopico.NAO_RESPONDIDO;
    }


    @PrePersist
    void onCreate() {
        this.dataCriacao = LocalDateTime.now(ZoneOffset.ofHours(-3));
        if (this.status == null) {
            this.status = StatusTopico.NAO_RESPONDIDO;
        }
    }

    public void atualizar(String novoTitulo, String novaMensagem) {
        this.titulo = novoTitulo;
        this.mensagem = novaMensagem;
    }

    // Getters
    public Long getId() {
        return id;
    }
    public String getTitulo() {
        return titulo;
    }
    public String getMensagem() {
        return mensagem;
    }
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    public StatusTopico getStatus() {
        return status;
    }
    public String getAutor() {
        return autor;
    }
    public String getCurso() {
        return curso;
    }
}