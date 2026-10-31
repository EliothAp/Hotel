package com.hotel.service;

/**
 *
 * @author Elioth Artavia
 */
import com.hotel.domain.Usuario;
import com.hotel.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Obtener todos los usuarios o solamente los activos
    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios(boolean activo) {
        if (activo) {
            return usuarioRepository.findByActivoTrue();
        }
        return usuarioRepository.findAll();
    }

    // Buscar un usuario por su ID
    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuario(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    // Buscar un usuario por correo electrónico
    @Transactional(readOnly = true)
    public Usuario getUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    // Verificar si un correo ya está registrado
    @Transactional(readOnly = true)
    public boolean existeCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    // Obtener los usuarios de una empresa específica
    @Transactional(readOnly = true)
    public List<Usuario> getUsuariosPorEmpresa(Integer idEmpresa) {
        return usuarioRepository.findByEmpresaIdEmpresa(idEmpresa);
    }

    // Obtener usuarios según su rol
    @Transactional(readOnly = true)
    public List<Usuario> getUsuariosPorRol(String nombreRol) {
        return usuarioRepository.findByRolNombre(nombreRol);
    }

    // Obtener clientes de un hotel específico
    @Transactional(readOnly = true)
    public List<Usuario> getClientesPorEmpresa(Integer idEmpresa) {
        return usuarioRepository
                .findByEmpresaIdEmpresaAndRolNombre(
                        idEmpresa, "CLIENTE");
    }

    // Buscar clientes por nombre dentro de un hotel
    @Transactional(readOnly = true)
    public List<Usuario> buscarClientes(Integer idEmpresa, String nombre) {
        return usuarioRepository
                .findByEmpresaIdEmpresaAndRolNombreAndNombreContainingIgnoreCase(
                        idEmpresa, "CLIENTE", nombre);
    }

    // Guardar o actualizar usuarios
    @Transactional
    public void save(Usuario usuario) {

        if (usuario.getIdUsuario() == null
                && usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new IllegalArgumentException(
                    "El correo electrónico ya está registrado.");
        }

        if (usuario.getRol() == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un rol.");
        }

        String nombreRol = usuario.getRol().getNombre();

        if (!"SUPERADMIN".equals(nombreRol)
                && usuario.getEmpresa() == null) {
            throw new IllegalArgumentException(
                    "El usuario debe pertenecer a un hotel.");
        }

        // Codificar la contraseña al registrar un usuario nuevo
        if (usuario.getIdUsuario() == null) {

            if (usuario.getContrasena() == null
                    || usuario.getContrasena().isBlank()) {
                throw new IllegalArgumentException(
                        "Debe ingresar una contraseña.");
            }

            usuario.setContrasena(
                    passwordEncoder.encode(usuario.getContrasena()));
        }

        usuarioRepository.save(usuario);
    }

    // Eliminar un usuario
    @Transactional
    public void delete(Integer idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new IllegalArgumentException(
                    "El usuario con ID " + idUsuario + " no existe.");
        }

        try {
            usuarioRepository.deleteById(idUsuario);
            usuarioRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "No se puede eliminar el usuario. Tiene datos asociados.",
                    e);
        }
    }
}
