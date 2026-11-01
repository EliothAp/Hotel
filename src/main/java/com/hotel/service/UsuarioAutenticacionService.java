package com.hotel.service;

import com.hotel.domain.Usuario;
import com.hotel.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioAutenticacionService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByCorreo(correo);

        if (usuario == null) {
            throw new UsernameNotFoundException(
                    "No se encontró un usuario con ese correo.");
        }

        boolean usuarioActivo = usuario.isActivo();

        // El SUPERADMIN puede no tener hotel asignado.
        // ADMIN y CLIENTE deben pertenecer a un hotel activo.
        if (usuario.getRol() != null
                && !"SUPERADMIN".equals(usuario.getRol().getNombre())) {

            usuarioActivo = usuarioActivo
                    && usuario.getEmpresa() != null
                    && usuario.getEmpresa().isActivo();
        }

        return User.builder()
                .username(usuario.getCorreo())
                .password(usuario.getContrasena())
                .roles(usuario.getRol().getNombre())
                .disabled(!usuarioActivo)
                .build();
    }
}
