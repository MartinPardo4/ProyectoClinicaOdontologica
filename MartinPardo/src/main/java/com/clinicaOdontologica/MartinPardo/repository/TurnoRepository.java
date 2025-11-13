package com.clinicaOdontologica.MartinPardo.repository;

import com.clinicaOdontologica.MartinPardo.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface TurnoRepository extends JpaRepository<Turno, Long> {
    boolean existsByPacienteIdAndOdontologoIdAndFecha(Long pacienteId, Long odontologoId, LocalDate fecha);
}
