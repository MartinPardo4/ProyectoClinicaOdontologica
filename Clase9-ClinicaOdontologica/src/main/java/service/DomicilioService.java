package service;

import dao.iDao;
import model.Domicilio;

import java.util.List;

public class DomicilioService {
    private iDao<Domicilio> domicilioiDao;

    public DomicilioService(iDao<Domicilio> domicilioiDao) {
        this.domicilioiDao = domicilioiDao;
    }
    
    public Domicilio guardarDomicilio(Domicilio domicilio){
        return domicilioiDao.guardar(domicilio);
    }
    
    public Domicilio buscarDomicilioPorId(Integer id){
        return domicilioiDao.buscar(id);
    }
    
    public void eliminarDomicilio(Integer id){
        domicilioiDao.eliminar(id);
    }
    
    public void actualizarDomicilio(Domicilio domicilio){
        domicilioiDao.actualizar(domicilio);
    }
    
    public Domicilio buscarDomicilioPorCalle(String calle){
        return domicilioiDao.buscarGenerico(calle);
    }
    
    public List<Domicilio> buscarDomicilios(){
        return domicilioiDao.buscarTodos();
    }
}

