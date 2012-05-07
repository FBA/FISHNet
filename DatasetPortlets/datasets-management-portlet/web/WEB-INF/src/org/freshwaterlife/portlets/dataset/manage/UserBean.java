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
package org.freshwaterlife.portlets.dataset.manage;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;


import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.freshwaterlife.portlets.dataset.process.ProcessResults;
import org.freshwaterlife.portlets.dataset.resulttypes.BasicResultDoc;
import org.freshwaterlife.portlets.dataset.resulttypes.Red;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.freshwaterlife.portlets.dataset.ApplicationBean;
import org.freshwaterlife.portlets.dataset.Global;

/**
 * @author Paul Johnson
 */
public class UserBean implements Serializable {

    private ApplicationBean applicationBean;
    private static final long serialVersionUID = 1L;
    private String imagesLink;
    private String profileLink;
    private String editMetadataLink;
    private String requestUrl;
    private int numOfDatasetResults;
    private int numOfRedResults;
    private int numOfOrangeResults;
    private int numOfGreenResults;
    private int numOfRedOrangeResults;
    private int numOfRedAndRedOrangeResults;
    private int numOfReviewableResults;
    private int numOfRecommendedforDOIResults;
    private int currentTab;
    private int currentPage;
    private int totalPages;
    private boolean isUserAReviewer;
    private boolean isUserAnEditor;
    private String solrURL;
    private String fedoraURL;
    private String fedoraUsername;
    private String fedoraPassword;
    private String premisTemplateFile;
    private String foxmlTemplateFile;
    private String keywordTemplateFile;
    private String serverURL;
    private boolean isUserAnAdministrator;
    private List listReviewersNames;
    private ArrayList<BasicResultDoc> searchResults;
    private ArrayList<BasicResultDoc> reviewableSearchResults;
    private ArrayList<Integer> pageIndex = new ArrayList<Integer>();
    private String workingPid;
    //private String selectedReviewerName;
    private List<User> userlistReviewers = new ArrayList<User>();
    private SelectItem siRevName;
    private String assignedReviewer;
    private String selectedReviewer;

