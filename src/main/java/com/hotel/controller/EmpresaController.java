/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hotel.controller;

import com.hotel.domain.Empresa;
import com.hotel.service.EmpresaService;
import jakarta.validation.Valid;
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
@RequestMapping("/empresa")
public class EmpresaController {
    private final EmpresaService empresaService;
    private final MessageSource messageSource;

    @GetMapping("/listado")
    public String listado(Model model) {
        var empresas = empresaService.getEmpresas(false);
        model.addAttribute("empresas", empresas);
        model.addAttribute("totalEmpresas", empresas.size());
        return "empresa/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Empresa empresa, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        
        String titulo = "todoOk";
        String mensaje = "mensaje.actualizado";
        
        if (bindingResult.hasErrors()) {
            titulo = "error";
            mensaje = "empresa.error04";
        } else {
            try {
                empresaService.save(empresa);
            } catch (DataIntegrityViolationException e) {
                titulo = "error";
                mensaje = "empresa.error05";
                log.error(""+e);
            }
        }
        redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(mensaje, null, LocaleContextHolder.getLocale()));
        return "redirect:/empresa/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idEmpresa, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            empresaService.delete(idEmpresa);
        } catch (IllegalArgumentException e) {
            titulo = "error"; // Captura la excepción de argumento inválido para el mensaje de "no existe"
            detalle = "empresa.error01";
        } catch (IllegalStateException e) {
            titulo = "error"; // Captura la excepción de estado ilegal para el mensaje de "datos asociados"
            detalle = "empresa.error02";
        } catch (Exception e) {
            titulo = "error";  // Captura cualquier otra excepción inesperada
            detalle = "empresa.error03";
        }
        redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(detalle, null, LocaleContextHolder.getLocale()));
        return "redirect:/empresa/listado";
    }
}
