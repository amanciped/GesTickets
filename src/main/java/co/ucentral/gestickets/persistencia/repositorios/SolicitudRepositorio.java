package co.ucentral.gestickets.persistencia.repositorios;

import co.ucentral.gestickets.persistencia.entidades.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitudRepositorio extends JpaRepository<Solicitud, Long> {
}

