/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package SppNavService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author root
 */
public class TreeNode {
    private int pk;
    private int parent;
    private int taxon_id;
    private int parentTaxon_id;
    private String name;
    private String parentName;
    private int noChildren;
    private boolean hasChildren;
    private boolean flag;
    private String treeName;
    private ArrayList<TreeNode> children;

    public TreeNode(){
        pk = -1;
        parent = -1;
        taxon_id = -1;
        parentTaxon_id = -1;
        name = null;
        parentName = null;
        noChildren = -1;
        hasChildren = false;
        flag = false;
        treeName = null;
        children = null;
    }

    /**
     * @return the pk
     */
    public int getPk() {
        return pk;
    }

    /**
     * @param pk the pk to set
     */
    public void setPk(int pk) {
        this.pk = pk;
    }

    /**
     * @return the parent
     */
    public int getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(int parent) {
        this.parent = parent;
    }

    /**
     * @return the taxon_id
     */
    public int getTaxon_id() {
        return taxon_id;
    }

    /**
     * @param taxon_id the taxon_id to set
     */
    public void setTaxon_id(int taxon_id) {
        this.taxon_id = taxon_id;
    }

    /**
     * @return the parentTaxon_id
     */
    public int getParentTaxon_id() {
        return parentTaxon_id;
    }

    /**
     * @param parentTaxon_id the parentTaxon_id to set
     */
    public void setParentTaxon_id(int parentTaxon_id) {
        this.parentTaxon_id = parentTaxon_id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the parentName
     */
    public String getParentName() {
        return parentName;
    }

    /**
     * @param parentName the parentName to set
     */
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    /**
     * @return the noChildren
     */
    public int getNoChildren() {
        return noChildren;
    }

    /**
     * @param noChildren the noChildren to set
     */
    public void setNoChildren(int noChildren) {
        this.noChildren = noChildren;
        if (noChildren > 0) hasChildren = true;
    }

    /**
     * @return the hasChildren
     */
    public boolean isHasChildren() {
        return hasChildren;
    }

    /**
     * @param hasChildren the hasChildren to set
     */
    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    /**
     * @return the flag
     */
    public boolean isFlag() {
        return flag;
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    /**
     * @return the treeName
     */
    public String getTreeName() {
        return treeName;
    }

    /**
     * @param treeName the treeName to set
     */
    public void setTreeName(String treeName) {
        this.treeName = treeName;
    }

    public void populateChildrenFromDatabase(){
        try {
            this.children = DatabaseQuery.getChildNodes(this.pk);
        } catch (SQLException ex) {
            Logger.getLogger(TreeNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
