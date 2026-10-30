package com.hotel.repository;

import com.hotel.domain.Habitacion;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, Integer> {

    //Consulta derivada que recupera las habitaciones de un solo hotel (empresa)
    public List<Habitacion> findByEmpresaIdEmpresa(Integer idEmpresa);

    //Consulta JPQL que recupera las habitaciones libres de un hotel para un rango de fechas y una cantidad de personas:
    //no estan en mantenimiento, caben las personas y no tienen una reserva (no cancelada) que choque con las fechas
    
    public List<Habitacion> consultaLibres(Integer idEmpresa, Integer personas,
            LocalDate fechaEntrada, LocalDate fechaSalida);

}
