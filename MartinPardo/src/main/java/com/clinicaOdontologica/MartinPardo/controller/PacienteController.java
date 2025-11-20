package com.clinicaOdontologica.MartinPardo.controller;

import com.clinicaOdontologica.MartinPardo.exception.DuplicateResourceException;
import com.clinicaOdontologica.MartinPardo.exception.ResourceNotFoundException;
import com.clinicaOdontologica.MartinPardo.model.Paciente;
import com.clinicaOdontologica.MartinPardo.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    @Autowired
    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping
    public ResponseEntity<?> crearPaciente(@RequestBody Paciente paciente) throws DuplicateResourceException {
        if (paciente.getEmail() != null && pacienteService.buscarPacientePorEmail(paciente.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Ya existe un paciente registrado con el email proporcionado: " + paciente.getEmail());
        }

        Paciente pacienteGuardado = pacienteService.guardarPaciente(paciente);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(pacienteGuardado.getId())
                .toUri();
        return ResponseEntity.created(location).body(pacienteGuardado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> buscarPorId(@PathVariable Long id) throws ResourceNotFoundException {
        Optional<Paciente> pacienteBuscado = pacienteService.buscarPacientePorId(id);
        return ResponseEntity.ok(pacienteBuscado.orElseThrow(() -> 
            new ResourceNotFoundException("Paciente no encontrado con ID: " + id)));
    }

    @GetMapping
    public ResponseEntity<List<Paciente>> listarPacientes() {
        return ResponseEntity.ok(pacienteService.buscarPacientes());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Paciente> buscarPorEmail(@PathVariable String email) throws ResourceNotFoundException {
        return ResponseEntity.ok(pacienteService.buscarPacientePorEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con email: " + email)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paciente> actualizarPaciente(@PathVariable Long id, @RequestBody Paciente paciente) throws ResourceNotFoundException, DuplicateResourceException {
        // Verificar si el paciente existe
        if (!pacienteService.buscarPacientePorId(id).isPresent()) {
            throw new ResourceNotFoundException("Paciente no encontrado con ID: " + id);
        }

        // Verificar si el email ya existe en otro paciente
        if (paciente.getEmail() != null) {
            Optional<Paciente> pacienteConEmail = pacienteService.buscarPacientePorEmail(paciente.getEmail());
            if (pacienteConEmail.isPresent() && !pacienteConEmail.get().getId().equals(id)) {
                throw new DuplicateResourceException("Ya existe otro paciente registrado con el email proporcionado: " + paciente.getEmail());
            }
        }

        return ResponseEntity.ok(pacienteService.actualizarPaciente(id, paciente)
                .orElseThrow(() -> new ResourceNotFoundException("Error al actualizar el paciente con ID: " + id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPaciente(@PathVariable Long id) throws ResourceNotFoundException {
        if (!pacienteService.eliminarPaciente(id)) {
            throw new ResourceNotFoundException("Paciente no encontrado con ID: " + id);
        }
        return ResponseEntity.noContent().build();
    }
}
