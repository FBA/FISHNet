/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SppNavService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author root
 */
public class RetrieveTrees {

    private static final String defaultTree = "fwl_novice";

    public static String getItem(int nav_pk) {
        StringBuilder xmlR = new StringBuilder();

        TreeNode item;
        try {
            item = DatabaseQuery.getNode(nav_pk);


            xmlR.append("<?xml version='1.0' encoding='iso-8859-1'?>");
            xmlR.append("<item nav_pk='").append(item.getPk()).append("' >");
            xmlR.append("<taxon taxon_id='").append(item.getTaxon_id()).append("' >").append(item.getName().trim()).append("</taxon>");
            xmlR.append("<nav_parent taxon_id='").append(item.getParentTaxon_id()).append("' >").append(item.getParentName()).append("</nav_parent>");
            xmlR.append("<no_children>").append(item.getNoChildren()).append("</no_children>");
            xmlR.append("<nav_list>").append(item.getTreeName().trim()).append("</nav_list>");
            xmlR.append("</item>");
        } catch (SQLException ex) {
            Logger.getLogger(RetrieveTrees.class.getName()).log(Level.SEVERE, null, ex);
        }

        return xmlR.toString();
    }

    public static String getTreesList() {
        StringBuilder xmlR = new StringBuilder();
        try {

            xmlR.append("<?xml version='1.0' encoding='iso-8859-1'?><trees>");
            List<Tree> trees = DatabaseQuery.getListOfTrees();
            Tree tree;
            Iterator i = trees.iterator();
            while (i.hasNext()) {
                tree = (Tree) i.next();
                xmlR.append("<tree start='").append(tree.getStartPoint()).append("'>").append(tree.getName()).append("</tree>");
            }
            xmlR.append("</trees>");

        } catch (SQLException ex) {
            Logger.getLogger(RetrieveTrees.class.getName()).log(Level.SEVERE, null, ex);
        }
        return xmlR.toString();
    }

    /**
     * returns the root and first level children of the defaul tree
     */
    public static String getTree() {
        return getTree(defaultTree);
    }

    /**
     * returns the root and first level children of the specified tree
     * @param tree - name of the tree as it appears in the database
     */
    public static String getTree(String tree) {
        StringBuilder xmlR = new StringBuilder();
        try {
            //TODO - replace following code with the simpler approach below - once dhtmlx tree is modified

            int node = DatabaseQuery.getStartPoint(tree);
            ArrayList<TreeNode> children = DatabaseQuery.getChildNodes(node);
            xmlR.append("<?xml version='1.0' encoding='iso-8859-1'?><tree id='0'>");
            Iterator i = children.iterator();
            TreeNode tn;
            while (i.hasNext()) {
                tn = (TreeNode) i.next();
                int chd = 0;
                if (tn.isHasChildren()) {
                    chd = 1;
                }
                xmlR.append("<item text='").append(tn.getName()).append("' id='").append(tn.getPk()).append("' child='").append(chd).append("'></item>");
            }
            xmlR.append("</tree>");

            //this is the way it should be done, but dhtmlx tree goes funny if the
            //top node doesn't have an id of 0
            //return getTree(tree, DatabaseQuery.getStartPoint(tree));
        } catch (SQLException ex) {
            Logger.getLogger(RetrieveTrees.class.getName()).log(Level.SEVERE, null, ex);
        }
        return xmlR.toString();

        //this is the way it should be done, but dhtmlx tree goes funny if the
        //top node doesn't have an id of 0
        //return getTree(tree, DatabaseQuery.getStartPoint(tree));
    }

    /**
     * returns the node specified and its first level children
     * @param node
     * @return String - tree in xml format
     */
    public static String getTree(int node) {
        return getTree(defaultTree, node);
    }

    /**
     * This is the method that actually does all the work
     * @param tree
     * @param node
     * @return
     */
    public static String getTree(String tree, int node) {
        StringBuilder xmlR = new StringBuilder();
        try {

            ArrayList<TreeNode> children = DatabaseQuery.getChildNodes(node);
            xmlR.append("<?xml version='1.0' encoding='iso-8859-1'?><tree id='").append(node).append("'>");
            Iterator i = children.iterator();
            TreeNode tn;
            while (i.hasNext()) {
                tn = (TreeNode) i.next();
                int chd = 0;
                if (tn.isHasChildren()) {
                    chd = 1;
                }
                xmlR.append("<item text='").append(tn.getName()).append("' id='").append(tn.getPk()).append("' child='").append(chd).append("'></item>");
            }
            xmlR.append("</tree>");

        } catch (SQLException ex) {
            Logger.getLogger(RetrieveTrees.class.getName()).log(Level.SEVERE, null, ex);
        }
        return xmlR.toString();
    }
}
