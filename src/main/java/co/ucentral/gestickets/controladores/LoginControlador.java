package co.ucentral.gestickets.controladores;

import co.ucentral.gestickets.dto.UsuarioDto;
import co.ucentral.gestickets.persistencia.entidades.Usuario;
import co.ucentral.gestickets.persistencia.repositorios.UsuarioRepositorio;
import co.ucentral.gestickets.servicios.UsuarioServicio;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginControlador {

    private final UsuarioServicio usuarioServicio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final BCryptPasswordEncoder passwordEncoder;

    // Vista: formulario login
    @GetMapping
    public String mostrarFormularioLogin(Model model) {
        model.addAttribute("usuario", new UsuarioDto());
        return "login";
    }

    // Procesar login desde vista web
    @PostMapping
    public String procesarLogin(@ModelAttribute UsuarioDto dto, HttpSession session, Model model) {
        Usuario usuario = usuarioServicio.autenticar(dto.getUsername(), dto.getPassword());

        if (usuario != null) {
            session.setAttribute("usuarioLogueado", usuario);
            return "redirect:/solicitudes/ver";
        } else {
            model.addAttribute("error", "Credenciales incorrectas.");
            return "login";
        }
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // Vista: formulario registro
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new UsuarioDto());
        return "registro";
    }

    // Procesar registro desde vista web
    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute UsuarioDto dto, Model model) {
        usuarioServicio.crear(dto);
        model.addAttribute("mensaje", "Registro exitoso, ahora puedes iniciar sesión.");
        return "redirect:/login";
    }

    // ✅ Registrar desde Postman o Flutter
    @PostMapping("/api/registro")
    @ResponseBody
    public ResponseEntity<String> registrarUsuarioDesdePostman(@RequestBody Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepositorio.save(usuario);
        return ResponseEntity.ok("Usuario registrado correctamente.");
    }

    // ✅ Autenticar desde Flutter (devuelve el username como token)
    @PostMapping("/api/autenticar")
    @ResponseBody
    public ResponseEntity<?> autenticarUsuarioDesdePostman(@RequestBody UsuarioDto dto) {
        Usuario usuario = usuarioServicio.autenticar(dto.getUsername(), dto.getPassword());

        if (usuario != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("username", usuario.getUsername());
            response.put("rol", usuario.getRol().name());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }
}