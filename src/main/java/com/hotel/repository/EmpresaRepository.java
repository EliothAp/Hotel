/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hotel.repository;

import com.hotel.domain.Empresa;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 *
 * @author rueda
 */


@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {

    //Se crea una consulta derivada para recuperar solo los productos activas
    public List<Empresa> findByActivoTrue();
    
} 