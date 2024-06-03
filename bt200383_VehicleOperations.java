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
import rs.etf.sab.operations.VehicleOperations;

/**
 *
 * @author Tijana
 */
public class bt200383_VehicleOperations implements VehicleOperations{

    @Override
    public boolean insertVehicle(String licencePlate, int i, BigDecimal bd) {
        Connection connection = DB.getInstance().getConnection();
        
        try (PreparedStatement prep = connection.prepareStatement("Insert into Vozilo(regBroj,potrosnja,tipGoriva) values (?,?,?)", Statement.RETURN_GENERATED_KEYS)){
            prep.setString(1, licencePlate);
            prep.setDouble(2, bd.doubleValue());
            prep.setInt(3, i);
            
            
            if (prep.executeUpdate()==1) return true;
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return false;
    }

    @Override
    public int deleteVehicles(String... strings) {
        int n = strings.length;
        
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<n; i++){
            builder.append("?,");
        }
        builder.deleteCharAt(builder.length() - 1);
        Connection connection = DB.getInstance().getConnection();
        try(PreparedStatement stmt = connection.prepareStatement("Delete from Vozilo where regBroj in (" + builder.toString() + ")")){
            for (int i=0; i<n; i++){
                stmt.setString(i+1, strings[i]);
            }
            return stmt.executeUpdate();
            
            
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return 0;
    }

    @Override
    public List<String> getAllVehichles() {
        Connection connection = DB.getInstance().getConnection();
        List<String> rez = new ArrayList<>();
        try (PreparedStatement prep = connection.prepareStatement("Select regBroj from Vozilo")){
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
    public boolean changeFuelType(String string, int i) {
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement prep = connection.prepareStatement("Update Vozilo set tipGoriva = ? where regBroj = ?")){
            prep.setInt(1, i);
            prep.setString(2, string);
            if(prep.executeUpdate()==1) return true;
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return false;
    }

    @Override
    public boolean changeConsumption(String string, BigDecimal bd) {
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement prep = connection.prepareStatement("Update Vozilo set potrosnja = ? where regBroj = ?")){
            prep.setDouble(1, bd.doubleValue());
            prep.setString(2, string);
            if(prep.executeUpdate()==1) return true;
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return false;
    }
    
}
