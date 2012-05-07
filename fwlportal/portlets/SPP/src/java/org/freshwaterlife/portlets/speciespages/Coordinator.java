package org.freshwaterlife.portlets.speciespages;

import java.util.ArrayList;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.portlet.PortletSession;
import org.icefaces.x.core.push.SessionRenderer;

/**
 * This JSF Session Bean is responsible for coordinating updates across the
 * Species Pages porltets via IceFaces Ajax Push.
 * The new - in development - SessionRenderer is used.  If this causes problems this should be
 * changed to the RenderManager approach, but this would involve more coding on all beans - not
 * jus this one.
 *
 * Note - even though in portlets there will be an instance of this session bean for each portlet, that's
 * OK because the SessionRenderer is application based and uses the user session ID's we pass to it here
 * to identify which portlets should be linked and rendered together.
 *
 * @author Kearon McNicol
 */
public class Coordinator {

    // this will be set to the user's portal session ID to identify them uniquely
    private String userSessionIdentifier;

    // these are the keys that will be used to store and retrieve the user's session variables accross portlets
    private static final String IPCTEST = "org.freshwaterlife.portlets.speciespages.tests.ipcTestString";
    private static final String SEL_TAXON_KEY = "org.freshwaterlife.portlets.speciespages.tests.selectedTaxon";
    private static final String MATCHING_TAXA_KEY = "org.freshwaterlife.portlets.speciespages.tests.matchingTaxa";
    private static final String TAXON_PROCESS_OUTPUT_KEY = "org.freshwaterlife.portlets.speciespages.tests.taxonProcessingOutput";

    //temp holding place for variables - real holding place is user session
    private String ipcTestString = "waiting...";
    private Taxon selectedTaxon;
    private ArrayList<Taxon> matchingTaxa;
    private int noMatchingTaxa;
    private String taxonomyProcessingOutput;

    /**
     * The default constructor.
     * Main thing this is responsible for is initialising the
     * IceFaces SessionRender stuff.
     */
    public Coordinator() {
        ensureSessionInitiated();
    }


    //***********************************************
    //These are the methods for the IPC test portlets
    //***********************************************
    /**
     * Method called by the PageBeans to update this parameter
     * @param input
     */
    public void setIpcTestString(String input) {
        ipcTestString = getIpcTestString() + "<br/>" + input;
        setUserSessionVariable(IPCTEST, ipcTestString);
        SessionRenderer.render(userSessionIdentifier);
    }

    /**
     * Method called by the page beans to retrive this parameter
     * @return
     */
    public String getIpcTestString() {
        ipcTestString = (String) getUserSessionVariable(IPCTEST);
        if (ipcTestString == null) {
            ipcTestString = "no variable was retrieved";
        }
        return ipcTestString;
    }

    //****************************************************
    //              SELECTED TAXON
    //These are the methods that manage the selected Taxon
    //****************************************************
    /**
     * Method called by the PageBeans to update this parameter
     * @param input
     */
    public void setSelectedTaxon(Taxon taxon) {
        selectedTaxon = taxon;
        setUserSessionVariable(SEL_TAXON_KEY, selectedTaxon);
        SessionRenderer.render(userSessionIdentifier);

        //TODO get taxon selection to consult nbn, etc and fill in the rest of the details, calculate the search string, etc.
    }

    /**
     * Method called by the page beans to retrive this parameter
     * @return
     */
    public Taxon getSelectedTaxon() {
        selectedTaxon = (Taxon) getUserSessionVariable(SEL_TAXON_KEY);
        if (selectedTaxon == null) {
            selectedTaxon = new Taxon();
        }
        return selectedTaxon;
    }

    //*************************************************************
    //              MATCHING TAXA
    //These are the methods that manage the Matching Taxa ArrayList
    //*************************************************************
    /**
     * @return the matchingTaxa
     * return is an ArrayList of TaxonResults;
     */
    public ArrayList<Taxon> getMatchingTaxa() {
        matchingTaxa = (ArrayList<Taxon>) getUserSessionVariable(MATCHING_TAXA_KEY);
        if (matchingTaxa == null) {
            matchingTaxa = new ArrayList<Taxon>();
        }

        return matchingTaxa;
    }

    /**
     * @param matchingTaxa the matchingTaxa to set
     */
    public void setMatchingTaxa(ArrayList<Taxon> matchingTaxa) {
        this.matchingTaxa = matchingTaxa;
        setUserSessionVariable(MATCHING_TAXA_KEY, this.matchingTaxa);
        SessionRenderer.render(userSessionIdentifier);
    }
    public int getNoMatchingTaxa(){
        int size = this.getMatchingTaxa().size();

        return size;
    }
    //**********************************************************
    //              TAXONOMY PROCESSING
    //These are the methods that manage the String array showing
    //progress of the taxonomy processing.
    //**********************************************************
    /**
     * @return the taxonomyProcessingOutput
     */
    public String getTaxonomyProcessingOutput() {
        taxonomyProcessingOutput = (String) getUserSessionVariable(TAXON_PROCESS_OUTPUT_KEY);
        if (taxonomyProcessingOutput == null) {
            taxonomyProcessingOutput = "";
        }
        return taxonomyProcessingOutput;
    }

    /**
     * @param taxonomyProcessingOutput the taxonomyProcessingOutput to set
     */
    public void setTaxonomyProcessingOutput(String taxonomyProcessingOutput) {
        this.taxonomyProcessingOutput = taxonomyProcessingOutput;
        setUserSessionVariable(TAXON_PROCESS_OUTPUT_KEY, this.taxonomyProcessingOutput);
        SessionRenderer.render(userSessionIdentifier);
    }

    //*********************************************************************
    //Here are some methods to make the user session Ajax push work easier:
    //*********************************************************************
    /**
     * This essential method makes sure that the IPC session is properly initialised.
     * It can be referred to from the bean constructor method, or give the @PostConstruct annotation
     * to make sure it's called immediately after bean construction.
     */
    public void ensureSessionInitiated() {
        if (userSessionIdentifier == null) {
            try {
                userSessionIdentifier = getUserSessionID();
                SessionRenderer.addCurrentSession(userSessionIdentifier);
            } catch (Exception ex) {
                setUserSessionVariable(IPCTEST, "a problem was encountered adding this portlet to the user render session");
            }
        }
    }

    private Object getUserSessionVariable(String name) {
        return getPortalUserSession().getAttribute(name, PortletSession.APPLICATION_SCOPE);
    }

    private void setUserSessionVariable(String name, Object val) {
        getPortalUserSession().setAttribute(name, val, PortletSession.APPLICATION_SCOPE);
    }

    private String getUserSessionID() {
        return getPortalUserSession().getId();
    }

    private PortletSession getPortalUserSession() {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        Object sessObj = ec.getSession(false);
        if (sessObj != null && sessObj instanceof PortletSession) {
            return (PortletSession) sessObj;
        }
        return null;
    }
    //this may end up a temp solution
    //making this method available to other classes so they can call a re-render
    //public void render() {
    //    ensureSessionInitiated();
    //    SessionRenderer.render(userSessionIdentifier);
    //}
}
