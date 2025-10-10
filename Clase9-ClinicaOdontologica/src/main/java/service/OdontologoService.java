package service;

import dao.iDao;
import model.Odontologo;

import java.util.List;

public class OdontologoService {
    private iDao<Odontologo> odontologoiDao;

    public OdontologoService(iDao<Odontologo> odontologoiDao) {
        this.odontologoiDao = odontologoiDao;
    }
    
    public Odontologo guardarOdontologo(Odontologo odontologo){
        return odontologoiDao.guardar(odontologo);
    }
    
    public Odontologo buscarOdontologoPorId(Integer id){
        return odontologoiDao.buscar(id);
    }
    
    public void eliminarOdontologo(Integer id){
        odontologoiDao.eliminar(id);
    }
    
    public void actualizarOdontologo(Odontologo odontologo){
        odontologoiDao.actualizar(odontologo);
    }
    
    public Odontologo buscarOdontologoPorMatricula(String matricula){
        return odontologoiDao.buscarGenerico(matricula);
    }
    
    public List<Odontologo> buscarOdontologos(){
        return odontologoiDao.buscarTodos();
    }
}

