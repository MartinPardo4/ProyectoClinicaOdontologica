package com.clinicaOdontologica.MartinPardo.service;

import com.clinicaOdontologica.MartinPardo.model.Odontologo;
import com.clinicaOdontologica.MartinPardo.repository.OdontologoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OdontologoServiceTest {

    @Mock
    private OdontologoRepository odontologoRepository;

    @InjectMocks
    private OdontologoService odontologoService;

    private Odontologo odontologo;

    @BeforeEach
    void setUp() {
        odontologo = new Odontologo("Carlos", "López", "MAT123");
        odontologo.setId(1L);
    }

    @Test
    void testGuardarOdontologo() {
        when(odontologoRepository.save(any(Odontologo.class))).thenReturn(odontologo);

        Odontologo resultado = odontologoService.guardarOdontologo(odontologo);

        assertNotNull(resultado);
        assertEquals("Carlos", resultado.getNombre());
        assertEquals("López", resultado.getApellido());
        assertEquals("MAT123", resultado.getMatricula());
        verify(odontologoRepository, times(1)).save(odontologo);
    }

    @Test
    void testBuscarOdontologos() {
        Odontologo odontologo2 = new Odontologo("Ana", "Martínez", "MAT456");
        List<Odontologo> odontologos = Arrays.asList(odontologo, odontologo2);

        when(odontologoRepository.findAll()).thenReturn(odontologos);

        List<Odontologo> resultado = odontologoService.buscarOdontologos();

        assertEquals(2, resultado.size());
        verify(odontologoRepository, times(1)).findAll();
    }

    @Test
    void testBuscarOdontologoPorId_Existe() {
        when(odontologoRepository.findById(1L)).thenReturn(Optional.of(odontologo));

        Optional<Odontologo> resultado = odontologoService.buscarOdontologoPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Carlos", resultado.get().getNombre());
        verify(odontologoRepository, times(1)).findById(1L);
    }

    @Test
    void testBuscarOdontologoPorId_NoExiste() {
        when(odontologoRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Odontologo> resultado = odontologoService.buscarOdontologoPorId(999L);

        assertFalse(resultado.isPresent());
        verify(odontologoRepository, times(1)).findById(999L);
    }

    @Test
    void testBuscarOdontologoPorMatricula_Existe() {
        when(odontologoRepository.findByMatricula("MAT123")).thenReturn(Optional.of(odontologo));

        Optional<Odontologo> resultado = odontologoService.buscarOdontologoPorMatricula("MAT123");

        assertTrue(resultado.isPresent());
        assertEquals("MAT123", resultado.get().getMatricula());
        verify(odontologoRepository, times(1)).findByMatricula("MAT123");
    }

    @Test
    void testBuscarOdontologoPorMatricula_NoExiste() {
        when(odontologoRepository.findByMatricula("MAT999")).thenReturn(Optional.empty());

        Optional<Odontologo> resultado = odontologoService.buscarOdontologoPorMatricula("MAT999");

        assertFalse(resultado.isPresent());
        verify(odontologoRepository, times(1)).findByMatricula("MAT999");
    }

    @Test
    void testActualizarOdontologo_Existe() {
        Odontologo odontologoActualizado = new Odontologo("Carlos Alberto", "López", "MAT123");
        
        when(odontologoRepository.findById(1L)).thenReturn(Optional.of(odontologo));
        when(odontologoRepository.save(any(Odontologo.class))).thenReturn(odontologo);

        Optional<Odontologo> resultado = odontologoService.actualizarOdontologo(1L, odontologoActualizado);

        assertTrue(resultado.isPresent());
        verify(odontologoRepository, times(1)).findById(1L);
        verify(odontologoRepository, times(1)).save(any(Odontologo.class));
    }

    @Test
    void testActualizarOdontologo_NoExiste() {
        Odontologo odontologoActualizado = new Odontologo("Carlos", "López", "MAT123");
        
        when(odontologoRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Odontologo> resultado = odontologoService.actualizarOdontologo(999L, odontologoActualizado);

        assertFalse(resultado.isPresent());
        verify(odontologoRepository, times(1)).findById(999L);
        verify(odontologoRepository, never()).save(any(Odontologo.class));
    }

    @Test
    void testEliminarOdontologo_Existe() {
        when(odontologoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(odontologoRepository).deleteById(1L);

        boolean resultado = odontologoService.eliminarOdontologo(1L);

        assertTrue(resultado);
        verify(odontologoRepository, times(1)).existsById(1L);
        verify(odontologoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarOdontologo_NoExiste() {
        when(odontologoRepository.existsById(999L)).thenReturn(false);

        boolean resultado = odontologoService.eliminarOdontologo(999L);

        assertFalse(resultado);
        verify(odontologoRepository, times(1)).existsById(999L);
        verify(odontologoRepository, never()).deleteById(anyLong());
    }
}

