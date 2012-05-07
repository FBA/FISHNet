/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package searchjsf;

import com.sun.rave.web.ui.appbase.AbstractApplicationBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.FacesException;
import org.sphx.api.*;

/**
 * <p>Application scope data bean for your application.  Create properties
 *  here to represent cached data that should be made available to all users
 *  and pages in the application.</p>
 *
 * <p>An instance of this class will be created for you automatically,
 * the first time your application evaluates a value binding expression
 * or method binding expression that references a managed bean using
 * this class.</p>
 *
 * @version ApplicationBean1.java
 * @version Created on 27-Oct-2009, 16:19:59
 * @author mhaft
 */
public class ApplicationBean1 extends AbstractApplicationBean {
    // <editor-fold defaultstate="collapsed" desc="Managed Component Definition">

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
    }
    // </editor-fold>
    private String host = "MYSQLSERVER";
    private int port = 3312;
    private int mode = SphinxClient.SPH_MATCH_EXTENDED;
    // * means search in all indexes; specific index name can also be specfied here
    private String index = "*";
    private int offset = 0;
    private int limit = 500;
    private int sortMode = SphinxClient.SPH_SORT_RELEVANCE;
    private String sortClause = "";
    private String groupBy = "";
    private String groupSort = "";
    private SphinxClient cl;

    /**
     * <p>Construct a new application data bean instance.</p>
     */
    public ApplicationBean1() {
    }

    /**
     * <p>This method is called when this bean is initially added to
     * application scope.  Typically, this occurs as a result of evaluating
     * a value binding or method binding expression, which utilizes the
     * managed bean facility to instantiate this bean and store it into
     * application scope.</p>
     * 
     * <p>You may customize this method to initialize and cache application wide
     * data values (such as the lists of valid options for dropdown list
     * components), or to allocate resources that are required for the
     * lifetime of the application.</p>
     */
    @Override
    public void init() {
        try {
            // Perform initializations inherited from our superclass
            super.init();
            // Perform application initialization that must complete
            // *before* managed components are initialized
            // TODO - add your own initialiation code here
            // <editor-fold defaultstate="collapsed" desc="Managed Component Initialization">
            // Initialize automatically managed components
            // *Note* - this logic should NOT be modified
            try {
                _init();
            } catch (Exception e) {
                log("ApplicationBean1 Initialization Failure", e);
                throw e instanceof FacesException ? (FacesException) e : new FacesException(e);
            }
            // </editor-fold>
            // Perform application initialization that must complete
            // *after* managed components are initialized
            // TODO - add your own initialization code here
            setCl(new SphinxClient());
            SphinxMatch info = null;
            getCl().SetServer(host, port);
            getCl().SetWeights(new int[]{100, 1});
            getCl().SetMatchMode(mode);
            getCl().SetLimits(offset, limit);
            getCl().SetSortMode(sortMode, sortClause);
        } catch (SphinxException ex) {
            Logger.getLogger(ApplicationBean1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * <p>This method is called when this bean is removed from
     * application scope.  Typically, this occurs as a result of
     * the application being shut down by its owning container.</p>
     * 
     * <p>You may customize this method to clean up resources allocated
     * during the execution of the <code>init()</code> method, or
     * at any later time during the lifetime of the application.</p>
     */
    @Override
    public void destroy() {
    }

    /**
     * <p>Return an appropriate character encoding based on the
     * <code>Locale</code> defined for the current JavaServer Faces
     * view.  If no more suitable encoding can be found, return
     * "UTF-8" as a general purpose default.</p>
     *
     * <p>The default implementation uses the implementation from
     * our superclass, <code>AbstractApplicationBean</code>.</p>
     */
    @Override
    public String getLocaleCharacterEncoding() {
        return super.getLocaleCharacterEncoding();
    }

    /**
     * @return the cl
     */
    public SphinxClient getCl() {
        return cl;
    }

    /**
     * @param cl the cl to set
     */
    public void setCl(SphinxClient cl) {
        this.cl = cl;
    }
}
