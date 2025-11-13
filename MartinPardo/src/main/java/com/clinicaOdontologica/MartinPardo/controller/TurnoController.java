package com.clinicaOdontologica.MartinPardo.controller;

import com.clinicaOdontologica.MartinPardo.dto.TurnoDTO;
import com.clinicaOdontologica.MartinPardo.model.Odontologo;
import com.clinicaOdontologica.MartinPardo.model.Paciente;
import com.clinicaOdontologica.MartinPardo.service.OdontologoService;
import com.clinicaOdontologica.MartinPardo.service.PacienteService;
import com.clinicaOdontologica.MartinPardo.service.TurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/turnos")
public class TurnoController {

    private final TurnoService turnoService;
    private final PacienteService pacienteService;
    private final OdontologoService odontologoService;

    @Autowired
    public TurnoController(TurnoService turnoService,
                           PacienteService pacienteService,
                           OdontologoService odontologoService) {
        this.turnoService = turnoService;
        this.pacienteService = pacienteService;
        this.odontologoService = odontologoService;
    }

    @PostMapping
    public ResponseEntity<?> crearTurno(@RequestBody TurnoDTO turnoDTO) {
        ResponseEntity<String> validacion = validarTurnoDTO(turnoDTO);
        if (validacion != null) {
            return validacion;
        }

        Optional<Paciente> paciente = pacienteService.buscarPacientePorId(turnoDTO.getPacienteId());
        if (paciente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Paciente no encontrado: " + turnoDTO.getPacienteId());
        }

        Optional<Odontologo> odontologo = odontologoService.buscarOdontologoPorId(turnoDTO.getOdontologoId());
        if (odontologo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Odontólogo no encontrado: " + turnoDTO.getOdontologoId());
        }

        TurnoDTO creado = turnoService.crearTurno(turnoDTO, paciente.get(), odontologo.get());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getId())
                .toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<TurnoDTO>> listarTurnos() {
        return ResponseEntity.ok(turnoService.listarTurnos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurnoDTO> buscarPorId(@PathVariable Long id) {
        return turnoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarTurno(@PathVariable Long id, @RequestBody TurnoDTO turnoDTO) {
        ResponseEntity<String> validacion = validarTurnoDTO(turnoDTO);
        if (validacion != null) {
            return validacion;
        }

        Optional<Paciente> paciente = pacienteService.buscarPacientePorId(turnoDTO.getPacienteId());
        if (paciente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Paciente no encontrado: " + turnoDTO.getPacienteId());
        }

        Optional<Odontologo> odontologo = odontologoService.buscarOdontologoPorId(turnoDTO.getOdontologoId());
        if (odontologo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Odontólogo no encontrado: " + turnoDTO.getOdontologoId());
        }

        return turnoService.actualizarTurno(id, turnoDTO, paciente.get(), odontologo.get())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTurno(@PathVariable Long id) {
        if (!turnoService.eliminarTurno(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<String> validarTurnoDTO(TurnoDTO turnoDTO) {
        if (turnoDTO.getFecha() == null) {
            return ResponseEntity.badRequest().body("El campo 'fecha' es obligatorio.");
        }
        if (turnoDTO.getPacienteId() == null) {
            return ResponseEntity.badRequest().body("El campo 'pacienteId' es obligatorio.");
        }
        if (turnoDTO.getOdontologoId() == null) {
            return ResponseEntity.badRequest().body("El campo 'odontologoId' es obligatorio.");
        }
        return null;
    }
}
