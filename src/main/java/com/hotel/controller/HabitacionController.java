package com.hotel.controller;

import com.hotel.domain.Habitacion;
import com.hotel.domain.Usuario;
import com.hotel.service.HabitacionService;
import com.hotel.service.TipoHabitacionService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/habitacion")
public class HabitacionController {

    private final HabitacionService habitacionService;
    private final TipoHabitacionService tipoHabitacionService;
    private final MessageSource messageSource;

    private Usuario obtenerAdmin(Principal principal) {

        Usuario usuario
                = habitacionService.getUsuario(principal.getName());

        if (usuario == null
                || usuario.getRol() == null
                || !"ADMIN".equals(usuario.getRol().getNombre())
                || usuario.getEmpresa() == null) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El usuario no tiene un hotel asignado.");
        }

        return usuario;
    }

    @GetMapping("/inicio")
    public String inicio(Principal principal) {

        Usuario usuario = obtenerAdmin(principal);

        return "redirect:/habitacion/listado?idEmpresa="
                + usuario.getEmpresa().getIdEmpresa();
    }

    @GetMapping("/listado")
    public String listado(
            @RequestParam Integer idEmpresa,
            Principal principal,
            Model model) {

        Usuario usuario = obtenerAdmin(principal);

        Integer idEmpresaAdmin
                = usuario.getEmpresa().getIdEmpresa();

        // Evita que el ADMIN consulte otro hotel cambiando la URL.
        if (!idEmpresaAdmin.equals(idEmpresa)) {
            return "redirect:/habitacion/listado?idEmpresa="
                    + idEmpresaAdmin;
        }

        var habitaciones
                = habitacionService.getHabitaciones(idEmpresaAdmin);

        model.addAttribute("idEmpresa", idEmpresaAdmin);
        model.addAttribute("habitaciones", habitaciones);
        model.addAttribute(
                "totalHabitaciones",
                habitaciones.size());

        model.addAttribute(
                "tiposHabitacion",
                tipoHabitacionService.getTiposHabitacion(
                        idEmpresaAdmin));

        return "habitacion/listado";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid Habitacion habitacion,
            BindingResult bindingResult,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = obtenerAdmin(principal);

        Integer idEmpresaAdmin
                = usuario.getEmpresa().getIdEmpresa();

        String titulo = "todoOk";
        String mensaje = "mensaje.actualizado";

        // El hotel se establece desde el usuario autenticado.
        // No se confía en el idEmpresa enviado por el formulario.
        habitacion.setEmpresa(usuario.getEmpresa());

        if (bindingResult.hasErrors()) {

            titulo = "error";
            mensaje = "habitacion.error04";

        } else {

            try {
                habitacionService.save(habitacion);

            } catch (DataIntegrityViolationException e) {

                titulo = "error";
                mensaje = "habitacion.error05";

                log.error(
                        "Error al guardar la habitación.",
                        e);
            }
        }

        redirectAttributes.addFlashAttribute(
                titulo,
                messageSource.getMessage(
                        mensaje,
                        null,
                        Locale.getDefault()));

        return "redirect:/habitacion/listado?idEmpresa="
                + idEmpresaAdmin;
    }

    @PostMapping("/eliminar")
    public String eliminar(
            @RequestParam Integer idHabitacion,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = obtenerAdmin(principal);

        Integer idEmpresaAdmin
                = usuario.getEmpresa().getIdEmpresa();

        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";

        var habitacionOptional
                = habitacionService.getHabitacion(idHabitacion);

        if (habitacionOptional.isEmpty()) {

            titulo = "error";
            detalle = "habitacion.error01";

        } else {

            Habitacion habitacion = habitacionOptional.get();

            // No permitir eliminar habitaciones de otro hotel.
            if (habitacion.getEmpresa() == null
                    || !idEmpresaAdmin.equals(
                            habitacion.getEmpresa().getIdEmpresa())) {

                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "La habitación no pertenece a su hotel.");
            }

            try {
                habitacionService.delete(idHabitacion);

            } catch (IllegalArgumentException e) {

                titulo = "error";
                detalle = "habitacion.error01";

            } catch (IllegalStateException e) {

                titulo = "error";
                detalle = "habitacion.error02";

            } catch (Exception e) {

                titulo = "error";
                detalle = "habitacion.error03";

                log.error(
                        "Error al eliminar la habitación.",
                        e);
            }
        }

        redirectAttributes.addFlashAttribute(
                titulo,
                messageSource.getMessage(
                        detalle,
                        null,
                        Locale.getDefault()));

        return "redirect:/habitacion/listado?idEmpresa="
                + idEmpresaAdmin;
    }
}
