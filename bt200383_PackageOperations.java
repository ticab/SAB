/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import rs.etf.sab.operations.PackageOperations;

/**
 *
 * @author Tijana
 */
public class bt200383_PackageOperations implements PackageOperations{

    @Override
    public int insertPackage(int distFrom, int distTo, String userName, int packageType, BigDecimal weight) {
        Connection conn = DB.getInstance().getConnection();
        try ( CallableStatement cs = conn.prepareCall("{ ? = call SP_InsertPackage (?,?,?,?,?) }")) {
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, distFrom);
            cs.setInt(3, distTo);
            cs.setString(4, userName);
            cs.setInt(5, packageType);
            cs.setDouble(6, weight.doubleValue());
            
            cs.execute();
            return cs.getInt(1);
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return -1;
    }

    @Override
    public int insertTransportOffer(String couriersUserName, int packageId, BigDecimal pricePercentage) {
        Connection conn = DB.getInstance().getConnection();
        try ( CallableStatement cs = conn.prepareCall("{ ? = call SP_InsertTranspOffer (?,?,?) }")) {
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setString(2, couriersUserName);
            cs.setInt(3, packageId);
            cs.setDouble(4, pricePercentage.doubleValue());
            
            cs.execute();
            return cs.getInt(1);
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return -1;
    }

    @Override
    public boolean acceptAnOffer(int offerId) {
        Connection connection = DB.getInstance().getConnection();
        int idPaket = -1;
        BigDecimal proc = BigDecimal.valueOf(0);
        String kurir_korisnickoIme = " ";
        try (PreparedStatement prep = connection.prepareStatement("Select idPaket, procenatCene, korisnickoIme from Ponuda where idPonuda = ?")){
            prep.setInt(1, offerId);
            ResultSet exec = prep.executeQuery();
            if (!exec.next()) {
                return false;
            }
            idPaket = exec.getInt(1);
            proc = exec.getBigDecimal(2);
            kurir_korisnickoIme = exec.getString(3);
            
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        try (PreparedStatement prep = connection.prepareStatement("Update Paket set kurir = ?, vremePrihvatanjaZahteva = ?, procenat = ? where idPaket = ?")){
            prep.setString(1, kurir_korisnickoIme);
            prep.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            prep.setDouble(3, proc.doubleValue());
            prep.setInt(4, idPaket);
            
            if (prep.executeUpdate()==1) return true;
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return false;
    }

    @Override
    public List<Integer> getAllOffers() {
        Connection connection = DB.getInstance().getConnection();
        List<Integer> rez = new ArrayList<>();
        try (PreparedStatement prep = connection.prepareStatement("Select idPonuda from Ponuda")){
            ResultSet exec = prep.executeQuery();
            
            while (exec.next()) {
                rez.add(exec.getInt(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return rez;
    }
    
    class Par<A, B> implements PackageOperations.Pair<A, B> {
        private A a;
        private B b;
              

        @Override
        public A getFirstParam() {
            return a;
        }

        @Override
        public B getSecondParam() {
            return b;
        }

        public Par(A a, B b) {
            this.a = a;
            this.b = b;
        }
    }

    @Override
    public List<Pair<Integer, BigDecimal>> getAllOffersForPackage(int i) {
        Connection connection = DB.getInstance().getConnection();
        List<Pair<Integer, BigDecimal>> rez = new ArrayList<>();
        try (PreparedStatement prep = connection.prepareStatement("Select idPonuda, procenat from Ponuda where idPaket=?")){
            prep.setInt(1, i);
            ResultSet exec = prep.executeQuery();
            
            while (exec.next()) {
                rez.add(new Par(exec.getInt(1), exec.getBigDecimal(2)));
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return rez;
    }

    @Override
    public boolean deletePackage(int i) {
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement prep = connection.prepareStatement("Delete from Paket where idPaket = ?")){
            prep.setInt(1, i);
            if(prep.executeUpdate()==1) return true;
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return false;
    }

    @Override
    public boolean changeWeight(int i, BigDecimal bd) {
        Connection connection = DB.getInstance().getConnection();
        if (!getAllOffersForPackage(i).isEmpty()) return false;
        
        try (PreparedStatement prep = connection.prepareStatement("Update Paket set tezina = ? where idPaket = ? and status = 0")){
            prep.setDouble(1, bd.doubleValue());
            prep.setInt(2, i);
            if(prep.executeUpdate()==1) return true;
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return false;
    }

    @Override
    public boolean changeType(int packageID, int newType) {
       Connection connection = DB.getInstance().getConnection();
        if (!getAllOffersForPackage(packageID).isEmpty()) return false;
        
        try (PreparedStatement prep = connection.prepareStatement("Update Paket set tip = ? where idPaket = ? and status = 0")){
            prep.setInt(1, newType);
            prep.setInt(2, packageID);
            if(prep.executeUpdate()==1) return true;
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return false; 
    }

    @Override
    public Integer getDeliveryStatus(int i) {
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement prep = connection.prepareStatement("Select statusIsporuke from Paket where idPaket = ?")){
            prep.setInt(1, i);
            ResultSet exec = prep.executeQuery();
            
            if (!exec.next()) {
                return null;
            }
            return exec.getInt(1);
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return null;
    }

    @Override
    public BigDecimal getPriceOfDelivery(int i) {
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement prep = connection.prepareStatement("Select cena from Paket where idPaket = ?")){
            prep.setInt(1, i);
            ResultSet exec = prep.executeQuery();
            
            if (!exec.next() || exec.wasNull()) {
                return null;
            }
            return exec.getBigDecimal(1);
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return null;
    }

    @Override
    public Date getAcceptanceTime(int i) {
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement prep = connection.prepareStatement("Select vremePrihvatanjaZahteva from Paket where idPaket = ?")){
            prep.setInt(1, i);
            ResultSet exec = prep.executeQuery();
            
            if (!exec.next()) {
                return null;
            }
            return exec.getDate(1);
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return null;
    }

    @Override
    public List<Integer> getAllPackagesWithSpecificType(int i) {
        Connection connection = DB.getInstance().getConnection();
        List<Integer> rez = new ArrayList<>();
        try (PreparedStatement prep = connection.prepareStatement("Select idPaket from Paket where tip = ?")){
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
    public List<Integer> getAllPackages() {
        Connection connection = DB.getInstance().getConnection();
        List<Integer> rez = new ArrayList<>();
        try (PreparedStatement prep = connection.prepareStatement("Select idPaket from Paket")){
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
    public List<Integer> getDrive(String string) {
        Connection connection = DB.getInstance().getConnection();
        List<Integer> rez = new ArrayList<>();
        try (PreparedStatement prep = connection.prepareStatement("Select idPaket from Paket where kurir = ? and statusIsporuke < 3 order by vremePrihvatanjaZahteva asc")){
            prep.setString(1, string);
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
    public int driveNextPackage(String kurir) {
        Connection connection = DB.getInstance().getConnection();
        int st = -1;
        try (PreparedStatement prep = connection.prepareStatement("Select status from Kurir where korisnickoIme = ?")) {
            prep.setString(1, kurir);
            ResultSet rs = prep.executeQuery();
            if (!rs.next()) {
                return -2;
            }
            st = rs.getInt(1);
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }

        List<Integer> lista = getDrive(kurir);

        if (lista.isEmpty()) {
            try (PreparedStatement prep = connection.prepareStatement("Update Kurir set status = 0 where korisnickoIme = ?")) {
                prep.setString(1, kurir);
                prep.executeUpdate();
            } catch (SQLException ex) {}
            return -1;
        }
        if (st == 0) {
            try (PreparedStatement prep = connection.prepareStatement("Update Kurir set status = 1 where korisnickoIme = ?")) {
                prep.setString(1, kurir);
                prep.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace(System.out);
            }
        }
        try (PreparedStatement prep = connection.prepareStatement("update Paket set statusIsporuke = ? where idPaket = ?")) {
            prep.setInt(1, 3);
            prep.setInt(2, lista.get(0));
            prep.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        
        
        int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
        try (PreparedStatement prep = connection.prepareStatement("select o.x, o.y from Paket p inner join Opstina o on p.idOpstinaPreuzmi = o.idOpstina and p.idPaket = ?")) {
            prep.setInt(1, lista.get(0));
            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                x1 = rs.getInt(1);
                y1 = rs.getInt(2);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        try (PreparedStatement prep = connection.prepareStatement("select o.x, o.y from Paket p inner join Opstina o on p.idOpstinaDostavi = o.idOpstina and p.idPaket = ?")) {
            prep.setInt(1, lista.get(0));
            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                x2 = rs.getInt(1);
                y2 = rs.getInt(2);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        Double dist = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        if (lista.size() > 1) {
            int x3 = 0, y3 = 0;
            try (PreparedStatement prep = connection.prepareStatement("select o.x, o.y from Paket p inner join Opstina o on p.idOpstinaPreuzmi = o.idOpstina and p.idPaket = ?")) {
                prep.setInt(1, lista.get(1));
                ResultSet rs = prep.executeQuery();
                if (rs.next()) {
                    x3 = rs.getInt(1);
                    y3 = rs.getInt(2);
                }
            } catch (SQLException ex) {
                ex.printStackTrace(System.out);
            }
            
            dist += Math.sqrt(Math.pow(x3 - x2, 2) + Math.pow(y3 - y2, 2));
            try (PreparedStatement prep = connection.prepareStatement("update Paket set statusIsporuke = ? where idPaket = ?")) {
                prep.setInt(1, 2);
                prep.setInt(2, lista.get(1));
                prep.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace(System.out);
            }
        }
        Double cena = Double.valueOf(0);
        try (PreparedStatement prep = connection.prepareStatement("select cena from Paket where idPaket = ?")) {
            prep.setInt(1, lista.get(0));
            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                cena = rs.getBigDecimal(1).doubleValue();
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        Double potrosnja = Double.valueOf(0);
        int tip = 0;
        try (PreparedStatement prep = connection.prepareStatement("select v.potrosnja, v.tipGoriva from Kurir k inner join Vozilo v on k.regBroj = v.regBroj and k.korisnickoIme = ?")) {
            prep.setString(1, kurir);
            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                potrosnja = rs.getBigDecimal(1).doubleValue();
                tip = rs.getInt(2);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        switch (tip) {
            case 0:
                potrosnja *= 15;
                break;
            case 1:
                potrosnja *= 32;
                break;
            case 2:
                potrosnja *= 36;
                break;
        }
        try (PreparedStatement prep = connection.prepareStatement("update Kurir set profit = profit + ?, brojIsporucenihPaketa = brojIsporucenihPaketa + 1  where korisnickoIme = ?")) {
            prep.setDouble(1, cena-potrosnja*dist);
            prep.setString(2, kurir);
            prep.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        
        return lista.get(0);
    }
    
}
