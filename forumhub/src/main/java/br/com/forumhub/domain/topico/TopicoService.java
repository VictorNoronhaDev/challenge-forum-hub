package br.com.forumhub.domain.topico;


import br.com.forumhub.controller.dto.TopicoCreateRequest;
import br.com.forumhub.controller.dto.TopicoListResponse;
import br.com.forumhub.controller.dto.TopicoResponse;
import br.com.forumhub.controller.dto.TopicoUpdateRequest;
import br.com.forumhub.domain.usuario.Usuario;
import br.com.forumhub.infra.exception.DuplicateTopicException;
import br.com.forumhub.infra.exception.TopicoNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;


@Service
public class TopicoService {


    private final TopicoRepository repository;


    public TopicoService(TopicoRepository repository) {
        this.repository = repository;
    }


    @Transactional
    public TopicoResponse criar(TopicoCreateRequest req) {
        String titulo = req.titulo().trim();
        String mensagem = req.mensagem().trim();
        String curso = req.curso().trim();


        if (repository.existsByTituloAndMensagem(titulo, mensagem)) {
            throw new DuplicateTopicException("Tópico duplicado (mesmo título e mensagem).");
        }
        String autor = nomeUsuarioAutenticado();
        Topico entity = new Topico(titulo, mensagem, autor, curso);
        Topico salvo = repository.save(entity);
        return TopicoResponse.from(salvo);
    }


    @Transactional(readOnly = true)
    public List<TopicoListResponse> listar() {
        return repository.findAll()
                .stream()
                .map(TopicoListResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TopicoResponse detalhar(Long id) {
        Topico t = repository.findById(id)
                .orElseThrow(() -> new TopicoNotFoundException(id));
        return TopicoResponse.from(t);
    }

    @Transactional
    public TopicoResponse atualizar(Long id, TopicoUpdateRequest req) {
        Topico entity = repository.findById(id)
                .orElseThrow(() -> new TopicoNotFoundException(id));

        String titulo = req.titulo().trim();
        String mensagem = req.mensagem().trim();

        if (repository.existsByTituloAndMensagemAndIdNot(titulo, mensagem, id)) {
            throw new DuplicateTopicException("Tópico duplicado (mesmo título e mensagem).");
        }

        entity.atualizar(titulo, mensagem); // não altera curso
        Topico salvo = repository.save(entity);
        return TopicoResponse.from(salvo);
    }

    @Transactional
    public void excluir(Long id) {
        var opt = repository.findById(id);
        if (opt.isEmpty()) {
            throw new TopicoNotFoundException(id);
        }
        repository.deleteById(id);
    }

    private String nomeUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof Usuario u) {
            if (u.getNome() != null && !u.getNome().isBlank()) return u.getNome();
        }
        return "ANONIMO";
    }
}