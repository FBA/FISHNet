/**
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package org.freshwaterlife.portlets.dataset.display.my;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.User;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.PortletURLUtil;

import java.io.Serializable;
import org.freshwaterlife.portlets.search.process.ProcessResults;
import org.freshwaterlife.portlets.search.resulttypes.BasicResultDoc;
import org.freshwaterlife.portlets.search.resulttypes.Image;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;

/**
 * @author Paul Johnson
 */
public class UserBean implements Serializable {

	private String solrURL;
	private String imagesLink;
	private String editMetadataLink;
	private String requestUrl;
    private int numOfDatasetResults;
    private int currentPage;
    private int totalPages;
    private ArrayList<BasicResultDoc> searchResults;
    private ArrayList<Integer> pageIndex = new ArrayList<Integer>();

    public UserBean() {
        this.currentPage = 1;
    }

    private void calculateTotalPages() {

        this.totalPages = ((int) (Math.ceil((double) numOfDatasetResults / Global.iNumOfResultsPerPage)));
    }

    public String doSearch() {

        this.currentPage = 1;

        return doReload(true);
    }

    public String doReload() {
        return doReload(false);
    }

    public String doReload(Boolean updateTotals) {
        //create empty results with some info in case something goes wrong
        Image blank = new Image();
        blank.setTitle("No results found or error occurred");

        ArrayList<BasicResultDoc> tmpResults = new ArrayList<BasicResultDoc>();
        tmpResults.add(blank);

        try {
            String strDoctypeQuery = "";
            String strRolesQuery = "";
            
            strDoctypeQuery = "(doctype:dataset) ";
                                 
            try {
            	FacesContext fc = FacesContext.getCurrentInstance();
            	ExternalContext externalContext = fc.getExternalContext();
            	if (externalContext.getUserPrincipal() == null) {
            	    System.out.println("current principal is null");
            	    strRolesQuery = "AND (1=0)";
            	} else {
		            // retrieves the user from the url
		            javax.servlet.http.HttpServletRequest request = (javax.servlet.http.HttpServletRequest) (FacesContext
		    				.getCurrentInstance().getExternalContext().getRequestMap()
		    				.get("com.liferay.portal.kernel.servlet.PortletServletRequest"));            
		            
		            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		            Layout layout = themeDisplay.getLayout();
		            long companyId = layout.getCompanyId();
		            long groupId = layout.getGroupId();
		            String friendlyURLByLayOut = null;
		            try {
		            	friendlyURLByLayOut = GroupLocalServiceUtil.getGroup(groupId).getFriendlyURL();
		            } catch (PortalException ex) {
		            	System.out.println(ex);
		            } catch (SystemException ex) {
		            	System.out.println(ex);
		            }
		                        
		            String currentUser = friendlyURLByLayOut.substring(1);            
		            User user = UserLocalServiceUtil.getUserByScreenName(companyId, currentUser);
		            long userId = user.getUserId();            
		            
		            strRolesQuery = "AND (roleIds:(";
		    	    String sUserId = "u" + Long.toString(userId);
		    	    System.out.println("UserId: " + sUserId);
		    	    strRolesQuery += sUserId + "))";
		    	    System.out.println("Roles Query: " + strRolesQuery);
            	}
            	
        	} catch (Exception e) {
                System.out.println(e);
            }
            
            //System.out.println("solr url:" + getApplicationBean().getSolrUrl());
            System.out.println("doctype query:" + strDoctypeQuery);
            System.out.println("roles query:" + strRolesQuery);
            System.out.println("start:" + (this.currentPage - 1) * Global.iNumOfResultsPerPage);
            System.out.println("rows:" + Global.iNumOfResultsPerPage);
            this.requestUrl = this.solrURL + "/select/?q=(" + strDoctypeQuery + strRolesQuery + ")&version=2.2&start=" 
            	+ (this.currentPage - 1) * Global.iNumOfResultsPerPage + "&rows=" + Global.iNumOfResultsPerPage 
            	+ "&indent=on&facet=on&facet.field=doctype";
            System.out.println("Request Url: " + this.requestUrl);
            ProcessResults pr = new ProcessResults(requestUrl);

            pr.process();
            
            tmpResults = pr.getResults();
            if (updateTotals) {
                numOfDatasetResults = pr.getINumOfDatasetResults();
            }

        } catch (Exception e) {
            numOfDatasetResults = 0;
            System.out.println("Exception: " + e.getMessage());
        }
        this.calculateTotalPages();
        this.setSearchResults(tmpResults);
        this.populatePageIndex(tmpResults.size()); //so 0 results can be handled

        return "submit"; //trigger the form in the view.xhtml file
    }

    private void populatePageIndex(int iResultSize) {
        this.pageIndex.clear();

        int iCurrentPage = this.getCurrentPage();
        int iLowerBound = iCurrentPage - Global.iPageOffset;
        int iUpperBound = iCurrentPage + Global.iPageOffset;

        if (iResultSize == 0) {
            iLowerBound = 1;
            iUpperBound = 1;
        } else {
            if (iLowerBound < 1) {
                iLowerBound = 1;
                iUpperBound = iLowerBound + (Global.iPageOffset * 2);
                if (iUpperBound > this.getTotalPages()) {
                    iUpperBound = this.getTotalPages();
                }
            } else if (iUpperBound > this.getTotalPages()) {
                iUpperBound = this.getTotalPages();
                iLowerBound = iUpperBound - (Global.iPageOffset * 2);
                if (iLowerBound < 1) {
                    iLowerBound = 1;
                }
            }
        }
        for (int i = iLowerBound; i <= iUpperBound; i++) {
            this.pageIndex.add(i);
        }
    }

    //*** Getters and Setters ***
    public ArrayList<Integer> getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(ArrayList<Integer> pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getNumOfDatasetResults() {
        return numOfDatasetResults;
    }

    public void setNumOfDatasetResults(int numOfDatasetResults) {
        this.numOfDatasetResults = numOfDatasetResults;
    }

    public int getNumOfCurrentTabResults() {
        return getNumOfDatasetResults();
    }

    public ArrayList<BasicResultDoc> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(ArrayList<BasicResultDoc> results) {
        this.searchResults = results;
    }

    public void setCurrentPage(int page) {
        this.currentPage = page;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void setTotalPages(int pages) {
        this.totalPages = pages;
    }

    public int getTotalPages() {
        return this.totalPages;
    }
    
    public String getSolrURL()
    {
    	System.out.println("Getting Solr URL: " + this.solrURL);
    	return this.solrURL;
    }
    
    public void setSolrURL(String solrURL)
    {
    	this.solrURL = solrURL;
    	System.out.println("Setting Solr URL: " + this.solrURL);
    	this.doSearch();
    }
    
    public void setEditMetadataLink(String link) {
        this.editMetadataLink = link;
    }

    public String getEditMetadataLink() {
        return this.editMetadataLink;
    }

	public void setImagesLink(String imagesLink) {
		this.imagesLink = imagesLink;
	}

	public String getImagesLink() {
		return imagesLink;
	}
}
