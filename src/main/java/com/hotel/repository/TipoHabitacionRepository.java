package com.hotel.repository;

import com.hotel.domain.TipoHabitacion;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoHabitacionRepository extends JpaRepository<TipoHabitacion, Integer> {

    //Consulta derivada que recupera los tipos de habitacion de un solo hotel (empresa)
    public List<TipoHabitacion> findByEmpresaIdEmpresa(Integer idEmpresa);
}
