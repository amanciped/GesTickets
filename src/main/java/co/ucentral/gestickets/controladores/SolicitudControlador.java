package co.ucentral.gestickets.controladores;

import co.ucentral.gestickets.persistencia.entidades.Solicitud;
import co.ucentral.gestickets.servicios.SolicitudServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/solicitudes")
public class SolicitudControlador {

    private final SolicitudServicio servicio;

    public SolicitudControlador(SolicitudServicio servicio) {
        this.servicio = servicio;
    }

    // Endpoint REST para crear solicitud (responde en JSON)
    @PostMapping
    @ResponseBody
    public Solicitud crear(@RequestBody Solicitud solicitud) {
        return servicio.crear(solicitud);
    }

    // Endpoint REST para listar solicitudes en formato JSON
    @GetMapping
    @ResponseBody
    public List<Solicitud> listar() {
        return servicio.listar();
    }

    // Muestra el formulario HTML para crear una solicitud
    @GetMapping("/formulario")
    public String mostrarFormulario(Model model) {
        model.addAttribute("solicitud", new Solicitud());
        return "formulario"; // busca templates/formulario.html
    }

    // Muestra todas las solicitudes en una tabla HTML
    @GetMapping("/ver")
    public String listarSolicitudes(Model model) {
        List<Solicitud> solicitudes = servicio.listar();
        model.addAttribute("solicitudes", solicitudes);
        return "lista"; // busca templates/lista.html
    }
}
