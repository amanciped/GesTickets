package co.ucentral.gestickets.persistencia.entidades;

import co.ucentral.gestickets.enums.Categoria;
import co.ucentral.gestickets.enums.EstadoTicket;
import co.ucentral.gestickets.enums.Prioridad;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "solicitud")
@Getter
@Setter
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridad prioridad;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTicket estado;

    @ManyToOne
    @JoinColumn(name = "gestor_id")
    private Usuario gestorAsignado;

    @ManyToOne
    @JoinColumn(name = "creador_id", nullable = false)
    private Usuario creador;
}