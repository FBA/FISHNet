package org.freshwaterlife.portlets.speciespages.taxonomy;

import com.icesoft.faces.component.tree.IceUserObject;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freshwaterlife.portlets.speciespages.Coordinator;
import org.freshwaterlife.portlets.speciespages.Taxon;

/**
 * The UrlNodeUserObject object is responsible for storing extra data
 * for a url.  The url along with text is bound to a ice:commanLink object which
 * will launch a new browser window pointed to the url.
 */
public class TaxonNodeIceUserObject extends IceUserObject {
    
    private boolean selected;
    private Taxon nodeTaxon;

    public TaxonNodeIceUserObject(DefaultMutableTreeNode wrapper) {
        super(wrapper);
        this.nodeTaxon = new Taxon();
    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        System.out.println("Setting value " + selected);
    }

    /**
     * ActionListener method called when a node in the tree is clicked.  Sets
     * the selected panel of the reference panelStack to the value of the instance
     * variable #displayPanel.
     *
     * @param action JSF action event.
     */
    public void userNodeSelection(ActionEvent action) {
        this.nodeTaxon.processTaxonSelction(action);
        /*
        Taxon; sT = getCoordinator().getSelectedTaxon();
        sT.setPrefSciName(name_sci);
        sT.setPrefEnName(name_en);
        getCoordinator().setSelectedTaxon(sT);
         */
    }
/*
    protected Coordinator getCoordinator() {
        return (Coordinator) getBean("coordinator");
    }

    protected Object getBean(String name) {
        FacesContext fc = FacesContext.getCurrentInstance();
        return fc.getApplication().getVariableResolver().resolveVariable(fc, name);
    }
*/
    /**
     * @return the nodeTaxon
     */
    public Taxon getNodeTaxon() {
        return nodeTaxon;
    }

    /**
     * @param nodeTaxon the nodeTaxon to set
     */
    public void setNodeTaxon(Taxon nodeTaxon) {
        this.nodeTaxon = nodeTaxon;
    }
}
