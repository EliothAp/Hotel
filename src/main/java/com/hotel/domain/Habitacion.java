package com.hotel.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "habitacion",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_empresa", "numero"}))
public class Habitacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_habitacion")
    private Integer idHabitacion;

    //Cada habitacion pertenece a un solo hotel (empresa)
    @ManyToOne
    @JoinColumn(name = "id_empresa", nullable = false)
    @NotNull(message = "La empresa no puede estar vacía.")
    private Empresa empresa;

    //Cada habitacion es de un tipo de habitacion
    @ManyToOne
    @JoinColumn(name = "id_tipo_habitacion", nullable = false)
    @NotNull(message = "El tipo de habitación no puede estar vacío.")
    private TipoHabitacion tipoHabitacion;

    @NotNull(message = "El número no puede estar vacío.")
    @Min(value = 1, message = "El número debe ser mayor o igual a 1.")
    private Integer numero;

    //Estados: DISPONIBLE, OCUPADA o MANTENIMIENTO
    @Column(nullable = false, length = 20)
    @NotBlank(message = "El estado no puede estar vacío.")
    @Size(max = 20, message = "El estado no puede tener más de 20 caracteres.")
    private String estado;

    @Column(name = "ruta_imagen", length = 1024)
    private String rutaImagen;

}
