import dao.BD;
import dao.PacienteDAOH2;
import model.Domicilio;
import model.Paciente;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.PacienteService;

import java.time.LocalDate;
import java.util.List;

public class PacienteTestService {
    
    @BeforeAll
    static void inicializarBD(){
        BD.crearTablas();
    }
    
    @Test
    public void guardarPaciente(){
        //DADO
        PacienteService pacienteService= new PacienteService(new PacienteDAOH2());
        Domicilio domicilio= new Domicilio("Calle Nueva", 456, "Ciudad", "Provincia");
        Paciente paciente= new Paciente("Juan", "Perez", 987654321, LocalDate.of(2025, 10, 10), domicilio, "juan@email.com");
        //CUANDO
        Paciente pacienteGuardado= pacienteService.guardarPaciente(paciente);
        System.out.println("paciente guardado con ID: "+pacienteGuardado.getId());
        //ENTONCES
        Assertions.assertNotNull(pacienteGuardado);
        Assertions.assertNotNull(pacienteGuardado.getId());
        Assertions.assertEquals("Juan", pacienteGuardado.getNombre());
        Assertions.assertEquals("Perez", pacienteGuardado.getApellido());
    }
    
    @Test
    public void buscarPacientePorId(){
        //DADO
        PacienteService pacienteService= new PacienteService(new PacienteDAOH2());
        //CUANDO
        Paciente paciente= pacienteService.buscarPacientePorId(2);
        System.out.println("datos encontrados: "+paciente.toString());
        //ENTONCES
        Assertions.assertNotNull(paciente);
        Assertions.assertEquals(2, paciente.getId());
    }
    
    @Test
    public void buscarPacientePorEmail(){
        //DADO
        PacienteService pacienteService= new PacienteService(new PacienteDAOH2());
        //CUANDO
        Paciente paciente= pacienteService.buscarPacientePorEmail("homer@disney.com");
        System.out.println("paciente encontrado por email: "+paciente.toString());
        //ENTONCES
        Assertions.assertNotNull(paciente);
        Assertions.assertEquals("homer@disney.com", paciente.getEmail());
        Assertions.assertEquals("Homero", paciente.getNombre());
    }
    
    @Test
    public void buscarTodosPacientes(){
        //DADO
        PacienteService pacienteService= new PacienteService(new PacienteDAOH2());
        //CUANDO
        List<Paciente> pacientes= pacienteService.buscarPacientes();
        System.out.println("total de pacientes encontrados: "+pacientes.size());
        //ENTONCES
        Assertions.assertNotNull(pacientes);
        Assertions.assertTrue(pacientes.size() >= 2);
    }
    
    @Test
    public void actualizarPaciente(){
        //DADO
        PacienteService pacienteService= new PacienteService(new PacienteDAOH2());
        Paciente paciente= pacienteService.buscarPacientePorId(1);
        String nuevoEmail= "homero.simpson@actualizado.com";
        //CUANDO
        paciente.setEmail(nuevoEmail);
        pacienteService.actualizarPaciente(paciente);
        Paciente pacienteActualizado= pacienteService.buscarPacientePorId(1);
        System.out.println("paciente actualizado: "+pacienteActualizado.getEmail());
        //ENTONCES
        Assertions.assertNotNull(pacienteActualizado);
        Assertions.assertEquals(nuevoEmail, pacienteActualizado.getEmail());
    }
    
    @Test
    public void eliminarPaciente(){
        //DADO
        PacienteService pacienteService= new PacienteService(new PacienteDAOH2());
        Domicilio domicilio= new Domicilio("Calle Temporal", 999, "Ciudad", "Provincia");
        Paciente paciente= new Paciente("Temporal", "Test", 111111111, LocalDate.now(), domicilio, "temporal@test.com");
        Paciente pacienteGuardado= pacienteService.guardarPaciente(paciente);
        Integer idPaciente= pacienteGuardado.getId();
        //CUANDO
        pacienteService.eliminarPaciente(idPaciente);
        Paciente pacienteEliminado= pacienteService.buscarPacientePorId(idPaciente);
        System.out.println("paciente eliminado, resultado de búsqueda: "+pacienteEliminado);
        //ENTONCES
        Assertions.assertNull(pacienteEliminado);
    }
}
