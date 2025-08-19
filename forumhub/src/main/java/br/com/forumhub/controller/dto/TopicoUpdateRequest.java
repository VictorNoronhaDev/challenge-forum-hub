package br.com.forumhub.controller.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


// Atualização: somente título e mensagem (curso não é alterável no PUT)
public record TopicoUpdateRequest(
        @NotBlank @Size(max = 180) String titulo,
        @NotBlank String mensagem
) {}