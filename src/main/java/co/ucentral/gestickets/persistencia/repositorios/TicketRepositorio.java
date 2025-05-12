package co.ucentral.gestickets.persistencia.repositorios;

import co.ucentral.gestickets.persistencia.entidades.Ticket;
import co.ucentral.gestickets.persistencia.entidades.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepositorio extends JpaRepository<Ticket, Long> {
    List<Ticket> findByGestorAsignadoIsNull();
    List<Ticket> findByGestorAsignado(Usuario gestorAsignado);
    List<Ticket> findByCreador(Usuario creador);

}

