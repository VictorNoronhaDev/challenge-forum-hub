package br.com.forumhub.controller;


import br.com.forumhub.controller.dto.TopicoCreateRequest;
import br.com.forumhub.controller.dto.TopicoResponse;
import br.com.forumhub.controller.dto.TopicoListResponse;
import br.com.forumhub.controller.dto.TopicoUpdateRequest;
import br.com.forumhub.domain.topico.TopicoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/topicos")
public class TopicoController {


    private final TopicoService service;


    public TopicoController(TopicoService service) {
        this.service = service;
    }


    @PostMapping
    public ResponseEntity<TopicoResponse> criar(@Valid @RequestBody TopicoCreateRequest request,
                                                UriComponentsBuilder uriBuilder) {
        TopicoResponse resp = service.criar(request);
        URI location = uriBuilder.path("/topicos/{id}").buildAndExpand(resp.id()).toUri();
        return ResponseEntity.created(location).body(resp);
    }


    @GetMapping
    public List<TopicoListResponse> listar() {
        return service.listar();
    }
    @GetMapping("/{id}")
    public TopicoResponse detalhar(@PathVariable Long id) {
        return service.detalhar(id);
    }
    @PutMapping("/{id}")
    public TopicoResponse atualizar(@PathVariable Long id, @Valid @RequestBody TopicoUpdateRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}