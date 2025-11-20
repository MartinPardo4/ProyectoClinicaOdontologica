package com.clinicaOdontologica.MartinPardo.controller;

import com.clinicaOdontologica.MartinPardo.model.Domicilio;
import com.clinicaOdontologica.MartinPardo.model.Paciente;
import com.clinicaOdontologica.MartinPardo.service.PacienteService;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PacienteController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PacienteService pacienteService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private Paciente paciente;
    private Domicilio domicilio;

    @BeforeEach
    void setUp() {
        domicilio = new Domicilio("Calle Principal", 123, "Buenos Aires", "CABA");
        paciente = new Paciente("Juan", "Pérez", 123456789, LocalDate.now(), domicilio, "juan@example.com");
        paciente.setId(1L);
    }

    @Test
    void testCrearPaciente_Success() throws Exception {
        when(pacienteService.buscarPacientePorEmail(anyString())).thenReturn(Optional.empty());
        when(pacienteService.guardarPaciente(any(Paciente.class))).thenReturn(paciente);

        mockMvc.perform(post("/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Pérez"));

        verify(pacienteService, times(1)).buscarPacientePorEmail("juan@example.com");
        verify(pacienteService, times(1)).guardarPaciente(any(Paciente.class));
    }

    @Test
    void testCrearPaciente_DuplicateEmail() throws Exception {
        when(pacienteService.buscarPacientePorEmail("juan@example.com"))
                .thenReturn(Optional.of(paciente));

        mockMvc.perform(post("/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().isConflict());

        verify(pacienteService, times(1)).buscarPacientePorEmail("juan@example.com");
        verify(pacienteService, never()).guardarPaciente(any(Paciente.class));
    }

    @Test
    void testBuscarPorId_Success() throws Exception {
        when(pacienteService.buscarPacientePorId(1L)).thenReturn(Optional.of(paciente));

        mockMvc.perform(get("/pacientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Juan"));

        verify(pacienteService, times(1)).buscarPacientePorId(1L);
    }

    @Test
    void testBuscarPorId_NotFound() throws Exception {
        when(pacienteService.buscarPacientePorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/pacientes/999"))
                .andExpect(status().isNotFound());

        verify(pacienteService, times(1)).buscarPacientePorId(999L);
    }

    @Test
    void testListarPacientes() throws Exception {
        Paciente paciente2 = new Paciente("María", "González", 987654321, LocalDate.now(), domicilio, "maria@example.com");
        List<Paciente> pacientes = Arrays.asList(paciente, paciente2);

        when(pacienteService.buscarPacientes()).thenReturn(pacientes);

        mockMvc.perform(get("/pacientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(pacienteService, times(1)).buscarPacientes();
    }

    @Test
    void testBuscarPorEmail_Success() throws Exception {
        when(pacienteService.buscarPacientePorEmail("juan@example.com"))
                .thenReturn(Optional.of(paciente));

        mockMvc.perform(get("/pacientes/email/juan@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@example.com"));

        verify(pacienteService, times(1)).buscarPacientePorEmail("juan@example.com");
    }

    @Test
    void testBuscarPorEmail_NotFound() throws Exception {
        when(pacienteService.buscarPacientePorEmail("noexiste@example.com"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/pacientes/email/noexiste@example.com"))
                .andExpect(status().isNotFound());

        verify(pacienteService, times(1)).buscarPacientePorEmail("noexiste@example.com");
    }

    @Test
    void testActualizarPaciente_Success() throws Exception {
        Paciente pacienteActualizado = new Paciente("Juan Carlos", "Pérez", 111222333, LocalDate.now(), domicilio, "juan@example.com");
        pacienteActualizado.setId(1L);

        when(pacienteService.buscarPacientePorId(1L)).thenReturn(Optional.of(paciente));
        when(pacienteService.buscarPacientePorEmail("juan@example.com")).thenReturn(Optional.of(paciente));
        when(pacienteService.actualizarPaciente(anyLong(), any(Paciente.class)))
                .thenReturn(Optional.of(pacienteActualizado));

        mockMvc.perform(put("/pacientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pacienteActualizado)))
                .andExpect(status().isOk());

        verify(pacienteService, times(1)).buscarPacientePorId(1L);
        verify(pacienteService, times(1)).actualizarPaciente(anyLong(), any(Paciente.class));
    }

    @Test
    void testActualizarPaciente_NotFound() throws Exception {
        Paciente pacienteActualizado = new Paciente("Juan", "Pérez", 123456789, LocalDate.now(), domicilio, "juan@example.com");

        when(pacienteService.buscarPacientePorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/pacientes/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pacienteActualizado)))
                .andExpect(status().isNotFound());

        verify(pacienteService, times(1)).buscarPacientePorId(999L);
        verify(pacienteService, never()).actualizarPaciente(anyLong(), any(Paciente.class));
    }

    @Test
    void testEliminarPaciente_Success() throws Exception {
        when(pacienteService.eliminarPaciente(1L)).thenReturn(true);

        mockMvc.perform(delete("/pacientes/1"))
                .andExpect(status().isNoContent());

        verify(pacienteService, times(1)).eliminarPaciente(1L);
    }

    @Test
    void testEliminarPaciente_NotFound() throws Exception {
        when(pacienteService.eliminarPaciente(999L)).thenReturn(false);

        mockMvc.perform(delete("/pacientes/999"))
                .andExpect(status().isNotFound());

        verify(pacienteService, times(1)).eliminarPaciente(999L);
    }
}

