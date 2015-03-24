package Pdf2DB;

/**
 * Created by pgicking on 3/23/15.
 */


import com.sun.tools.javac.jvm.ClassFile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLHandler {

    public static void InsertIntoDB(){
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;


        String url = "jdbc:postgresql://localhost/testdb";
        String user = "user12";
        String password = "34klq*";

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery("SELECT VERSION()");

            if (rs.next()) {
                System.out.println(rs.getString(1));
            }

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(ClassFile.Version.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(ClassFile.Version.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }

    public static void GenerateSQLStatements(LinkedHashMap<String, String> map){
        String table = "TBA";

        String query = "INSERT INTO " + table + "(";

        String key = null;
        String value = null;

        for(Map.Entry<String,String> entry : map.entrySet()){
            key += "," + entry.getKey();
            value += "," + entry.getValue();
        }

        query += key + ") VALUES (" + value + ");";

        System.out.println(query);


    }
}
