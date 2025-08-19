package br.com.forumhub.infra.exception;


public class TopicoNotFoundException extends RuntimeException {
    public TopicoNotFoundException(Long id) {
        super("Tópico não encontrado: id=" + id);
    }
}