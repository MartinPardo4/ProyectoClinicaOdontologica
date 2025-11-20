package com.clinicaOdontologica.MartinPardo.controller;

import com.clinicaOdontologica.MartinPardo.model.Odontologo;
import com.clinicaOdontologica.MartinPardo.service.OdontologoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.clinicaOdontologica.MartinPardo.security.JwtService;
import com.clinicaOdontologica.MartinPardo.service.UsuarioService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OdontologoController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
class OdontologoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OdontologoService odontologoService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private Odontologo odontologo;

    @BeforeEach
    void setUp() {
        odontologo = new Odontologo("Carlos", "López", "MAT123");
        odontologo.setId(1L);
    }

    @Test
    void testCrearOdontologo_Success() throws Exception {
        when(odontologoService.buscarOdontologoPorMatricula(anyString())).thenReturn(Optional.empty());
        when(odontologoService.guardarOdontologo(any(Odontologo.class))).thenReturn(odontologo);

        mockMvc.perform(post("/odontologos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(odontologo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andExpect(jsonPath("$.matricula").value("MAT123"));

        verify(odontologoService, times(1)).buscarOdontologoPorMatricula("MAT123");
        verify(odontologoService, times(1)).guardarOdontologo(any(Odontologo.class));
    }

    @Test
    void testCrearOdontologo_DuplicateMatricula() throws Exception {
        when(odontologoService.buscarOdontologoPorMatricula("MAT123"))
                .thenReturn(Optional.of(odontologo));

        mockMvc.perform(post("/odontologos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(odontologo)))
                .andExpect(status().isConflict());

        verify(odontologoService, times(1)).buscarOdontologoPorMatricula("MAT123");
        verify(odontologoService, never()).guardarOdontologo(any(Odontologo.class));
    }

    @Test
    void testBuscarPorId_Success() throws Exception {
        when(odontologoService.buscarOdontologoPorId(1L)).thenReturn(Optional.of(odontologo));

        mockMvc.perform(get("/odontologos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Carlos"));

        verify(odontologoService, times(1)).buscarOdontologoPorId(1L);
    }

    @Test
    void testBuscarPorId_NotFound() throws Exception {
        when(odontologoService.buscarOdontologoPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/odontologos/999"))
                .andExpect(status().isNotFound());

        verify(odontologoService, times(1)).buscarOdontologoPorId(999L);
    }

    @Test
    void testListarOdontologos() throws Exception {
        Odontologo odontologo2 = new Odontologo("Ana", "Martínez", "MAT456");
        List<Odontologo> odontologos = Arrays.asList(odontologo, odontologo2);

        when(odontologoService.buscarOdontologos()).thenReturn(odontologos);

        mockMvc.perform(get("/odontologos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(odontologoService, times(1)).buscarOdontologos();
    }

    @Test
    void testBuscarPorMatricula_Success() throws Exception {
        when(odontologoService.buscarOdontologoPorMatricula("MAT123"))
                .thenReturn(Optional.of(odontologo));

        mockMvc.perform(get("/odontologos/matricula/MAT123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula").value("MAT123"));

        verify(odontologoService, times(1)).buscarOdontologoPorMatricula("MAT123");
    }

    @Test
    void testBuscarPorMatricula_NotFound() throws Exception {
        when(odontologoService.buscarOdontologoPorMatricula("MAT999"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/odontologos/matricula/MAT999"))
                .andExpect(status().isNotFound());

        verify(odontologoService, times(1)).buscarOdontologoPorMatricula("MAT999");
    }

    @Test
    void testActualizarOdontologo_Success() throws Exception {
        Odontologo odontologoActualizado = new Odontologo("Carlos Alberto", "López", "MAT123");
        odontologoActualizado.setId(1L);

        when(odontologoService.buscarOdontologoPorId(1L)).thenReturn(Optional.of(odontologo));
        when(odontologoService.buscarOdontologoPorMatricula("MAT123")).thenReturn(Optional.of(odontologo));
        when(odontologoService.actualizarOdontologo(anyLong(), any(Odontologo.class)))
                .thenReturn(Optional.of(odontologoActualizado));

        mockMvc.perform(put("/odontologos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(odontologoActualizado)))
                .andExpect(status().isOk());

        verify(odontologoService, times(1)).buscarOdontologoPorId(1L);
        verify(odontologoService, times(1)).actualizarOdontologo(anyLong(), any(Odontologo.class));
    }

    @Test
    void testActualizarOdontologo_NotFound() throws Exception {
        Odontologo odontologoActualizado = new Odontologo("Carlos", "López", "MAT123");

        when(odontologoService.buscarOdontologoPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/odontologos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(odontologoActualizado)))
                .andExpect(status().isNotFound());

        verify(odontologoService, times(1)).buscarOdontologoPorId(999L);
        verify(odontologoService, never()).actualizarOdontologo(anyLong(), any(Odontologo.class));
    }

    @Test
    void testEliminarOdontologo_Success() throws Exception {
        when(odontologoService.eliminarOdontologo(1L)).thenReturn(true);

        mockMvc.perform(delete("/odontologos/1"))
                .andExpect(status().isNoContent());

        verify(odontologoService, times(1)).eliminarOdontologo(1L);
    }

    @Test
    void testEliminarOdontologo_NotFound() throws Exception {
        when(odontologoService.eliminarOdontologo(999L)).thenReturn(false);

        mockMvc.perform(delete("/odontologos/999"))
                .andExpect(status().isNotFound());

        verify(odontologoService, times(1)).eliminarOdontologo(999L);
    }
}

