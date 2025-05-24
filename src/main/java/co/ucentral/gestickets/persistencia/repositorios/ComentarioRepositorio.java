package co.ucentral.gestickets.persistencia.repositorios;

import co.ucentral.gestickets.persistencia.entidades.Comentario;
import co.ucentral.gestickets.persistencia.entidades.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComentarioRepositorio extends JpaRepository<Comentario, Long> {
    List<Comentario> findByTicket(Ticket ticket);
    void deleteByTicket(Ticket ticket);
}
