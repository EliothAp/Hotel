package com.hotel.controller;

import com.hotel.domain.Usuario;
import com.hotel.service.UsuarioService;
import com.hotel.service.HabitacionService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cliente")
public class ClienteController {

    private final UsuarioService usuarioService;
    private final HabitacionService habitacionService;

    @GetMapping("/inicio")
    public String inicio(Principal principal, Model model) {

        if (principal == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debe iniciar sesión.");
        }

        Usuario usuario = usuarioService.getUsuarioPorCorreo(
                principal.getName());

        if (usuario == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No se encontró el usuario.");
        }

        if (usuario.getRol() == null
                || !"CLIENTE".equals(usuario.getRol().getNombre())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Esta página es solamente para clientes.");
        }

        if (usuario.getEmpresa() == null
                || !usuario.getEmpresa().isActivo()) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El hotel asociado al usuario no está disponible.");
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("empresa", usuario.getEmpresa());

        model.addAttribute("habitaciones",
                habitacionService.getHabitaciones(usuario.getEmpresa().getIdEmpresa()));

        return "cliente/inicio";
    }
}
