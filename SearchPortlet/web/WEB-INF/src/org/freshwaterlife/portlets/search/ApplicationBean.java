package org.freshwaterlife.portlets.search;

import com.liferay.portal.kernel.util.PropsUtil;
import java.io.Serializable;

/**
 * <p>Application scope data bean
 * @version ApplicationBean.java
 * @version Created on 23-Mar-2010, 20:51:39
 * @author Kearon McNicol
 */
public class ApplicationBean implements Serializable {

//    private String[] arrSolrCores = {
//        "/solr/core_liferay",
//        "/solr/core_gallery",
//        "/solr/core_species"};
//    private String[] arrSolrUrl;
    private String solrCore = "/solr/core_all";
    private String solrUrl;
    private String strSolrServer;

    /**
     * <p>Construct a new application data bean instance.</p>
     */
    public ApplicationBean() {

//        arrSolrUrl = new String[arrSolrCores.length];

        try {
            strSolrServer = PropsUtil.get(Global.strSolrServerProperty);
        } catch (Exception e) {
            strSolrServer = "localhost";
        }
        
        if (strSolrServer == null || strSolrServer.compareTo("null") == 0 || strSolrServer == "")
        {
            strSolrServer = "localhost:8080";
        } 

//        for (int i = 0; i < arrSolrCores.length; i++) {
//            String strURL = "http://" + strSolrServer + arrSolrCores[i];
//            setArrSolrUrl(i, strURL);
//        }

        setSolrUrl("http://" + strSolrServer + solrCore);
    }

    /**
     * Get the value of arrSolrUrl
     *
     * @return the value of arrSolrUrl
     */
//    public String[] getArrSolrUrl() {
//        return arrSolrUrl;
//    }

//    public String getArrSolrUrl(int i) {
//        return arrSolrUrl[i];
//    }

//    public void setArrSolrUrl(String[] arrSolrUrl) {
//        this.arrSolrUrl = arrSolrUrl;
//    }

//    public void setArrSolrUrl(int i, String strSolrUrl) {
//        this.arrSolrUrl[i] = strSolrUrl;
//    }

    public String getSolrUrl() {
        return solrUrl;
    }

    public void setSolrUrl(String solrUrl) {
        this.solrUrl = solrUrl;
    }
}
