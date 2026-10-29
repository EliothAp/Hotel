package com.hotel.service;

import com.hotel.domain.TipoHabitacion;
import com.hotel.repository.TipoHabitacionRepository;
import java.math.BigDecimal;
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
public class TipoHabitacionService {

    // El repositorio es final para asegurar la inmutabilidad
    private final TipoHabitacionRepository tipoHabitacionRepository;

    @Transactional(readOnly = true)
    public List<TipoHabitacion> getTiposHabitacion(Integer idEmpresa) {
        return tipoHabitacionRepository.findByEmpresaIdEmpresa(idEmpresa);
    }

    //Recupera los tipos de habitacion de todos los hoteles (para la pagina de consultas)
    @Transactional(readOnly = true)
    public List<TipoHabitacion> getTodosTiposHabitacion() {
        return tipoHabitacionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<TipoHabitacion> getTipoHabitacion(Integer idTipoHabitacion) {
        return tipoHabitacionRepository.findById(idTipoHabitacion);
    }

    @Transactional
    public void save(TipoHabitacion tipoHabitacion) {
        tipoHabitacionRepository.save(tipoHabitacion);
    }

    @Transactional
    public void delete(Integer idTipoHabitacion) {
        // Verifica si el tipo de habitacion existe antes de intentar eliminarlo
        if (!tipoHabitacionRepository.existsById(idTipoHabitacion)) {
            // Lanza una excepción para indicar que el tipo de habitacion no fue encontrado
            throw new IllegalArgumentException("El tipo de habitación con ID " + idTipoHabitacion + " no existe.");
        }
        try {
            tipoHabitacionRepository.deleteById(idTipoHabitacion);
            tipoHabitacionRepository.flush();
        } catch (DataIntegrityViolationException e) {
            // Lanza una nueva excepción para encapsular el problema de integridad de datos
            throw new IllegalStateException("No se puede eliminar el tipo de habitación. Tiene datos asociados.", e);
        }
    }



}
