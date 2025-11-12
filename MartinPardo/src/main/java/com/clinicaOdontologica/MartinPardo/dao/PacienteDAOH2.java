package com.clinicaOdontologica.MartinPardo.dao;

import com.clinicaOdontologica.MartinPardo.model.Domicilio;
import com.clinicaOdontologica.MartinPardo.model.Paciente;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PacienteDAOH2 implements iDao<Paciente>{
    private static final String SQL_INSERT="INSERT INTO PACIENTES(NOMBRE, APELLIDO, NUMEROCONTACTO, FECHAINGRESO, DOMICILIO_ID, EMAIL) VALUES(?,?,?,?,?,?)";
    private static final String SQL_SELECT_ONE="SELECT * FROM PACIENTES WHERE ID=?";
    private static final String SQL_DELETE="DELETE FROM PACIENTES WHERE ID=?";
    private static final String SQL_UPDATE="UPDATE PACIENTES SET NOMBRE=?, APELLIDO=?, NUMEROCONTACTO=?, FECHAINGRESO=?, DOMICILIO_ID=?, EMAIL=? WHERE ID=?";
    private static final String SQL_SELECT_BY_EMAIL="SELECT * FROM PACIENTES WHERE EMAIL=?";
    private static final String SQL_SELECT_ALL="SELECT * FROM PACIENTES";


    @Override
    public Paciente guardar(Paciente paciente) {
        Connection connection= null;
        try{
            connection= BD.getConnection();
            DomicilioDAOH2 domicilioDAO= new DomicilioDAOH2();
            Domicilio domicilio= domicilioDAO.guardar(paciente.getDomicilio());
            
            PreparedStatement ps= connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, paciente.getNombre());
            ps.setString(2, paciente.getApellido());
            ps.setInt(3, paciente.getNumeroContacto());
            ps.setDate(4, Date.valueOf(paciente.getFechaIngreso()));
            ps.setLong(5, domicilio.getId());
            ps.setString(6, paciente.getEmail());
            ps.executeUpdate();
            
            ResultSet rs= ps.getGeneratedKeys();
            while(rs.next()){
                paciente.setId(rs.getLong(1));
            }
            System.out.println("paciente guardado");
        }catch (Exception e){
            e.printStackTrace();
        }
        return paciente;
    }

    @Override
    public Paciente buscar(Long id) {
        Connection connection=null;
        Paciente paciente= null;
        Domicilio domicilio= null;
        try{
            connection=BD.getConnection();
            PreparedStatement ps_select_one= connection.prepareStatement(SQL_SELECT_ONE);
            ps_select_one.setLong(1,id);
            ResultSet rs= ps_select_one.executeQuery();
            DomicilioDAOH2 daoAux= new DomicilioDAOH2();
            while(rs.next()){
                domicilio=daoAux.buscar(rs.getLong(6));
                paciente= new Paciente(rs.getLong(1),rs.getString(2),rs.getString(3),rs.getInt(4),rs.getDate(5).toLocalDate(),domicilio,rs.getString(7));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("paciente encontrado");
        return paciente;
    }

    @Override
    public void eliminar(Long id) {
        Connection connection= null;
        try{
            connection= BD.getConnection();
            PreparedStatement ps= connection.prepareStatement(SQL_DELETE);
            ps.setLong(1, id);
            ps.executeUpdate();
            System.out.println("paciente eliminado");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(Paciente paciente) {
        Connection connection= null;
        try{
            connection= BD.getConnection();
            DomicilioDAOH2 domicilioDAO= new DomicilioDAOH2();
            domicilioDAO.actualizar(paciente.getDomicilio());
            
            PreparedStatement ps= connection.prepareStatement(SQL_UPDATE);
            ps.setString(1, paciente.getNombre());
            ps.setString(2, paciente.getApellido());
            ps.setInt(3, paciente.getNumeroContacto());
            ps.setDate(4, Date.valueOf(paciente.getFechaIngreso()));
            ps.setLong(5, paciente.getDomicilio().getId());
            ps.setString(6, paciente.getEmail());
            ps.setLong(7, paciente.getId());
            ps.executeUpdate();
            System.out.println("paciente actualizado");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Paciente buscarGenerico(String parametro) {
        Connection connection= null;
        Paciente paciente= null;
        Domicilio domicilio= null;
        try{
            connection= BD.getConnection();
            PreparedStatement ps= connection.prepareStatement(SQL_SELECT_BY_EMAIL);
            ps.setString(1, parametro);
            ResultSet rs= ps.executeQuery();
            DomicilioDAOH2 daoAux= new DomicilioDAOH2();
            while(rs.next()){
                domicilio=daoAux.buscar(rs.getLong(6));
                paciente= new Paciente(rs.getLong(1),rs.getString(2),rs.getString(3),rs.getInt(4),rs.getDate(5).toLocalDate(),domicilio,rs.getString(7));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("paciente encontrado por email");
        return paciente;
    }

    @Override
    public List<Paciente> buscarTodos() {
        Connection connection= null;
        List<Paciente> pacientes= new ArrayList<>();
        try{
            connection= BD.getConnection();
            PreparedStatement ps= connection.prepareStatement(SQL_SELECT_ALL);
            ResultSet rs= ps.executeQuery();
            DomicilioDAOH2 daoAux= new DomicilioDAOH2();
            while(rs.next()){
                Domicilio domicilio=daoAux.buscar(rs.getLong(6));
                Paciente paciente= new Paciente(rs.getLong(1),rs.getString(2),rs.getString(3),rs.getInt(4),rs.getDate(5).toLocalDate(),domicilio,rs.getString(7));
                pacientes.add(paciente);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("total de pacientes: " + pacientes.size());
        return pacientes;
    }
}
