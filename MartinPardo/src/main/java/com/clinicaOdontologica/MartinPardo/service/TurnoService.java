package com.clinicaOdontologica.MartinPardo.service;

import com.clinicaOdontologica.MartinPardo.dto.TurnoDTO;
import com.clinicaOdontologica.MartinPardo.model.Odontologo;
import com.clinicaOdontologica.MartinPardo.model.Paciente;
import com.clinicaOdontologica.MartinPardo.model.Turno;
import com.clinicaOdontologica.MartinPardo.repository.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TurnoService {

    private final TurnoRepository turnoRepository;

    @Autowired
    public TurnoService(TurnoRepository turnoRepository) {
        this.turnoRepository = turnoRepository;
    }

    public TurnoDTO crearTurno(TurnoDTO turnoDTO, Paciente paciente, Odontologo odontologo) {
        Turno turno = new Turno();
        turno.setFecha(turnoDTO.getFecha());
        turno.setPaciente(paciente);
        turno.setOdontologo(odontologo);

        Turno guardado = turnoRepository.save(turno);
        return mapearADTO(guardado);
    }

    public List<TurnoDTO> listarTurnos() {
        return turnoRepository.findAll()
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    public Optional<TurnoDTO> buscarPorId(Long id) {
        return turnoRepository.findById(id)
                .map(this::mapearADTO);
    }

    public Optional<TurnoDTO> actualizarTurno(Long id, TurnoDTO turnoDTO, Paciente paciente, Odontologo odontologo) {
        return turnoRepository.findById(id)
                .map(turno -> {
                    turno.setFecha(turnoDTO.getFecha());
                    turno.setPaciente(paciente);
                    turno.setOdontologo(odontologo);
                    return mapearADTO(turnoRepository.save(turno));
                });
    }

    public boolean eliminarTurno(Long id) {
        if (!turnoRepository.existsById(id)) {
            return false;
        }
        turnoRepository.deleteById(id);
        return true;
    }

    private TurnoDTO mapearADTO(Turno turno) {
        TurnoDTO dto = new TurnoDTO();
        dto.setId(turno.getId());
        dto.setFecha(turno.getFecha());
        dto.setPacienteId(turno.getPaciente() != null ? turno.getPaciente().getId() : null);
        dto.setOdontologoId(turno.getOdontologo() != null ? turno.getOdontologo().getId() : null);
        return dto;
    }
}
