package br.com.forumhub.domain.topico;


import org.springframework.data.jpa.repository.JpaRepository;


public interface TopicoRepository extends JpaRepository<Topico, Long> {
    boolean existsByTituloAndMensagem(String titulo, String mensagem);
    boolean existsByTituloAndMensagemAndIdNot(String titulo, String mensagem, Long id);
}