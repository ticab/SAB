/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import rs.etf.sab.operations.CourierRequestOperation;

/**
 *
 * @author Tijana
 */
public class bt200383_CourierRequestOperation implements CourierRequestOperation{

    @Override
    public boolean insertCourierRequest(String userName, String licencePlate) {
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement prep = connection.prepareStatement("Select * from Vozilo where regBroj = ?")){
            prep.setString(1, licencePlate);
            ResultSet exec = prep.executeQuery();
            if (!exec.next()) {
                System.out.println("ne postoji vozilo");
                return false; //ne postoji vozilo
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        try (PreparedStatement prep = connection.prepareStatement("Select * from Kurir where korisnickoIme = ? and regBroj = ?")){
            prep.setString(1, userName);
            prep.setString(2, licencePlate);
            ResultSet exec = prep.executeQuery();
            if (exec.next()) {
                System.out.println("postoji kurir");
                return false; //postoji kurir
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        try (PreparedStatement prep = connection.prepareStatement("Insert into Zahtev(korisnickoIme, regBroj) values (?,?)")){
            prep.setString(1, userName);
            prep.setString(2, licencePlate);
            
            if (prep.executeUpdate()==1) return true;
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return false; //postoji vec kurir
    }

    @Override
    public boolean deleteCourierRequest(String userName) {
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement prep = connection.prepareStatement("Delete from Zahtev where korisnickoIme = ?")){
            prep.setString(1, userName);
            if(prep.executeUpdate()==1) return true;
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return false;
    }

    @Override
    public boolean changeVehicleInCourierRequest(String userName, String licencePlate) {
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement prep = connection.prepareStatement("Select * from Vozilo where regBroj = ?")){
            prep.setString(1, licencePlate);
            ResultSet exec = prep.executeQuery();
            if (!exec.next()) {
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        try (PreparedStatement prep = connection.prepareStatement("Update Zahtev set regBroj = ? where korisnickoIme = ?")){
            prep.setString(1, licencePlate);
            prep.setString(2, userName);
            if(prep.executeUpdate()==1) return true;
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return false;
    }

    @Override
    public List<String> getAllCourierRequests() {
        Connection connection = DB.getInstance().getConnection();
        List<String> rez = new ArrayList<>();
        try (PreparedStatement prep = connection.prepareStatement("Select korisnickoIme from Zahtev")){
            ResultSet exec = prep.executeQuery();
            
            while (exec.next()) {
                rez.add(exec.getString(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return rez;
    }

    @Override
    public boolean grantRequest(String userName) {
        Connection conn = DB.getInstance().getConnection();
        try ( CallableStatement cs = conn.prepareCall("{ ? = call SP_GrantRequest (?) }")) {
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setString(2, userName);
            
            cs.execute();
            if (cs.getInt(1) == 1) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return false;
    }
    
}
