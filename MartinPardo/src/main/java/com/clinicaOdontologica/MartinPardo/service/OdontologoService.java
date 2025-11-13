package com.clinicaOdontologica.MartinPardo.service;

import com.clinicaOdontologica.MartinPardo.model.Odontologo;
import com.clinicaOdontologica.MartinPardo.repository.OdontologoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OdontologoService {

    private final OdontologoRepository odontologoRepository;

    @Autowired
    public OdontologoService(OdontologoRepository odontologoRepository) {
        this.odontologoRepository = odontologoRepository;
    }

    public Odontologo guardarOdontologo(Odontologo odontologo) {
        return odontologoRepository.save(odontologo);
    }

    public List<Odontologo> buscarOdontologos() {
        return odontologoRepository.findAll();
    }

    public Optional<Odontologo> buscarOdontologoPorId(Long id) {
        return odontologoRepository.findById(id);
    }

    public Optional<Odontologo> buscarOdontologoPorMatricula(String matricula) {
        return odontologoRepository.findByMatricula(matricula);
    }

    public Optional<Odontologo> actualizarOdontologo(Long id, Odontologo odontologo) {
        return odontologoRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(odontologo.getNombre());
                    existing.setApellido(odontologo.getApellido());
                    existing.setMatricula(odontologo.getMatricula());
                    return odontologoRepository.save(existing);
                });
    }

    public boolean eliminarOdontologo(Long id) {
        if (!odontologoRepository.existsById(id)) {
            return false;
        }
        odontologoRepository.deleteById(id);
        return true;
    }
}
