package com.hotel.service;

import com.hotel.domain.Habitacion;
import com.hotel.repository.HabitacionRepository;
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


    private final HabitacionRepository habitacionRepository;

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
        
        if (!habitacionRepository.existsById(idHabitacion)) {
            
            throw new IllegalArgumentException("La habitación con ID " + idHabitacion + " no existe.");
        }
        try {
            habitacionRepository.deleteById(idHabitacion);
            habitacionRepository.flush();
        } catch (DataIntegrityViolationException e) {
            
            throw new IllegalStateException("No se puede eliminar la habitación. Tiene datos asociados.", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Habitacion> consultaLibres(Integer idEmpresa, Integer personas,
            LocalDate fechaEntrada, LocalDate fechaSalida) {
        return habitacionRepository.consultaLibres(idEmpresa, personas, fechaEntrada, fechaSalida);
    }
}
