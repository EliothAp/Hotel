package com.hotel.controller;

import com.hotel.domain.TipoHabitacion;
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
@RequestMapping("/tipoHabitacion")
public class TipoHabitacionController {

    private final TipoHabitacionService tipoHabitacionService;
    private final MessageSource messageSource;

    @GetMapping("/listado")
    public String listado(@RequestParam Integer idEmpresa, Model model) {
        var tiposHabitacion = tipoHabitacionService.getTiposHabitacion(idEmpresa);
        model.addAttribute("idEmpresa", idEmpresa);
        model.addAttribute("tiposHabitacion", tiposHabitacion);
        model.addAttribute("totalTiposHabitacion", tiposHabitacion.size());
        return "tipoHabitacion/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid TipoHabitacion tipoHabitacion, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String mensaje = "mensaje.actualizado";
        Integer idEmpresa = tipoHabitacion.getEmpresa().getIdEmpresa();
        if (bindingResult.hasErrors()) {
            titulo = "error";
            mensaje = "tipoHabitacion.error04";
        } else {
            try {
                tipoHabitacionService.save(tipoHabitacion);
            } catch (DataIntegrityViolationException e) {
                titulo = "error";
                mensaje = "tipoHabitacion.error05";
                log.error("" + e);
            }
        }
        redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(mensaje, null, Locale.getDefault()));
        return "redirect:/tipoHabitacion/listado?idEmpresa=" + idEmpresa;
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idTipoHabitacion, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        //Se busca el hotel del tipo de habitacion antes de eliminarlo, para regresar a su listado
        Integer idEmpresa = tipoHabitacionService.getTipoHabitacion(idTipoHabitacion)
                .get().getEmpresa().getIdEmpresa();
        try {
            tipoHabitacionService.delete(idTipoHabitacion);
        } catch (IllegalArgumentException e) {
            titulo = "error"; // Captura la excepción de argumento inválido para el mensaje de "no existe"
            detalle = "tipoHabitacion.error01";
        } catch (IllegalStateException e) {
            titulo = "error"; // Captura la excepción de estado ilegal para el mensaje de "datos asociados"
            detalle = "tipoHabitacion.error02";
        } catch (Exception e) {
            titulo = "error";  // Captura cualquier otra excepción inesperada
            detalle = "tipoHabitacion.error03";
        }
        redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/tipoHabitacion/listado?idEmpresa=" + idEmpresa;
    }
}
