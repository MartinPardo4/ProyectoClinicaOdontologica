package com.clinicaOdontologica.MartinPardo.controller;

import com.clinicaOdontologica.MartinPardo.model.Domicilio;
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

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    @Autowired
    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping
    public ResponseEntity<Paciente> crearPaciente(@RequestBody Paciente paciente) {
        Paciente pacienteGuardado = pacienteService.guardarPaciente(paciente);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(pacienteGuardado.getId())
                .toUri();
        return ResponseEntity.created(location).body(pacienteGuardado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> buscarPorId(@PathVariable Long id) {
        Paciente pacienteBuscado = pacienteService.buscarPacientePorId(id);
        if (pacienteBuscado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pacienteBuscado);
    }

    @GetMapping
    public ResponseEntity<List<Paciente>> listarPacientes() {
        return ResponseEntity.ok(pacienteService.buscarPacientes());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Paciente> buscarPorEmail(@PathVariable String email) {
        Paciente paciente = pacienteService.buscarPacientePorEmail(email);
        if (paciente == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(paciente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paciente> actualizarPaciente(@PathVariable Long id, @RequestBody Paciente paciente) {
        Paciente existente = pacienteService.buscarPacientePorId(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        paciente.setId(id);
        ajustarDomicilioParaActualizacion(paciente, existente.getDomicilio());

        pacienteService.actualizarPaciente(paciente);
        Paciente actualizado = pacienteService.buscarPacientePorId(id);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPaciente(@PathVariable Long id) {
        Paciente existente = pacienteService.buscarPacientePorId(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        pacienteService.eliminarPaciente(id);
        return ResponseEntity.noContent().build();
    }

    private void ajustarDomicilioParaActualizacion(Paciente paciente, Domicilio domicilioExistente) {
        if (paciente.getDomicilio() != null) {
            Domicilio domicilioActualizado = paciente.getDomicilio();
            if (domicilioActualizado.getId() == null && domicilioExistente != null) {
                domicilioActualizado.setId(domicilioExistente.getId());
            }
        } else if (domicilioExistente != null) {
            paciente.setDomicilio(domicilioExistente);
        }
    }
}
