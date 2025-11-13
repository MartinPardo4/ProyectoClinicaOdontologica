package com.clinicaOdontologica.MartinPardo.controller;

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
    public ResponseEntity<?> crearPaciente(@RequestBody Paciente paciente) {
        if (paciente.getEmail() != null && pacienteService.buscarPacientePorEmail(paciente.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Ya existe un paciente registrado con el email proporcionado.");
        }

        Paciente pacienteGuardado = pacienteService.guardarPaciente(paciente);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(pacienteGuardado.getId())
                .toUri();
        return ResponseEntity.created(location).body(pacienteGuardado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> buscarPorId(@PathVariable Long id) {
        Optional<Paciente> pacienteBuscado = pacienteService.buscarPacientePorId(id);
        return pacienteBuscado.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Paciente>> listarPacientes() {
        return ResponseEntity.ok(pacienteService.buscarPacientes());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Paciente> buscarPorEmail(@PathVariable String email) {
        return pacienteService.buscarPacientePorEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paciente> actualizarPaciente(@PathVariable Long id, @RequestBody Paciente paciente) {
        return pacienteService.actualizarPaciente(id, paciente)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPaciente(@PathVariable Long id) {
        if (!pacienteService.eliminarPaciente(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
