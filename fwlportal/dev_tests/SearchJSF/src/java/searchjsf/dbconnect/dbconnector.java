/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package searchjsf.dbconnect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author mhaft
 */
public class dbconnector {
    private static String jndiCon;
    private static Connection conn;
    private static Statement stmt;

    /*private static void connectDatabase() throws SQLException {
    setConn(getJNDIConnection(getJndiCon()));
    setStmt(getConn().createStatement());
    }

    private static void disconnectDatabase() throws SQLException {
    getConn().close();
    }*/

    public static Connection getJNDIConnection(String jndiContext) {
        String DATASOURCE_CONTEXT = jndiContext;

        Connection result = null;
        try {
            Context initialContext = new InitialContext();
            if (initialContext == null) {
                System.out.println("JNDI problem. Cannot get InitialContext.");
            }
            DataSource datasource = (DataSource) initialContext.lookup(DATASOURCE_CONTEXT);
            if (datasource != null) {
                result = datasource.getConnection();
            } else {
                System.out.println("Failed to lookup datasource.");
            }
        } catch (NamingException ex) {
            System.out.println("Cannot get connection: " + ex);
        } catch (SQLException ex) {
            System.out.println("Cannot get connection: " + ex);
        }
        return result;
    }

    /**
     * @return the jndiCon
     */
    public static String getJndiCon() {
        return jndiCon;
    }

    /**
     * @param aJndiCon the jndiCon to set
     */
    public static void setJndiCon(String aJndiCon) {
    /*// sorts which dbpool to use
    if (aJndiCon.equals("Images")) {
    jndiCon = "jdbc/gallery";
    } else if (aJndiCon.equals("Other"))  {
    jndiCon = "jdbc/other";
    } else {
    System.out.println("Unable to connect. Unknown Database.");
    }*/
    jndiCon = aJndiCon;
    }

    /**
     * @return the stmt
     */
    public static Statement getStmt() {
        return stmt;
    }

    /**
     * @param aStmt the stmt to set
     */
    public static void setStmt(Statement aStmt) {
        stmt = aStmt;
    }
}
