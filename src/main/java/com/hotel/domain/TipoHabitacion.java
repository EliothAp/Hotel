package com.hotel.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tipo_habitacion",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_empresa", "nombre"}))
public class TipoHabitacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_habitacion")
    private Integer idTipoHabitacion;

    //Cada tipo de habitacion pertenece a un solo hotel (empresa)
    @ManyToOne
    @JoinColumn(name = "id_empresa", nullable = false)
    @NotNull(message = "La empresa no puede estar vacía.")
    private Empresa empresa;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres.")
    private String nombre;

    @NotNull(message = "La capacidad no puede estar vacía.")
    @Min(value = 1, message = "La capacidad debe ser un número mayor o igual a 1.")
    private Integer capacidad;

    @Column(name = "precio_noche", precision = 12, scale = 2)
    @NotNull(message = "El precio por noche no puede estar vacío.")
    @DecimalMin(value = "0.01", inclusive = true, message = "El precio por noche debe ser mayor a 0.")
    private BigDecimal precioNoche;

}
