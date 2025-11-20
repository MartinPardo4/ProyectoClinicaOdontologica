package com.clinicaOdontologica.MartinPardo.integration;

import com.clinicaOdontologica.MartinPardo.dto.TurnoDTO;
import com.clinicaOdontologica.MartinPardo.model.Domicilio;
import com.clinicaOdontologica.MartinPardo.model.Odontologo;
import com.clinicaOdontologica.MartinPardo.model.Paciente;
import com.clinicaOdontologica.MartinPardo.repository.OdontologoRepository;
import com.clinicaOdontologica.MartinPardo.repository.PacienteRepository;
import com.clinicaOdontologica.MartinPardo.repository.TurnoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.clinicaOdontologica.MartinPardo.config.TestSecurityConfig;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@Transactional
class TurnoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private OdontologoRepository odontologoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Paciente paciente;
    private Odontologo odontologo;

    @BeforeEach
    void setUp() {
        turnoRepository.deleteAll();
        pacienteRepository.deleteAll();
        odontologoRepository.deleteAll();

        Domicilio domicilio = new Domicilio("Calle Test", 123, "Buenos Aires", "CABA");
        paciente = pacienteRepository.save(new Paciente("Juan", "Pérez", 123456789, LocalDate.now(), domicilio, "juan@test.com"));
        odontologo = odontologoRepository.save(new Odontologo("Carlos", "López", "MAT123"));
    }

    @Test
    void testCrearYBuscarTurno() throws Exception {
        TurnoDTO turnoDTO = new TurnoDTO();
        turnoDTO.setFecha(LocalDate.now().plusDays(1));
        turnoDTO.setPacienteId(paciente.getId());
        turnoDTO.setOdontologoId(odontologo.getId());

        // Crear turno
        mockMvc.perform(post("/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turnoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pacienteId").value(paciente.getId()))
                .andExpect(jsonPath("$.odontologoId").value(odontologo.getId()));

        // Listar turnos
        mockMvc.perform(get("/turnos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testCrearTurnoConPacienteInexistente() throws Exception {
        TurnoDTO turnoDTO = new TurnoDTO();
        turnoDTO.setFecha(LocalDate.now().plusDays(1));
        turnoDTO.setPacienteId(999L);
        turnoDTO.setOdontologoId(odontologo.getId());

        mockMvc.perform(post("/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turnoDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCrearTurnoConOdontologoInexistente() throws Exception {
        TurnoDTO turnoDTO = new TurnoDTO();
        turnoDTO.setFecha(LocalDate.now().plusDays(1));
        turnoDTO.setPacienteId(paciente.getId());
        turnoDTO.setOdontologoId(999L);

        mockMvc.perform(post("/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turnoDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCrearTurnoConFechaNull() throws Exception {
        TurnoDTO turnoDTO = new TurnoDTO();
        turnoDTO.setFecha(null);
        turnoDTO.setPacienteId(paciente.getId());
        turnoDTO.setOdontologoId(odontologo.getId());

        mockMvc.perform(post("/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turnoDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testActualizarTurno() throws Exception {
        // Crear turno inicial
        TurnoDTO turnoDTO = new TurnoDTO();
        turnoDTO.setFecha(LocalDate.now().plusDays(1));
        turnoDTO.setPacienteId(paciente.getId());
        turnoDTO.setOdontologoId(odontologo.getId());

        String response = mockMvc.perform(post("/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turnoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TurnoDTO turnoCreado = objectMapper.readValue(response, TurnoDTO.class);

        // Actualizar turno
        TurnoDTO turnoActualizado = new TurnoDTO();
        turnoActualizado.setFecha(LocalDate.now().plusDays(2));
        turnoActualizado.setPacienteId(paciente.getId());
        turnoActualizado.setOdontologoId(odontologo.getId());

        mockMvc.perform(put("/turnos/" + turnoCreado.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turnoActualizado)))
                .andExpect(status().isOk());
    }

    @Test
    void testEliminarTurno() throws Exception {
        // Crear turno
        TurnoDTO turnoDTO = new TurnoDTO();
        turnoDTO.setFecha(LocalDate.now().plusDays(1));
        turnoDTO.setPacienteId(paciente.getId());
        turnoDTO.setOdontologoId(odontologo.getId());

        String response = mockMvc.perform(post("/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turnoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TurnoDTO turnoCreado = objectMapper.readValue(response, TurnoDTO.class);

        // Eliminar turno
        mockMvc.perform(delete("/turnos/" + turnoCreado.getId()))
                .andExpect(status().isNoContent());

        // Verificar que fue eliminado
        mockMvc.perform(get("/turnos/" + turnoCreado.getId()))
                .andExpect(status().isNotFound());
    }
}

