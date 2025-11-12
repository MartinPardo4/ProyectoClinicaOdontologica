package com.clinicaOdontologica.MartinPardo.service;

import com.clinicaOdontologica.MartinPardo.dao.iDao;
import com.clinicaOdontologica.MartinPardo.model.Paciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {
    private iDao<Paciente> pacienteiDao;

    @Autowired
    public PacienteService(iDao<Paciente> pacienteiDao) {
        this.pacienteiDao = pacienteiDao;
    }
    public Paciente guardarPaciente(Paciente paciente){
        return pacienteiDao.guardar(paciente);
    }
    
    public Paciente buscarPacientePorId(Long id){
        return pacienteiDao.buscar(id);
    }
    
    public void eliminarPaciente(Long id){
        pacienteiDao.eliminar(id);
    }
    
    public void actualizarPaciente(Paciente paciente){
        pacienteiDao.actualizar(paciente);
    }
    
    public Paciente buscarPacientePorEmail(String email){
        return pacienteiDao.buscarGenerico(email);
    }
    
    public List<Paciente> buscarPacientes(){
        return pacienteiDao.buscarTodos();
    }
}
