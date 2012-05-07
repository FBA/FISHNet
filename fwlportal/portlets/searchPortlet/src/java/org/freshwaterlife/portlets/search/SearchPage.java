/*
 * SearchPage.java
 *
 * Created on 23-Mar-2010, 20:51:42
 */
package org.freshwaterlife.portlets.search;

import com.sun.rave.web.ui.appbase.AbstractPageBean;
import com.sun.webui.jsf.component.Form;
import com.sun.webui.jsf.component.ThemeLinks;
import java.util.ArrayList;
import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.RenderRequest;
import org.freshwaterlife.portlets.QueryString;
import org.freshwaterlife.portlets.search.process.ProcessResults;
import org.freshwaterlife.portlets.search.resulttypes.BasicResultDoc;
import org.freshwaterlife.portlets.search.resulttypes.Image;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 *
 * @author root
 */
public class SearchPage extends AbstractPageBean {
    // <editor-fold defaultstate="collapsed" desc="Managed Component Definition">

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
    }
    private Form searchForm = new Form();

    public Form getSearchForm() {
        return searchForm;
    }

    public void setSearchForm(Form f) {
        this.searchForm = f;
    }
    private ThemeLinks themeLinks1 = new ThemeLinks();

    public ThemeLinks getThemeLinks1() {
        return themeLinks1;
    }

    public void setThemeLinks1(ThemeLinks t) {
        this.themeLinks1 = t;
    }
    // </editor-fold>
    //private String searchResults = "Results to go here...";
    private ArrayList<BasicResultDoc> searchResults;
    private RenderRequest renderRequest;
    private String baseUrl;
    private String solrCss;
    private String requestUrl;

    /**
     * <p>Construct a new Page bean instance.</p>
     */
    public SearchPage() {
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
        // TODO - add your own initialiation code here

        // <editor-fold defaultstate="collapsed" desc="Managed Component Initialization">
        // Initialize automatically managed components
        // *Note* - this logic should NOT be modified
        try {
            _init();
        } catch (Exception e) {
            log("SearchPage Initialization Failure", e);
            throw e instanceof FacesException ? (FacesException) e : new FacesException(e);
        }

        // </editor-fold>
        // Perform application initialization that must complete
        // *after* managed components are initialized
        // TODO - add your own initialization code here
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
        //getUrlQValue();
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
        getUrlQValue();
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
    protected RequestBean1 getRequestBean1() {
        return (RequestBean1) getBean("RequestBean1");
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
     * @return the searchResults
     */
    public ArrayList<BasicResultDoc> getSearchResults() {
        this.requestUrl = getApplicationBean1().getSolrUrl() + "/select/?q=" + this.getSessionBean1().getqValue() + "&facet=true&facet.field=keywords&version=2.2&start=0&rows=20&indent=on";

        //create empty results with some info in case something goes wrong
        ArrayList<BasicResultDoc> tmpResults = new ArrayList();
        Image blank = new Image();
        blank.setTitle("No results found or error occurred");
        tmpResults.add(blank);

        try {
/*
            //Method One: simply getting the xml
            UrlContentReader solrResults = new UrlContentReader( requestUrl );
            searchResults = solrResults.retrieveContent();
*/
            //Method Two: parsing the xml into data structures
            ProcessResults pr = new ProcessResults(requestUrl);
            pr.process();

            /*
             *quick display of hasmap
             *
            searchResults = "";
            ArrayList rs = pr.getResultHashMaps();
            Iterator rsi = rs.iterator();
            while(rsi.hasNext()){
                searchResults = searchResults + "<div class='doc'>";
                
                HashMap vals = (HashMap) rsi.next();
                Collection vs = vals.values();
                searchResults = searchResults + vs.toString();

                searchResults = searchResults + "</div>";
            }
             */
           tmpResults = pr.getResults();
            
        } catch (Exception e) {
            //searchResults = "Error retrieving results : " + e.getMessage();
        }

        return tmpResults;

    }

    /**
     * @param searchResults the searchResults to set
     */
    public void setSearchResults(ArrayList<BasicResultDoc> results) {
        this.searchResults = results;
    }

    private void getUrlQValue() {

        String t = (String) this.getRenderRequest().getAttribute("javax.servlet.forward.query_string");

        if (t != null) {
            QueryString qString = new QueryString(t);
            String qV = qString.getParameter("q");
            if (qV != null) {
                this.getSessionBean1().setqValue(qV);
            }
        }

    }

    public String submit_action() {
        // TODO: Process the action. Return value is a navigation
        // case name where null will return to the same page.
        return "case1";
    }

    /**
     * @return the renderRequest
     */
    public RenderRequest getRenderRequest() {
        if (renderRequest == null) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext extContext = facesContext.getExternalContext();
            renderRequest = (RenderRequest) extContext.getRequest();
        }
        return renderRequest;
    }

    /**
     * @param renderRequest the renderRequest to set
     */
    public void setRenderRequest(RenderRequest renderRequest) {
        this.renderRequest = renderRequest;
    }

    /**
     * @return the solrCss
     */
    public String getSolrCss() {
        solrCss = getBaseUrl() + "resources/solrResults.css";
        return solrCss;
    }

    /**
     * @param solrCss the solrCss to set
     */
    public void setSolrCss(String solrCss) {
        this.solrCss = solrCss;
    }

    /**
     * @return the baseUrl
     */
    public String getBaseUrl() {
        if (baseUrl == null) {
            this.getRenderRequest();
            baseUrl = renderRequest.getScheme() + "://" + renderRequest.getServerName() + ":" + renderRequest.getServerPort() + renderRequest.getContextPath() + "/";
        }
        return baseUrl;
    }

    /**
     * @param baseUrl the baseUrl to set
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * @return the requestUrl
     */
    public String getRequestUrl() {
        return requestUrl;
    }

    /**
     * @param requestUrl the requestUrl to set
     */
    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }
}
