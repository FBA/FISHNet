/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SppNavService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author root
 */
public class RecalculateChildren {

    public static String recalculateChildren(String treeName) {
        StringBuilder xmlR = new StringBuilder();
        xmlR.append("<?xml version='1.0' encoding='iso-8859-1'?><div>");

        try {

            //loop round nodes performing update
            int success = 0;
            int fail = 0;

            ArrayList<Integer> nodes = DatabaseQuery.getAllNodes(treeName);
            Iterator i = nodes.iterator();
            while (i.hasNext()) {
                Integer n = (Integer) i.next();
                boolean worked;
                try {
                    worked = DatabaseQuery.updateNoChildren(n.intValue());
                    if (worked) {
                        success++;
                    } else {
                        fail++;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(RecalculateChildren.class.getName()).log(Level.SEVERE, null, ex);
                    fail++;
                }

            }

            xmlR.append("Navigation tree '").append(treeName).append("' updated with ").append(success).append(" nodes successfully changed");
            xmlR.append(" and ").append(fail).append(" failures.");
            xmlR.append("</div>");
        } catch (Exception ex){
            Logger.getLogger(RecalculateChildren.class.getName()).log(Level.SEVERE, null, ex);
            xmlR.append("severe database error occurred</div>");
        }
        return xmlR.toString();
    }
}
