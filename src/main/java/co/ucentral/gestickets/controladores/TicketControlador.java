package co.ucentral.gestickets.controladores;

import co.ucentral.gestickets.persistencia.entidades.Usuario;
import co.ucentral.gestickets.persistencia.entidades.Ticket;
import co.ucentral.gestickets.persistencia.repositorios.TicketRepositorio;
import co.ucentral.gestickets.servicios.TicketServicio;
import co.ucentral.gestickets.dto.TicketDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import co.ucentral.gestickets.persistencia.entidades.EstadoTicket;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tickets")
public class TicketControlador {

    @Autowired
    private TicketRepositorio ticketRepositorio;

    @Autowired
    private TicketServicio ticketServicio;

    // Ruta para crear un ticket
    @PostMapping("/create")
    public Ticket crearTicket(@RequestBody TicketDto ticketDto, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            throw new RuntimeException("Debes iniciar sesión.");
        }

        Ticket ticket = new Ticket();
        ticket.setTitulo(ticketDto.getTitulo());
        ticket.setDescripcion(ticketDto.getDescripcion());
        ticket.setCategoria(ticketDto.getCategoria());  // Esto debería funcionar si el DTO está correcto
        ticket.setEstado(EstadoTicket.ABIERTO);  // Estado por defecto
        ticket.setCreador(usuario);  // Establecer el creador

        return ticketRepositorio.save(ticket);
    }

    // Ruta para editar un ticket (solo lo puede hacer el creador)
    @PutMapping("/edit/{id}")
    public Object editarTicket(@PathVariable Long id, @RequestBody TicketDto ticketDto, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "Debes iniciar sesión.";
        }

        Optional<Ticket> ticketOptional = ticketRepositorio.findById(id);
        if (ticketOptional.isEmpty()) {
            return "El ticket no existe.";
        }

        Ticket ticketExistente = ticketOptional.get();

        // Verificar que el usuario que está haciendo la modificación es el creador
        if (!ticketExistente.getCreador().getId().equals(usuario.getId())) {
            return "No puedes modificar un ticket que no creaste.";
        }

        // Actualizar los campos permitidos
        ticketExistente.setTitulo(ticketDto.getTitulo());
        ticketExistente.setDescripcion(ticketDto.getDescripcion());
        ticketExistente.setCategoria(ticketDto.getCategoria());

        return ticketRepositorio.save(ticketExistente);
    }

    // Ruta para ver todos los tickets creados por el usuario
    @GetMapping("/created-by-me")
    public Object verTicketsCreados(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "Debes iniciar sesión.";
        }

        // Obtener los tickets creados por el usuario
        return ticketRepositorio.findByCreador(usuario);
    }

    // Ruta para ver todos los tickets asignados a un gestor
    @GetMapping("/my")
    public Object verMisTickets(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "Debes iniciar sesión.";
        }

        // Solo los gestores pueden ver sus tickets asignados
        if (usuario.getRol() != Usuario.Rol.GESTOR) {
            return "Acceso denegado. Solo los GESTORES pueden ver sus tickets.";
        }

        return ticketRepositorio.findByGestorAsignado(usuario);
    }

    // Ruta para asignar un ticket a un gestor
    @PostMapping("/assign/{id}")
    public Object asignarTicket(@PathVariable Long id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "Debes iniciar sesión.";
        }

        // Verificar que solo los gestores pueden asignarse tickets
        if (usuario.getRol() != Usuario.Rol.GESTOR) {
            return "Acceso denegado. Solo los GESTORES pueden asignarse tickets.";
        }

        // Asignar ticket usando el servicio
        Optional<Ticket> asignada = ticketServicio.asignarSolicitud(id, usuario);

        if (asignada.isPresent()) {
            return "Ticket asignado correctamente.";
        } else {
            return "El ticket ya fue asignado o no existe.";
        }
    }

    // Ruta para ver todos los tickets (solo accesible para gestores)
    @GetMapping("/all")
    public Object verTodosLosTickets(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "Debes iniciar sesión.";
        }

        // Solo los gestores pueden ver todos los tickets
        if (usuario.getRol() != Usuario.Rol.GESTOR) {
            return "Acceso denegado. Solo los GESTORES pueden ver todos los tickets.";
        }

        List<Ticket> tickets = ticketRepositorio.findAll();
        return tickets;
    }
    @DeleteMapping("/delete/{id}")
    public Object eliminarTicket(@PathVariable Long id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "Debes iniciar sesión.";
        }

        Optional<Ticket> ticketOptional = ticketRepositorio.findById(id);
        if (ticketOptional.isEmpty()) {
            return "El ticket no existe.";
        }

        Ticket ticket = ticketOptional.get();

        // Verificar que el usuario es el creador del ticket
        if (!ticket.getCreador().getId().equals(usuario.getId())) {
            return "No puedes eliminar un ticket que no creaste.";
        }

        ticketRepositorio.delete(ticket);
        return "Ticket eliminado correctamente.";
    }
}
