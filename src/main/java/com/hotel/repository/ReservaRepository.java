package com.hotel.repository;

import com.hotel.domain.Reserva;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    
    public List<Reserva> findByEmpresaIdEmpresaOrderByFechaEntradaDesc(
            Integer idEmpresa);

    
    @Query("""
        SELECT COUNT(r)
        FROM Reserva r
        WHERE r.habitacion.idHabitacion = ?1
          AND r.idReserva <> ?2
          AND r.estado = 'CONFIRMADA'
          AND r.fechaEntrada <= ?3
          AND r.fechaSalida > ?3
        """)
    public long contarConfirmadasEnCurso(Integer idHabitacion,
            Integer idReservaExcluida, LocalDate fecha);
}
