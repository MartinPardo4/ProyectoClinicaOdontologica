package service;

import dao.iDao;
import model.Paciente;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {
    private iDao<Paciente> pacienteiDao;

    public PacienteService(iDao<Paciente> pacienteiDao) {
        this.pacienteiDao = pacienteiDao;
    }
    public Paciente guardarPaciente(Paciente paciente){
        return pacienteiDao.guardar(paciente);
    }
    
    public Paciente buscarPacientePorId(Integer id){
        return pacienteiDao.buscar(id);
    }
    
    public void eliminarPaciente(Integer id){
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
