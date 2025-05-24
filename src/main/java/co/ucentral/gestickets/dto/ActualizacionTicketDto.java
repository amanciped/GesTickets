package co.ucentral.gestickets.dto;

import co.ucentral.gestickets.enums.EstadoTicket;
import co.ucentral.gestickets.enums.Prioridad;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualizacionTicketDto {
    private Prioridad prioridad;
    private EstadoTicket estado;
}