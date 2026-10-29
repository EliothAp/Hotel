package com.hotel.controller;

/**
 *
 * @author Elioth Artavia
 */
import com.hotel.domain.Empresa;
import com.hotel.domain.Rol;
import com.hotel.domain.Usuario;
import com.hotel.service.EmpresaService;
import com.hotel.service.UsuarioService;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final EmpresaService empresaService;
    private final EntityManager entityManager;

    // La administración se habilitará cuando exista
    // autenticación y autorización de SUPERADMIN.
    @Value("${hotel.admin.registration.enabled:false}")
    private boolean administracionHabilitada;

    private void verificarAdministracion() {
        if (!administracionHabilitada) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La administración de usuarios no está habilitada.");
        }
    }

    // US-02: Listado de usuarios y administradores
    @GetMapping("/listado")
    public String listado(Model model) {

        verificarAdministracion();

        List<Usuario> usuarios = usuarioService.getUsuarios(false);

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarios.size());

        return "usuario/listado";
    }

    // US-02: Formulario para crear administradores
    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        verificarAdministracion();

        model.addAttribute("usuario", new Usuario());
        model.addAttribute("empresas", empresaService.getEmpresas(true));

        return "usuario/modifica";
    }

    // US-11: Listado de clientes de un hotel
    @GetMapping("/clientes")
    public String clientes(
            @RequestParam Integer idEmpresa,
            @RequestParam(required = false, defaultValue = "") String nombre,
            Model model) {

        verificarAdministracion();

        List<Usuario> clientes;

        if (nombre.isBlank()) {
            clientes = usuarioService.getClientesPorEmpresa(idEmpresa);
        } else {
            clientes = usuarioService.buscarClientes(idEmpresa, nombre);
        }

        model.addAttribute("clientes", clientes);
        model.addAttribute("idEmpresa", idEmpresa);
        model.addAttribute("nombre", nombre);
        model.addAttribute("totalClientes", clientes.size());

        return "usuario/clientes";
    }

    // US-13: Formulario público de registro de clientes
    @GetMapping("/registro")
    public String registro(Model model) {

        model.addAttribute("usuario", new Usuario());
        model.addAttribute("empresas", empresaService.getEmpresas(true));

        return "usuario/registro";
    }

    // US-02: Guardar un nuevo administrador
    @PostMapping("/guardar")
    public String guardar(
            @RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam String contrasena,
            @RequestParam Integer idEmpresa,
            RedirectAttributes redirectAttributes) {

        verificarAdministracion();

        try {

            validarDatos(nombre, correo, telefono, contrasena);

            Empresa empresa = obtenerEmpresaActiva(idEmpresa);
            Rol rol = obtenerRol("ADMIN");

            Usuario usuario = new Usuario();

            usuario.setNombre(nombre.trim());
            usuario.setCorreo(correo.trim().toLowerCase());
            usuario.setTelefono(telefono.trim());
            usuario.setContrasena(cifrarContrasena(contrasena));
            usuario.setActivo(true);
            usuario.setEmpresa(empresa);
            usuario.setRol(rol);

            usuarioService.save(usuario);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Administrador registrado correctamente.");

            return "redirect:/usuario/listado";

        } catch (IllegalArgumentException
                | DataIntegrityViolationException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "No se pudo guardar el administrador. "
                    + "Verifique los datos y el correo.");

            return "redirect:/usuario/nuevo";
        }
    }

    // US-13: Registrar un nuevo cliente
    @PostMapping("/registrar")
    public String registrarCliente(
            @RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam String contrasena,
            @RequestParam Integer idEmpresa,
            RedirectAttributes redirectAttributes) {

        try {

            validarDatos(nombre, correo, telefono, contrasena);

            Empresa empresa = obtenerEmpresaActiva(idEmpresa);
            Rol rol = obtenerRol("CLIENTE");

            Usuario usuario = new Usuario();

            usuario.setNombre(nombre.trim());
            usuario.setCorreo(correo.trim().toLowerCase());
            usuario.setTelefono(telefono.trim());
            usuario.setContrasena(cifrarContrasena(contrasena));
            usuario.setActivo(true);
            usuario.setEmpresa(empresa);
            usuario.setRol(rol);

            usuarioService.save(usuario);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Cliente registrado correctamente.");

            return "redirect:/usuario/registro";

        } catch (IllegalArgumentException
                | DataIntegrityViolationException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "No se pudo registrar el cliente. "
                    + "Verifique los datos y el correo.");

            return "redirect:/usuario/registro";
        }
    }

    // US-02: Mostrar formulario de modificación
    @GetMapping("/modificar")
    public String modificar(
            @RequestParam Integer idUsuario,
            Model model,
            RedirectAttributes redirectAttributes) {

        verificarAdministracion();

        var usuario = usuarioService.getUsuario(idUsuario);

        if (usuario.isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "El usuario no existe.");

            return "redirect:/usuario/listado";
        }

        if (!"ADMIN".equals(usuario.get().getRol().getNombre())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo se pueden modificar administradores.");
        }

        model.addAttribute("usuario", usuario.get());
        model.addAttribute("empresas", empresaService.getEmpresas(true));

        return "usuario/modifica";
    }

    // US-02: Actualizar los datos de un administrador
    @PostMapping("/actualizar")
    public String actualizar(
            @RequestParam Integer idUsuario,
            @RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam Integer idEmpresa,
            RedirectAttributes redirectAttributes) {

        verificarAdministracion();

        try {

            Usuario usuario = usuarioService.getUsuario(idUsuario)
                    .orElseThrow(()
                            -> new IllegalArgumentException(
                            "El usuario no existe."));

            if (!"ADMIN".equals(usuario.getRol().getNombre())) {
                throw new IllegalArgumentException(
                        "Solo se pueden modificar administradores.");
            }

            validarActualizacion(
                    idUsuario, nombre, correo, telefono);

            Empresa empresa = obtenerEmpresaActiva(idEmpresa);

            usuario.setNombre(nombre.trim());
            usuario.setCorreo(correo.trim().toLowerCase());
            usuario.setTelefono(telefono.trim());
            usuario.setEmpresa(empresa);

            // Se conserva el rol y la contraseña existentes.
            usuarioService.save(usuario);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Administrador actualizado correctamente.");

            return "redirect:/usuario/listado";

        } catch (IllegalArgumentException
                | DataIntegrityViolationException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "No se pudo actualizar el administrador.");

            return "redirect:/usuario/listado";
        }
    }

    // US-02: Eliminar un administrador
    @PostMapping("/eliminar")
    public String eliminar(
            @RequestParam Integer idUsuario,
            RedirectAttributes redirectAttributes) {

        verificarAdministracion();

        try {

            Usuario usuario = usuarioService.getUsuario(idUsuario)
                    .orElseThrow(()
                            -> new IllegalArgumentException(
                            "El usuario no existe."));

            if (!"ADMIN".equals(usuario.getRol().getNombre())) {
                throw new IllegalArgumentException(
                        "Solo se pueden eliminar administradores.");
            }

            usuarioService.delete(idUsuario);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Administrador eliminado correctamente.");

        } catch (IllegalArgumentException
                | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/usuario/listado";
    }

    // Obtener un rol desde la base de datos
    private Rol obtenerRol(String nombreRol) {

        List<Rol> roles = entityManager.createQuery(
                "SELECT r FROM Rol r WHERE r.nombre = :nombre",
                Rol.class)
                .setParameter("nombre", nombreRol)
                .getResultList();

        if (roles.isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe el rol " + nombreRol);
        }

        return roles.get(0);
    }

    // Obtener un hotel y verificar que esté activo
    private Empresa obtenerEmpresaActiva(Integer idEmpresa) {

        Empresa empresa = empresaService.getEmpresa(idEmpresa)
                .orElseThrow(()
                        -> new IllegalArgumentException(
                        "El hotel seleccionado no existe."));

        if (!empresa.isActivo()) {
            throw new IllegalArgumentException(
                    "El hotel seleccionado está inactivo.");
        }

        return empresa;
    }

    // Validar los datos de registro
    private void validarDatos(
            String nombre,
            String correo,
            String telefono,
            String contrasena) {

        validarCampos(nombre, correo, telefono);

        if (contrasena == null || contrasena.length() < 8) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 8 caracteres.");
        }

        if (usuarioService.existeCorreo(
                correo.trim().toLowerCase())) {

            throw new IllegalArgumentException(
                    "El correo electrónico ya está registrado.");
        }
    }

    // Validar una actualización
    private void validarActualizacion(
            Integer idUsuario,
            String nombre,
            String correo,
            String telefono) {

        validarCampos(nombre, correo, telefono);

        Usuario existente = usuarioService.getUsuarioPorCorreo(
                correo.trim().toLowerCase());

        if (existente != null
                && !existente.getIdUsuario().equals(idUsuario)) {

            throw new IllegalArgumentException(
                    "El correo electrónico pertenece a otro usuario.");
        }
    }

    // Validar campos comunes
    private void validarCampos(
            String nombre,
            String correo,
            String telefono) {

        if (nombre == null || nombre.isBlank()
                || nombre.length() > 50) {

            throw new IllegalArgumentException(
                    "El nombre debe tener entre 1 y 50 caracteres.");
        }

        if (correo == null || correo.isBlank()
                || correo.length() > 80
                || !correo.matches(
                        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {

            throw new IllegalArgumentException(
                    "Debe ingresar un correo electrónico válido.");
        }

        if (telefono == null
                || !telefono.matches("[0-9]{8}")) {

            throw new IllegalArgumentException(
                    "El teléfono debe contener 8 números.");
        }
    }

}
