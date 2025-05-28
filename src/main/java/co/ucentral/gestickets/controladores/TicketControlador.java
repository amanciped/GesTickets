package co.ucentral.gestickets.controladores;

import co.ucentral.gestickets.dto.*;
import co.ucentral.gestickets.enums.EstadoTicket;
import co.ucentral.gestickets.enums.Prioridad;
import co.ucentral.gestickets.persistencia.entidades.*;
import co.ucentral.gestickets.persistencia.repositorios.*;
import co.ucentral.gestickets.servicios.TicketServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketControlador {

    private final TicketRepositorio ticketRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final ComentarioRepositorio comentarioRepositorio;
    private final TicketServicio ticketServicio;

    private Optional<Usuario> getUsuarioDesdeToken(String token) {
        String username = token.replace("Bearer", "").trim();
        return usuarioRepositorio.findByUsername(username);
    }

    @PostMapping("/create")
    public ResponseEntity<?> crearTicket(@RequestBody TicketDto ticketDto, @RequestHeader("Authorization") String token) {
        Optional<Usuario> usuarioOpt = getUsuarioDesdeToken(token);
        if (usuarioOpt.isEmpty()) return ResponseEntity.status(401).body("Usuario no autenticado.");

        Ticket ticket = new Ticket();
        ticket.setTitulo(ticketDto.getTitulo());
        ticket.setDescripcion(ticketDto.getDescripcion());
        ticket.setCategoria(ticketDto.getCategoria());
        ticket.setEstado(EstadoTicket.ABIERTO);//valor predeterminado
        ticket.setPrioridad(Prioridad.MEDIA);//valor predeterminado
        ticket.setCreador(usuarioOpt.get());

        return ResponseEntity.ok(ticketRepositorio.save(ticket));
    }

    @GetMapping("/by-usuario")
    public ResponseEntity<?> ticketsPorUsuario(@RequestHeader("Authorization") String token) {
        Optional<Usuario> usuarioOpt = getUsuarioDesdeToken(token);
        if (usuarioOpt.isEmpty()) return ResponseEntity.status(401).body("Usuario no autenticado.");

        return ResponseEntity.ok(ticketRepositorio.findByCreador(usuarioOpt.get()));
    }

    @PostMapping("/comentario/{ticketId}")
    public ResponseEntity<?> agregarComentario(@PathVariable Long ticketId, @RequestBody ComentarioDto comentarioDto, @RequestHeader("Authorization") String token) {
        Optional<Usuario> usuarioOpt = getUsuarioDesdeToken(token);
        Optional<Ticket> ticketOpt = ticketRepositorio.findById(ticketId);

        if (usuarioOpt.isEmpty()) return ResponseEntity.status(401).body("Usuario no autenticado.");
        if (ticketOpt.isEmpty()) return ResponseEntity.status(404).body("Ticket no encontrado.");

        Comentario comentario = Comentario.builder()
                .contenido(comentarioDto.getContenido())
                .autor(usuarioOpt.get())
                .ticket(ticketOpt.get())
                .build();

        comentario.setFecha(LocalDateTime.now());


        return ResponseEntity.ok(comentarioRepositorio.save(comentario));
    }

    @GetMapping("/comentario/{ticketId}")
    public ResponseEntity<?> verComentarios(@PathVariable Long ticketId) {
        Optional<Ticket> ticketOpt = ticketRepositorio.findById(ticketId);
        if (ticketOpt.isEmpty()) return ResponseEntity.status(404).body("Ticket no encontrado.");

        return ResponseEntity.ok(comentarioRepositorio.findByTicket(ticketOpt.get()));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editarTicket(@PathVariable Long id, @RequestBody TicketDto ticketDto, @RequestHeader("Authorization") String token) {
        Optional<Usuario> usuarioOpt = getUsuarioDesdeToken(token);
        if (usuarioOpt.isEmpty()) return ResponseEntity.status(401).body("No autenticado");

        Optional<Ticket> ticketOpt = ticketRepositorio.findById(id);
        if (ticketOpt.isEmpty()) return ResponseEntity.status(404).body("Ticket no encontrado");

        Ticket ticket = ticketOpt.get();
        if (!ticket.getCreador().getId().equals(usuarioOpt.get().getId()))
            return ResponseEntity.status(403).body("No puedes editar este ticket");

        ticket.setTitulo(ticketDto.getTitulo());
        ticket.setDescripcion(ticketDto.getDescripcion());
        ticket.setCategoria(ticketDto.getCategoria());

        return ResponseEntity.ok(ticketRepositorio.save(ticket));
    }

    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<?> eliminarTicket(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Optional<Usuario> usuarioOpt = getUsuarioDesdeToken(token);
        if (usuarioOpt.isEmpty()) return ResponseEntity.status(401).body("No autenticado");

        Optional<Ticket> ticketOpt = ticketRepositorio.findById(id);
        if (ticketOpt.isEmpty()) return ResponseEntity.status(404).body("Ticket no encontrado");

        Ticket ticket = ticketOpt.get();
        if (!ticket.getCreador().getId().equals(usuarioOpt.get().getId()))
            return ResponseEntity.status(403).body("No puedes eliminar este ticket");

        comentarioRepositorio.deleteByTicket(ticket); // primero los comentarios
        ticketRepositorio.delete(ticket);             //  luego el ticket

        return ResponseEntity.ok("Eliminado");
    }

    @GetMapping("/my")
    public ResponseEntity<?> verMisTicketsAsignados(@RequestHeader("Authorization") String token) {
        Optional<Usuario> usuarioOpt = getUsuarioDesdeToken(token);
        if (usuarioOpt.isEmpty()) return ResponseEntity.status(401).body("No autenticado");

        Usuario usuario = usuarioOpt.get();
        if (usuario.getRol() != Usuario.Rol.GESTOR)
            return ResponseEntity.status(403).body("Solo los GESTORES pueden ver esta información");

        return ResponseEntity.ok(ticketRepositorio.findByGestorAsignado(usuario));
    }

    @PostMapping("/assign/{id}")
    public ResponseEntity<?> asignarTicket(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Optional<Usuario> usuarioOpt = getUsuarioDesdeToken(token);
        if (usuarioOpt.isEmpty()) return ResponseEntity.status(401).body("No autenticado");

        Usuario usuario = usuarioOpt.get();
        if (usuario.getRol() != Usuario.Rol.GESTOR)
            return ResponseEntity.status(403).body("Solo los GESTORES pueden asignarse tickets");

        Optional<Ticket> ticketOpt = ticketRepositorio.findById(id);
        if (ticketOpt.isEmpty()) return ResponseEntity.status(404).body("Ticket no encontrado");

        Ticket ticket = ticketOpt.get();

        if (ticket.getGestorAsignado() != null) {
            return ResponseEntity.status(400).body("El ticket ya fue asignado a otro gestor");
        }

        ticket.setGestorAsignado(usuario);

        // Solo cambia el estado si aún no fue definido por el gestor en frontend
        if (ticket.getEstado() == EstadoTicket.ABIERTO) {
            ticket.setEstado(EstadoTicket.EN_ESPERA);
        }

        ticketRepositorio.save(ticket);
        return ResponseEntity.ok("Ticket asignado correctamente");
    }

    @GetMapping("/all")
    public ResponseEntity<?> verTodos(@RequestHeader("Authorization") String token) {
        Optional<Usuario> usuarioOpt = getUsuarioDesdeToken(token);
        if (usuarioOpt.isEmpty()) return ResponseEntity.status(401).body("No autenticado");

        Usuario usuario = usuarioOpt.get();
        if (usuario.getRol() != Usuario.Rol.GESTOR)
            return ResponseEntity.status(403).body("Solo GESTORES pueden ver todos los tickets");

        return ResponseEntity.ok(ticketRepositorio.findAll());
    }

    @PutMapping("/gestor/update/{id}")
    public ResponseEntity<?> actualizarTicketComoGestor(@PathVariable Long id, @RequestBody ActualizacionTicketDto dto, @RequestHeader("Authorization") String token) {
        Optional<Usuario> usuarioOpt = getUsuarioDesdeToken(token);
        if (usuarioOpt.isEmpty()) return ResponseEntity.status(401).body("No autenticado");

        Usuario usuario = usuarioOpt.get();
        if (usuario.getRol() != Usuario.Rol.GESTOR)
            return ResponseEntity.status(403).body("Solo GESTORES pueden actualizar tickets");

        Optional<Ticket> ticketOpt = ticketRepositorio.findById(id);
        if (ticketOpt.isEmpty()) return ResponseEntity.status(404).body("Ticket no encontrado");

        Ticket ticket = ticketOpt.get();
        if (ticket.getGestorAsignado() == null || !ticket.getGestorAsignado().getId().equals(usuario.getId()))
            return ResponseEntity.status(403).body("No puedes modificar un ticket no asignado a ti");

        ticket.setEstado(dto.getEstado());
        ticket.setPrioridad(dto.getPrioridad());

        return ResponseEntity.ok(ticketRepositorio.save(ticket));
    }
}