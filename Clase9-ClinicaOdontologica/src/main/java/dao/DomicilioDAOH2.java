package dao;

import model.Domicilio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DomicilioDAOH2 implements iDao<Domicilio> {
    private static final String SQL_INSERT="INSERT INTO DOMICILIOS(CALLE, NUMERO, LOCALIDAD, PROVINCIA) VALUES(?,?,?,?)";
    private static final String SQL_SELECT_ONE="SELECT * FROM DOMICILIOS WHERE ID=?";
    private static final String SQL_DELETE="DELETE FROM DOMICILIOS WHERE ID=?";
    private static final String SQL_UPDATE="UPDATE DOMICILIOS SET CALLE=?, NUMERO=?, LOCALIDAD=?, PROVINCIA=? WHERE ID=?";
    private static final String SQL_SELECT_BY_CALLE="SELECT * FROM DOMICILIOS WHERE CALLE=?";
    private static final String SQL_SELECT_ALL="SELECT * FROM DOMICILIOS";

    @Override
    public Domicilio guardar(Domicilio domicilio) {
        Connection connection= null;
        try{
            connection= BD.getConnection();
            PreparedStatement ps= connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, domicilio.getCalle());
            ps.setInt(2, domicilio.getNumero());
            ps.setString(3, domicilio.getLocalidad());
            ps.setString(4, domicilio.getProvincia());
            ps.executeUpdate();
            
            ResultSet rs= ps.getGeneratedKeys();
            while(rs.next()){
                domicilio.setId(rs.getInt(1));
            }
            System.out.println("domicilio guardado");
        }catch (Exception e){
            e.printStackTrace();
        }
        return domicilio;
    }

    @Override
    public Domicilio buscar(Integer id) {
        Connection connection= null;
        Domicilio domicilio= null;
        try{
            connection= BD.getConnection();
            PreparedStatement ps_select_one= connection.prepareStatement(SQL_SELECT_ONE);
            ps_select_one.setInt(1,id);
            ResultSet rs= ps_select_one.executeQuery();
            while(rs.next()){
                domicilio= new Domicilio(rs.getInt(1),rs.getString(2),rs.getInt(3),rs.getString(4),rs.getString(5));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("domicilio encontrado");
        return domicilio;
    }

    @Override
    public void eliminar(Integer id) {
        Connection connection= null;
        try{
            connection= BD.getConnection();
            PreparedStatement ps= connection.prepareStatement(SQL_DELETE);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("domicilio eliminado");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(Domicilio domicilio) {
        Connection connection= null;
        try{
            connection= BD.getConnection();
            PreparedStatement ps= connection.prepareStatement(SQL_UPDATE);
            ps.setString(1, domicilio.getCalle());
            ps.setInt(2, domicilio.getNumero());
            ps.setString(3, domicilio.getLocalidad());
            ps.setString(4, domicilio.getProvincia());
            ps.setInt(5, domicilio.getId());
            ps.executeUpdate();
            System.out.println("domicilio actualizado");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Domicilio buscarGenerico(String parametro) {
        Connection connection= null;
        Domicilio domicilio= null;
        try{
            connection= BD.getConnection();
            PreparedStatement ps= connection.prepareStatement(SQL_SELECT_BY_CALLE);
            ps.setString(1, parametro);
            ResultSet rs= ps.executeQuery();
            while(rs.next()){
                domicilio= new Domicilio(rs.getInt(1),rs.getString(2),rs.getInt(3),rs.getString(4),rs.getString(5));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("domicilio encontrado por calle");
        return domicilio;
    }

    @Override
    public List<Domicilio> buscarTodos() {
        Connection connection= null;
        List<Domicilio> domicilios= new ArrayList<>();
        try{
            connection= BD.getConnection();
            PreparedStatement ps= connection.prepareStatement(SQL_SELECT_ALL);
            ResultSet rs= ps.executeQuery();
            while(rs.next()){
                Domicilio domicilio= new Domicilio(rs.getInt(1),rs.getString(2),rs.getInt(3),rs.getString(4),rs.getString(5));
                domicilios.add(domicilio);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("total de domicilios: " + domicilios.size());
        return domicilios;
    }
}
