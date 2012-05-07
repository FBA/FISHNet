package org.freshwaterlife.portlets.speciespages.biorecordmaps;

import java.util.List;
import javax.faces.event.ActionEvent;
import net.searchnbn.webservices.data.DatasetSummary;
import net.searchnbn.webservices.data.DatasetSummaryList;
import net.searchnbn.webservices.query.GridMapRequest;
import net.searchnbn.webservices.query.GridMapSettings;
import net.searchnbn.webservices.data.GridMap;
import net.searchnbn.webservices.data.ProviderMetadata;
import net.searchnbn.webservices.data.Species;
import net.searchnbn.webservices.service.GatewayWebService;
import net.searchnbn.webservices.service.GatewaySoapPort;

/**
 *
 * @author KMcNicol
 */
public class RetrieveNBNMap {

    private String nbnTVK;
    private String htmlOutput = "NBN data to show here";
    private String mapTitle;
    private GridMap nbnGmap;
    private String mapUrl;
    private String dataProviders;
    private String nbnTerms;
    private String nbnLogoUrl;
    private Species nbnTaxon = null;

    public RetrieveNBNMap(String spName, String tvk) {
        if (tvk == null || tvk.length() == 0) {
            // Error handling to go here -- htmlOutput = "Sorry, we were unable to retrive any NBN maps for " + spName + ".  It may be that the NBN do not provide maps for this group.";
            mapUrl = "#error occurred updating the map";
            mapTitle = "An error occurred retrieving this map";
            dataProviders = "An error occurred retrieving data";
        } else if (this.updateMap(tvk)) {

            //work has been done by the call to this.updateMap in the condition statement
        } else {
            // Error handling to go here -- htmlOutput = "Sorry, we were unable to retrive any NBN maps for " + spName + ".  It appears there was a problem resovling the name correctly. (TVK used: " + tvk + ").";
            mapUrl = "#error occurred updating the map";
            mapTitle = "An error occurred retrieving this map";
            dataProviders = "An error occurred retrieving data";
        }
    }

    /**
     * This is the main method.
     * It takes a tvk input and returns html with the map, etc in it.
     * @return the htmlOutput
     */
    public String getHtmlOutput(String spName, String tvk) {
        if (tvk == null || tvk.length() == 0) {
            htmlOutput = "Sorry, we were unable to retrive any NBN maps for " + spName + ".  It may be that the NBN do not provide maps for this group.";
        } else if (this.updateMap(tvk)) {
            htmlOutput = "";
            htmlOutput = htmlOutput + "<h4>British Isles records for ";
            htmlOutput = htmlOutput + getMapTitle();
            htmlOutput = htmlOutput + "<a href='http://data.nbn.org.uk' target='_blank'>";
            htmlOutput = htmlOutput + "<img style='float:right;' src='" + getNbnLogoUrl() + "'/>";
            htmlOutput = htmlOutput + "</a></h4>";
            htmlOutput = htmlOutput + "<div><img src='" + getMapUrl() + "'/></div>";
            htmlOutput = htmlOutput + "<h5>Datasets used</h5>";
            htmlOutput = htmlOutput + "<div>" + getDataProviders() + "</div>";
            htmlOutput = htmlOutput + "<div><a href='" + this.getNbnTerms() + "' target='_blank'>terms and conditions</a></div>";
//          htmlOutput = htmlOutput + "TVK: " + tvk;
        } else {
            htmlOutput = "Sorry, we were unable to retrive any NBN maps for " + spName + ".  It appears there was a problem resovling the name correctly. (TVK used: " + tvk + ").";
        }

        return htmlOutput;
    }

    /**
     * @param htmlOutput the htmlOutput to set
     */
    public void setHtmlOutput(String htmlOutput) {
        this.htmlOutput = htmlOutput;
    }

