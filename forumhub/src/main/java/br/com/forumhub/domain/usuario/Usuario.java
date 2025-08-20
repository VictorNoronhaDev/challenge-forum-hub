package br.com.forumhub.domain.usuario;


import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, length = 100)
    private String nome;


    @Column(nullable = false, unique = true, length = 120)
    private String email;


    @Column(nullable = false, length = 120)
    private String senha;


    public Usuario() {}


    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }


    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }


    // UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }


    @Override
    public String getPassword() { return senha; }


    @Override
    public String getUsername() { return email; }


    @Override
    public boolean isAccountNonExpired() { return true; }


    @Override
    public boolean isAccountNonLocked() { return true; }


    @Override
    public boolean isCredentialsNonExpired() { return true; }


    @Override
    public boolean isEnabled() { return true; }
}