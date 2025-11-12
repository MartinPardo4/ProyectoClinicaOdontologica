package com.clinicaOdontologica.MartinPardo.dao;

import com.clinicaOdontologica.MartinPardo.model.Odontologo;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OdontologoDAOH2 implements iDao<Odontologo> {
    private static final String SQL_INSERT="INSERT INTO ODONTOLOGOS(NOMBRE, APELLIDO, MATRICULA) VALUES(?,?,?)";
    private static final String SQL_SELECT_ONE="SELECT * FROM ODONTOLOGOS WHERE ID=?";
    private static final String SQL_DELETE="DELETE FROM ODONTOLOGOS WHERE ID=?";
    private static final String SQL_UPDATE="UPDATE ODONTOLOGOS SET NOMBRE=?, APELLIDO=?, MATRICULA=? WHERE ID=?";
    private static final String SQL_SELECT_BY_MATRICULA="SELECT * FROM ODONTOLOGOS WHERE MATRICULA=?";
    private static final String SQL_SELECT_ALL="SELECT * FROM ODONTOLOGOS";

    @Override
    public Odontologo guardar(Odontologo odontologo) {
        Connection connection= null;
        try{
            connection= BD.getConnection();
            PreparedStatement ps= connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, odontologo.getNombre());
            ps.setString(2, odontologo.getApellido());
            ps.setString(3, odontologo.getMatricula());
            ps.executeUpdate();
            
            ResultSet rs= ps.getGeneratedKeys();
            while(rs.next()){
                odontologo.setId(rs.getLong(1));
            }
            System.out.println("odontologo guardado");
        }catch (Exception e){
            e.printStackTrace();
        }
        return odontologo;
    }

    @Override
    public Odontologo buscar(Long id) {
        Connection connection= null;
        Odontologo odontologo= null;
        try{
            connection= BD.getConnection();
            PreparedStatement ps_select_one= connection.prepareStatement(SQL_SELECT_ONE);
            ps_select_one.setLong(1,id);
            ResultSet rs= ps_select_one.executeQuery();
            while(rs.next()){
                odontologo= new Odontologo(rs.getLong(1),rs.getString(2),rs.getString(3),rs.getString(4));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("odontologo encontrado");
        return odontologo;
    }

    @Override
    public void eliminar(Long id) {
        Connection connection= null;
        try{
            connection= BD.getConnection();
            PreparedStatement ps= connection.prepareStatement(SQL_DELETE);
            ps.setLong(1, id);
            ps.executeUpdate();
            System.out.println("odontologo eliminado");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(Odontologo odontologo) {
        Connection connection= null;
        try{
            connection= BD.getConnection();
            PreparedStatement ps= connection.prepareStatement(SQL_UPDATE);
            ps.setString(1, odontologo.getNombre());
            ps.setString(2, odontologo.getApellido());
            ps.setString(3, odontologo.getMatricula());
            ps.setLong(4, odontologo.getId());
            ps.executeUpdate();
            System.out.println("odontologo actualizado");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Odontologo buscarGenerico(String parametro) {
        Connection connection= null;
        Odontologo odontologo= null;
        try{
            connection= BD.getConnection();
            PreparedStatement ps= connection.prepareStatement(SQL_SELECT_BY_MATRICULA);
            ps.setString(1, parametro);
            ResultSet rs= ps.executeQuery();
            while(rs.next()){
                odontologo= new Odontologo(rs.getLong(1),rs.getString(2),rs.getString(3),rs.getString(4));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("odontologo encontrado por matricula");
        return odontologo;
    }

    @Override
    public List<Odontologo> buscarTodos() {
        Connection connection= null;
        List<Odontologo> odontologos= new ArrayList<>();
        try{
            connection= BD.getConnection();
            PreparedStatement ps= connection.prepareStatement(SQL_SELECT_ALL);
            ResultSet rs= ps.executeQuery();
            while(rs.next()){
                Odontologo odontologo= new Odontologo(rs.getLong(1),rs.getString(2),rs.getString(3),rs.getString(4));
                odontologos.add(odontologo);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("total de odontologos: " + odontologos.size());
        return odontologos;
    }
}

