package br.com.forumhub.infra.dev;


import br.com.forumhub.domain.usuario.Usuario;
import br.com.forumhub.domain.usuario.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
public class DevDataConfig {


    @Bean
    CommandLineRunner seedDefaultUser(UsuarioRepository repo, PasswordEncoder encoder) {
        return args -> {
            repo.findByEmail("admin@forumhub.dev").orElseGet(() -> {
                Usuario u = new Usuario("Admin", "admin@forumhub.dev", encoder.encode("123456"));
                return repo.save(u);
            });
        };
    }
}