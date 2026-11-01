/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hotel;

/**
 *
 * @author Elioth Artavia
 */

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarClave {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String clave = "SuperHotel#2026";

        String hash = encoder.encode(clave);

        System.out.println("Contraseña: " + clave);
        System.out.println("Hash: " + hash);
    }
}