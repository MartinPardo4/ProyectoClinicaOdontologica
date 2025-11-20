package com.clinicaOdontologica.MartinPardo.integration;

import com.clinicaOdontologica.MartinPardo.model.Domicilio;
import com.clinicaOdontologica.MartinPardo.model.Paciente;
import com.clinicaOdontologica.MartinPardo.repository.PacienteRepository;
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
class PacienteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Paciente paciente;

    @BeforeEach
    void setUp() {
        pacienteRepository.deleteAll();
        Domicilio domicilio = new Domicilio("Calle Test", 123, "Buenos Aires", "CABA");
        paciente = new Paciente("Juan", "Pérez", 123456789, LocalDate.now(), domicilio, "juan@test.com");
    }

    @Test
    void testCrearYBuscarPaciente() throws Exception {
        // Crear paciente
        mockMvc.perform(post("/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Pérez"))
                .andExpect(jsonPath("$.email").value("juan@test.com"));

        // Buscar por ID
        Long pacienteId = pacienteRepository.findByEmail("juan@test.com").get().getId();
        mockMvc.perform(get("/pacientes/" + pacienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    void testListarPacientes() throws Exception {
        // Crear múltiples pacientes
        Domicilio domicilio2 = new Domicilio("Calle Test 2", 456, "Córdoba", "Córdoba");
        Paciente paciente2 = new Paciente("María", "González", 987654321, LocalDate.now(), domicilio2, "maria@test.com");

        pacienteRepository.save(paciente);
        pacienteRepository.save(paciente2);

        // Listar todos
        mockMvc.perform(get("/pacientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testBuscarPacientePorEmail() throws Exception {
        pacienteRepository.save(paciente);

        mockMvc.perform(get("/pacientes/email/juan@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    void testActualizarPaciente() throws Exception {
        Paciente guardado = pacienteRepository.save(paciente);
        Long id = guardado.getId();

        Paciente pacienteActualizado = new Paciente("Juan Carlos", "Pérez", 111222333, LocalDate.now(), 
                new Domicilio("Nueva Calle", 789, "La Plata", "Buenos Aires"), "juan@test.com");

        mockMvc.perform(put("/pacientes/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pacienteActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Carlos"));
    }

    @Test
    void testEliminarPaciente() throws Exception {
        Paciente guardado = pacienteRepository.save(paciente);
        Long id = guardado.getId();

        mockMvc.perform(delete("/pacientes/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/pacientes/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCrearPacienteConEmailDuplicado() throws Exception {
        pacienteRepository.save(paciente);

        Paciente pacienteDuplicado = new Paciente("Otro", "Nombre", 111111111, LocalDate.now(), 
                new Domicilio("Calle", 1, "Ciudad", "Provincia"), "juan@test.com");

        mockMvc.perform(post("/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pacienteDuplicado)))
                .andExpect(status().isConflict());
    }

    @Test
    void testBuscarPacienteNoExistente() throws Exception {
        mockMvc.perform(get("/pacientes/999"))
                .andExpect(status().isNotFound());
    }
}

