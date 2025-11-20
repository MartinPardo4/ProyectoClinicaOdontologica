package com.clinicaOdontologica.MartinPardo.controller;

import com.clinicaOdontologica.MartinPardo.exception.DuplicateResourceException;
import com.clinicaOdontologica.MartinPardo.exception.ResourceNotFoundException;
import com.clinicaOdontologica.MartinPardo.model.Odontologo;
import com.clinicaOdontologica.MartinPardo.service.OdontologoService;
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
@RequestMapping("/odontologos")
public class OdontologoController {

    private final OdontologoService odontologoService;

    @Autowired
    public OdontologoController(OdontologoService odontologoService) {
        this.odontologoService = odontologoService;
    }

    @PostMapping
    public ResponseEntity<?> crearOdontologo(@RequestBody Odontologo odontologo) throws DuplicateResourceException {
        if (odontologo.getMatricula() != null && odontologoService.buscarOdontologoPorMatricula(odontologo.getMatricula()).isPresent()) {
            throw new DuplicateResourceException("Ya existe un odontólogo registrado con la matrícula proporcionada: " + odontologo.getMatricula());
        }

        Odontologo odontologoGuardado = odontologoService.guardarOdontologo(odontologo);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(odontologoGuardado.getId())
                .toUri();
        return ResponseEntity.created(location).body(odontologoGuardado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Odontologo> buscarPorId(@PathVariable Long id) throws ResourceNotFoundException {
        Optional<Odontologo> odontologo = odontologoService.buscarOdontologoPorId(id);
        return ResponseEntity.ok(odontologo.orElseThrow(() -> 
            new ResourceNotFoundException("Odontólogo no encontrado con ID: " + id)));
    }

    @GetMapping
    public ResponseEntity<List<Odontologo>> listarOdontologos() {
        return ResponseEntity.ok(odontologoService.buscarOdontologos());
    }

    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<Odontologo> buscarPorMatricula(@PathVariable String matricula) throws ResourceNotFoundException {
        return ResponseEntity.ok(odontologoService.buscarOdontologoPorMatricula(matricula)
                .orElseThrow(() -> new ResourceNotFoundException("Odontólogo no encontrado con matrícula: " + matricula)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Odontologo> actualizarOdontologo(@PathVariable Long id, @RequestBody Odontologo odontologo) throws ResourceNotFoundException, DuplicateResourceException {
        // Verificar si el odontólogo existe
        if (!odontologoService.buscarOdontologoPorId(id).isPresent()) {
            throw new ResourceNotFoundException("Odontólogo no encontrado con ID: " + id);
        }

        // Verificar si la matrícula ya existe en otro odontólogo
        if (odontologo.getMatricula() != null) {
            Optional<Odontologo> odontologoConMatricula = odontologoService.buscarOdontologoPorMatricula(odontologo.getMatricula());
            if (odontologoConMatricula.isPresent() && !odontologoConMatricula.get().getId().equals(id)) {
                throw new DuplicateResourceException("Ya existe otro odontólogo registrado con la matrícula proporcionada: " + odontologo.getMatricula());
            }
        }

        return ResponseEntity.ok(odontologoService.actualizarOdontologo(id, odontologo)
                .orElseThrow(() -> new ResourceNotFoundException("Error al actualizar el odontólogo con ID: " + id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOdontologo(@PathVariable Long id) throws ResourceNotFoundException {
        if (!odontologoService.eliminarOdontologo(id)) {
            throw new ResourceNotFoundException("Odontólogo no encontrado con ID: " + id);
        }
        return ResponseEntity.noContent().build();
    }
}
