package com.clinicaOdontologica.MartinPardo.integration;

import com.clinicaOdontologica.MartinPardo.model.Odontologo;
import com.clinicaOdontologica.MartinPardo.repository.OdontologoRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@Transactional
class OdontologoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OdontologoRepository odontologoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Odontologo odontologo;

    @BeforeEach
    void setUp() {
        odontologoRepository.deleteAll();
        odontologo = new Odontologo("Carlos", "López", "MAT123");
    }

    @Test
    void testCrearYBuscarOdontologo() throws Exception {
        // Crear odontólogo
        mockMvc.perform(post("/odontologos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(odontologo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andExpect(jsonPath("$.apellido").value("López"))
                .andExpect(jsonPath("$.matricula").value("MAT123"));

        // Buscar por ID
        Long odontologoId = odontologoRepository.findByMatricula("MAT123").get().getId();
        mockMvc.perform(get("/odontologos/" + odontologoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Carlos"));
    }

    @Test
    void testListarOdontologos() throws Exception {
        Odontologo odontologo2 = new Odontologo("Ana", "Martínez", "MAT456");

        odontologoRepository.save(odontologo);
        odontologoRepository.save(odontologo2);

        mockMvc.perform(get("/odontologos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testBuscarOdontologoPorMatricula() throws Exception {
        odontologoRepository.save(odontologo);

        mockMvc.perform(get("/odontologos/matricula/MAT123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula").value("MAT123"));
    }

    @Test
    void testActualizarOdontologo() throws Exception {
        Odontologo guardado = odontologoRepository.save(odontologo);
        Long id = guardado.getId();

        Odontologo odontologoActualizado = new Odontologo("Carlos Alberto", "López", "MAT123");

        mockMvc.perform(put("/odontologos/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(odontologoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Carlos Alberto"));
    }

    @Test
    void testEliminarOdontologo() throws Exception {
        Odontologo guardado = odontologoRepository.save(odontologo);
        Long id = guardado.getId();

        mockMvc.perform(delete("/odontologos/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/odontologos/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCrearOdontologoConMatriculaDuplicada() throws Exception {
        odontologoRepository.save(odontologo);

        Odontologo odontologoDuplicado = new Odontologo("Otro", "Nombre", "MAT123");

        mockMvc.perform(post("/odontologos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(odontologoDuplicado)))
                .andExpect(status().isConflict());
    }

    @Test
    void testBuscarOdontologoNoExistente() throws Exception {
        mockMvc.perform(get("/odontologos/999"))
                .andExpect(status().isNotFound());
    }
}

