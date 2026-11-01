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

    @Query(nativeQuery = true, value = """
        SELECT h.*
        FROM habitacion h
        JOIN tipo_habitacion t ON t.id_tipo_habitacion = h.id_tipo_habitacion
        WHERE h.id_empresa = ?1
          AND h.estado = 'DISPONIBLE'
          AND t.capacidad >= ?2
          AND h.id_habitacion NOT IN (
              SELECT r.id_habitacion
              FROM reserva r
              WHERE r.estado <> 'CANCELADA'
                AND r.fecha_entrada < ?4
                AND r.fecha_salida > ?3
          )
        ORDER BY h.numero
        """)
    public List<Habitacion> consultaLibres(Integer idEmpresa, Integer personas,
            LocalDate fechaEntrada, LocalDate fechaSalida);

    public long countByEmpresaIdEmpresaAndEstado(Integer idEmpresa, String estado);

}
