package com.clinicaOdontologica.MartinPardo.service;

import com.clinicaOdontologica.MartinPardo.dto.TurnoDTO;
import com.clinicaOdontologica.MartinPardo.model.Odontologo;
import com.clinicaOdontologica.MartinPardo.model.Paciente;
import com.clinicaOdontologica.MartinPardo.model.Turno;
import com.clinicaOdontologica.MartinPardo.repository.TurnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TurnoServiceTest {

    @Mock
    private TurnoRepository turnoRepository;

    @InjectMocks
    private TurnoService turnoService;

    private TurnoDTO turnoDTO;
    private Turno turno;
    private Paciente paciente;
    private Odontologo odontologo;

    @BeforeEach
    void setUp() {
        paciente = new Paciente("Juan", "Pérez", 123456789, LocalDate.now(), null, "juan@example.com");
        paciente.setId(1L);
        
        odontologo = new Odontologo("Carlos", "López", "MAT123");
        odontologo.setId(1L);

        turnoDTO = new TurnoDTO();
        turnoDTO.setFecha(LocalDate.now().plusDays(1));
        turnoDTO.setPacienteId(1L);
        turnoDTO.setOdontologoId(1L);

        turno = new Turno();
        turno.setId(1L);
        turno.setFecha(turnoDTO.getFecha());
        turno.setPaciente(paciente);
        turno.setOdontologo(odontologo);
    }

    @Test
    void testCrearTurno() {
        when(turnoRepository.save(any(Turno.class))).thenReturn(turno);

        TurnoDTO resultado = turnoService.crearTurno(turnoDTO, paciente, odontologo);

        assertNotNull(resultado);
        assertEquals(turnoDTO.getFecha(), resultado.getFecha());
        assertEquals(1L, resultado.getPacienteId());
        assertEquals(1L, resultado.getOdontologoId());
        verify(turnoRepository, times(1)).save(any(Turno.class));
    }

    @Test
    void testListarTurnos() {
        Turno turno2 = new Turno();
        turno2.setId(2L);
        turno2.setFecha(LocalDate.now().plusDays(2));
        turno2.setPaciente(paciente);
        turno2.setOdontologo(odontologo);

        List<Turno> turnos = Arrays.asList(turno, turno2);
        when(turnoRepository.findAll()).thenReturn(turnos);

        List<TurnoDTO> resultado = turnoService.listarTurnos();

        assertEquals(2, resultado.size());
        verify(turnoRepository, times(1)).findAll();
    }

    @Test
    void testBuscarPorId_Existe() {
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));

        Optional<TurnoDTO> resultado = turnoService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        verify(turnoRepository, times(1)).findById(1L);
    }

    @Test
    void testBuscarPorId_NoExiste() {
        when(turnoRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<TurnoDTO> resultado = turnoService.buscarPorId(999L);

        assertFalse(resultado.isPresent());
        verify(turnoRepository, times(1)).findById(999L);
    }

    @Test
    void testActualizarTurno_Existe() {
        TurnoDTO turnoDTOActualizado = new TurnoDTO();
        turnoDTOActualizado.setFecha(LocalDate.now().plusDays(3));
        turnoDTOActualizado.setPacienteId(1L);
        turnoDTOActualizado.setOdontologoId(1L);

        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(turnoRepository.save(any(Turno.class))).thenReturn(turno);

        Optional<TurnoDTO> resultado = turnoService.actualizarTurno(1L, turnoDTOActualizado, paciente, odontologo);

        assertTrue(resultado.isPresent());
        verify(turnoRepository, times(1)).findById(1L);
        verify(turnoRepository, times(1)).save(any(Turno.class));
    }

    @Test
    void testActualizarTurno_NoExiste() {
        TurnoDTO turnoDTOActualizado = new TurnoDTO();
        turnoDTOActualizado.setFecha(LocalDate.now().plusDays(3));

        when(turnoRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<TurnoDTO> resultado = turnoService.actualizarTurno(999L, turnoDTOActualizado, paciente, odontologo);

        assertFalse(resultado.isPresent());
        verify(turnoRepository, times(1)).findById(999L);
        verify(turnoRepository, never()).save(any(Turno.class));
    }

    @Test
    void testEliminarTurno_Existe() {
        when(turnoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(turnoRepository).deleteById(1L);

        boolean resultado = turnoService.eliminarTurno(1L);

        assertTrue(resultado);
        verify(turnoRepository, times(1)).existsById(1L);
        verify(turnoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarTurno_NoExiste() {
        when(turnoRepository.existsById(999L)).thenReturn(false);

        boolean resultado = turnoService.eliminarTurno(999L);

        assertFalse(resultado);
        verify(turnoRepository, times(1)).existsById(999L);
        verify(turnoRepository, never()).deleteById(anyLong());
    }

    @Test
    void testExisteTurno() {
        when(turnoRepository.existsByPacienteIdAndOdontologoIdAndFecha(1L, 1L, LocalDate.now()))
                .thenReturn(true);

        boolean resultado = turnoService.existeTurno(LocalDate.now(), 1L, 1L);

        assertTrue(resultado);
        verify(turnoRepository, times(1))
                .existsByPacienteIdAndOdontologoIdAndFecha(1L, 1L, LocalDate.now());
    }

    @Test
    void testNoExisteTurno() {
        when(turnoRepository.existsByPacienteIdAndOdontologoIdAndFecha(1L, 1L, LocalDate.now()))
                .thenReturn(false);

        boolean resultado = turnoService.existeTurno(LocalDate.now(), 1L, 1L);

        assertFalse(resultado);
        verify(turnoRepository, times(1))
                .existsByPacienteIdAndOdontologoIdAndFecha(1L, 1L, LocalDate.now());
    }
}

