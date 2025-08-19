package br.com.forumhub.controller.dto;


import br.com.forumhub.domain.topico.Topico;
import java.time.format.DateTimeFormatter;


public record TopicoListResponse(
        Long id,
        String titulo,
        String mensagem,
        String dataCriacao // yyyy-MM-dd HH:mm (GMT-3)
) {
    private static final DateTimeFormatter YYYY_MM_DD_HH_MM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    public static TopicoListResponse from(Topico t) {
        String data = t.getDataCriacao().format(YYYY_MM_DD_HH_MM);
        return new TopicoListResponse(
                t.getId(),
                t.getTitulo(),
                t.getMensagem(),
                data
        );
    }
}