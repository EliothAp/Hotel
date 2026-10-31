package com.hotel.repository;

import com.hotel.domain.Habitacion;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, Integer> {

    //Consulta derivada que recupera las habitaciones de un solo hotel (empresa)
    public List<Habitacion> findByEmpresaIdEmpresa(Integer idEmpresa);

    //Consulta JPQL que recupera las habitaciones libres de un hotel para un rango de fechas y una cantidad de personas:
    //no estan en mantenimiento, caben las personas y no tienen una reserva (no cancelada) que choque con las fechas
    @Query("""
            SELECT h FROM Habitacion h
            WHERE h.empresa.idEmpresa = ?1
            AND h.estado = 'DISPONIBLE'
            AND h.tipoHabitacion.capacidad >= ?2
            AND NOT EXISTS (
                SELECT r FROM Reserva r
                WHERE r.habitacion.idHabitacion = h.idHabitacion
                AND r.estado <> 'CANCELADA'
                AND r.fechaEntrada < ?4
                AND r.fechaSalida > ?3
            )
            """)
    public List<Habitacion> consultaLibres(Integer idEmpresa, Integer personas,
            LocalDate fechaEntrada, LocalDate fechaSalida);

}
