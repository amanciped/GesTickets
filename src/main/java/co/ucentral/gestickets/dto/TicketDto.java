package co.ucentral.gestickets.dto;

import co.ucentral.gestickets.enums.Categoria;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketDto {
    private String titulo;
    private String descripcion;
    private Categoria categoria;
}
