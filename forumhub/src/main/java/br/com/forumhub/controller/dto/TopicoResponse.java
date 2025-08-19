package br.com.forumhub.controller.dto;


import br.com.forumhub.domain.topico.Topico;
import java.time.format.DateTimeFormatter;


public record TopicoResponse(
        Long id,
        String titulo,
        String mensagem,
        String dataCriacao, // formato "yyyy-MM-dd HH:mm" em GMT-3
        String status,
        String autor,
        String curso
) {
    private static final DateTimeFormatter YYYY_MM_DD_HH_MM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    public static TopicoResponse from(Topico t) {
        String data = t.getDataCriacao().format(YYYY_MM_DD_HH_MM);
        return new TopicoResponse(
                t.getId(),
                t.getTitulo(),
                t.getMensagem(),
                data,
                t.getStatus().name(),
                t.getAutor(),
                t.getCurso()
        );
    }
}