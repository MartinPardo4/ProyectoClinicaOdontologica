import dao.BD;
import dao.DomicilioDAOH2;
import model.Domicilio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.DomicilioService;

import java.util.List;

public class DomicilioTestService {
    
    @BeforeAll
    static void inicializarBD(){
        BD.crearTablas();
    }
    
    @Test
    public void guardarDomicilio(){
        //DADO
        DomicilioService domicilioService= new DomicilioService(new DomicilioDAOH2());
        Domicilio domicilio= new Domicilio("Avenida Libertador", 1500, "Buenos Aires", "CABA");
        //CUANDO
        Domicilio domicilioGuardado= domicilioService.guardarDomicilio(domicilio);
        System.out.println("domicilio guardado con ID: "+domicilioGuardado.getId());
        //ENTONCES
        Assertions.assertNotNull(domicilioGuardado);
        Assertions.assertNotNull(domicilioGuardado.getId());
        Assertions.assertEquals("Avenida Libertador", domicilioGuardado.getCalle());
        Assertions.assertEquals(1500, domicilioGuardado.getNumero());
    }
    
    @Test
    public void buscarDomicilioPorId(){
        //DADO
        DomicilioService domicilioService= new DomicilioService(new DomicilioDAOH2());
        //CUANDO
        Domicilio domicilio= domicilioService.buscarDomicilioPorId(1);
        System.out.println("domicilio encontrado: "+domicilio.getCalle()+" "+domicilio.getNumero());
        //ENTONCES
        Assertions.assertNotNull(domicilio);
        Assertions.assertEquals(1, domicilio.getId());
        Assertions.assertEquals("siempre viva", domicilio.getCalle());
    }
    
    @Test
    public void buscarDomicilioPorCalle(){
        //DADO
        DomicilioService domicilioService= new DomicilioService(new DomicilioDAOH2());
        //CUANDO
        Domicilio domicilio= domicilioService.buscarDomicilioPorCalle("siempre viva");
        System.out.println("domicilio encontrado por calle: "+domicilio.getCalle()+" "+domicilio.getNumero());
        //ENTONCES
        Assertions.assertNotNull(domicilio);
        Assertions.assertEquals("siempre viva", domicilio.getCalle());
        Assertions.assertEquals(723, domicilio.getNumero());
    }
    
    @Test
    public void buscarTodosDomicilios(){
        //DADO
        DomicilioService domicilioService= new DomicilioService(new DomicilioDAOH2());
        //CUANDO
        List<Domicilio> domicilios= domicilioService.buscarDomicilios();
        System.out.println("total de domicilios encontrados: "+domicilios.size());
        //ENTONCES
        Assertions.assertNotNull(domicilios);
        Assertions.assertTrue(domicilios.size() >= 2);
    }
    
    @Test
    public void actualizarDomicilio(){
        //DADO
        DomicilioService domicilioService= new DomicilioService(new DomicilioDAOH2());
        Domicilio domicilio= domicilioService.buscarDomicilioPorId(2);
        String nuevaProvincia= "Provincia Actualizada";
        //CUANDO
        domicilio.setProvincia(nuevaProvincia);
        domicilioService.actualizarDomicilio(domicilio);
        Domicilio domicilioActualizado= domicilioService.buscarDomicilioPorId(2);
        System.out.println("domicilio actualizado: "+domicilioActualizado.getProvincia());
        //ENTONCES
        Assertions.assertNotNull(domicilioActualizado);
        Assertions.assertEquals(nuevaProvincia, domicilioActualizado.getProvincia());
    }
    
    @Test
    public void eliminarDomicilio(){
        //DADO
        DomicilioService domicilioService= new DomicilioService(new DomicilioDAOH2());
        Domicilio domicilio= new Domicilio("Calle Temporal", 999, "Ciudad Temporal", "Provincia Temporal");
        Domicilio domicilioGuardado= domicilioService.guardarDomicilio(domicilio);
        Integer idDomicilio= domicilioGuardado.getId();
        //CUANDO
        domicilioService.eliminarDomicilio(idDomicilio);
        Domicilio domicilioEliminado= domicilioService.buscarDomicilioPorId(idDomicilio);
        System.out.println("domicilio eliminado, resultado de búsqueda: "+domicilioEliminado);
        //ENTONCES
        Assertions.assertNull(domicilioEliminado);
    }
}

