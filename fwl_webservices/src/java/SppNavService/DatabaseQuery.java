/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SppNavService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author root
 */
public class DatabaseQuery {

    static String jndiCon = "jdbc/FwlSpeciesListsPool";
    static Connection conn;
    static Statement stmt;

    /**
     * Returns an array list of Tree objects, each one representing the nav trees
     * held in the database.
     * @return List<Tree>
     */
    public static List<Tree> getListOfTrees() throws SQLException {
        List<Tree> trees = new ArrayList();

        try {
            connectDatabase();
            ResultSet rs = stmt.executeQuery("SELECT * FROM navigation_lists n;");

            while (rs.next()) {
                trees.add(new Tree(rs.getString("name"), rs.getInt("start_point")));
            }

            disconnectDatabase();

            return trees;

        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
            trees = null;
            disconnectDatabase();
            return trees;
        }
    }

    /**
     * Returns the primary key of the first / top node of a navigation tree.
     * @param treeName
     * @return
     */
    public static int getStartPoint(String treeName) throws SQLException {
        int startPoint = -1;

        try {
            connectDatabase();
            ResultSet rs = stmt.executeQuery("SELECT start_point FROM navigation_lists WHERE name='" + treeName + "';");

            //check we got some results
            if (rs.first()) {
                startPoint = rs.getInt("start_point");
            }

            disconnectDatabase();

        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
            startPoint = -1;
            disconnectDatabase();
        }

        return startPoint;
    }

    public static TreeNode getNode(int pk) throws SQLException {
        TreeNode node = new TreeNode();
        node.setPk(pk);

        try {
            connectDatabase();
            ResultSet rs = stmt.executeQuery("SELECT * FROM web_nav WHERE nav_pk='" + pk + "';");

            //check we got some results
            if (rs.last()) {
                rs.first();
                node.setParent(rs.getInt("nav_parent"));
                node.setTaxon_id(rs.getInt("taxon_id"));
                node.setTreeName(rs.getString("nav_list"));
                node.setName(rs.getString("name"));
                node.setParentTaxon_id(rs.getInt("parent_id"));
                node.setParentName(rs.getString("parent"));
                node.setNoChildren(rs.getInt("no_children"));
                node.setFlag(rs.getBoolean("display_flag"));
            }

            disconnectDatabase();

        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
            node = null;
            disconnectDatabase();
        }

        return node;
    }

    public static ArrayList<TreeNode> getChildNodes(int parent_pk) throws SQLException {
        ArrayList<TreeNode> children = new ArrayList();

        try {
            connectDatabase();
            ResultSet rs = stmt.executeQuery("SELECT * FROM web_nav WHERE nav_parent='" + parent_pk + "';");

            while (rs.next()) {
                TreeNode node = new TreeNode();
                node.setPk(rs.getInt("nav_pk"));
                node.setParent(rs.getInt("nav_parent"));
                node.setTaxon_id(rs.getInt("taxon_id"));
                node.setTreeName(rs.getString("nav_list"));
                node.setName(rs.getString("name"));
                node.setParentTaxon_id(rs.getInt("parent_id"));
                node.setParentName(rs.getString("parent"));
                node.setNoChildren(rs.getInt("no_children"));
                node.setFlag(rs.getBoolean("display_flag"));

                children.add(node);
            }

            disconnectDatabase();

        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
            children = null;
            disconnectDatabase();
        }

        return children;
    }

    public static ArrayList<Integer> getAllNodes(String treeName) throws SQLException {
        ArrayList<Integer> nodes = new ArrayList();
        try {
            connectDatabase();
            ResultSet rs = stmt.executeQuery("SELECT nav_pk, nav_parent, nav_list FROM web_nav WHERE nav_list = '" + treeName + "';");

            rs.beforeFirst();
            while (rs.next()) {
                nodes.add(rs.getInt("nav_pk"));
            }

            disconnectDatabase();

        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
            disconnectDatabase();
        }

        return nodes;
    }

    public static boolean updateNoChildren(int pk) throws SQLException {
        boolean completedWithoutErrors = false;

        try {
            //find the number of child nodes
            connectDatabase();
            ResultSet rs = stmt.executeQuery("SELECT Count(*) AS rowcount FROM web_nav WHERE nav_parent = " + pk + ";");
            rs.next();
            int noChildren = rs.getInt("rowcount");
            rs.close();
            disconnectDatabase();

            //update noChildren field
            connectDatabaseUpdateable();
            rs = stmt.executeQuery("SELECT * FROM navigation WHERE nav_pk = " + pk + ";"); //this has to be a table (not a view) and only one table
            rs.next();
            rs.updateInt("children", noChildren);
            rs.updateRow();

            disconnectDatabase();

            completedWithoutErrors = true;

        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
            completedWithoutErrors = false;
            disconnectDatabase();
        }

        return completedWithoutErrors;
    }

    private static void connectDatabase() throws SQLException {
        conn = getJNDIConnection(jndiCon);
        stmt = conn.createStatement();
    }

    private static void connectDatabaseUpdateable() throws SQLException {
        conn = getJNDIConnection(jndiCon);
        stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
    }

    private static void disconnectDatabase() throws SQLException {
        conn.close();
    }

    static Connection getJNDIConnection(String jndiContext) {
        String DATASOURCE_CONTEXT = jndiContext;
//        String DATASOURCE_CONTEXT = "java:comp/env/jdbc/blah";

        Connection result = null;
        try {
            Context initialContext = new InitialContext();
            if (initialContext == null) {
                log("JNDI problem. Cannot get InitialContext.");
            }
            DataSource datasource = (DataSource) initialContext.lookup(DATASOURCE_CONTEXT);
            if (datasource != null) {
                result = datasource.getConnection();
            } else {
                log("Failed to lookup datasource.");
            }
        } catch (NamingException ex) {
            log("Cannot get connection: " + ex);
        } catch (SQLException ex) {
            log("Cannot get connection: " + ex);
        }
        return result;
    }

    /** Uses DriverManager.  */
    static Connection getSimpleConnection(String databaseUrl) {
        //See your driver documentation for the proper format of this string :
        String DB_CONN_STRING = databaseUrl;
//        String DB_CONN_STRING = "jdbc:mysql://MYSQLSERVER:3306/airplanes";
        //Provided by your driver documentation. In this case, a MySql driver is used :
        String DRIVER_CLASS_NAME = "org.gjt.mm.mysql.Driver";
        String USER_NAME = "USERNAME";
        String PASSWORD = "PASSWORD";

        Connection result = null;
        try {
            Class.forName(DRIVER_CLASS_NAME).newInstance();
        } catch (Exception ex) {
            log("Check classpath. Cannot load db driver: " + DRIVER_CLASS_NAME);
        }

        try {
            result = DriverManager.getConnection(DB_CONN_STRING, USER_NAME, PASSWORD);
        } catch (SQLException e) {
            log("Driver loaded, but cannot connect to db: " + DB_CONN_STRING);
        }
        return result;
    }

    private static void log(Object aObject) {
        System.out.println(aObject);
    }
}
