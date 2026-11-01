package com.hotel.controller;

import com.hotel.domain.Usuario;
import com.hotel.service.EmpresaService;
import com.hotel.service.ServicioService;
import com.hotel.service.UsuarioService;
import java.security.Principal;
import java.util.Collections;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class IndexController {

    private final EmpresaService empresaService;
    private final ServicioService servicioService;
    private final UsuarioService usuarioService;

    public IndexController(
            EmpresaService empresaService,
            ServicioService servicioService,
            UsuarioService usuarioService) {

        this.empresaService = empresaService;
        this.servicioService = servicioService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String cargarPaginaInicio(Model model) {

        model.addAttribute(
                "empresas",
                empresaService.getEmpresas(true));

        model.addAttribute(
                "servicios",
                servicioService.getServicios(true));

        return "index";
    }

    @GetMapping("/consultas/{idEmpresa}")
    public String listado(
            @PathVariable Integer idEmpresa,
            Model model) {

        model.addAttribute("idEmpresaActual", idEmpresa);

        var empresaOptional = empresaService.getEmpresa(idEmpresa);

        if (empresaOptional.isEmpty()) {
            model.addAttribute(
                    "servicios",
                    Collections.emptyList());
        } else {
            model.addAttribute(
                    "servicios",
                    empresaOptional.get().getServicios());
        }

        model.addAttribute(
                "empresas",
                empresaService.getEmpresas(true));

        return "index";
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @GetMapping("/inicio-rol")
    public String inicioPorRol(Principal principal) {

        Usuario usuario
                = usuarioService.getUsuarioPorCorreo(principal.getName());

        if (usuario == null || usuario.getRol() == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No se encontró el usuario o su rol.");
        }

        String rol = usuario.getRol().getNombre();

        switch (rol) {
            case "SUPERADMIN":
                return "redirect:/superadmin/dashboard";

            case "ADMIN":
                return "redirect:/admin/dashboard";

            case "CLIENTE":
                return "redirect:/cliente/inicio";

            default:
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "El usuario no tiene un rol válido.");
        }
    }
}
