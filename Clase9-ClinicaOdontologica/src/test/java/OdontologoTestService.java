import dao.BD;
import dao.OdontologoDAOH2;
import model.Odontologo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.OdontologoService;

import java.util.List;

public class OdontologoTestService {
    
    @BeforeAll
    static void inicializarBD(){
        BD.crearTablas();
    }
    
    @Test
    public void guardarOdontologo(){
        //DADO
        OdontologoService odontologoService= new OdontologoService(new OdontologoDAOH2());
        Odontologo odontologo= new Odontologo("Carlos", "Martinez", "MAT-99999");
        //CUANDO
        Odontologo odontologoGuardado= odontologoService.guardarOdontologo(odontologo);
        System.out.println("odontologo guardado con ID: "+odontologoGuardado.getId());
        //ENTONCES
        Assertions.assertNotNull(odontologoGuardado);
        Assertions.assertNotNull(odontologoGuardado.getId());
        Assertions.assertEquals("Carlos", odontologoGuardado.getNombre());
        Assertions.assertEquals("Martinez", odontologoGuardado.getApellido());
        Assertions.assertEquals("MAT-99999", odontologoGuardado.getMatricula());
    }
    
    @Test
    public void buscarOdontologoPorId(){
        //DADO
        OdontologoService odontologoService= new OdontologoService(new OdontologoDAOH2());
        //CUANDO
        Odontologo odontologo= odontologoService.buscarOdontologoPorId(1);
        System.out.println("odontologo encontrado: "+odontologo.toString());
        //ENTONCES
        Assertions.assertNotNull(odontologo);
        Assertions.assertEquals(1, odontologo.getId());
        Assertions.assertEquals("Dr. Nick", odontologo.getNombre());
    }
    
    @Test
    public void buscarOdontologoPorMatricula(){
        //DADO
        OdontologoService odontologoService= new OdontologoService(new OdontologoDAOH2());
        //CUANDO
        Odontologo odontologo= odontologoService.buscarOdontologoPorMatricula("MAT-12345");
        System.out.println("odontologo encontrado por matricula: "+odontologo.toString());
        //ENTONCES
        Assertions.assertNotNull(odontologo);
        Assertions.assertEquals("MAT-12345", odontologo.getMatricula());
        Assertions.assertEquals("Dr. Nick", odontologo.getNombre());
        Assertions.assertEquals("Riviera", odontologo.getApellido());
    }
    
    @Test
    public void buscarTodosOdontologos(){
        //DADO
        OdontologoService odontologoService= new OdontologoService(new OdontologoDAOH2());
        //CUANDO
        List<Odontologo> odontologos= odontologoService.buscarOdontologos();
        System.out.println("total de odontologos encontrados: "+odontologos.size());
        //ENTONCES
        Assertions.assertNotNull(odontologos);
        Assertions.assertTrue(odontologos.size() >= 2);
    }
    
    @Test
    public void actualizarOdontologo(){
        //DADO
        OdontologoService odontologoService= new OdontologoService(new OdontologoDAOH2());
        Odontologo odontologo= odontologoService.buscarOdontologoPorId(2);
        String nuevaMatricula= "MAT-ACTUALIZADO";
        //CUANDO
        odontologo.setMatricula(nuevaMatricula);
        odontologoService.actualizarOdontologo(odontologo);
        Odontologo odontologoActualizado= odontologoService.buscarOdontologoPorId(2);
        System.out.println("odontologo actualizado: "+odontologoActualizado.getMatricula());
        //ENTONCES
        Assertions.assertNotNull(odontologoActualizado);
        Assertions.assertEquals(nuevaMatricula, odontologoActualizado.getMatricula());
    }
    
    @Test
    public void eliminarOdontologo(){
        //DADO
        OdontologoService odontologoService= new OdontologoService(new OdontologoDAOH2());
        Odontologo odontologo= new Odontologo("Temporal", "Test", "MAT-TEMP");
        Odontologo odontologoGuardado= odontologoService.guardarOdontologo(odontologo);
        Integer idOdontologo= odontologoGuardado.getId();
        //CUANDO
        odontologoService.eliminarOdontologo(idOdontologo);
        Odontologo odontologoEliminado= odontologoService.buscarOdontologoPorId(idOdontologo);
        System.out.println("odontologo eliminado, resultado de búsqueda: "+odontologoEliminado);
        //ENTONCES
        Assertions.assertNull(odontologoEliminado);
    }
}

