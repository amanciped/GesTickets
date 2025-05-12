package co.ucentral.gestickets.servicios;

import co.ucentral.gestickets.persistencia.entidades.Ticket;
import co.ucentral.gestickets.persistencia.entidades.Usuario;
import co.ucentral.gestickets.persistencia.repositorios.TicketRepositorio;
import co.ucentral.gestickets.persistencia.entidades.EstadoTicket;
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

        if (solicitudOpt.isPresent()) {
            Ticket solicitud = solicitudOpt.get();
            if (solicitud.getGestorAsignado() == null) {
                solicitud.setGestorAsignado(gestor);
                solicitud.setEstado(EstadoTicket.EN_ESPERA);
                return Optional.of(repositorio.save(solicitud));
            }
        }
        return Optional.empty(); // Ya fue asignado o no existe
    }
}