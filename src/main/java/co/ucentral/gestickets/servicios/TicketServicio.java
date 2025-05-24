package co.ucentral.gestickets.servicios;

import co.ucentral.gestickets.persistencia.entidades.Ticket;
import co.ucentral.gestickets.persistencia.entidades.Usuario;
import co.ucentral.gestickets.persistencia.repositorios.TicketRepositorio;
import co.ucentral.gestickets.enums.EstadoTicket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketServicio {

    private final TicketRepositorio repositorio;

    public Ticket crear(Ticket solicitud) {
        return repositorio.save(solicitud);
    }

    public List<Ticket> listar() {
        return repositorio.findAll();
    }

    public Optional<Ticket> asignarSolicitud(Long solicitudId, Usuario gestor) {
        Optional<Ticket> solicitudOpt = repositorio.findById(solicitudId);

        if (solicitudOpt.isEmpty()) {
            return Optional.empty(); // No existe
        }

        Ticket solicitud = solicitudOpt.get();

        if (solicitud.getGestorAsignado() != null) {
            return Optional.empty(); // Ya asignado
        }

        solicitud.setGestorAsignado(gestor);

        // Solo cambiar el estado si sigue en ABIERTO
        if (solicitud.getEstado() == EstadoTicket.ABIERTO) {
            solicitud.setEstado(EstadoTicket.EN_ESPERA);
        }

        return Optional.of(repositorio.save(solicitud));
    }
}