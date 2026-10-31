package com.hotel.controller;

import com.hotel.service.EmpresaService;
import com.hotel.service.ServicioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class IndexController {

    // Las últimas versiones de Spring, recomiendan utilziar final y contructor en lugar de @autowired
    private final EmpresaService empresaService;
    private final ServicioService servicioService;

    // (Spring inyecta automáticamente)
    public IndexController(EmpresaService empresaService, ServicioService servicioService) {
        this.empresaService = empresaService;
        this.servicioService = servicioService;
    }

    @GetMapping("/")
    public String cargarPaginaInicio(Model model) {
        var lista = empresaService.getEmpresas(true);
        model.addAttribute("empresas", lista);
        var servicios = servicioService.getServicios(true);
        model.addAttribute("servicios", servicios);
        return "index";
    }

    @GetMapping("/consultas/{idEmpresa}")
    public String listado(@PathVariable("idEmpresa") Integer idEmpresa, Model model) {
        model.addAttribute("idEmpresaActual", idEmpresa);
        var empresaOptional = empresaService.getEmpresa(idEmpresa);
        if (empresaOptional.isEmpty()) {
            //Puede ser que no exista el hotel buscado...
            model.addAttribute("servicios", java.util.Collections.emptyList());
        } else {
            var empresa = empresaOptional.get();
            model.addAttribute("servicios", empresa.getServicios());
        }
        var empresas = empresaService.getEmpresas(true);
        model.addAttribute("empresas", empresas);
        return "index";
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }
}
