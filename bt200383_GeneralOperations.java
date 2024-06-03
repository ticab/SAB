/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import rs.etf.sab.operations.GeneralOperations;

/**
 *
 * @author Tijana
 */
public class bt200383_GeneralOperations implements GeneralOperations{

    @Override
    public void eraseAll() {
        Connection conn = DB.getInstance().getConnection();
        try ( CallableStatement cs = conn.prepareCall("{ call SP_EraseAll() }")) {
            cs.execute();
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
    }
}
