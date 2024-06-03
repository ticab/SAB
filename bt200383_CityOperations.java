package student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import rs.etf.sab.operations.CityOperations;

/**
 *
 * @author Tijana
 */

public class bt200383_CityOperations implements CityOperations{
    
    @Override
    public int insertCity(String name, String postalCode) {

        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement prep = connection.prepareStatement("Select * from Grad where naziv = ? or postBroj = ?")){
            prep.setString(1, name);
            prep.setString(2, postalCode);
            ResultSet exec = prep.executeQuery();
            if (exec.next()) {
                return -1;
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
  
        try (PreparedStatement prep = connection.prepareStatement("Insert into Grad(naziv,postBroj) values (?,?)", Statement.RETURN_GENERATED_KEYS)){
            prep.setString(1, name);
            prep.setString(2, postalCode);
            
            if (prep.executeUpdate()==0) return -1;
            ResultSet rs = prep.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return -1;
    }

    @Override
    public int deleteCity(String... strings) {
        int n = strings.length;
        
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<n; i++){
            builder.append("?,");
        }
        builder.deleteCharAt(builder.length() - 1);
        Connection connection = DB.getInstance().getConnection();
        try(PreparedStatement prep = connection.prepareStatement("Delete from Grad where naziv in (" + builder.toString() + ")")){
            for (int i=0; i<n; i++){
                prep.setString(i+1, strings[i]);
            }
            return prep.executeUpdate();
            
            
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return 0;
    }

    @Override
    public boolean deleteCity(int i) {
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement prep = connection.prepareStatement("Delete from Grad where idGrad = ?")){
            prep.setInt(1, i);
            if(prep.executeUpdate()==1) return true;
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return false;
    }

    @Override
    public List<Integer> getAllCities() {
        Connection connection = DB.getInstance().getConnection();
        List<Integer> rez = new ArrayList<>();
        try (PreparedStatement prep = connection.prepareStatement("Select idGrad from Grad")){
            ResultSet exec = prep.executeQuery();
            
            while (exec.next()) {
                rez.add(exec.getInt(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return rez;
    }
    
}
