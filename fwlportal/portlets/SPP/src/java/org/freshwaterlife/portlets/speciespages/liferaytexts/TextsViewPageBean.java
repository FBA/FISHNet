/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freshwaterlife.portlets.speciespages.liferaytexts;

import com.sun.rave.web.ui.appbase.AbstractPageBean;
import org.freshwaterlife.portlets.speciespages.Coordinator;

/**
 *
 * @author kearon
 */
public class TextsViewPageBean extends AbstractPageBean {

    private StringBuilder htmlOut;
    private Coordinator coordinator;
    private LiferayTextsSessionBean textsSessionBean;

    @Override
    public void init() {
        super.init();
        getTextsSessionBean()._init();
    }

    /**
     * @return the htmlOut
     */
    public String getHtmlOut() {
        this.htmlOut = new StringBuilder();

        RetrieveArticles ra = new RetrieveArticles(textsSessionBean);


// TODO - Put this back to using the coordinator
        String spp = coordinator.getSelectedTaxon().getPrefSciName().trim();
        //String spp = "perca fluviatilis";


        if (spp == null || spp.trim().length() == 0) {
            this.htmlOut.append("no species selected");
        } else {
           this.htmlOut.append(ra.getArticle(spp, "novice", "tbc", true));
        }
        return htmlOut.toString();
    }

    /**
     * @param htmlOut the htmlOut to set
     */
    public void setHtmlOut(String htmlOut) {
        this.htmlOut = new StringBuilder(htmlOut);
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
     * @return the textsSessionBean
     */
    public LiferayTextsSessionBean getTextsSessionBean() {
        return textsSessionBean;
    }

    /**
     * @param textsSessionBean the textsSessionBean to set
     */
    public void setTextsSessionBean(LiferayTextsSessionBean textsSessionBean) {
        this.textsSessionBean = textsSessionBean;
    }
}
