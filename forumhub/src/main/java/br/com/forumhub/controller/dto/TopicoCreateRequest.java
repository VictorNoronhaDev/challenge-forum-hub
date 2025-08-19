package br.com.forumhub.controller.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;



public record TopicoCreateRequest(
        @NotBlank @Size(max = 180) String titulo,
        @NotBlank String mensagem,
        @NotBlank @Size(max = 120) String curso
) {}