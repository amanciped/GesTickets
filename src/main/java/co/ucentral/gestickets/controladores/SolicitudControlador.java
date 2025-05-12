package co.ucentral.gestickets.controladores;

import co.ucentral.gestickets.persistencia.entidades.Solicitud;
import co.ucentral.gestickets.persistencia.repositorios.SolicitudRepositorio;
import co.ucentral.gestickets.servicios.SolicitudServicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/solicitudes")
public class SolicitudControlador {

    private final SolicitudServicio servicio;

    @Autowired
    private SolicitudRepositorio solicitudRepositorio;

    public SolicitudControlador(SolicitudServicio servicio) {
        this.servicio = servicio;
    }

    // Crear solicitud (formato JSON)
    @PostMapping
    @ResponseBody
    public Solicitud crear(@RequestBody Solicitud solicitud) {
        return servicio.crear(solicitud);
    }

    // Listar solicitudes (formato JSON)
    @GetMapping
    @ResponseBody
    public List<Solicitud> listar() {
        return servicio.listar();
    }

    // Mostrar formulario para crear
    @GetMapping("/formulario")
    public String mostrarFormulario(Model model) {
        model.addAttribute("solicitud", new Solicitud());
        return "formulario";
    }

    // Mostrar lista en HTML
    @GetMapping("/ver")
    public String listarSolicitudes(Model model) {
        List<Solicitud> solicitudes = servicio.listar();
        model.addAttribute("solicitudes", solicitudes);
        return "lista";
    }

    // Mostrar formulario de edición
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Solicitud solicitud = solicitudRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("solicitud", solicitud);
        return "formulario";
    }

    // Procesar formulario de actualización
    @PostMapping("/actualizar/{id}")
    public String actualizarSolicitud(@PathVariable Long id, @ModelAttribute Solicitud solicitudActualizada) {
        Solicitud solicitud = solicitudRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));

        solicitud.setTitulo(solicitudActualizada.getTitulo());
        solicitud.setDescripcion(solicitudActualizada.getDescripcion());
        solicitud.setPrioridad(solicitudActualizada.getPrioridad());
        solicitud.setEstado(solicitudActualizada.getEstado());

        solicitudRepositorio.save(solicitud);
        return "redirect:/solicitudes/ver";
    }
    @GetMapping("/eliminar/{id}")
    public String eliminarSolicitud(@PathVariable Long id) {
        solicitudRepositorio.deleteById(id);
        return "redirect:/solicitudes/ver";
    }
}
