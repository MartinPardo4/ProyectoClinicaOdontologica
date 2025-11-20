package com.clinicaOdontologica.MartinPardo.controller;

import com.clinicaOdontologica.MartinPardo.dto.TurnoDTO;
import com.clinicaOdontologica.MartinPardo.model.Odontologo;
import com.clinicaOdontologica.MartinPardo.model.Paciente;
import com.clinicaOdontologica.MartinPardo.service.OdontologoService;
import com.clinicaOdontologica.MartinPardo.service.PacienteService;
import com.clinicaOdontologica.MartinPardo.service.TurnoService;
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

@WebMvcTest(controllers = TurnoController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
class TurnoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TurnoService turnoService;

    @MockitoBean
    private PacienteService pacienteService;

    @MockitoBean
    private OdontologoService odontologoService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private TurnoDTO turnoDTO;
    private Paciente paciente;
    private Odontologo odontologo;

    @BeforeEach
    void setUp() {
        paciente = new Paciente("Juan", "Pérez", 123456789, LocalDate.now(), null, "juan@example.com");
        paciente.setId(1L);

        odontologo = new Odontologo("Carlos", "López", "MAT123");
        odontologo.setId(1L);

        turnoDTO = new TurnoDTO();
        turnoDTO.setId(1L);
        turnoDTO.setFecha(LocalDate.now().plusDays(1));
        turnoDTO.setPacienteId(1L);
        turnoDTO.setOdontologoId(1L);
    }

    @Test
    void testCrearTurno_Success() throws Exception {
        when(pacienteService.buscarPacientePorId(1L)).thenReturn(Optional.of(paciente));
        when(odontologoService.buscarOdontologoPorId(1L)).thenReturn(Optional.of(odontologo));
        when(turnoService.existeTurno(any(LocalDate.class), anyLong(), anyLong())).thenReturn(false);
        when(turnoService.crearTurno(any(TurnoDTO.class), any(Paciente.class), any(Odontologo.class)))
                .thenReturn(turnoDTO);

        mockMvc.perform(post("/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turnoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.pacienteId").value(1L))
                .andExpect(jsonPath("$.odontologoId").value(1L));

        verify(pacienteService, times(1)).buscarPacientePorId(1L);
        verify(odontologoService, times(1)).buscarOdontologoPorId(1L);
        verify(turnoService, times(1)).existeTurno(any(LocalDate.class), anyLong(), anyLong());
        verify(turnoService, times(1)).crearTurno(any(TurnoDTO.class), any(Paciente.class), any(Odontologo.class));
    }

    @Test
    void testCrearTurno_PacienteNoEncontrado() throws Exception {
        when(pacienteService.buscarPacientePorId(999L)).thenReturn(Optional.empty());

        turnoDTO.setPacienteId(999L);

        mockMvc.perform(post("/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turnoDTO)))
                .andExpect(status().isNotFound());

        verify(pacienteService, times(1)).buscarPacientePorId(999L);
        verify(turnoService, never()).crearTurno(any(), any(), any());
    }

    @Test
    void testCrearTurno_OdontologoNoEncontrado() throws Exception {
        when(pacienteService.buscarPacientePorId(1L)).thenReturn(Optional.of(paciente));
        when(odontologoService.buscarOdontologoPorId(999L)).thenReturn(Optional.empty());

        turnoDTO.setOdontologoId(999L);

        mockMvc.perform(post("/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turnoDTO)))
                .andExpect(status().isNotFound());

        verify(pacienteService, times(1)).buscarPacientePorId(1L);
        verify(odontologoService, times(1)).buscarOdontologoPorId(999L);
        verify(turnoService, never()).crearTurno(any(), any(), any());
    }

    @Test
    void testCrearTurno_TurnoConflict() throws Exception {
        when(pacienteService.buscarPacientePorId(1L)).thenReturn(Optional.of(paciente));
        when(odontologoService.buscarOdontologoPorId(1L)).thenReturn(Optional.of(odontologo));
        when(turnoService.existeTurno(any(LocalDate.class), anyLong(), anyLong())).thenReturn(true);

        mockMvc.perform(post("/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turnoDTO)))
                .andExpect(status().isConflict());

        verify(turnoService, times(1)).existeTurno(any(LocalDate.class), anyLong(), anyLong());
        verify(turnoService, never()).crearTurno(any(), any(), any());
    }

    @Test
    void testCrearTurno_ValidationException_FechaNull() throws Exception {
        turnoDTO.setFecha(null);

        mockMvc.perform(post("/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turnoDTO)))
                .andExpect(status().isBadRequest());

        verify(turnoService, never()).crearTurno(any(), any(), any());
    }

    @Test
    void testCrearTurno_ValidationException_PacienteIdNull() throws Exception {
        turnoDTO.setPacienteId(null);

        mockMvc.perform(post("/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turnoDTO)))
                .andExpect(status().isBadRequest());

        verify(turnoService, never()).crearTurno(any(), any(), any());
    }

    @Test
    void testListarTurnos() throws Exception {
        TurnoDTO turnoDTO2 = new TurnoDTO();
        turnoDTO2.setId(2L);
        turnoDTO2.setFecha(LocalDate.now().plusDays(2));
        List<TurnoDTO> turnos = Arrays.asList(turnoDTO, turnoDTO2);

        when(turnoService.listarTurnos()).thenReturn(turnos);

        mockMvc.perform(get("/turnos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(turnoService, times(1)).listarTurnos();
    }

    @Test
    void testBuscarPorId_Success() throws Exception {
        when(turnoService.buscarPorId(1L)).thenReturn(Optional.of(turnoDTO));

        mockMvc.perform(get("/turnos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(turnoService, times(1)).buscarPorId(1L);
    }

    @Test
    void testBuscarPorId_NotFound() throws Exception {
        when(turnoService.buscarPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/turnos/999"))
                .andExpect(status().isNotFound());

        verify(turnoService, times(1)).buscarPorId(999L);
    }

    @Test
    void testActualizarTurno_Success() throws Exception {
        when(pacienteService.buscarPacientePorId(1L)).thenReturn(Optional.of(paciente));
        when(odontologoService.buscarOdontologoPorId(1L)).thenReturn(Optional.of(odontologo));
        when(turnoService.actualizarTurno(anyLong(), any(TurnoDTO.class), any(Paciente.class), any(Odontologo.class)))
                .thenReturn(Optional.of(turnoDTO));

        mockMvc.perform(put("/turnos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turnoDTO)))
                .andExpect(status().isOk());

        verify(turnoService, times(1)).actualizarTurno(anyLong(), any(TurnoDTO.class), any(Paciente.class), any(Odontologo.class));
    }

    @Test
    void testActualizarTurno_NotFound() throws Exception {
        when(pacienteService.buscarPacientePorId(1L)).thenReturn(Optional.of(paciente));
        when(odontologoService.buscarOdontologoPorId(1L)).thenReturn(Optional.of(odontologo));
        when(turnoService.actualizarTurno(anyLong(), any(TurnoDTO.class), any(Paciente.class), any(Odontologo.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/turnos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turnoDTO)))
                .andExpect(status().isNotFound());

        verify(turnoService, times(1)).actualizarTurno(anyLong(), any(TurnoDTO.class), any(Paciente.class), any(Odontologo.class));
    }

    @Test
    void testEliminarTurno_Success() throws Exception {
        when(turnoService.eliminarTurno(1L)).thenReturn(true);

        mockMvc.perform(delete("/turnos/1"))
                .andExpect(status().isNoContent());

        verify(turnoService, times(1)).eliminarTurno(1L);
    }

    @Test
    void testEliminarTurno_NotFound() throws Exception {
        when(turnoService.eliminarTurno(999L)).thenReturn(false);

        mockMvc.perform(delete("/turnos/999"))
                .andExpect(status().isNotFound());

        verify(turnoService, times(1)).eliminarTurno(999L);
    }
}

