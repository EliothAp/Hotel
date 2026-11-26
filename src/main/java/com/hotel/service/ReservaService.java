package com.hotel.service;

import com.hotel.domain.Habitacion;
import com.hotel.domain.Reserva;
import com.hotel.repository.HabitacionRepository;
import com.hotel.repository.ReservaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservaService {

    
    public static final String PENDIENTE = "PENDIENTE";
    public static final String CONFIRMADA = "CONFIRMADA";
    public static final String CANCELADA = "CANCELADA";

    private final ReservaRepository reservaRepository;
    private final HabitacionRepository habitacionRepository;

    
    @Transactional(readOnly = true)
    public List<Reserva> getReservas(Integer idEmpresa) {
        return reservaRepository
                .findByEmpresaIdEmpresaOrderByFechaEntradaDesc(idEmpresa);
    }

    @Transactional(readOnly = true)
    public Optional<Reserva> getReserva(Integer idReserva) {
        return reservaRepository.findById(idReserva);
    }

    // US-08: pasar una reserva de PENDIENTE a CONFIRMADA.
    // Solo se puede confirmar una reserva que esté pendiente.
    @Transactional
    public void confirmar(Integer idReserva) {

        Reserva reserva = buscarReserva(idReserva);

        if (!PENDIENTE.equals(reserva.getEstado())) {
            throw new IllegalStateException(
                    "Solo se pueden confirmar reservas pendientes.");
        }

        reserva.setEstado(CONFIRMADA);
        reservaRepository.save(reserva);
    }

    
    @Transactional
    public void cancelar(Integer idReserva) {

        Reserva reserva = buscarReserva(idReserva);

        if (CANCELADA.equals(reserva.getEstado())) {
            throw new IllegalStateException(
                    "La reserva ya estaba cancelada.");
        }

        reserva.setEstado(CANCELADA);
        reservaRepository.save(reserva);

        liberarHabitacion(reserva);
    }

    
    private void liberarHabitacion(Reserva reserva) {

        Habitacion habitacion = reserva.getHabitacion();
        LocalDate hoy = LocalDate.now();

        
        boolean estabaEnCurso
                = !reserva.getFechaEntrada().isAfter(hoy)
                && reserva.getFechaSalida().isAfter(hoy);

       
        long otrasEnCurso = reservaRepository.contarConfirmadasEnCurso(
                habitacion.getIdHabitacion(),
                reserva.getIdReserva(),
                hoy);

        if (estabaEnCurso
                && "OCUPADA".equals(habitacion.getEstado())
                && otrasEnCurso == 0) {

            habitacion.setEstado("DISPONIBLE");
            habitacionRepository.save(habitacion);
        }
    }

    
    private Reserva buscarReserva(Integer idReserva) {
        return reservaRepository.findById(idReserva)
                .orElseThrow(() -> new IllegalArgumentException(
                "La reserva con ID " + idReserva + " no existe."));
    }
}
