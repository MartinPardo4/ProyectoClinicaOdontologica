package com.clinicaOdontologica.MartinPardo.service;

import com.clinicaOdontologica.MartinPardo.model.Domicilio;
import com.clinicaOdontologica.MartinPardo.model.Paciente;
import com.clinicaOdontologica.MartinPardo.repository.PacienteRepository;
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
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private PacienteService pacienteService;

    private Paciente paciente;
    private Domicilio domicilio;

    @BeforeEach
    void setUp() {
        domicilio = new Domicilio("Calle Principal", 123, "Buenos Aires", "CABA");
        paciente = new Paciente("Juan", "Pérez", 123456789, LocalDate.now(), domicilio, "juan@example.com");
        paciente.setId(1L);
    }

    @Test
    void testGuardarPaciente() {
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(paciente);

        Paciente resultado = pacienteService.guardarPaciente(paciente);

        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());
        assertEquals("Pérez", resultado.getApellido());
        assertEquals("juan@example.com", resultado.getEmail());
        verify(pacienteRepository, times(1)).save(paciente);
    }

    @Test
    void testBuscarPacientePorId_Existe() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));

        Optional<Paciente> resultado = pacienteService.buscarPacientePorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Juan", resultado.get().getNombre());
        verify(pacienteRepository, times(1)).findById(1L);
    }

    @Test
    void testBuscarPacientePorId_NoExiste() {
        when(pacienteRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Paciente> resultado = pacienteService.buscarPacientePorId(999L);

        assertFalse(resultado.isPresent());
        verify(pacienteRepository, times(1)).findById(999L);
    }

    @Test
    void testBuscarPacientes() {
        Paciente paciente2 = new Paciente("María", "González", 987654321, LocalDate.now(), domicilio, "maria@example.com");
        List<Paciente> pacientes = Arrays.asList(paciente, paciente2);

        when(pacienteRepository.findAll()).thenReturn(pacientes);

        List<Paciente> resultado = pacienteService.buscarPacientes();

        assertEquals(2, resultado.size());
        verify(pacienteRepository, times(1)).findAll();
    }

    @Test
    void testBuscarPacientePorEmail_Existe() {
        when(pacienteRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(paciente));

        Optional<Paciente> resultado = pacienteService.buscarPacientePorEmail("juan@example.com");

        assertTrue(resultado.isPresent());
        assertEquals("juan@example.com", resultado.get().getEmail());
        verify(pacienteRepository, times(1)).findByEmail("juan@example.com");
    }

    @Test
    void testBuscarPacientePorEmail_NoExiste() {
        when(pacienteRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        Optional<Paciente> resultado = pacienteService.buscarPacientePorEmail("noexiste@example.com");

        assertFalse(resultado.isPresent());
        verify(pacienteRepository, times(1)).findByEmail("noexiste@example.com");
    }

    @Test
    void testActualizarPaciente_Existe() {
        Paciente pacienteActualizado = new Paciente("Juan Carlos", "Pérez", 111222333, LocalDate.now(), domicilio, "juan@example.com");
        
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(paciente);

        Optional<Paciente> resultado = pacienteService.actualizarPaciente(1L, pacienteActualizado);

        assertTrue(resultado.isPresent());
        verify(pacienteRepository, times(1)).findById(1L);
        verify(pacienteRepository, times(1)).save(any(Paciente.class));
    }

    @Test
    void testActualizarPaciente_NoExiste() {
        Paciente pacienteActualizado = new Paciente("Juan", "Pérez", 123456789, LocalDate.now(), domicilio, "juan@example.com");
        
        when(pacienteRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Paciente> resultado = pacienteService.actualizarPaciente(999L, pacienteActualizado);

        assertFalse(resultado.isPresent());
        verify(pacienteRepository, times(1)).findById(999L);
        verify(pacienteRepository, never()).save(any(Paciente.class));
    }

    @Test
    void testEliminarPaciente_Existe() {
        when(pacienteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(pacienteRepository).deleteById(1L);

        boolean resultado = pacienteService.eliminarPaciente(1L);

        assertTrue(resultado);
        verify(pacienteRepository, times(1)).existsById(1L);
        verify(pacienteRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarPaciente_NoExiste() {
        when(pacienteRepository.existsById(999L)).thenReturn(false);

        boolean resultado = pacienteService.eliminarPaciente(999L);

        assertFalse(resultado);
        verify(pacienteRepository, times(1)).existsById(999L);
        verify(pacienteRepository, never()).deleteById(anyLong());
    }
}

