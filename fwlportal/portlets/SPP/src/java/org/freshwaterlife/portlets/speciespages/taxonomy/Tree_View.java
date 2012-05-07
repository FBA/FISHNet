/*
 * Tree_View.java
 *
 * Created on 27-Jan-2009, 12:15:22
 * Copyright kmcnicol
 */
package org.freshwaterlife.portlets.speciespages.taxonomy;

import com.icesoft.faces.component.tree.Tree;
import com.sun.data.provider.impl.CachedRowSetDataProvider;
import com.sun.rave.web.ui.appbase.AbstractPageBean;
import java.util.ArrayList;
import javax.faces.FacesException;
import javax.faces.event.ActionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import org.freshwaterlife.portlets.speciespages.Coordinator;
import org.freshwaterlife.portlets.speciespages.Taxon;

/**
 * <p>Page bean that corresponds to a similarly named JSPX page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class Tree_View extends AbstractPageBean {
    // <editor-fold defaultstate="collapsed" desc="Managed Component Definition">

    private int __placeholder;
    // </editor-fold>

    //JSF managed bean attributes
    private TaxonomySessionBean taxonomySessionBean;
    private Coordinator coordinator;
    private String userSearchStr;
    private String searchResultsHtml;
    private ArrayList<Taxon> taxaMatches;
    public boolean initialised = false;

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
        if (!taxonomySessionBean.initialised) {
            taxonomySessionBean._init();
        }
        list_noviceDataProvider.setCachedRowSet((javax.sql.rowset.CachedRowSet) taxonomySessionBean.getList_noviceRowSet());
        this.initialised = true;
    }
    private CachedRowSetDataProvider list_noviceDataProvider = new CachedRowSetDataProvider();

    public CachedRowSetDataProvider getList_noviceDataProvider() {
        return list_noviceDataProvider;
    }

    public void setList_noviceDataProvider(CachedRowSetDataProvider crsdp) {
        this.list_noviceDataProvider = crsdp;
    }

    /**
     * <p>Construct a new Page bean instance.</p>
     */
    public Tree_View() {
    }

    /**
     * <p>Callback method that is called whenever a page is navigated to,
     * either directly via a URL, or indirectly via page navigation.
     * Customize this method to acquire resources that will be needed
     * for event handlers and lifecycle methods, whether or not this
     * page is performing post back processing.</p>
     * 
     * <p>Note that, if the current request is a postback, the property
     * values of the components do <strong>not</strong> represent any
     * values submitted with this request.  Instead, they represent the
     * property values that were saved for this view when it was rendered.</p>
     */
    @Override
    public void init() {
        // Perform initializations inherited from our superclass
        super.init();
        // Perform application initialization that must complete
        // *before* managed components are initialized


        // <editor-fold defaultstate="collapsed" desc="Managed Component Initialization">
        // Initialize automatically managed components
        // *Note* - this logic should NOT be modified
        try {
            _init();
        } catch (Exception e) {
            log("tree_view Initialization Failure", e);
            throw e instanceof FacesException ? (FacesException) e : new FacesException(e);
        }

    // </editor-fold>
    // Perform application initialization that must complete
    // *after* managed components are initialized

    }

    /**
     * <p>Callback method that is called after the component tree has been
     * restored, but before any event processing takes place.  This method
     * will <strong>only</strong> be called on a postback request that
     * is processing a form submit.  Customize this method to allocate
     * resources that will be required in your event handlers.</p>
     */
    @Override
    public void preprocess() {
    }

    /*
     *
     * <p>Callback method that is called just before rendering takes place.
     * This method will <strong>only</strong> be called for the page that
     * will actually be rendered (and not, for example, on a page that
     * handled a postback and then navigated to a different page).  Customize
     * this method to allocate resources that will be required for rendering
     * this page.</p>
     */
    @Override
    public void prerender() {
    }

    /**
     * <p>Callback method that is called after rendering is completed for
     * this request, if <code>init()</code> was called (regardless of whether
     * or not this was the page that was actually rendered).  Customize this
     * method to release resources acquired in the <code>init()</code>,
     * <code>preprocess()</code>, or <code>prerender()</code> methods (or
     * acquired during execution of an event handler).</p>
     */
    @Override
    public void destroy() {
        list_noviceDataProvider.close();

    }

    public void sppTree_processAction(ActionEvent ae) {
        taxonomySessionBean.setOutput("Node selection detected, passing to tree bean....");

        Tree tree = (Tree) ae.getSource();
        TreeNode node = tree.getNavigatedNode();
        if ("expand".equals(tree.getNavigationEventType())) {
            taxonomySessionBean.setOutput("*action confirmed as 'expand'*");
            getTreeBean().expandNode((DefaultMutableTreeNode) node);
        }

        // add any action to deal with leaf selection here

        log("Exception gathering tree data" + ae.toString());
        error("Exception gathering tree data: " + ae.toString());
    }

    protected TreeBean getTreeBean() {
        return (TreeBean) getBean("treeBean");
    }

    /**
     * @return the coordinator
     */
    public Coordinator getCoordinator() {
        return coordinator;
    }

    /**
     * @param coordinator the coordinator to set
     */
    public void setCoordinator(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    /**
     * @return the taxonomySessionBean
     */
    public TaxonomySessionBean getTaxonomySessionBean() {
        return taxonomySessionBean;
    }

    /**
     * @param taxonomySessionBean the taxonomySessionBean to set
     */
    public void setTaxonomySessionBean(TaxonomySessionBean taxonomySessionBean) {
        this.taxonomySessionBean = taxonomySessionBean;
    }

    /**
     * @return the userSearchStr
     */
    public String getUserSearchStr() {
        return userSearchStr;
    }

    /**
     * @param userSearchStr the userSearchStr to set
     */
    public void setUserSearchStr(String userSearchStr) {
        this.userSearchStr = userSearchStr;
    }

    /**
     * @return the searchResultsHtml
     */
    public String getSearchResultsHtml() {
        return searchResultsHtml;
    }

    /**
     * @param searchResultsHtml the searchResultsHtml to set
     */
    public void setSearchResultsHtml(String searchResults) {
        this.searchResultsHtml = searchResults;
    }

    public void processSearch(ActionEvent event) {
        // keep the user informed throught throught updates.

        String processingOutput = "Retrieving taxonomic information . . .<br/>";
        coordinator.setTaxonomyProcessingOutput(processingOutput);
        processingOutput = processingOutput + "Consulting the UK NBN/NHM Species Dictionary:<br/>";
        coordinator.setTaxonomyProcessingOutput(processingOutput);

        NbnSpeciesDictionary nbn = new NbnSpeciesDictionary();
        boolean success = nbn.nameSearch(userSearchStr);
        if (success) {
            processingOutput = processingOutput + "- checking synonyms<br/>";
            coordinator.setTaxonomyProcessingOutput(processingOutput);

            searchResultsHtml = nbn.getHtmlOutput();

            processingOutput = processingOutput + "- checking lower taxa<br/>";
            coordinator.setTaxonomyProcessingOutput(processingOutput);

            taxaMatches = nbn.getTaxaMatchResults();

            coordinator.setMatchingTaxa(taxaMatches);
        } else {
            processingOutput = "No matches were found";
        }
    }
}

