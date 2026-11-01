package com.hotel.service;

import com.hotel.domain.Habitacion;
import com.hotel.domain.Usuario;
import com.hotel.repository.HabitacionRepository;
import com.hotel.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HabitacionService {

    // El repositorio es final para asegurar la inmutabilidad
    private final HabitacionRepository habitacionRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<Habitacion> getHabitaciones(Integer idEmpresa) {
        return habitacionRepository.findByEmpresaIdEmpresa(idEmpresa);
    }

    @Transactional(readOnly = true)
    public Optional<Habitacion> getHabitacion(Integer idHabitacion) {
        return habitacionRepository.findById(idHabitacion);
    }

    @Transactional
    public void save(Habitacion habitacion) {
        habitacionRepository.save(habitacion);
    }

    @Transactional
    public void delete(Integer idHabitacion) {
        // Verifica si la habitacion existe antes de intentar eliminarla
        if (!habitacionRepository.existsById(idHabitacion)) {
            // Lanza una excepción para indicar que la habitacion no fue encontrada
            throw new IllegalArgumentException("La habitación con ID " + idHabitacion + " no existe.");
        }
        try {
            habitacionRepository.deleteById(idHabitacion);
            habitacionRepository.flush();
        } catch (DataIntegrityViolationException e) {
            // Lanza una nueva excepción para encapsular el problema de integridad de datos
            throw new IllegalStateException("No se puede eliminar la habitación. Tiene datos asociados.", e);
        }
    }

    //Recupera el usuario que inicio sesion (su correo es el nombre de usuario)
    @Transactional(readOnly = true)
    public Usuario getUsuario(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Transactional(readOnly = true)
    public long contarPorEstado(Integer idEmpresa, String estado) {
        return habitacionRepository.countByEmpresaIdEmpresaAndEstado(idEmpresa, estado);
    }

    @Transactional(readOnly = true)
    public List<Habitacion> consultaLibres(Integer idEmpresa, Integer personas,
            LocalDate fechaEntrada, LocalDate fechaSalida) {
        return habitacionRepository.consultaLibres(idEmpresa, personas, fechaEntrada, fechaSalida);
    }
}
