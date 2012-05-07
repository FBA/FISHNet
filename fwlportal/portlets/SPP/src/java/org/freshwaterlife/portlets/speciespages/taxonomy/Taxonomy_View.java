/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.freshwaterlife.portlets.speciespages.taxonomy;

import java.util.ArrayList;
import org.freshwaterlife.portlets.speciespages.Coordinator;
import org.freshwaterlife.portlets.speciespages.Taxon;

/**
 *
 * @author KMcNicol
 */
public class Taxonomy_View {

    //managed jsf property
    private Coordinator coordinator;

    private ArrayList<Taxon> matchingTaxa;
    private String matchingTaxaHtml;

    /**
     * @return the matchingTaxa
     */
    public ArrayList<Taxon> getMatchingTaxa() {
        matchingTaxa = coordinator.getMatchingTaxa();
        return matchingTaxa;
    }

    /**
     * @param matchingTaxa the matchingTaxa to set
     */
    public void setMatchingTaxa(ArrayList<Taxon> matchingTaxa) {
        this.matchingTaxa = matchingTaxa;
        coordinator.setMatchingTaxa(this.matchingTaxa);
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
     * @return the matchingTaxaHtml
     */
    public String getMatchingTaxaHtml() {
        matchingTaxaHtml = this.getMatchingTaxa().toString();

        return matchingTaxaHtml;
    }

    /**
     * @param matchingTaxaHtml the matchingTaxaHtml to set
     */
    public void setMatchingTaxaHtml(String matchingTaxaHtml) {
        this.matchingTaxaHtml = matchingTaxaHtml;
    }



}
