package co.ucentral.gestickets.persistencia.entidades;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String nacionalidad;
    @Enumerated(EnumType.STRING)
    private Rol rol;

    public enum Rol {
        GESTOR, USUARIO
    }
}