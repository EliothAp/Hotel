package com.hotel.controller;

import com.hotel.service.EmpresaService;
import com.hotel.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author rueda
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/superadmin")
public class SuperAdminController {

    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;

    // Misma propiedad que usa UsuarioController para habilitar la gestión de administradores
    @Value("${hotel.admin.registration.enabled:false}")
    private boolean administracionHabilitada;

    // Panel principal del SUPERADMIN
    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
        // Solo el SUPERADMIN puede ver este panel
        if (!request.isUserInRole("SUPERADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo el superadministrador puede ver este panel.");
        }

        var empresas = empresaService.getEmpresas(false);
        var activas = empresaService.getEmpresas(true);
        var administradores = usuarioService.getUsuariosPorRol("ADMIN");

        model.addAttribute("empresas", empresas);
        model.addAttribute("totalEmpresas", empresas.size());
        model.addAttribute("totalActivas", activas.size());
        model.addAttribute("totalInactivas", empresas.size() - activas.size());
        model.addAttribute("administradores", administradores);
        model.addAttribute("totalAdministradores", administradores.size());
        model.addAttribute("administracionHabilitada", administracionHabilitada);
        return "superadmin/dashboard";
    }
}