    public UserBean() {
	this.currentTab = 0;
	this.currentPage = 1;
	String userId = null;
	long lReviewerUserId = 0;
	List<Role> roles = null;
	listReviewersNames = new ArrayList();
	Role rReviewer;

	//populate list of reviewers from reviewer role
	siRevName = new SelectItem("No Reviewers");
	listReviewersNames.add(siRevName);

	this.setIsUserAReviewer(false);
	this.setIsUserAnEditor(false);
	isUserAnAdministrator = false;
	long lCompanyId = getUser().getCompanyId();

	try {
	    rReviewer = RoleLocalServiceUtil.getRole(lCompanyId, Global.strDatasetReviewerRole);
	    userlistReviewers = UserLocalServiceUtil.getRoleUsers(rReviewer.getRoleId());
	    if (!userlistReviewers.isEmpty()) {
		listReviewersNames.clear();
		for (User user : userlistReviewers) {
		    lReviewerUserId = user.getUserId();

		    siRevName = new SelectItem(lReviewerUserId, user.getFullName());

		    listReviewersNames.add(this.siRevName);
		}
	    }
	} catch (PortalException e) {
	    e.printStackTrace();
	} catch (SystemException e) {
	    e.printStackTrace();
	}
	try {
	    userId = getUser().getLogin();
	    roles = RoleLocalServiceUtil.getUserRoles(getUser().getUserId());
	    for (Role tmpRole : roles) {
		if (tmpRole.getName().equalsIgnoreCase(Global.strDatasetReviewerRole)) {
		    this.setIsUserAReviewer(true);
		}
		if (tmpRole.getName().equalsIgnoreCase(Global.strDatasetEditorRole)) {
		    this.setIsUserAnEditor(true);
		}
		if (tmpRole.getName().equalsIgnoreCase(Global.strAdministrator)) {
		    isUserAnAdministrator = true;
		}
	    }
	} catch (PortalException ex) {
	    Logger.getLogger(UserBean.class.getName()).log(Level.SEVERE, null, ex);
	} catch (SystemException ex) {
	    Logger.getLogger(UserBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    @PostConstruct
    public void init() {
	setSolrURL(getApplicationBean().getSolrURL());
	setFedoraURL(getApplicationBean().getFedoraURL());
	setFedoraUsername(getApplicationBean().getFedoraUsername());
	setFedoraPassword(getApplicationBean().getFedoraPassword());
	setPremisTemplateFile(getApplicationBean().getPremisTemplateFile());
	setFoxmlTemplateFile(getApplicationBean().getFoxmlTemplateFile());
	setKeywordTemplateFile(getApplicationBean().getKeywordTemplateFile());
	setServerURL(getApplicationBean().getServerURL());
	setEditMetadataLink(getApplicationBean().getServerURL() + "/web/guest/edit-data/-/dataset-edit/");
	setProfileLink(getApplicationBean().getServerURL() + "/web/");
    }

    private User getUser() {
	return ((ThemeDisplay) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(WebKeys.THEME_DISPLAY)).getUser();
    }

    private void calculateTotalPages() {

	switch (this.currentTab) {
	    case (Global.ALLDATASETS): {
		this.totalPages = ((int) (Math.ceil((double) numOfDatasetResults / Global.iNumOfResultsPerPage)));
		break;
	    }
	    case (Global.RED): {
		this.totalPages = ((int) (Math.ceil((double) numOfRedResults / Global.iNumOfResultsPerPage)));
		break;
	    }
	    case (Global.ORANGE): {
		this.totalPages = ((int) (Math.ceil((double) numOfOrangeResults / Global.iNumOfResultsPerPage)));
		break;
	    }
	    case (Global.GREEN): {
		this.totalPages = ((int) (Math.ceil((double) numOfGreenResults / Global.iNumOfResultsPerPage)));
		break;
	    }
	    case (Global.NEEDREVIEWER): {
		this.totalPages = ((int) (Math.ceil((double) numOfRedOrangeResults / Global.iNumOfResultsPerPage)));
		break;
	    }
	    case (Global.RECOMMENDEDFORDOI): {
		this.totalPages = ((int) (Math.ceil((double) numOfRecommendedforDOIResults / Global.iNumOfResultsPerPage)));
		break;
	    }
	}
    }

    public String doSearch() {
	this.currentTab = 0;
	this.currentPage = 1;

	return doReload(true);
    }

    public String doReload() {
	return doReload(false);
    }

    public String doReload(Boolean updateTotals) {
	String strDoctypeQuery = "";
	String strRolesQuery = "";
	String strReviewerQuery = "";
	String strQueryText = "";
	//create empty results with some info in case something goes wrong
	Red blank = new Red();
	blank.setTitle("No results found or error occurred");

	ArrayList<BasicResultDoc> tmpResults = new ArrayList<BasicResultDoc>();
	tmpResults.add(blank);

	try {
	    strDoctypeQuery = "(doctype:dataset) ";

	    try {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext externalContext = fc.getExternalContext();
		if (externalContext.getUserPrincipal() == null) {
		    System.out.println("current principal is null");
		    strRolesQuery = "AND (1=0)";
		} else {
		    // TODO: need to check for editor role?
		    strRolesQuery = "";
		}

	    } catch (Exception e) {
		System.out.println(e);
	    }
	    switch (this.currentTab) {
		case Global.RED: {
		    strRolesQuery += "";
		    strQueryText += " AND (mods.datasetCategory:red OR mods.datasetCategory:redorange) ";
		    break;
		}
		case Global.ORANGE: {
		    strQueryText += " AND (mods.datasetCategory:orange) ";
		    break;
		}
		case Global.GREEN: {
		    strQueryText += " AND (mods.datasetCategory:green) ";
		    break;
		}
		default: {
		    strRolesQuery += "";
		    strQueryText += "";
		}
	    }
	    //System.out.println("solr url:" + getApplicationBean().getSolrUrl());
	    System.out.println("doctype query:" + strDoctypeQuery);
	    System.out.println("roles query:" + strRolesQuery);
	    System.out.println("start:" + (this.currentPage - 1)
		    * Global.iNumOfResultsPerPage);
	    System.out.println("rows:" + Global.iNumOfResultsPerPage);
	    this.requestUrl = getSolrURL() + "/select/?q=(" + strDoctypeQuery
		    + strRolesQuery + strQueryText + ")&version=2.2&start="
		    + (this.currentPage - 1) * Global.iNumOfResultsPerPage
		    + "&rows=" + Global.iNumOfResultsPerPage
		    + "&indent=on&facet=on&facet.field=doctype";
	    System.out.println("Request Url: " + this.requestUrl);
	    ProcessResults pr = new ProcessResults(requestUrl);

	    pr.process();

	    tmpResults = pr.getResults();
	    if (updateTotals) {
		numOfDatasetResults = pr.getINumOfDatasetResults();
		numOfRedResults = pr.getINumOfRedResults();
		numOfRedOrangeResults = pr.getINumOfRedOrangeResults();
		numOfOrangeResults = pr.getINumOfOrangeResults();
		numOfGreenResults = pr.getINumOfGreenResults();
		numOfReviewableResults = pr.getINumOfRedOrangeResults();
		numOfRecommendedforDOIResults = pr.getINumOfRecommendedForDOIResults();
	    }

	} catch (Exception e) {
	    numOfDatasetResults = 0;
	    System.out.println("Exception: " + e.getMessage());
	}
	this.setSearchResults(tmpResults);

	//Section to populate the reviewable results
	//http://10.0.10.204:8080/solr/core_all/select/?q=((doctype:dataset)
	//AND (mods.reviewer.userid:(10785)))&version=2.2&start=0&rows=20&indent=on&facet=on&facet.field=doctype
	// the above search is the one required for the "reviewable datasets" tab

	// create empty results with some info in case something goes wrong
	blank = new Red();
	blank.setTitle("No results found or error occurred");

	ArrayList<BasicResultDoc> tmpReviewableResults = new ArrayList<BasicResultDoc>();
	tmpReviewableResults.add(blank);

	try {
	    strDoctypeQuery = "(doctype:dataset) ";
	    try {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext externalContext = fc.getExternalContext();
		if (externalContext.getUserPrincipal() == null) {
		    System.out.println("current principal is null");
		    strReviewerQuery = "AND (1=0)";
		} else {
		    strReviewerQuery = "AND (mods.reviewer.userid:";
		    String strReviewerUserId = externalContext.getUserPrincipal().getName();
		    System.out.println("UserId: " + strReviewerUserId);
		    strReviewerQuery += strReviewerUserId + ")";
		    System.out.println("Reviewer Query: " + strReviewerQuery);
		}
	    } catch (Exception e) {
		System.out.println(e);
	    }

	    // System.out.println("solr url:" +
	    // getApplicationBean().getSolrUrl());
	    System.out.println("doctype query:" + strDoctypeQuery);
	    System.out.println("reviewer query:" + strReviewerQuery);
	    System.out.println("start:" + (this.currentPage - 1)
		    * Global.iNumOfResultsPerPage);
	    System.out.println("rows:" + Global.iNumOfResultsPerPage);
	    this.requestUrl = getSolrURL() + "/select/?q=(" + strDoctypeQuery
		    //		    + strReviewerQuery
		    + strQueryText + ")&version=2.2&start="
		    + (this.currentPage - 1) * Global.iNumOfResultsPerPage
		    + "&rows=50" //+ Global.iNumOfResultsPerPage
		    + "&indent=on&facet=on&facet.field=doctype";
	    System.out.println("Request Url: " + this.requestUrl);
	    ProcessResults pr = new ProcessResults(requestUrl);

	    pr.process();

	    tmpReviewableResults = pr.getResults();
	    if (updateTotals) {
		numOfReviewableResults = pr.getINumOfDatasetResults();
	    }

	} catch (Exception e) {
	    numOfDatasetResults = 0;
	    System.out.println("Exception: " + e.getMessage());
	}
	this.setReviewableSearchResults(tmpReviewableResults);
	switch (this.currentTab) {
	    case (Global.ALLDATASETS):
	    case (Global.RED):
	    case (Global.ORANGE):
	    case (Global.GREEN): {
		this.populatePageIndex(tmpResults.size()); //so 0 results can be handled
		break;
	    }
	    case (Global.NEEDREVIEWER): {
		this.populatePageIndex(tmpReviewableResults.size()); // so 0 results can be handled
		break;
	    }
	}
	this.calculateTotalPages();

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
	int iNumOfTabResults = 0;
	switch (this.currentTab) {
	    case (Global.ALLDATASETS): {
		iNumOfTabResults = getNumOfDatasetResults();
		break;
	    }
	    case (Global.RED): {
		iNumOfTabResults = getNumOfRedResults();
		break;
	    }
	    case (Global.ORANGE): {
		iNumOfTabResults = getNumOfOrangeResults();
		break;
	    }
	    case (Global.GREEN): {
		iNumOfTabResults = getNumOfGreenResults();
		break;
	    }
	    case (Global.NEEDREVIEWER): {
		iNumOfTabResults = getNumOfRedOrangeResults();
		break;
	    }
	    case (Global.RECOMMENDEDFORDOI): {
		iNumOfTabResults = getNumOfRecommendedforDOIResults();
		break;
	    }

	}

	return iNumOfTabResults;
    }

    public ArrayList<BasicResultDoc> getSearchResults() {
	return searchResults;
    }

    public void setSearchResults(ArrayList<BasicResultDoc> results) {
	this.searchResults = results;
    }

    public ArrayList<BasicResultDoc> getReviewableSearchResults() {
	return reviewableSearchResults;
    }

    public void setReviewableSearchResults(ArrayList<BasicResultDoc> results) {
	this.reviewableSearchResults = results;
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

    public String getSolrURL() {
	System.out.println("Getting Solr URL: " + this.solrURL);
	return this.solrURL;
    }

    public void setSolrURL(String solrURL) {
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

    public int getNumOfRedResults() {
	return numOfRedResults;
    }

    public void setNumOfRedResults(int numOfRedResults) {
	this.numOfRedResults = numOfRedResults;
    }

    public int getNumOfRedOrangeResults() {
	return numOfRedOrangeResults + numOfRedAndRedOrangeResults;
    }

    public void setNumOfRedOrangeResults(int numOfRedOrangeResults) {
	this.numOfRedOrangeResults = numOfRedOrangeResults;
    }

    public int getNumOfOrangeResults() {
	return numOfOrangeResults;
    }

    public void setNumOfOrangeResults(int numOfOrangeResults) {
	this.numOfOrangeResults = numOfOrangeResults;
    }

    public int getNumOfGreenResults() {
	return numOfGreenResults;
    }

    public void setNumOfGreenResults(int numOfGreenResults) {
	this.numOfGreenResults = numOfGreenResults;
    }

    public int getNumOfReviewableResults() {
	return numOfReviewableResults;
    }

    public void setNumOfReviewableResults(int numOfReviewableResults) {
	this.numOfReviewableResults = numOfReviewableResults;
    }

    public int getCurrentTab() {
	return currentTab;
    }

    public void setCurrentTab(int currentTab) {
	this.currentTab = currentTab;
    }

    public void setImagesLink(String imagesLink) {
	this.imagesLink = imagesLink;
    }

    public String getImagesLink() {
	return imagesLink;
    }

    public void setProfileLink(String profileLink) {
	this.profileLink = profileLink;
    }

    public String getProfileLink() {
	return profileLink;
    }

    public void setWorkingPid(String workingPid) {
	this.workingPid = workingPid;
    }

    public String getWorkingPid() {
	return workingPid;
    }

    public boolean isIsUserAReviewer() {
	return isUserAReviewer;
    }

    public void setIsUserAReviewer(boolean isUserAReviewer) {
	this.isUserAReviewer = isUserAReviewer;
    }

    public boolean isIsUserAnEditor() {
	return isUserAnEditor;
    }

    public void setIsUserAnEditor(boolean isUserAnEditor) {
	this.isUserAnEditor = isUserAnEditor;
    }

    public boolean isIsUserAnAdministrator() {
	return isUserAnAdministrator;
    }

    public void setIsUserAnAdministrator(boolean isUserAnAdministrator) {
	this.isUserAnAdministrator = isUserAnAdministrator;
    }

    public int getNumOfRedAndRedOrangeResults() {
	return numOfRedAndRedOrangeResults;
    }

    public void setNumOfRedAndRedOrangeResults(int numOfRedAndRedOrangeResults) {
	this.numOfRedAndRedOrangeResults = numOfRedAndRedOrangeResults;
    }

    public int getNumOfRecommendedforDOIResults() {
	return numOfRecommendedforDOIResults;
    }

    public void setNumOfRecommendedforDOIResults(int numOfRecommendedforDOIResults) {
	this.numOfRecommendedforDOIResults = numOfRecommendedforDOIResults;
    }

    public void setListReviewersNames(List listReviewersNames) {
	this.listReviewersNames = listReviewersNames;
    }

    public List getListReviewersNames() {
	return listReviewersNames;
    }

    public String getAssignedReviewer() {
	return assignedReviewer;
    }

    public void setAssignedReviewer(String assignedReviewer) {
	this.assignedReviewer = assignedReviewer;
    }

    public String getSelectedReviewer() {
	return selectedReviewer;
    }

    public void setSelectedReviewer(String selectedReviewer) {
	this.selectedReviewer = selectedReviewer;
    }

    public String getFedoraPassword() {
	return fedoraPassword;
    }

    public void setFedoraPassword(String fedoraPassword) {
	this.fedoraPassword = fedoraPassword;
    }

    public String getFedoraURL() {
	return fedoraURL;
    }

    public void setFedoraURL(String fedoraURL) {
	this.fedoraURL = fedoraURL;
    }

    public String getFedoraUsername() {
	return fedoraUsername;
    }

    public void setFedoraUsername(String fedoraUsername) {
	this.fedoraUsername = fedoraUsername;
    }

    public String getKeywordTemplateFile() {
	return keywordTemplateFile;
    }

    public void setKeywordTemplateFile(String keywordTemplateFile) {
	this.keywordTemplateFile = keywordTemplateFile;
    }

    public String getFoxmlTemplateFile() {
	return foxmlTemplateFile;
    }

    public void setFoxmlTemplateFile(String foxmlTemplateFile) {
	this.foxmlTemplateFile = foxmlTemplateFile;
    }

    public String getPremisTemplateFile() {
	return premisTemplateFile;
    }

    public void setPremisTemplateFile(String premisTemplateFile) {
	this.premisTemplateFile = premisTemplateFile;
    }

    public String getServerURL() {
	return serverURL;
    }

    public void setServerURL(String serverURL) {
	this.serverURL = serverURL;
    }

    public ApplicationBean getApplicationBean() {
	return applicationBean;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
	this.applicationBean = applicationBean;
    }
}
