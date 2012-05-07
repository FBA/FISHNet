/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freshwaterlife.portlets.search;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.RenderRequest;
import org.freshwaterlife.portlets.QueryString;

/**
 * <p>Request scope data bean that backs the similarly named page</p>
 * @version ViewPageBean.java
 * @version Created on 23-Mar-2010, 20:51:39
 * @author Kearon McNicol
 */
public class PageBean {

    private RenderRequest renderRequest;
    private String baseUrl;
    private String solrCss;
    private UserBean userBean;
    private ApplicationBean applicationBean;
    private Boolean urlQValue;

    /**
     * <p>Construct a new Page bean instance.</p>
     */
    public PageBean() {
    }

    @PostConstruct
    public void init() {
    }

    @PreDestroy
    public void clean() {
    }

    public boolean isUrlQValue() {
        return getUrlQValue();
    }

    public void setUrlQValue(Boolean urlQValue) {
        this.urlQValue = urlQValue;
    }

    public Boolean getUrlQValue() {
        //TODO - understand why this method gets call twice on page load
        //TODO - sometimes takes 2 page loads to get the url param to have an effect - probably because of the order things occur in and becuase a value is already in the userBean (session).
        Boolean success = false;
        String t = null;
        try {
            t = (String) this.getRenderRequest().getAttribute("javax.servlet.forward.query_string");
        } catch (Exception e) {
            t = null;
            System.out.println(e);
        }

        if (t != null) {
            QueryString qString = new QueryString(t);
            try {
                String qV = qString.getParameter("q");
                if (qV != null) {
                    userBean.setqValue(qV);
                    userBean.doSearch();
                    success = true;
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        return success;
    }

    /**
     * @return the renderRequest
     */
    public RenderRequest getRenderRequest() {
        if (renderRequest == null) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext extContext = facesContext.getExternalContext();
            try {
                renderRequest = (RenderRequest) extContext.getRequest();
            } catch (Exception e) {
                System.out.println(e);
            }
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
     * <p>Return a reference to the scoped data bean.</p>
     *
     * @return reference to the scoped data bean
     */
    public UserBean getUserBean() {
        return this.userBean;
    }

    /**
     * @param userBean the userBean to set
     */
    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    /**
     * @return the applicationBean
     */
    public ApplicationBean getApplicationBean() {
        return applicationBean;
    }

    /**
     * @param applicationBean the applicationBean to set
     */
    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }
}
