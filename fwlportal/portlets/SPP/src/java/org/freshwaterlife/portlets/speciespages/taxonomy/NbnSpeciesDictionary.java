package org.freshwaterlife.portlets.speciespages.taxonomy;

import java.util.ArrayList;
import net.searchnbn.webservices.taxonomyservice.GatewayWebService;
import net.searchnbn.webservices.taxonomydata.Taxon;
import net.searchnbn.webservices.taxonomyquery.TaxonomySearchRequest;
import net.searchnbn.webservices.taxonomydata.TaxonomySearchList;
import net.searchnbn.webservices.taxonomydata.TaxonList;
import net.searchnbn.webservices.taxonomydata.TaxonName;

import java.util.List;
import javax.xml.ws.soap.SOAPFaultException;

/**
 *
 * @author KMcNicol
 */
public class NbnSpeciesDictionary {

    // NBN variables
    private GatewayWebService gws;
    private TaxonomySearchRequest ts;
    private TaxonomySearchList taxonSearchList;
    private List<net.searchnbn.webservices.taxonomydata.Taxon> list;
    // FwL variables for returning
    private ArrayList<org.freshwaterlife.portlets.speciespages.Taxon> taxaMatchResults;
    private String htmlOutput;

    public NbnSpeciesDictionary() {
    }

    /**
     * Given a name, this method populates the taxaMatchResults
     * arrayList with all potential matches and minimial info for each
     *
     * If at least one match is found then true is returned.  Otherwise the method returns false.
     *
     * @param taxonName
     * @return
     */
    public boolean nameSearch(String taxonName) {

        boolean searchSuccess = false;

        //TODO - add a connection timeout to catch problems communicating with NBN

        htmlOutput = "";
        taxaMatchResults = new ArrayList();

        //set up the connection
        gws = new GatewayWebService();
        ts = new TaxonomySearchRequest();

        if (taxonName != null && taxonName.length() > 0) {
            ts.setSpeciesName(taxonName);
        } else {
            searchSuccess = false;
            return false;
        }

        // getting the list of matching taxa
        try {
        taxonSearchList = gws.getNBNTaxonomySoapPort().getTaxonomySearch(ts);
        } catch (SOAPFaultException ex) {
            htmlOutput = ex.getMessage();
            return false;
        }
        list = taxonSearchList.getTaxon();

        int size = list.size();
        if (size > 0) {
            searchSuccess = true;

            htmlOutput = htmlOutput + "<strong>" + size + " matches found:</strong>";
            htmlOutput = htmlOutput + "<ul>";

            //iterate around the list of matches
            for (int i = 0; i < size; i++) {

                //retrieve the nbn taxon object
                Taxon t = list.get(i);

                //generate the html output version
                htmlOutput = htmlOutput + "<li>" +
                        "<i>" + ((TaxonName) (t.getTaxonName())).getValue() + "</i>, " +
                        t.getAuthority() + " - " +
                        t.getTaxonVersionKey() + " - " +
                        t.getTaxonReportingCategory();

                //generate the array list of strings for the results to pass back
                org.freshwaterlife.portlets.speciespages.Taxon fwlResult = this.getFwLTaxonDetails(t);

                //retrieve synonyms - strings only for now.
                htmlOutput = htmlOutput + getSynonymsHtml(t);
                fwlResult.setPrefEnName(getEnglishName(t));
                fwlResult.setSynonyms(getSynonyms(t));

                //add this to the results to return
                taxaMatchResults.add(fwlResult);
            }
        } else {
            searchSuccess = false;
            htmlOutput = htmlOutput + "<p>Your search did not produce any hits.</p>";
        }

        return searchSuccess;
    }

