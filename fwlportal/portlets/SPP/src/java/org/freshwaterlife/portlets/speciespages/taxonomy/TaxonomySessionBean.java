/*
 * taxonomySessionBean.java
 *
 * Created on 27-Jan-2009, 12:15:22
 * Copyright kmcnicol
 */
package org.freshwaterlife.portlets.speciespages.taxonomy;

import com.sun.rave.web.ui.appbase.AbstractSessionBean;
import com.sun.sql.rowset.CachedRowSetXImpl;
import java.sql.SQLException;
import javax.faces.FacesException;

/**
 * <p>Session scope data bean for your application.  Create properties
 *  here to represent cached data that should be made available across
 *  multiple HTTP requests for an individual user.</p>
 *
 * <p>An instance of this class will be created for you automatically,
 * the first time your application evaluates a value binding expression
 * or method binding expression that references a managed bean using
 * this class.</p>
 */
public class TaxonomySessionBean extends AbstractSessionBean {
    // <editor-fold defaultstate="collapsed" desc="Managed Component Definition">

    private int __placeholder;
    public boolean initialised = false;

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    protected void _init() throws Exception {
        list_noviceRowSet = new CachedRowSetXImpl();
        try {
            list_noviceRowSet.setDataSourceName("jdbc/FwlSpeciesListsPool");
        } catch (SQLException sQLException) {
            addToInitErrors("<br/>DataSourceName error<br/>");
        }
        try {
            list_noviceRowSet.setCommand("select list_novice.NAME_SCI, list_novice.NAME_EN, list_novice.PARENT from list_novice");
        } catch (SQLException sQLException) {
            addToInitErrors("<br/>Error setting sql query<br/>");
        }
        try {
            list_noviceRowSet.setTableName("LIST_NOVICE");
        } catch (SQLException sQLException) {
            addToInitErrors("<br/>Error setting table name<br/>");
        }
        try {
            list_noviceRowSet.execute();
        } catch (SQLException sQLException) {
            addToInitErrors("<br/>Error executing the query refresh>" + sQLException);
        }
/*
        try {
            boolean hasNext = list_noviceRowSet.first();
            while (hasNext) {
                addToInitErrors(list_noviceRowSet.getString("NAME_SCI") + " : ");
                addToInitErrors(list_noviceRowSet.getString("NAME_EN") + " : ");
                addToInitErrors(list_noviceRowSet.getString("PARENT") + " ------------ ");
                hasNext = list_noviceRowSet.next();
            }
        } catch (SQLException sQLException) {
            addToInitErrors("Error retrieving data from cached row set" + sQLException);
        }
 */
        addToInitErrors("Initialisation completed.");

        this.initialised = true;
    }

    private String output = "Ready to take data<br/>";
    private String initErrors = "Ready to initialise*****";

    private CachedRowSetXImpl list_noviceRowSet = new CachedRowSetXImpl();

    public CachedRowSetXImpl getList_noviceRowSet() {
        return list_noviceRowSet;
    }

    public void setList_noviceRowSet(CachedRowSetXImpl crsxi) {
        this.list_noviceRowSet = crsxi;
    }

    /**
     * <p>Construct a new session data bean instance.</p>
     */
    public TaxonomySessionBean() {
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
            log("taxonomySessionBean Initialization Failure", e);
            throw e instanceof FacesException ? (FacesException) e : new FacesException(e);
        }

    // </editor-fold>
    // Perform application initialization that must complete
    // *after* managed components are initialized

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
    public void destroy() {
        list_noviceRowSet.close();
    }

    /**
     * @return the output
     */
    public String getOutput() {
        return output;
    }

    /**
     * @param output the output to set
     */
    public void setOutput(String output) {
        this.output = output;
    }

    public void addToOutput(String addition){
        this.output = this.output + addition;
    }
    public void addToInitErrors (String addition){
        this.initErrors = this.initErrors + addition;
    }

    /**
     * @return the initErrors
     */
    public String getInitErrors() {
        return initErrors;
    }

    /**
     * @param initErrors the initErrors to set
     */
    public void setInitErrors(String initErrors) {
        this.initErrors = initErrors;
    }
}
