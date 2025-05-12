package co.ucentral.gestickets.servicios;

import co.ucentral.gestickets.dto.UsuarioDto;
import co.ucentral.gestickets.persistencia.entidades.Usuario;
import co.ucentral.gestickets.persistencia.repositorios.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final BCryptPasswordEncoder passwordEncoder;

    public Usuario crear(UsuarioDto dto) {
        Usuario usuario = Usuario.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nacionalidad(dto.getNacionalidad())
                .rol(dto.getRol())
                .build();
        return usuarioRepositorio.save(usuario);
    }

    public Usuario autenticar(String username, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByUsername(username);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (passwordEncoder.matches(password, usuario.getPassword())) {
                return usuario;
            }
        }
        return null;
    }
}