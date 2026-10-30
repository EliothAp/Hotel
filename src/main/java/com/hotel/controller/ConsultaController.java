package com.hotel.controller;

import com.hotel.service.EmpresaService;
import com.hotel.service.HabitacionService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    private final EmpresaService empresaService;
    private final HabitacionService habitacionService;

    public ConsultaController(EmpresaService empresaService, HabitacionService habitacionService) {
        this.empresaService = empresaService;
        this.habitacionService = habitacionService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var empresas = empresaService.getEmpresas(true);
        model.addAttribute("empresas", empresas);
        return "/consultas/listado";
    }

    @PostMapping("/consultaLibres")
    public String consultaLibres(@RequestParam() Integer idEmpresa,
            @RequestParam() @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEntrada,
            @RequestParam() @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida,
            @RequestParam() Integer personas,
            Model model) {
        var empresas = empresaService.getEmpresas(true);
        model.addAttribute("empresas", empresas);
        var habitaciones = habitacionService.consultaLibres(idEmpresa, personas, fechaEntrada, fechaSalida);
        model.addAttribute("habitaciones", habitaciones);
        model.addAttribute("empresaBuscada", empresaService.getEmpresa(idEmpresa).get());
        model.addAttribute("fechaEntrada", fechaEntrada);
        model.addAttribute("fechaSalida", fechaSalida);
        model.addAttribute("personas", personas);
        return "/consultas/listado";
    }
}
