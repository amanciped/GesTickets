package co.ucentral.gestickets.servicios;

import co.ucentral.gestickets.persistencia.entidades.Solicitud;
import co.ucentral.gestickets.persistencia.repositorios.SolicitudRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolicitudServicio {

    private final SolicitudRepositorio repositorio;

    public SolicitudServicio(SolicitudRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public Solicitud crear(Solicitud solicitud) {
        return repositorio.save(solicitud);
    }

    public List<Solicitud> listar() {
        return repositorio.findAll();
    }
}