    /**
     * returns an fwl taxon format of the first match from an NBN search
     * using the string for the search
     * @param taxonName
     * @return
     */
    public org.freshwaterlife.portlets.speciespages.Taxon taxonDetails(String taxonName) {

        //TODO - add a connection timeout to catch problems communicating with NBN
        org.freshwaterlife.portlets.speciespages.Taxon fwlTaxon = new org.freshwaterlife.portlets.speciespages.Taxon();

        //set up the connection
        gws = new GatewayWebService();
        ts = new TaxonomySearchRequest();

        // check the search string is not null and not 0 length
        // then set the search criteria
        if (taxonName != null && taxonName.length() > 0) {
            ts.setSpeciesName(taxonName);
        } else {
            return fwlTaxon;
        }

        // getting the list of matching taxa
        taxonSearchList = gws.getNBNTaxonomySoapPort().getTaxonomySearch(ts);
        list = taxonSearchList.getTaxon();


        // check there is at least one result.
        int size = list.size();
        if (size > 0) {

            Taxon t = list.get(0);

            //get the basic details
            fwlTaxon = this.getFwLTaxonDetails(t);

            //retrieve synonyms - strings only for now.
            fwlTaxon.setPrefEnName(this.getEnglishName(t));
            fwlTaxon.setSynonyms(this.getSynonyms(t));
            fwlTaxon.setLowerTaxa(this.getLowerTaxa(t));
        }

        return fwlTaxon;
    }

    /**
     * Returns and fwl taxon with details from the NBN for the
     * taxon with the given Taxon Version Key (tvk)
     * @param tvk
     * @return
     */
    public org.freshwaterlife.portlets.speciespages.Taxon taxonDetails_tvk(String tvk) {

        //TODO - add a connection timeout to catch problems communicating with NBN
        org.freshwaterlife.portlets.speciespages.Taxon fwlTaxon = new org.freshwaterlife.portlets.speciespages.Taxon();

        //set up the connection
        gws = new GatewayWebService();
        ts = new TaxonomySearchRequest();

        // check the search string is not null and not 0 length
        // then set the search criteria
        if (tvk != null && tvk.length() > 0) {
            ts.setTaxonVersionKey(tvk);
        } else {
            return fwlTaxon;
        }

        // getting the list of matching taxa
        taxonSearchList = gws.getNBNTaxonomySoapPort().getTaxonomySearch(ts);
        list = taxonSearchList.getTaxon();


        // check there is at least one result.
        int size = list.size();
        if (size > 0) {

            Taxon t = list.get(0);

            //get the basic details
            fwlTaxon = this.getFwLTaxonDetails(t);

            //retrieve synonyms - strings only for now.
            fwlTaxon.setPrefEnName(this.getEnglishName(t));
            fwlTaxon.setSynonyms(this.getSynonyms(t));
            fwlTaxon.setLowerTaxa(this.getLowerTaxa(t));
        }

        return fwlTaxon;
    }


    private org.freshwaterlife.portlets.speciespages.Taxon getFwLTaxonDetails(Taxon t) {
        // setup return variable of an fwl taxon
        org.freshwaterlife.portlets.speciespages.Taxon fwlResult = new org.freshwaterlife.portlets.speciespages.Taxon();

        //get the variables out of the nbn object so they're easier to work with
        TaxonName tN = t.getTaxonName();
        String auth = t.getAuthority();
        String tvk = t.getTaxonVersionKey();

        //process the name
        if (tN.isIsScientific()) {
            fwlResult.setPrefSciName(tN.getValue());
        } else {
            fwlResult.setPrefEnName(tN.getValue());
        }

        //process the authority
        if (t.getAuthority().equals("&nbsp;")) {
            fwlResult.setPrefSciNameAuth("");
        } else {
            fwlResult.setPrefSciNameAuth(t.getAuthority());
        }

        //process the tvk
        fwlResult.setNbnTvk(tvk);

        return fwlResult;
    }

    private String getSynonymsHtml(Taxon t) {
        String results = "";
        if (t.getSynonymList() != null && t.getSynonymList().getTaxon().size() > 0) {
            results = results + "<ul>";
            TaxonList taxonList = t.getSynonymList();
            if (taxonList != null) {
                List<Taxon> list2 = taxonList.getTaxon();
                for (int j = 0; j < list2.size(); j++) {
                    Taxon sT = list2.get(j);
                    TaxonName syn = sT.getTaxonName();
                    results = results +
                            "<li><i>" + syn.getValue() + "</i>, " +
                            sT.getAuthority() + " - " + sT.getTaxonVersionKey() + "|| isPreferred=" + syn.isIsPreferredName() + " | isScientific=" + syn.isIsScientific() + "</li>";
                }
            }
            results = results + "</ul>";
        }
        return results;
    }

