/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import rs.etf.sab.operations.UserOperations;

/**
 *
 * @author Tijana
 */
public class bt200383_UserOperations implements UserOperations {

    @Override
    public boolean insertUser(String userName, String firstName, String lastName, String password) {
        if (!Character.isUpperCase(firstName.charAt(0)))
                return false;
        if (!Character.isUpperCase(lastName.charAt(0)))
                return false;
        if (!password.matches(".*[a-zA-Z].*") || !password.matches(".*\\d.*")) {
            return false;
        }
        if (password.length() < 8)
                return false;
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement prep = connection.prepareStatement("Select * from Korisnik where korisnickoIme = ?")){
            prep.setString(1, userName);
            ResultSet exec = prep.executeQuery();
            if (exec.next()) {
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
    
        try (PreparedStatement prep = connection.prepareStatement("Insert into Korisnik(korisnickoIme,ime,prezime,sifra) values (?,?,?,?)")){
            prep.setString(1, userName);
            prep.setString(2, firstName);
            prep.setString(3, lastName);
            prep.setString(4, password);
            
            if (prep.executeUpdate()==1) return true;
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return false;
    }

    @Override
    public int declareAdmin(String userName) {
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement prep = connection.prepareStatement("Select * from Korisnik where korisnickoIme = ?")){
            prep.setString(1, userName);
            ResultSet exec = prep.executeQuery();
            if (!exec.next()) {
                return 2;
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        
        try (PreparedStatement prep = connection.prepareStatement("Select * from Administrator where korisnickoIme = ?")){
            prep.setString(1, userName);
            ResultSet exec = prep.executeQuery();
            if (exec.next()) {
                return 1;
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        try (PreparedStatement prep = connection.prepareStatement("Insert into Administrator(korisnickoIme) values (?)")){
            prep.setString(1, userName);
            
            if (prep.executeUpdate()==1) return 0;
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return 2;
    }

    @Override
    public Integer getSentPackages(String... userNames) {
        int n = userNames.length;
        Integer res = null;
        
        for(int i=0; i<n; i++){
            Connection connection = DB.getInstance().getConnection();
            try (PreparedStatement prep = connection.prepareStatement("Select brojPoslatihPaketa from Korisnik where korisnickoIme = ?")){
                prep.setString(1, userNames[i]);
                ResultSet r = prep.executeQuery();
                if(r.next()){
                    if(res==null) res = 0;
                    res += r.getInt(1);
                }
                
            } catch (SQLException ex) {
                ex.printStackTrace(System.out);
            }
        }
        return res;
    }

    @Override
    public int deleteUsers(String... strings) {
        int n = strings.length;
        
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<n; i++){
            builder.append("?,");
        }
        builder.deleteCharAt(builder.length() - 1);
        Connection connection = DB.getInstance().getConnection();
        try(PreparedStatement stmt = connection.prepareStatement("Delete from Korisnik where korisnickoIme in (" + builder.toString() + ")")){
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
    public List<String> getAllUsers() {
        Connection connection = DB.getInstance().getConnection();
        List<String> rez = new ArrayList<>();
        try (PreparedStatement prep = connection.prepareStatement("Select korisnickoIme from Korisnik")){
            ResultSet exec = prep.executeQuery();
            
            while (exec.next()) {
                rez.add(exec.getString(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return rez;
    }
    
}