    public boolean updateMap(String tvk) {
        boolean updateSuccessful = true;
        try {
            nbnTVK = tvk;
            if (nbnTVK == null || nbnTVK.equals("")) {
                nbnTVK = "NBNSYS0000002407";
            }

            GatewayWebService gws = new GatewayWebService();
            GridMapRequest r = new GridMapRequest();

            // Apply some map settings, for example image size, background, zoom to vice county.
            // Here only the image size is set, the default settings are used for remaining map properties
            GridMapSettings mapSettings = new GridMapSettings();
            mapSettings.setWidth(new Integer(300));
            mapSettings.setHeight(new Integer(300));
            r.setGridMapSettings(mapSettings);
            r.setTaxonVersionKey(nbnTVK);
            r.getTaxonVersionKey();

            // send the request and put the results into a GridMap object
            GatewaySoapPort gwSoapPort = gws.getGatewaySoapPort();
            System.out.println(gwSoapPort.toString());
            nbnGmap = gwSoapPort.getGridMap(r);

            //now we've got the map - set the local variables
            mapUrl = getNbnGmap().getMap().getUrl();
            nbnTerms = getNbnGmap().getTermsAndConditions();
            updateDataProviders();
            nbnTaxon = getNbnGmap().getSpecies();
            nbnLogoUrl = getNbnGmap().getNBNLogo();
        } catch (Exception e) {
            updateSuccessful = false;
            System.out.println(e.getMessage() + e.getStackTrace());
        }

        return updateSuccessful;
    }

    public String getNbnLogoUrl() {
        return nbnLogoUrl;
    }

    public String getNbnTerms() {
        return nbnTerms;
    }

    public String getDataProviders() {
        return dataProviders;
    }

    private void updateDataProviders() {
        dataProviders = "";
        // Get an array of datasts
        DatasetSummaryList datasetSummarylist = getNbnGmap().getDatasetSummaryList();
        List<DatasetSummary> datasetsummarylist = datasetSummarylist.getDatasetSummary();
        DatasetSummary datasetSummary = new DatasetSummary();

        ProviderMetadata providerMetadata = new ProviderMetadata();
        if (datasetsummarylist.size() > 0) {
            dataProviders = "<ul class='nbn-dataprovders'>";
            for (int i = 0; i < datasetsummarylist.size(); i++) {
                datasetSummary = (DatasetSummary) datasetsummarylist.get(i);
                providerMetadata = (ProviderMetadata) datasetSummary.getProviderMetadata();
                dataProviders = dataProviders + "<li>" + datasetSummary.getProviderMetadata().getDatasetTitle() + " - ";
                dataProviders = dataProviders + providerMetadata.getDatasetProvider() + "</li>";
            }
            dataProviders = dataProviders + "</ul>";
        }
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public String getMapTitle() {
        mapTitle = "";
        if (nbnTaxon != null) {
            if (nbnTaxon.getCommonName() != null && nbnTaxon.getCommonName().length() > 0) {
                mapTitle = nbnTaxon.getCommonName() + " (<em>" + nbnTaxon.getScientificName() + "</em>)";
            } else {
                mapTitle = "<em>" + nbnTaxon.getScientificName() + "</em>";
            }
        } else {
            mapTitle = "Waiting for valid Taxon";
        }
        return mapTitle;
    }

    /**
     * @return the nbnTVK
     */
    public String getNbnTVK() {
        return nbnTVK;
    }

    public GridMap getNbnGmap() {
        return nbnGmap;
    }

    /**
     * @param nbnTVK the nbnTVK to set
     */
    public void setNbnTVK(String nbnTVK) {
        this.nbnTVK = nbnTVK;
    }

    /**
     * @param mapTitle the mapTitle to set
     */
    public void setMapTitle(String mapTitle) {
        this.mapTitle = mapTitle;
    }

    /**
     * @param nbnGmap the nbnGmap to set
     */
    public void setNbnGmap(GridMap nbnGmap) {
        this.nbnGmap = nbnGmap;
    }

    /**
     * @param mapUrl the mapUrl to set
     */
    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }

    /**
     * @param dataProviders the dataProviders to set
     */
    public void setDataProviders(String dataProviders) {
        this.dataProviders = dataProviders;
    }

    /**
     * @param nbnTerms the nbnTerms to set
     */
    public void setNbnTerms(String nbnTerms) {
        this.nbnTerms = nbnTerms;
    }

    /**
     * @param nbnLogoUrl the nbnLogoUrl to set
     */
    public void setNbnLogoUrl(String nbnLogoUrl) {
        this.nbnLogoUrl = nbnLogoUrl;
    }
}
