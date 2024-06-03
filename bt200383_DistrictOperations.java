package student;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import rs.etf.sab.operations.DistrictOperations;

/**
 *
 * @author Tijana
 */
public class bt200383_DistrictOperations implements DistrictOperations{

    @Override
    public int insertDistrict(String name, int cityId, int xCord, int yCord) {
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement prep = connection.prepareStatement("Insert into Opstina(naziv,x,y,idGrad) values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS)){
            prep.setString(1, name);
            prep.setInt(2, xCord);
            prep.setInt(3, yCord);
            prep.setInt(4, cityId);
            
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
    public int deleteDistricts(String... strings) {
        int n = strings.length;
        
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<n; i++){
            builder.append("?,");
        }
        builder.deleteCharAt(builder.length() - 1);
        Connection connection = DB.getInstance().getConnection();
        try(PreparedStatement prep = connection.prepareStatement("Delete from Opstina where naziv in (" + builder.toString() + ")")){
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
    public boolean deleteDistrict(int i) {
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement prep = connection.prepareStatement("Delete from Opstina where idOpstina = ?")){
            prep.setInt(1, i);
            if(prep.executeUpdate()==1) return true;
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return false;
    }

    @Override
    public int deleteAllDistrictsFromCity(String cityName) {
        int idGrad = -1;
        
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement prep = connection.prepareStatement("Select idGrad from Grad where naziv = ?")){
            prep.setString(1, cityName);
            ResultSet r = prep.executeQuery();
            if(!r.next()){
                return 0;
            }
            idGrad = r.getInt(1);
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        
        try (PreparedStatement prep = connection.prepareStatement("Delete from Opstina where idGrad = ?")){
            prep.setInt(1, idGrad);
            return prep.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        
        return 0;
    }

    @Override
    public List<Integer> getAllDistrictsFromCity(int i) {
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement prep = connection.prepareStatement("Select * from Grad where idGrad = ?")) {
            prep.setInt(1, i);
            try (ResultSet rs = prep.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
            return null;
        }
        List<Integer> rez = new ArrayList<>();
        try (PreparedStatement prep = connection.prepareStatement("Select idOpstina from Opstina where idGrad = ?")){
            prep.setInt(1, i);
            ResultSet exec = prep.executeQuery();
            
            while (exec.next()) {
                rez.add(exec.getInt(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return rez;
    }

    @Override
    public List<Integer> getAllDistricts() {
        Connection connection = DB.getInstance().getConnection();
        List<Integer> rez = new ArrayList<>();
        try (PreparedStatement prep = connection.prepareStatement("Select idOpstina from Opstina")){
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
