/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package searchjsf;

import com.sun.data.provider.impl.TableRowDataProvider;
import com.sun.rave.web.ui.appbase.AbstractPageBean;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import searchjsf.images.Image;
import org.sphx.api.*;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 *
 * @version SearchResults.java
 * @version Created on 27-Oct-2009, 16:20:59
 * @author mhaft
 */
public class SearchResults extends AbstractPageBean {
    
    // <editor-fold defaultstate="collapsed" desc="Managed Component Definition">

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
    }

    // </editor-fold>
    private String searchString;
    private boolean newSearch = true;
    private String searchResponse;
    private ArrayList<Image> images = new ArrayList();

    /**
     * <p>Construct a new Page bean instance.</p>
     */
    public SearchResults() {
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
                log("SearchResults Initialization Failure", e);
                throw e instanceof FacesException ? (FacesException) e : new FacesException(e);
            }
            // </editor-fold>
            // Perform application initialization that must complete
            // *after* managed components are initialized
            // TODO - add your own initialization code here
            //this code retrieves the query string passed via the URL
            try {
                FacesContext fc = FacesContext.getCurrentInstance();
                Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
                newSearch = Boolean.getBoolean(params.get("new"));
                if (true) {
                    //TODO - if (newSearch) {
                    searchString = params.get("q");
                    if (searchString == null) {
                        searchString = "no parameter found";
                    }
                    Search search = new Search(this);
                }
            } catch (Exception e) {
                log("Problem with MH's page init procedure: ", e);
            }
            String host = "MYSQLSERVER";
            int port = 3312;
            int mode = SphinxClient.SPH_MATCH_EXTENDED;
            // * means search in all indexes; specific index name can also be specfied here
            String index = "*";
            int offset = 0;
            int limit = 500;
            int sortMode = SphinxClient.SPH_SORT_RELEVANCE;
            String sortClause = "";
            String groupBy = "";
            String groupSort = "";
            SphinxMatch info = null;
            SphinxClient cl = new SphinxClient();
            cl.SetServer(host, port);
            cl.SetWeights(new int[]{100, 1});
            cl.SetMatchMode(mode);
            cl.SetLimits(offset, limit);
            cl.SetSortMode(sortMode, sortClause);
            //Vector thumbnailIds = new Vector();
            StringBuffer q = new StringBuffer();
            if (searchString != null || !searchString.equals("")) {
                q.append("@* " + searchString);
                SphinxResult res = cl.Query(q.toString(), index);
                if (res == null) {
                    setSearchResponse("Sorry no search results available!");
                //response.setRenderParameter("q", "Sorry no search results available!");
                } else {
                    //response.setRenderParameter("q", "Results found");
                    //response.setRenderParameter("totalResults", String.valueOf(res.total));
                    //response.setRenderParameter("resultsTime", String.valueOf(res.time));
                    //setSearchResponseTime(String.valueOf(res.time));
                    //setSearchResponseTotal(String.valueOf(res.total));
                    for (int i = 0; i < res.matches.length; i++) {
                        info = res.matches[i];
                        Image img = new Image(info.docId);
                        images.add(img);
                    }
                }
            }

        } catch (SphinxException ex) {
            Logger.getLogger(SearchResults.class.getName()).log(Level.SEVERE, null, ex);
        }


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

    /**
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
    }

    /**
     * <p>Return a reference to the scoped data bean.</p>
     *
     * @return reference to the scoped data bean
     */
    protected SessionBean1 getSessionBean1() {
        return (SessionBean1) getBean("SessionBean1");
    }

    /**
     * <p>Return a reference to the scoped data bean.</p>
     *
     * @return reference to the scoped data bean
     */
    protected ApplicationBean1 getApplicationBean1() {
        return (ApplicationBean1) getBean("ApplicationBean1");
    }

    /**
     * <p>Return a reference to the scoped data bean.</p>
     *
     * @return reference to the scoped data bean
     */
    protected RequestBean1 getRequestBean1() {
        return (RequestBean1) getBean("RequestBean1");
    }

    /**
     * @return the searchString
     */
    public String getSearchString() {
        return searchString;
    }

    /**
     * @param searchString the searchString to set
     */
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    /**
     * @return the images
     */
    public ArrayList<Image> getImages() {
        return images;
    }

    /**
     * @param images the images to set
     */
    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

    /**
     * @return the searchResponse
     */
    public String getSearchResponse() {
        return searchResponse;
    }

    /**
     * @param searchResponse the searchResponse to set
     */
    public void setSearchResponse(String searchResponse) {
        this.searchResponse = searchResponse;
    }

}

