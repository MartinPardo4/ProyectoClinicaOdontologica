package com.clinicaOdontologica.MartinPardo.controller;

import com.clinicaOdontologica.MartinPardo.dto.TurnoDTO;
import com.clinicaOdontologica.MartinPardo.exception.ResourceNotFoundException;
import com.clinicaOdontologica.MartinPardo.exception.TurnoConflictException;
import com.clinicaOdontologica.MartinPardo.exception.ValidationException;
import com.clinicaOdontologica.MartinPardo.model.Odontologo;
import com.clinicaOdontologica.MartinPardo.model.Paciente;
import com.clinicaOdontologica.MartinPardo.service.OdontologoService;
import com.clinicaOdontologica.MartinPardo.service.PacienteService;
import com.clinicaOdontologica.MartinPardo.service.TurnoService;
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
    public ResponseEntity<?> crearTurno(@RequestBody TurnoDTO turnoDTO) throws ValidationException, ResourceNotFoundException, TurnoConflictException {
        validarTurnoDTO(turnoDTO);

        Optional<Paciente> paciente = pacienteService.buscarPacientePorId(turnoDTO.getPacienteId());
        if (paciente.isEmpty()) {
            throw new ResourceNotFoundException("Paciente no encontrado: " + turnoDTO.getPacienteId());
        }

        Optional<Odontologo> odontologo = odontologoService.buscarOdontologoPorId(turnoDTO.getOdontologoId());
        if (odontologo.isEmpty()) {
            throw new ResourceNotFoundException("Odontólogo no encontrado: " + turnoDTO.getOdontologoId());
        }

        if (turnoService.existeTurno(turnoDTO.getFecha(), turnoDTO.getPacienteId(), turnoDTO.getOdontologoId())) {
            throw new TurnoConflictException("Ya existe un turno asignado con la misma fecha, paciente y odontólogo.");
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
    public ResponseEntity<TurnoDTO> buscarPorId(@PathVariable Long id) throws ResourceNotFoundException {
        return turnoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado con ID: " + id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarTurno(@PathVariable Long id, @RequestBody TurnoDTO turnoDTO) throws ValidationException, ResourceNotFoundException {
        validarTurnoDTO(turnoDTO);

        Optional<Paciente> paciente = pacienteService.buscarPacientePorId(turnoDTO.getPacienteId());
        if (paciente.isEmpty()) {
            throw new ResourceNotFoundException("Paciente no encontrado: " + turnoDTO.getPacienteId());
        }

        Optional<Odontologo> odontologo = odontologoService.buscarOdontologoPorId(turnoDTO.getOdontologoId());
        if (odontologo.isEmpty()) {
            throw new ResourceNotFoundException("Odontólogo no encontrado: " + turnoDTO.getOdontologoId());
        }

        return turnoService.actualizarTurno(id, turnoDTO, paciente.get(), odontologo.get())
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado con ID: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTurno(@PathVariable Long id) throws ResourceNotFoundException {
        if (!turnoService.eliminarTurno(id)) {
            throw new ResourceNotFoundException("Turno no encontrado con ID: " + id);
        }
        return ResponseEntity.noContent().build();
    }

    private void validarTurnoDTO(TurnoDTO turnoDTO) throws ValidationException {
        if (turnoDTO.getFecha() == null) {
            throw new ValidationException("El campo 'fecha' es obligatorio.");
        }
        if (turnoDTO.getPacienteId() == null) {
            throw new ValidationException("El campo 'pacienteId' es obligatorio.");
        }
        if (turnoDTO.getOdontologoId() == null) {
            throw new ValidationException("El campo 'odontologoId' es obligatorio.");
        }
    }
}
