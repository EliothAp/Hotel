/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hotel.domain;

/**
 *
 * @author Elioth Artavia
 */
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres.")
    private String nombre;

    @Column(nullable = false, unique = true, length = 80)
    @NotBlank(message = "El correo no puede estar vacío.")
    @Email(message = "Debe proporcionar un correo electrónico válido.")
    @Size(max = 80, message = "El correo no puede tener más de 80 caracteres.")
    private String correo;

    @Column(nullable = false, length = 8)
    @NotBlank(message = "El teléfono no puede estar vacío.")
    @Size(min = 8, max = 8,
            message = "El teléfono debe tener exactamente 8 caracteres.")
    @Pattern(regexp = "^[0-9]+$",
            message = "El teléfono solo debe contener números.")
    private String telefono;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "La contraseña no puede estar vacía.")
    private String contrasena;

    @Column(nullable = false)
    private boolean activo = true;

    // Cada usuario pertenece a una empresa (hotel)
    @ManyToOne
    @JoinColumn(name = "id_empresa", nullable = true)
    private Empresa empresa;

    // Cada usuario tiene un rol
    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    @NotNull(message = "El rol no puede estar vacío.")
    private Role role;

}
