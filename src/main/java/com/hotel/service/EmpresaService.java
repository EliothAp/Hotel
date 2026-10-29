/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hotel.service;

import com.hotel.domain.Empresa;
import com.hotel.repository.EmpresaRepository;
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
public class EmpresaService {
    private final EmpresaRepository empresaRepository;    

    @Transactional(readOnly = true)
    public List<Empresa> getEmpresas(boolean activo) {
        if (activo) { //Sólo activos...            
            return empresaRepository.findByActivoTrue();
        }
        return empresaRepository.findAll();
    }
    
     @Transactional(readOnly = true)
    public Optional<Empresa> getEmpresa(Integer idEmpresa) {
        return empresaRepository.findById(idEmpresa);
    }

    @Transactional
    public void save(Empresa empresa) {
       empresaRepository.save(empresa);
    }

    @Transactional
    public void delete(Integer idEmpresa) {
        // Verifica si la empresa existe antes de intentar eliminarlo
        if (!empresaRepository.existsById(idEmpresa)) {
            // Lanza una excepción para indicar que la empresa no fue encontrada
            throw new IllegalArgumentException("La empresa con ID " + idEmpresa + " no existe.");
        }
        try {
            empresaRepository.deleteById(idEmpresa);
            empresaRepository.flush();
        } catch (DataIntegrityViolationException e) {
            // Lanza una nueva excepción para encapsular el problema de integridad de datos
            throw new IllegalStateException("No se puede eliminar la empresa. Tiene datos asociados.", e);
        }
    }
}