    private ArrayList<org.freshwaterlife.portlets.speciespages.Taxon> getSynonyms(Taxon t) {
        //set up FwL synonyms variable
        ArrayList<org.freshwaterlife.portlets.speciespages.Taxon> synonyms = new ArrayList();

        if (t.getSynonymList() != null && t.getSynonymList().getTaxon().size() > 0) {
            TaxonList taxonList = t.getSynonymList();
            if (taxonList != null) {
                List<Taxon> list2 = taxonList.getTaxon();
                for (int j = 0; j < list2.size(); j++) {
                    Taxon sT = list2.get(j);
                    org.freshwaterlife.portlets.speciespages.Taxon fST = this.getFwLTaxonDetails(sT);
                    synonyms.add(fST);
                }
            }
        }

        return synonyms;
    }

    private String getEnglishName(Taxon t) {
        //set up FwL synonyms variable
        String name_en = "";

        if (t.getSynonymList() != null && t.getSynonymList().getTaxon().size() > 0) {
            TaxonList taxonList = t.getSynonymList();
            if (taxonList != null) {
                List<Taxon> list2 = taxonList.getTaxon();
                for (int j = 0; j <
                        list2.size(); j++) {
                    Taxon sT = list2.get(j);
                    TaxonName n = sT.getTaxonName();
                    if (!n.isIsScientific()) {
                        name_en = n.getValue();
                        break;

                    }


                }
            }

        }
        return name_en;
    }

    private String getLowerTaxaHtml(Taxon t) {
        String results = "";
        TaxonList nbnlowerTaxonList = t.getLowerTaxaList();
        if (nbnlowerTaxonList != null) {
            List<Taxon> list2 = nbnlowerTaxonList.getTaxon();
            if (list2 != null && list2.size() > 0) {
                results = results + "<h4>Lower Taxa</h4>";
                results =
                        results + "<ul>";

                for (int j = 0; j <
                        list2.size(); j++) {
                    results = results + "<li><i>" +
                            ((TaxonName) (list2.get(j).getTaxonName())).getValue() + "</i>, ";
                    results =
                            results + list2.get(j).getAuthority() + " - " + list2.get(j).getTaxonVersionKey() + "</li>";
                }

                results = results + "</ul>";
            }

        }
        return results;
    }

    private ArrayList<org.freshwaterlife.portlets.speciespages.Taxon> getLowerTaxa(Taxon t) {
        ArrayList<org.freshwaterlife.portlets.speciespages.Taxon> lowertaxa = new ArrayList();

        TaxonList nbnlowerTaxonList = t.getLowerTaxaList();
        if (nbnlowerTaxonList != null) {
            List<Taxon> list2 = nbnlowerTaxonList.getTaxon();
            if (list2 != null && list2.size() > 0) {

                for (int j = 0; j <
                        list2.size(); j++) {
                    Taxon lT = list2.get(j);

                    //generate an FwL Taxon version of the match
                    org.freshwaterlife.portlets.speciespages.Taxon fwlLowTaxon = new org.freshwaterlife.portlets.speciespages.Taxon();
                    fwlLowTaxon.setPrefSciName(lT.getTaxonName().getValue());
                    fwlLowTaxon.setPrefSciNameAuth(lT.getAuthority());
                    fwlLowTaxon.setNbnTvk(lT.getTaxonVersionKey());
                    //add this to the results to return
                    lowertaxa.add(fwlLowTaxon);

                }

            }
        }
        return lowertaxa;
    }

    private String getAggregates(Taxon t) {
        String results = "";

        TaxonList nbnTaxonAggregate = t.getAggregateList();
        if (nbnTaxonAggregate != null) {
            List<Taxon> list3 = nbnTaxonAggregate.getTaxon();
            if (list3 != null && list3.size() > 0) {
                results = results + "<h4>Aggregates</h4>";
                results =
                        results + "<ul>";
                for (int j = 0; j <
                        list3.size(); j++) {
                    results = results + "<li><i>";
                    results =
                            results + ((TaxonName) (list3.get(j).getTaxonName())).getValue();
                    results =
                            results + "</i>, ";
                    results =
                            results + list3.get(j).getAuthority() + " - " + list3.get(j).getTaxonVersionKey();
                    results =
                            results + "</li>";
                }

            }
            results = results + "</ul>";
        }

        return results;
    }

    /**
     * @return the taxaMatchResults
     */
    public ArrayList<org.freshwaterlife.portlets.speciespages.Taxon> getTaxaMatchResults() {
        return taxaMatchResults;
    }

    /**
     * @return the htmlOutput
     */
    public String getHtmlOutput() {
        return htmlOutput;
    }
}
