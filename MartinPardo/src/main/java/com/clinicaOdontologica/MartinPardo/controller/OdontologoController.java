package com.clinicaOdontologica.MartinPardo.controller;

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
    public ResponseEntity<?> crearOdontologo(@RequestBody Odontologo odontologo) {
        if (odontologo.getMatricula() != null && odontologoService.buscarOdontologoPorMatricula(odontologo.getMatricula()).isPresent()) {
            return ResponseEntity.badRequest().body("Ya existe un odontólogo registrado con la matrícula proporcionada.");
        }

        Odontologo odontologoGuardado = odontologoService.guardarOdontologo(odontologo);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(odontologoGuardado.getId())
                .toUri();
        return ResponseEntity.created(location).body(odontologoGuardado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Odontologo> buscarPorId(@PathVariable Long id) {
        Optional<Odontologo> odontologo = odontologoService.buscarOdontologoPorId(id);
        return odontologo.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Odontologo>> listarOdontologos() {
        return ResponseEntity.ok(odontologoService.buscarOdontologos());
    }

    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<Odontologo> buscarPorMatricula(@PathVariable String matricula) {
        return odontologoService.buscarOdontologoPorMatricula(matricula)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Odontologo> actualizarOdontologo(@PathVariable Long id, @RequestBody Odontologo odontologo) {
        return odontologoService.actualizarOdontologo(id, odontologo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOdontologo(@PathVariable Long id) {
        if (!odontologoService.eliminarOdontologo(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
