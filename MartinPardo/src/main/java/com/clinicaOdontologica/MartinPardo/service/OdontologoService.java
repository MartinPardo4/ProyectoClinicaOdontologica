package com.clinicaOdontologica.MartinPardo.service;

import com.clinicaOdontologica.MartinPardo.dao.iDao;
import com.clinicaOdontologica.MartinPardo.model.Odontologo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OdontologoService {
    private iDao<Odontologo> odontologoiDao;

    @Autowired
    public OdontologoService(iDao<Odontologo> odontologoiDao) {
        this.odontologoiDao = odontologoiDao;
    }
    
    public Odontologo guardarOdontologo(Odontologo odontologo){
        return odontologoiDao.guardar(odontologo);
    }
    
    public Odontologo buscarOdontologoPorId(Long id){
        return odontologoiDao.buscar(id);
    }
    
    public void eliminarOdontologo(Long id){
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

