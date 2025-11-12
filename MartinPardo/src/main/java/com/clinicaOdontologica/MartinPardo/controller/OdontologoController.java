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

@RestController
@RequestMapping("/odontologos")
public class OdontologoController {

    private final OdontologoService odontologoService;

    @Autowired
    public OdontologoController(OdontologoService odontologoService) {
        this.odontologoService = odontologoService;
    }

    @PostMapping
    public ResponseEntity<Odontologo> crearOdontologo(@RequestBody Odontologo odontologo) {
        Odontologo odontologoGuardado = odontologoService.guardarOdontologo(odontologo);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(odontologoGuardado.getId())
                .toUri();
        return ResponseEntity.created(location).body(odontologoGuardado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Odontologo> buscarPorId(@PathVariable Long id) {
        Odontologo odontologo = odontologoService.buscarOdontologoPorId(id);
        if (odontologo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(odontologo);
    }

    @GetMapping
    public ResponseEntity<List<Odontologo>> listarOdontologos() {
        return ResponseEntity.ok(odontologoService.buscarOdontologos());
    }

    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<Odontologo> buscarPorMatricula(@PathVariable String matricula) {
        Odontologo odontologo = odontologoService.buscarOdontologoPorMatricula(matricula);
        if (odontologo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(odontologo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Odontologo> actualizarOdontologo(@PathVariable Long id, @RequestBody Odontologo odontologo) {
        Odontologo existente = odontologoService.buscarOdontologoPorId(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        odontologo.setId(id);
        odontologoService.actualizarOdontologo(odontologo);
        Odontologo actualizado = odontologoService.buscarOdontologoPorId(id);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOdontologo(@PathVariable Long id) {
        Odontologo existente = odontologoService.buscarOdontologoPorId(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        odontologoService.eliminarOdontologo(id);
        return ResponseEntity.noContent().build();
    }
}
