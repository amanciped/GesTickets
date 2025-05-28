package co.ucentral.gestickets.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class InicioControlador {

    @GetMapping("/")
    @ResponseBody
    public String inicio() {
        return "Bienvenido a Gestickets 🚀";
    }
}
