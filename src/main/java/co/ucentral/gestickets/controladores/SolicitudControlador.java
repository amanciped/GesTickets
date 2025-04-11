package co.ucentral.gestickets.controladores;

import co.ucentral.gestickets.persistencia.entidades.Solicitud;
import co.ucentral.gestickets.servicios.SolicitudServicio;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitudes")
public class SolicitudControlador {

    private final SolicitudServicio servicio;

    public SolicitudControlador(SolicitudServicio servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public Solicitud crear(@RequestBody Solicitud solicitud) {
        return servicio.crear(solicitud);
    }

    @GetMapping
    public List<Solicitud> listar() {
        return servicio.listar();
    }
}
