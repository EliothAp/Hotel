package com.hotel.controller;

import com.hotel.domain.Usuario;
import com.hotel.service.HabitacionService;
import com.hotel.service.TipoHabitacionService;
import com.hotel.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final HabitacionService habitacionService;
    private final TipoHabitacionService tipoHabitacionService;
    private final UsuarioService usuarioService;

    // Panel principal del ADMIN (solo muestra datos de su propio hotel)
    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Principal principal, Model model) {
        // Solo el ADMIN puede ver este panel
        if (!request.isUserInRole("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo el administrador puede ver este panel.");
        }

        Usuario usuario = usuarioService.getUsuarioPorCorreo(principal.getName());

        // El ADMIN debe tener un hotel asignado
        if (usuario == null || usuario.getEmpresa() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "El usuario no tiene un hotel asignado.");
        }

        var empresa = usuario.getEmpresa();
        var idEmpresa = empresa.getIdEmpresa();

        var habitaciones = habitacionService.getHabitaciones(idEmpresa);
        var tiposHabitacion = tipoHabitacionService.getTiposHabitacion(idEmpresa);
        var clientes = usuarioService.getClientesPorEmpresa(idEmpresa);

        model.addAttribute("usuario", usuario);
        model.addAttribute("empresa", empresa);
        model.addAttribute("habitaciones", habitaciones);
        model.addAttribute("totalHabitaciones", habitaciones.size());
        model.addAttribute("totalDisponibles", habitacionService.contarPorEstado(idEmpresa, "DISPONIBLE"));
        model.addAttribute("totalOcupadas", habitacionService.contarPorEstado(idEmpresa, "OCUPADA"));
        model.addAttribute("totalMantenimiento", habitacionService.contarPorEstado(idEmpresa, "MANTENIMIENTO"));
        model.addAttribute("tiposHabitacion", tiposHabitacion);
        model.addAttribute("totalTiposHabitacion", tiposHabitacion.size());
        model.addAttribute("totalClientes", clientes.size());
        return "admin/dashboard";
    }
}
