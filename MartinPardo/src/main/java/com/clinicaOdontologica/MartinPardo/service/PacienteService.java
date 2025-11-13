package com.clinicaOdontologica.MartinPardo.service;

import com.clinicaOdontologica.MartinPardo.model.Domicilio;
import com.clinicaOdontologica.MartinPardo.model.Paciente;
import com.clinicaOdontologica.MartinPardo.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    @Autowired
    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public Paciente guardarPaciente(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    public Optional<Paciente> buscarPacientePorId(Long id) {
        return pacienteRepository.findById(id);
    }

    public List<Paciente> buscarPacientes() {
        return pacienteRepository.findAll();
    }

    public Optional<Paciente> buscarPacientePorEmail(String email) {
        return pacienteRepository.findByEmail(email);
    }

    public Optional<Paciente> actualizarPaciente(Long id, Paciente paciente) {
        return pacienteRepository.findById(id)
                .map(existing -> mapPaciente(existing, paciente));
    }

    public boolean eliminarPaciente(Long id) {
        if (!pacienteRepository.existsById(id)) {
            return false;
        }
        pacienteRepository.deleteById(id);
        return true;
    }

    private Paciente mapPaciente(Paciente destino, Paciente origen) {
        destino.setNombre(origen.getNombre());
        destino.setApellido(origen.getApellido());
        destino.setEmail(origen.getEmail());
        destino.setNumeroContacto(origen.getNumeroContacto());
        destino.setFechaIngreso(origen.getFechaIngreso());

        if (origen.getDomicilio() != null) {
            if (destino.getDomicilio() == null) {
                destino.setDomicilio(origen.getDomicilio());
            } else {
                actualizarDomicilio(destino.getDomicilio(), origen.getDomicilio());
            }
        }

        return pacienteRepository.save(destino);
    }

    private void actualizarDomicilio(Domicilio destino, Domicilio origen) {
        destino.setCalle(origen.getCalle());
        destino.setNumero(origen.getNumero());
        destino.setLocalidad(origen.getLocalidad());
        destino.setProvincia(origen.getProvincia());
    }
}
