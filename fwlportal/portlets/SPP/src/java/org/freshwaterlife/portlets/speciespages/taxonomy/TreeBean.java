// TODO - add code to look for branches with no sub-nodes, catch the resulting error and set them to be leaf nodes.
package org.freshwaterlife.portlets.speciespages.taxonomy;

import com.sun.rave.web.ui.appbase.AbstractSessionBean;
import javax.faces.FacesException;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import com.sun.data.provider.impl.CachedRowSetDataProvider;

/**
 * <p>Session scope data bean for your application.  Create properties
 *  here to represent cached data that should be made available across
 *  multiple HTTP requests for an individual user.</p>
 *
 * <p>An instance of this class will be created for you automatically,
 * the first time your application evaluates a value binding expression
 * or method binding expression that references a managed bean using
 * this class.</p>
 *
 * @version TreeBean.java
 * @version Created on 27-Jan-2009, 12:24:40
 * @author kmcnicol
 */
public class TreeBean extends AbstractSessionBean {
    // <editor-fold defaultstate="collapsed" desc="Managed Component Definition">

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
    }
    // </editor-fold>

    //JSF managed link to sessin bean
    private TaxonomySessionBean taxonomySessionBean;
    private DefaultTreeModel model;
    DefaultMutableTreeNode rootTreeNode;
    private CachedRowSetDataProvider list_noviceDataProvider = new CachedRowSetDataProvider();

    //styling variables
    private static final String BRANCH_CONTRACTED_ICON = "../resources/css/royale/css-images/tree_line_blank.gif";
    private static final String BRANCH_EXPANDED_ICON = "../resources/css/royale/css-images/tree_line_blank.gif";
    private static final String BRANCH_LEAF_ICON = "../resources/css/royale/css-images/tree_line_blank.gif";

    public CachedRowSetDataProvider getList_noviceDataProvider() {
        return list_noviceDataProvider;
    }

    public void setList_noviceDataProvider(CachedRowSetDataProvider crsdp) {
        this.list_noviceDataProvider = crsdp;
    }

    /**
     * <p>Construct a new session data bean instance.</p>
     */
    public TreeBean() {
    }

    /**
     * Gets the tree's default model.
     *
     * @return tree model.
     */
    public DefaultTreeModel getModel() {
        return model;
    }

    /**
     * <p>This method is called when this bean is initially added to
     * session scope.  Typically, this occurs as a result of evaluating
     * a value binding or method binding expression, which utilizes the
     * managed bean facility to instantiate this bean and store it into
     * session scope.</p>
     * 
     * <p>You may customize this method to initialize and cache data values
     * or resources that are required for the lifetime of a particular
     * user session.</p>
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
            log("TreeBean Initialization Failure", e);
            throw e instanceof FacesException ? (FacesException) e : new FacesException(e);
        }

        // </editor-fold>
        // Perform application initialization that must complete
        // *after* managed components are initialized



        // create root node with its children expanded

        rootTreeNode = new DefaultMutableTreeNode();
        TaxonNodeIceUserObject rootObject = new TaxonNodeIceUserObject(rootTreeNode);
        rootObject.setText("Species Collection");
        rootObject.getNodeTaxon().setPrefSciName("Species Collection");
        rootObject.getNodeTaxon().setPrefEnName("Species Collection");
        rootObject.setExpanded(true);
        rootObject.setBranchContractedIcon(BRANCH_CONTRACTED_ICON);
        rootObject.setBranchExpandedIcon(BRANCH_EXPANDED_ICON);
        rootObject.setLeafIcon(BRANCH_LEAF_ICON);
        rootTreeNode.setUserObject(rootObject);

        // model is accessed by the ice:tree component
        model = new DefaultTreeModel(rootTreeNode); //this should also blank the model in case it's still held somewhere

        //get the first level of the tree (hopefully subsequent levels will be retrieved when requested
        expandNode(rootTreeNode);
    }

    public void expandNode(DefaultMutableTreeNode nodeToExpand) {
        //what happens if we reset the session bean's output string here?
        taxonomySessionBean.setOutput("Tree bean: ready to expand node: " + nodeToExpand.getUserObject().toString() + "---><br/>");


        // get the text from the selected node to use in the database query
        TaxonNodeIceUserObject objectToExpand = (TaxonNodeIceUserObject) nodeToExpand.getUserObject();
        String p_name_sci = objectToExpand.getNodeTaxon().getPrefSciName();

        //remove any existing children (e.g. the Loading ... nodes)
        nodeToExpand.removeAllChildren();

        /*
         * retrieve values from the database
         */
        try {
            //get the data on the next level from SQL
            //sql is passed the placeTaxon variable to match againgst the parent field
            taxonomySessionBean.addToOutput("getting row cache<br/>");

            //check session bean properly initialised
            if (!taxonomySessionBean.initialised) {
                taxonomySessionBean._init();
            }

            list_noviceDataProvider.setCachedRowSet((javax.sql.rowset.CachedRowSet) taxonomySessionBean.getList_noviceRowSet());
        } catch (Exception ex) {
            taxonomySessionBean.addToOutput("***Problem retrieving the row cache: exception----> <br/>" + ex + "<br/><br/>");
            log("Problem retrieving the row cache: exception---->", ex);
            error("Problem retrieving the row cache: exception----> " + ex);
        }
        try {
            taxonomySessionBean.addToOutput("setting sql. <br/> ");
            list_noviceDataProvider.getCachedRowSet().setCommand("select list_novice.NAME_SCI, list_novice.NAME_EN, list_novice.PARENT from list_novice where list_novice.PARENT = ?");
        } catch (Exception ex) {
            taxonomySessionBean.addToOutput("Problem setting the sql statment: exception ----><br/> " + ex + "<br/><br/>");
            log("Problem setting the sql statment: exception ---->", ex);
            error("Problem setting the sql statment: exception ---->" + ex);
        }
        try {
            taxonomySessionBean.addToOutput("setting where parameter to " + p_name_sci + "<br/>");
            list_noviceDataProvider.getCachedRowSet().setObject(1, p_name_sci);
        } catch (Exception ex) {
            taxonomySessionBean.addToOutput("problem setting the sql parameter as: " + p_name_sci + "**Gave exception----> <br/>" + ex + "<br/><br/>");
            log("problem setting the sql parameter as:", ex);
            error("problem setting the sql parameter as: " + ex);
        }
        try {
            taxonomySessionBean.addToOutput(", refreshing the query.............<br/>");
            list_noviceDataProvider.refresh();
        } catch (Exception ex) {
            taxonomySessionBean.addToOutput("problem refreshing the cached query for : " + p_name_sci + " -resulting exception ----> <br/>" + ex + "<br/><br/>");
            log("problem refreshing the cached query", ex);
            error("problem refreshing the cached query " + ex);
        }
        /*
        catch (Exception ex) {
        taxonomySessionBean.addToOutput("problem refreshing the query to expand the node: " + p_name_sci + "exception; " + ex);
        log("Exception gathering tree data", ex);
        error("Exception gathering tree data: " + ex);
        }
         */
        //Iterate round the results, creating tree nodes as we go
        boolean hasNext = list_noviceDataProvider.cursorFirst();
        while (hasNext) {
            //retrieve taxon info for this row
            String name_sci = (String) list_noviceDataProvider.getValue("NAME_SCI");
            String name_en = (String) list_noviceDataProvider.getValue("NAME_EN");
            // create a tree node based on this data
            DefaultMutableTreeNode branchNode = new DefaultMutableTreeNode();
            TaxonNodeIceUserObject branchObject = new TaxonNodeIceUserObject(branchNode);
            branchObject.setText(name_en + " (" + name_sci + ")");
            branchObject.getNodeTaxon().setPrefEnName(name_en);
            branchObject.getNodeTaxon().setPrefSciName(name_sci);
            branchObject.setBranchContractedIcon(BRANCH_CONTRACTED_ICON);
            branchObject.setBranchExpandedIcon(BRANCH_EXPANDED_ICON);
            branchObject.setLeafIcon(BRANCH_LEAF_ICON);
            branchNode.setUserObject(branchObject);

            taxonomySessionBean.addToOutput("***Added branch " + name_sci + "::" + name_en + "***<br/>");

            /*
             * this bit adds a placeholder child to each node to display while the real children are being read from the database
             */
            DefaultMutableTreeNode placeHolderNode = new DefaultMutableTreeNode();
            TaxonNodeIceUserObject placeHolderObject = new TaxonNodeIceUserObject(placeHolderNode);
            placeHolderObject.setText("Loading . . .");
            placeHolderObject.getNodeTaxon().setPrefEnName("loading ...");
            placeHolderObject.getNodeTaxon().setPrefSciName("loadaeth ...");
            placeHolderObject.setExpanded(false);
            placeHolderNode.setUserObject(placeHolderObject);
            branchNode.add(placeHolderNode);

            //don't forget to add your node to the parent!
            nodeToExpand.add(branchNode);
            //make sure you advance the loop or it'll hang forever !!
            hasNext = list_noviceDataProvider.cursorNext();
        }



    /*
     * doing it without a database
     */
    /*
    // add some child notes
    for (int i = 0; i < 3; i++) {
    DefaultMutableTreeNode branchNode = new DefaultMutableTreeNode();
    IceUserObject branchObject = new IceUserObject(branchNode);
    branchObject.setText("node-" + i);
    branchNode.setUserObject(branchObject);
    branchObject.setLeaf(true);
    rootTreeNode.add(branchNode);
    }
     */
    }

    /**
     * <p>This method is called when the session containing it is about to be
     * passivated.  Typically, this occurs in a distributed servlet container
     * when the session is about to be transferred to a different
     * container instance, after which the <code>activate()</code> method
     * will be called to indicate that the transfer is complete.</p>
     * 
     * <p>You may customize this method to release references to session data
     * or resources that can not be serialized with the session itself.</p>
     */
    @Override
    public void passivate() {
    }

    /**
     * <p>This method is called when the session containing it was
     * reactivated.</p>
     * 
     * <p>You may customize this method to reacquire references to session
     * data or resources that could not be serialized with the
     * session itself.</p>
     */
    @Override
    public void activate() {
    }

    /**
     * <p>This method is called when this bean is removed from
     * session scope.  Typically, this occurs as a result of
     * the session timing out or being terminated by the application.</p>
     * 
     * <p>You may customize this method to clean up resources allocated
     * during the execution of the <code>init()</code> method, or
     * at any later time during the lifetime of the application.</p>
     */
    @Override
    public void destroy() {
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
}
