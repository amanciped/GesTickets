package co.ucentral.gestickets.dto;

import co.ucentral.gestickets.persistencia.entidades.Usuario.Rol;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDto {
    private String username;
    private String password;
    private String nacionalidad;
    private Rol rol;
}
