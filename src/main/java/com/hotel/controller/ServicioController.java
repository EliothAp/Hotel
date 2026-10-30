/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hotel.controller;

import com.hotel.domain.Servicio;
import com.hotel.service.ServicioService;
import jakarta.validation.Valid;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author rueda
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/servicio")
public class ServicioController {
    private final ServicioService servicioService;
    private final MessageSource messageSource;

    @GetMapping("/listado")
    public String listado(Model model) {
        var servicios = servicioService.getServicios(false);
        model.addAttribute("servicios", servicios);
        model.addAttribute("totalServicios", servicios.size());
        return "servicio/listado";
    }

@PostMapping("/guardar")
public String guardar(@Valid Servicio servicio, BindingResult bindingResult,
        RedirectAttributes redirectAttributes) {
    
    String titulo = "todoOk";
    String mensaje = "mensaje.actualizado";
    
    if (bindingResult.hasErrors()) {
        titulo = "error";
        mensaje = "servicio.error04";
    } else {
        try {
            servicioService.save(servicio);
        } catch (DataIntegrityViolationException e) {
            titulo = "error";
            mensaje = "servicio.error05";
            log.error("Error al guardar el servicio: ", e);
        }
    }
    
    redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(mensaje, null, LocaleContextHolder.getLocale()));
    return "redirect:/servicio/listado";
}

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idServicio, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            servicioService.delete(idServicio);
        } catch (IllegalArgumentException e) {
            titulo = "error"; // Captura la excepción de argumento inválido para el mensaje de "no existe"
            detalle = "servicio.error01";
        } catch (IllegalStateException e) {
            titulo = "error"; // Captura la excepción de estado ilegal para el mensaje de "datos asociados"
            detalle = "servicio.error02";
        } catch (Exception e) {
            titulo = "error";  // Captura cualquier otra excepción inesperada
            detalle = "servicio.error03";
        }
        
        redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(detalle, null, LocaleContextHolder.getLocale()));
        return "redirect:/servicio/listado";
    }
}