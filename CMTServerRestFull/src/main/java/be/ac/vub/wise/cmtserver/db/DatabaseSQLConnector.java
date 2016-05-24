/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtserver.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author Sandra
 */
public class DatabaseSQLConnector {
    
    private static DatabaseSQL comp= null;
    static InitialContext ctx = null;
    static DataSource ds = null;
    
    public static void initDB(){
        try {
            ctx = new InitialContext();
            ds = (DataSource)ctx.lookup("jdbc/myCmtData");
            
        } catch (NamingException ex) {
            Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static ResultSet selectQueryDB(String SQLQuery)throws SQLException{
        Connection conn = null;
        conn = ds.getConnection();
        Statement ps = conn.createStatement();
        ResultSet rs = ps.executeQuery(SQLQuery);
        ps.close();
        conn.close();
        return rs;
       
    }
    
    
    
}
