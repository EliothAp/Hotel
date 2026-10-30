/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hotel.service;
import com.hotel.domain.Servicio;
import com.hotel.repository.ServicioRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 * @author rueda
 */
@Slf4j
@Service
@RequiredArgsConstructor

public class ServicioService {
    private final ServicioRepository servicioRepository;   

    @Transactional(readOnly = true)
    public List<Servicio> getServicios(boolean activo) {
        if (activo) {            
            return servicioRepository.findByActivoTrue();
        }
        return servicioRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Servicio> getServicio(Integer idServicio) {
        return servicioRepository.findById(idServicio);
    }

    @Transactional
    public void save(Servicio servicio) {
        servicioRepository.save(servicio);
    }

    @Transactional
    public void delete(Integer idServicio) {
        if (!servicioRepository.existsById(idServicio)) {
            throw new IllegalArgumentException("El servicio con ID " + idServicio + " no existe.");
        }
        try {
            servicioRepository.deleteById(idServicio);
            servicioRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el servicio. Tiene datos asociados.", e);
        }
    }
}
