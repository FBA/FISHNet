package org.freshwaterlife.portlets.speciespages.biorecordmaps;

import org.freshwaterlife.portlets.speciespages.Coordinator;
import org.freshwaterlife.portlets.speciespages.Taxon;

/**
 *
 * @author KMcNicol
 */
public class BioRecordsView {

    //JSF managed property
    private Coordinator coordinator;

    //variable to hold copy of selected taxon info
    private Taxon selectedTaxon;

    //string variable that holds the html results - probably could make this a smarter approach later.
    private String mapTitle="waiting for map to update";
    private String mapURL="#";
    private String mapDataProviders="waiting for map provider";
    String htmlOutput;

    //variable for holding which map provider to use
    private int useProvider = NBN;
    private static final int NBN = 1;
    private static final int GBIF = 2;

    //state management
    private boolean providerChanged = true;

    //used in NBN cases
    RetrieveNBNMap nbn;

    public BioRecordsView() {
    }

    private void initialiseProvider() {
        //make we are getting maps for the right taxon
        selectedTaxon = coordinator.getSelectedTaxon();

        switch (useProvider) {
            case NBN:
                setupNBN();
                break;
            default:
                setupNBN();
                break;
        }

       // coordinator.render();
    }

    private void setupNBN() {
        nbn = new RetrieveNBNMap(selectedTaxon.getPrefSciName(), selectedTaxon.getNbnTvk());
        setMapURL(nbn.getMapUrl());
        setMapTitle(nbn.getMapTitle());
        setMapDataProviders(nbn.getDataProviders());
    }

    /**
     * @return the mapImgUrl
     */
    public String getHtmlOutput() {
        htmlOutput="html to go here";
        selectedTaxon = coordinator.getSelectedTaxon();

        if (providerChanged) {
            this.initialiseProvider();
        }

        switch (useProvider) {
            case NBN:
                htmlOutput = nbn.getHtmlOutput(selectedTaxon.getPrefSciName(), selectedTaxon.getNbnTvk());
                break;
            default:
                htmlOutput = nbn.getHtmlOutput(selectedTaxon.getPrefSciName(), selectedTaxon.getNbnTvk());
                break;
        }

        return htmlOutput;
    }

    /**
     * @param mapImgUrl the mapImgUrl to set
     */
    public void setHtmlOutput(String html) {
        this.htmlOutput = html;
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
     * @return the mapImage
     */
    public String getMapURL() {
        if (providerChanged){
            this.initialiseProvider();
            providerChanged = false;
        }
        // mapURL = "http://data.nbn.org.uk/output/gridGBv4_nbnims-2489612721756.gif";
        return mapURL;
    }

    /**
     * @param mapImage the mapImage to set
     */
    public void setMapURL(String mapURL) {
        this.mapURL = mapURL;
    }

    /**
     * @return the mapTitle
     */
    public String getMapTitle() {
        if (providerChanged){
            this.initialiseProvider();
            providerChanged = false;
        }
        // mapTitle = "PerchTest";
        return mapTitle;
    }

    /**
     * @param mapTitle the mapTitle to set
     */
    public void setMapTitle(String mapTitle) {
        this.mapTitle = mapTitle;
    }

    /**
     * @return the mapDataProviders
     */
    public //string variable that holds the html results - probably could make this a smarter approach later.
    String getMapDataProviders() {
        if (providerChanged){
            this.initialiseProvider();
            providerChanged = false;
        }
        // mapDataProviders = "map-data";
        return mapDataProviders;
    }

    /**
     * @param mapDataProviders the mapDataProviders to set
     */
    public void setMapDataProviders(String providers) {
        this.mapDataProviders = providers;
    }
}
