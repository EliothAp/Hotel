package com.hotel.controller;

import com.hotel.domain.Habitacion;
import com.hotel.service.HabitacionService;
import com.hotel.service.TipoHabitacionService;
import jakarta.validation.Valid;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/habitacion")
public class HabitacionController {

    private final HabitacionService habitacionService;
    private final TipoHabitacionService tipoHabitacionService;
    private final MessageSource messageSource;

    @GetMapping("/listado")
    public String listado(@RequestParam Integer idEmpresa, Model model) {
        var habitaciones = habitacionService.getHabitaciones(idEmpresa);
        model.addAttribute("idEmpresa", idEmpresa);
        model.addAttribute("habitaciones", habitaciones);
        model.addAttribute("totalHabitaciones", habitaciones.size());
        var tiposHabitacion = tipoHabitacionService.getTiposHabitacion(idEmpresa);
        model.addAttribute("tiposHabitacion", tiposHabitacion);
        return "habitacion/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Habitacion habitacion, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String mensaje = "mensaje.actualizado";
        Integer idEmpresa = habitacion.getEmpresa().getIdEmpresa();
        if (bindingResult.hasErrors()) {
            titulo = "error";
            mensaje = "habitacion.error04";
        } else {
            try {
                habitacionService.save(habitacion);
            } catch (DataIntegrityViolationException e) {
                titulo = "error";
                mensaje = "habitacion.error05";
                log.error("" + e);
            }
        }
        redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(mensaje, null, Locale.getDefault()));
        return "redirect:/habitacion/listado?idEmpresa=" + idEmpresa;
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idHabitacion, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        //Se busca el hotel de la habitacion antes de eliminarla, para regresar a su listado
        Integer idEmpresa = habitacionService.getHabitacion(idHabitacion)
                .get().getEmpresa().getIdEmpresa();
        try {
            habitacionService.delete(idHabitacion);
        } catch (IllegalArgumentException e) {
            titulo = "error"; // Captura la excepción de argumento inválido para el mensaje de "no existe"
            detalle = "habitacion.error01";
        } catch (IllegalStateException e) {
            titulo = "error"; // Captura la excepción de estado ilegal para el mensaje de "datos asociados"
            detalle = "habitacion.error02";
        } catch (Exception e) {
            titulo = "error";  // Captura cualquier otra excepción inesperada
            detalle = "habitacion.error03";
        }
        redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/habitacion/listado?idEmpresa=" + idEmpresa;
    }
}
