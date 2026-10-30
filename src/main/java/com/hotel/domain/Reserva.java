package com.hotel.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "reserva")
public class Reserva implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;

    @ManyToOne
    @JoinColumn(name = "id_empresa", nullable = false)
    @NotNull(message = "La empresa no puede estar vacía.")
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "id_habitacion", nullable = false)
    @NotNull(message = "La habitación no puede estar vacía.")
    private Habitacion habitacion;

    @Column(name = "fecha_entrada", nullable = false)
    @NotNull(message = "La fecha de entrada no puede estar vacía.")
    private LocalDate fechaEntrada;

    @Column(name = "fecha_salida", nullable = false)
    @NotNull(message = "La fecha de salida no puede estar vacía.")
    private LocalDate fechaSalida;

    //Estados: PENDIENTE, CONFIRMADA o CANCELADA
    @Column(nullable = false, length = 20)
    private String estado;

    @Column(precision = 12, scale = 2)
    private BigDecimal total;

}
