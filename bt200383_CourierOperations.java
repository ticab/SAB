/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import rs.etf.sab.operations.CourierOperations;

/**
 *
 * @author Tijana
 */
public class bt200383_CourierOperations implements CourierOperations{

    @Override
    public boolean insertCourier(String userName, String licencePlate) {
        bt200383_CourierRequestOperation c = new bt200383_CourierRequestOperation();
        //vec postoji kurir sa istim vozilom
        if (c.insertCourierRequest(userName, licencePlate)!=true && c.changeVehicleInCourierRequest(userName, licencePlate)!=true) {
            return false;
        }
        //dozvoli da postane kurir
        return c.grantRequest(userName);
    }

    @Override
    public boolean deleteCourier(String userName) {
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement prep = connection.prepareStatement("Delete from Kurir where korisnickoIme = ?")){
            prep.setString(1, userName);
            if(prep.executeUpdate()==1) return true;
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return false;
    }

    @Override
    public List<String> getCouriersWithStatus(int i) {
        Connection connection = DB.getInstance().getConnection();
        List<String> rez = new ArrayList<>();
        try (PreparedStatement prep = connection.prepareStatement("Select korisnickoIme from Kurir where status=?")){
            prep.setInt(1, i);
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
    public List<String> getAllCouriers() {
        Connection connection = DB.getInstance().getConnection();
        List<String> rez = new ArrayList<>();
        try (PreparedStatement prep = connection.prepareStatement("Select korisnickoIme from Kurir order by profit desc")){
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
    public BigDecimal getAverageCourierProfit(int i) {
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement prep = connection.prepareStatement("Select AVG(profit) from Kurir where brojIsporucenihPaketa>=?")){
            prep.setInt(1, i);
            ResultSet exec = prep.executeQuery();
            
            if (exec.next()) {
                return exec.getBigDecimal(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return null;
    }
    
}
