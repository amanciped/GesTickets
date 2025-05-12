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
            //Aquí se guarda el usuario en sesión (incluyendo el rol)
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

    // Registrar desde Postman (JSON)
    @PostMapping("/api/registro")
    @ResponseBody
    public ResponseEntity<String> registrarUsuarioDesdePostman(@RequestBody Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepositorio.save(usuario);
        return ResponseEntity.ok("Usuario registrado correctamente.");
    }

    // Autenticar desde Postman (JSON)
    @PostMapping("/api/autenticar")
    @ResponseBody
    public ResponseEntity<String> autenticarUsuarioDesdePostman(@RequestBody UsuarioDto dto, HttpSession session) {
        Usuario usuario = usuarioServicio.autenticar(dto.getUsername(), dto.getPassword());

        if (usuario != null) {
            session.setAttribute("usuarioLogueado", usuario);  // guardar usuario en sesión
            return ResponseEntity.ok("Autenticación exitosa");
        } else {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }
}
