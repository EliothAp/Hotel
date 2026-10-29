/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hotel.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author rueda
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "empresa")
public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empresa")
    private Integer idEmpresa;

    @Column(nullable = false, unique = true, length = 35)
    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(max = 35, message = "El nombre no puede tener más de 35 caracteres.")
    private String nombre;

    @Column(nullable = false, length = 80)
    @NotBlank(message = "El correo no puede estar vacío.")
    @Email(message = "Debe proporcionar un correo electrónico válido.")
    @Size(max = 80, message = "El correo no puede tener más de 80 caracteres.")
    private String correo;

    @Column(nullable = false, length = 8)
    @NotBlank(message = "El teléfono no puede estar vacío.")
    @Size(min = 8, max = 8, message = "El teléfono debe tener exactamente 8 caracteres.")
    @Pattern(regexp = "^[0-9]+$", message = "El teléfono solo debe contener números.")
    private String telefono;

    @Column(nullable = false)
    private boolean activo = true;
    
    
}
