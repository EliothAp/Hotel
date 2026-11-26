package com.hotel.controller;

import com.hotel.domain.Reserva;
import com.hotel.domain.Usuario;
import com.hotel.service.ReservaService;
import com.hotel.service.UsuarioService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final UsuarioService usuarioService;
    private final MessageSource messageSource;

    private Usuario obtenerAdmin(Principal principal) {

        Usuario usuario
                = usuarioService.getUsuarioPorCorreo(principal.getName());

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

    @GetMapping("/listado")
    public String listado(Principal principal, Model model) {

        Usuario usuario = obtenerAdmin(principal);
        var empresa = usuario.getEmpresa();

        List<Reserva> reservas
                = reservaService.getReservas(empresa.getIdEmpresa());

        long pendientes = reservas.stream()
                .filter(r -> ReservaService.PENDIENTE.equals(r.getEstado()))
                .count();
        long confirmadas = reservas.stream()
                .filter(r -> ReservaService.CONFIRMADA.equals(r.getEstado()))
                .count();
        long canceladas = reservas.stream()
                .filter(r -> ReservaService.CANCELADA.equals(r.getEstado()))
                .count();

        model.addAttribute("empresa", empresa);
        model.addAttribute("reservas", reservas);
        model.addAttribute("totalPendientes", pendientes);
        model.addAttribute("totalConfirmadas", confirmadas);
        model.addAttribute("totalCanceladas", canceladas);

        return "reserva/listado";
    }

    @PostMapping("/confirmar")
    public String confirmar(
            @RequestParam Integer idReserva,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = obtenerAdmin(principal);
        validarReservaDelHotel(idReserva, usuario);

        String titulo = "todoOk";
        String mensaje = "reserva.confirmada";

        try {
            reservaService.confirmar(idReserva);

        } catch (IllegalArgumentException e) {
            titulo = "error";
            mensaje = "reserva.error.noExiste";

        } catch (IllegalStateException e) {
            titulo = "error";
            mensaje = "reserva.error.confirmar";
        }

        agregarMensaje(redirectAttributes, titulo, mensaje);
        return "redirect:/admin/reservas/listado";
    }

    @PostMapping("/cancelar")
    public String cancelar(
            @RequestParam Integer idReserva,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = obtenerAdmin(principal);
        validarReservaDelHotel(idReserva, usuario);

        String titulo = "todoOk";
        String mensaje = "reserva.cancelada";

        try {
            reservaService.cancelar(idReserva);

        } catch (IllegalArgumentException e) {
            titulo = "error";
            mensaje = "reserva.error.noExiste";

        } catch (IllegalStateException e) {
            titulo = "error";
            mensaje = "reserva.error.cancelar";
        }

        agregarMensaje(redirectAttributes, titulo, mensaje);
        return "redirect:/admin/reservas/listado";
    }

    private void validarReservaDelHotel(Integer idReserva, Usuario usuario) {

        var reservaOptional = reservaService.getReserva(idReserva);

        if (reservaOptional.isPresent()) {

            Integer idEmpresaReserva
                    = reservaOptional.get().getEmpresa().getIdEmpresa();

            if (!usuario.getEmpresa().getIdEmpresa().equals(idEmpresaReserva)) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "La reserva no pertenece a su hotel.");
            }
        }
    }

    private void agregarMensaje(RedirectAttributes redirectAttributes,
            String titulo, String clave) {

        redirectAttributes.addFlashAttribute(
                titulo,
                messageSource.getMessage(
                        clave,
                        null,
                        LocaleContextHolder.getLocale()));
    }
}
