/**
 * Copyright (c) 2000-2010 Red, Inc. All rights reserved.
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
package org.freshwaterlife.portlets.dataset.mydatasets;

import org.freshwaterlife.portlets.dataset.resulttypes.Red;
import org.freshwaterlife.portlets.dataset.resulttypes.BasicResultDoc;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.RoleServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.util.bridges.jsf.common.JSFPortletUtil;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.freshwaterlife.fedora.client.CustomFedoraClient;
import org.freshwaterlife.fedora.client.CustomPurgeDatastream;
import org.freshwaterlife.fedora.client.CustomGetObjectXML;
import org.freshwaterlife.fedora.client.CustomGetDatastreamDissemination;
import org.freshwaterlife.fedora.client.CustomModifyDatastream;
import org.freshwaterlife.portlets.dataset.process.ProcessResults;
import org.freshwaterlife.util.XPathNodeUpdater;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.xml.sax.InputSource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.ActionRequest;
import javax.ws.rs.core.Cookie;
import org.freshwaterlife.portlets.dataset.ApplicationBean;
import org.freshwaterlife.portlets.dataset.Global;

/**
 * @author Paul Johnson
 */
public class SessionBean implements Serializable {

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
    private ArrayList<BasicResultDoc> searchResults;
    private ArrayList<BasicResultDoc> reviewableSearchResults;
    private ArrayList<Integer> pageIndex = new ArrayList<Integer>();
    private String workingPid;
    private String reviewerUserId;
    private List listEditorsNames;
    private List<User> userlistEditors = new ArrayList<User>();
    private SelectItem siEditorName;
    private String selectedReviewer;
    private static Log _log = LogFactoryUtil.getLog(SessionBean.class);

    public SessionBean() {
	this.currentTab = 0;
	this.currentPage = 1;

	String userId = null;
	long lEditorUserId = 0;

	List<Role> roles;
	Role rEditor;
	long lCompanyId = getUser().getCompanyId();

	listEditorsNames = new ArrayList();
	//populate list of editors from editor role
	siEditorName = new SelectItem("No Editors");
	listEditorsNames.add(siEditorName);
	try {
	    rEditor = RoleLocalServiceUtil.getRole(lCompanyId, Global.strDatasetEditorRole);
	    userlistEditors = UserLocalServiceUtil.getRoleUsers(rEditor.getRoleId());
	    if (!userlistEditors.isEmpty()) {
		listEditorsNames.clear();
		for (User user : userlistEditors) {
		    lEditorUserId = user.getUserId();

		    siEditorName = new SelectItem(lEditorUserId, user.getFullName());

		    listEditorsNames.add(this.siEditorName);
		}
	    }
	} catch (PortalException e) {
	    e.printStackTrace();
	} catch (SystemException e) {
	    e.printStackTrace();
	}


	setIsUserAReviewer(false);
	setIsUserAnEditor(false);
	try {
	    userId = getUser().getLogin();
	    roles = RoleServiceUtil.getUserRoles(getUser().getUserId());

	    for (Role tmpRole : roles) {
		if (tmpRole.getName().equalsIgnoreCase(Global.strDatasetReviewerRole)) {
		    setIsUserAReviewer(true);
		}
		if (tmpRole.getName().equalsIgnoreCase(Global.strDatasetEditorRole)) {
		    setIsUserAnEditor(true);
		}
	    }
	} catch (PortalException ex) {
	    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
	} catch (SystemException ex) {
	    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
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
	    case (Global.ALLMYDATASETS): {
		this.totalPages = ((int) (Math.ceil((double) numOfDatasetResults / Global.iNumOfResultsPerPage)));
		break;
	    }
	    case (Global.RED): {
		this.totalPages = ((int) (Math.ceil(((double) numOfRedResults + (double) numOfRedOrangeResults)
			/ Global.iNumOfResultsPerPage)));
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
	    case (Global.REVIEWABLE): {
		this.totalPages = ((int) (Math.ceil((double) numOfReviewableResults / Global.iNumOfResultsPerPage)));
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

	// create empty results with some info in case something goes wrong
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
		    strRolesQuery = "AND (roleIds:(";
		    String sUserId = externalContext.getUserPrincipal().getName();
		    sUserId = "u" + sUserId;
		    System.out.println("UserId: " + sUserId);
		    strRolesQuery += sUserId + "))";
		    System.out.println("Roles Query: " + strRolesQuery);
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
	    System.out.println("doctype query:" + strDoctypeQuery);
	    System.out.println("roles query:" + strRolesQuery);
	    System.out.println("start:" + (this.currentPage - 1)
		    * Global.iNumOfResultsPerPage);
	    System.out.println("rows:" + Global.iNumOfResultsPerPage);
	    this.requestUrl = getSolrURL() + "/select/?q=(" + strDoctypeQuery
		    + strRolesQuery + strQueryText + ")&version=2.2&start="
		    + (this.currentPage - 1) * Global.iNumOfResultsPerPage
		    + "&rows=50" //+ Global.iNumOfResultsPerPage
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
		    + strReviewerQuery + strQueryText + ")&version=2.2&start="
		    + (this.currentPage - 1) * Global.iNumOfResultsPerPage
		    + "&rows=50" //SFOX + Global.iNumOfResultsPerPage
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
	    case (Global.ALLMYDATASETS):
	    case (Global.RED):
	    case (Global.ORANGE):
	    case (Global.GREEN): {
		this.populatePageIndex(tmpResults.size()); // so 0 results can be handled
		break;
	    }
	    case (Global.REVIEWABLE): {
		this.populatePageIndex(tmpReviewableResults.size()); // so 0 results can be handled
		break;
	    }
	}

	this.calculateTotalPages();

	return "submit"; // trigger the form in the view.xhtml file
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

    public String doPromote() {

	System.out.println("doPromote - start");

	// PREMIS event metadata
	// get PID and modify descMetadata
	String datasetPID = this.getWorkingPid();
	String new_category = "";

	try {
	    String category = getCurrentCategory();

	    System.out.println("doPromote category: " + category);

	    if (category != null) {
		if (category.equals("red")) {
		    // red -> orange
		    new_category = "redorange";
		}
		changeDatasetCategory(new_category);

		// Add a 'submit for review' PREMIS event
		doWritePremisRecord(datasetPID,
			"http://www.fba.org.uk/vocabulary/preservationEvents/submit4Review",
			"Submission of dataset to review process",
			"success",
			"Successfully submitted dataset to review process");
	    }
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    System.out.println("doPromote Error: " + e.getMessage());
	}

	System.out.println("doPromote Object: " + datasetPID + " is now in the " + new_category);

	return "agree";
    }

    public String doDemoteToRed() {
	// Move dataset to red category, set mods.reviewer.name to 'noreviewer', set mods.reviewer.userid to 0
	System.out.println("doDemoteToRed - start");

	// get PID and modify descMetadata
	String datasetPID = this.getWorkingPid();
	String new_category = "";
	try {
	    String category = getCurrentCategory();

	    System.out.println("doDemoteToRed category: " + category);

	    if (category != null) {
		if (category.equals("redorange")) {
		    // red -> orange
		    new_category = "red";
		}
		changeDatasetCategory(new_category);

		// Remove Reviewer name and user id by resetting to default values
		setReviewer(Global.NOREVIEWER);
		setRevUserid("0");

		// Add a PREMIS event

		doWritePremisRecord(datasetPID,
			"http://www.fba.org.uk/vocabulary/preservationEvents/submit4Review",
			"Demotion of dataset to red",
			"success",
			"Successfully moved dataset to red category");
	    }
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    System.out.println("Error: " + e.getMessage());
	}

	System.out.println("Object: " + datasetPID + " is now in the "
		+ new_category);

	return "demoted";
    }

    public Element XPathGetNode(Document doc, String path,
	    Map<String, Namespace> ns) throws JDOMException {
	try {
	    XPath xpath = XPath.newInstance(path);

	    // Add Namespaces
	    Namespace n;
	    Collection<Namespace> c = ns.values();

	    Iterator<Namespace> it = c.iterator();
	    while (it.hasNext()) {
		n = (Namespace) it.next();
		xpath.addNamespace(n);
	    }

	    return (Element) xpath.selectSingleNode(doc);
	} catch (Exception e) {
	    System.out.println("Error XPath: " + e.getMessage());
	    return null;
	}
    }

    private Map<String, Namespace> _buildNamespaceList() {

	Map<String, Namespace> nsList = new HashMap<String, Namespace>();

	nsList.put("mods",
		Namespace.getNamespace("mods", "http://www.loc.gov/mods/v3"));
	nsList.put("premis", Namespace.getNamespace("premis",
		"http://www.loc.gov/standards/premis"));

	return nsList;
    }

    // *** Getters and Setters ***
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
	    case (Global.ALLMYDATASETS): {
		iNumOfTabResults = getNumOfDatasetResults();
		break;
	    }
	    case (Global.RED): {
		iNumOfTabResults = getNumOfRedResults() + getNumOfRedOrangeResults();
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
	    case (Global.REVIEWABLE): {
		iNumOfTabResults = getNumOfReviewableResults();
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

    public String getReviewerUserId() {
	return reviewerUserId;
    }

    public void setReviewerUserId(String reviewerUserId) {
	this.reviewerUserId = reviewerUserId;
    }

    public void setListEditorsNames(List listEditorsNames) {
	this.listEditorsNames = listEditorsNames;
    }

    public List getListEditorsNames() {
	return listEditorsNames;
    }

    public String getSelectedReviewer() {
	return selectedReviewer;
    }

    public void setSelectedReviewer(String selectedReviewer) {
	this.selectedReviewer = selectedReviewer;
    }

    public void changeDatasetCategory(String new_category) {
	FedoraCredentials credentials;
	CustomFedoraClient fedora = null;
	FedoraResponse fResponse = null;


	XMLOutputter outputter = new XMLOutputter();
	String sContent = "";
	Map<String, Namespace> nsList = _buildNamespaceList();
	String datasetPID = this.getWorkingPid();

	try {
	    credentials = new FedoraCredentials(new URL(getFedoraURL()),
		    getFedoraUsername(),
		    getFedoraPassword());

	    fedora = new CustomFedoraClient(credentials);
	    List<Cookie> lCookies = getCookies();
	    fResponse = (new CustomGetDatastreamDissemination(datasetPID, "descMetadata")).execute(fedora, lCookies);
	    if (fResponse.getStatus() == 200) {
		sContent = fResponse.getEntity(String.class);

		org.jdom.input.SAXBuilder jdomParser = new org.jdom.input.SAXBuilder();
		Document doc = jdomParser.build(new InputSource(new ByteArrayInputStream(sContent.getBytes("utf-8"))));
		XPathGetNode(doc, Global.datasetCategoryPath, nsList).setText(new_category);
		ByteArrayOutputStream descMetadata = new ByteArrayOutputStream();

		outputter.output(doc, descMetadata);
		((CustomModifyDatastream) (new CustomModifyDatastream(datasetPID, "descMetadata")).content(descMetadata.toString())).execute(fedora, lCookies);
	    } else {
		System.out.println("setCategory FResponse Error");
	    }
	} catch (MalformedURLException ex) {
	    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
	} catch (JDOMException ex) {
	    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
	} catch (IOException ex) {
	    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
	} catch (FedoraClientException ex) {
	    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public String getCurrentCategory() {
	FedoraCredentials credentials;
	CustomFedoraClient fedora = null;
	FedoraResponse fResponse = null;

	String datasetPID = this.getWorkingPid();

	String category = null;
	Map<String, Namespace> nsList = _buildNamespaceList();
	String sContent = "";
	List<Cookie> lCookies = getCookies();
	try {
	    credentials = new FedoraCredentials(new URL(getFedoraURL()),
		    getFedoraUsername(),
		    getFedoraPassword());

	    fedora = new CustomFedoraClient(credentials);

	    fResponse = (new CustomGetDatastreamDissemination(datasetPID, "descMetadata")).execute(fedora, lCookies);
	    if (fResponse.getStatus() == 200) {
		sContent = fResponse.getEntity(String.class);

		System.out.println("getCurrentCategory - start parsing descMetadata");

		org.jdom.input.SAXBuilder jdomParser = new org.jdom.input.SAXBuilder();
		try {
		    Document doc = jdomParser.build(new InputSource(new ByteArrayInputStream(sContent.getBytes("utf-8"))));
		    category = XPathGetNode(doc, Global.datasetCategoryPath, nsList).getText();
		} catch (JDOMException ex) {
		    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
		    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
		}
	    } else {
		System.out.println("getCurrentCategory FResponse Error");
	    }
	} catch (MalformedURLException ex) {
	    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
	} catch (FedoraClientException ex) {
	    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
	}

	return category;
    }

    public String getCurrentReviewer() {
	FedoraCredentials credentials;
	CustomFedoraClient fedora = null;
	FedoraResponse fResponse = null;

	String datasetPID = this.getWorkingPid();

	String reviewer = null;
	String datasetReviewerPath = "/mods:mods/mods:name/mods:namePart";

	Map<String, Namespace> nsList = _buildNamespaceList();
	String sContent = "";
	List<Cookie> lCookies = getCookies();
	try {
	    credentials = new FedoraCredentials(new URL(getFedoraURL()),
		    getFedoraUsername(),
		    getFedoraPassword());

	    fedora = new CustomFedoraClient(credentials);

	    fResponse = (new CustomGetDatastreamDissemination(datasetPID, "descMetadata")).execute(fedora, lCookies);
	    if (fResponse.getStatus() == 200) {
		sContent = fResponse.getEntity(String.class);

		System.out.println("getCurrentOutcome - start parsing descMetadata");

		org.jdom.input.SAXBuilder jdomParser = new org.jdom.input.SAXBuilder();
		try {
		    Document doc = jdomParser.build(new InputSource(new ByteArrayInputStream(sContent.getBytes("utf-8"))));
		    reviewer = XPathGetNode(doc, datasetReviewerPath, nsList).getText();
		} catch (JDOMException ex) {
		    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
		    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
		}
	    } else {
		System.out.println("getCurrentOutcome FResponse Error");
	    }
	} catch (MalformedURLException ex) {
	    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
	} catch (FedoraClientException ex) {
	    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
	}

	return reviewer;
    }

    public void setReviewer(String new_reviewer) {
	FedoraCredentials credentials;
	CustomFedoraClient fedora = null;
	FedoraResponse fResponse = null;


	XMLOutputter outputter = new XMLOutputter();
	String sContent = "";
	Map<String, Namespace> nsList = _buildNamespaceList();
	String datasetCategoryPath = "/mods:mods/mods:name/mods:namePart";
	String datasetPID = this.getWorkingPid();

	try {
	    credentials = new FedoraCredentials(new URL(getFedoraURL()),
		    getFedoraUsername(),
		    getFedoraPassword());

	    fedora = new CustomFedoraClient(credentials);
	    List<Cookie> lCookies = getCookies();
	    fResponse = (new CustomGetDatastreamDissemination(datasetPID, "descMetadata")).execute(fedora, lCookies);
	    if (fResponse.getStatus() == 200) {
		sContent = fResponse.getEntity(String.class);

		org.jdom.input.SAXBuilder jdomParser = new org.jdom.input.SAXBuilder();
		Document doc = jdomParser.build(new InputSource(new ByteArrayInputStream(sContent.getBytes("utf-8"))));
		XPathGetNode(doc, datasetCategoryPath, nsList).setText(new_reviewer);
		ByteArrayOutputStream descMetadata = new ByteArrayOutputStream();

		outputter.output(doc, descMetadata);
		((CustomModifyDatastream) (new CustomModifyDatastream(datasetPID, "descMetadata")).content(descMetadata.toString())).execute(fedora, lCookies);
	    } else {
		System.out.println("setOutcome FResponse Error");
	    }
	} catch (MalformedURLException ex) {
	    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
	} catch (JDOMException ex) {
	    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
	} catch (IOException ex) {
	    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
	} catch (FedoraClientException ex) {
	    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public String getCurrentRevUserid() {
	FedoraCredentials credentials;
	CustomFedoraClient fedora = null;
	FedoraResponse fResponse = null;

	String datasetPID = this.getWorkingPid();

	String revUserid = null;
	String datasetReviewerPath = "/mods:mods/mods:name/mods:description";

	Map<String, Namespace> nsList = _buildNamespaceList();
	String sContent = "";
	List<Cookie> lCookies = getCookies();
	try {
	    credentials = new FedoraCredentials(new URL(getFedoraURL()),
		    getFedoraUsername(),
		    getFedoraPassword());

	    fedora = new CustomFedoraClient(credentials);

	    fResponse = (new CustomGetDatastreamDissemination(datasetPID, "descMetadata")).execute(fedora, lCookies);
	    if (fResponse.getStatus() == 200) {
		sContent = fResponse.getEntity(String.class);

		System.out.println("getCurrentOutcome - start parsing descMetadata");

		org.jdom.input.SAXBuilder jdomParser = new org.jdom.input.SAXBuilder();
		try {
		    Document doc = jdomParser.build(new InputSource(new ByteArrayInputStream(sContent.getBytes("utf-8"))));
		    revUserid = XPathGetNode(doc, datasetReviewerPath, nsList).getText();
		} catch (JDOMException ex) {
		    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
		    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
		}
	    } else {
		System.out.println("getCurrentOutcome FResponse Error");
	    }
	} catch (MalformedURLException ex) {
	    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
	} catch (FedoraClientException ex) {
	    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
	}

	return revUserid;
    }

    public void setRevUserid(String new_revUserid) {
	FedoraCredentials credentials;
	CustomFedoraClient fedora = null;
	FedoraResponse fResponse = null;


	XMLOutputter outputter = new XMLOutputter();
	String sContent = "";
	Map<String, Namespace> nsList = _buildNamespaceList();
	String datasetCategoryPath = "/mods:mods/mods:name/mods:description";
	String datasetPID = this.getWorkingPid();

	try {
	    credentials = new FedoraCredentials(new URL(getFedoraURL()),
		    getFedoraUsername(),
		    getFedoraPassword());

	    fedora = new CustomFedoraClient(credentials);
	    List<Cookie> lCookies = getCookies();
	    fResponse = (new CustomGetDatastreamDissemination(datasetPID, "descMetadata")).execute(fedora, lCookies);
	    if (fResponse.getStatus() == 200) {
		sContent = fResponse.getEntity(String.class);

		org.jdom.input.SAXBuilder jdomParser = new org.jdom.input.SAXBuilder();
		Document doc = jdomParser.build(new InputSource(new ByteArrayInputStream(sContent.getBytes("utf-8"))));
		XPathGetNode(doc, datasetCategoryPath, nsList).setText(new_revUserid);
		ByteArrayOutputStream descMetadata = new ByteArrayOutputStream();

		outputter.output(doc, descMetadata);
		((CustomModifyDatastream) (new CustomModifyDatastream(datasetPID, "descMetadata")).content(descMetadata.toString())).execute(fedora, lCookies);
	    } else {
		System.out.println("setOutcome FResponse Error");
	    }
	} catch (MalformedURLException ex) {
	    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
	} catch (JDOMException ex) {
	    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
	} catch (IOException ex) {
	    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
	} catch (FedoraClientException ex) {
	    Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void doWritePremisRecord(String strPid, String strEventType, String strEventDetail, String strEventOutcome, String strEventOutcomeDetail) {
	Map<String, Namespace> nsList = _buildNamespaceList();
	String PREMISEventPath = "foxml:digitalObject/foxml:datastream[@ID='PREMIS']/foxml:datastreamVersion[last()]/foxml:xmlContent/premis:premis";
	String PREMISDatastreamVersionPath = "foxml:digitalObject/foxml:datastream[@ID='PREMIS']/foxml:datastreamVersion[last()]";

	// PREMIS event metadata
	String PREMISPath = "premis:premis/premis:event";
	String eventIdentifierPath = PREMISPath + "/premis:eventIdentifier";
	String eventTypePath = PREMISPath + "/premis:eventType";
	String eventDateTimePath = PREMISPath + "/premis:eventDateTime";
	String eventDetailPath = PREMISPath + "/premis:eventDetail";

	String eventOutcomePath = PREMISPath + "/premis:eventOutcomeInformation/premis:eventOutcome";
	String eventOutcomeDetailPath = PREMISPath + "/premis:eventOutcomeInformation/premis:eventOutcomeDetail";

	String agentPath = PREMISPath + "/premis:linkingAgentIdentifier[premis:linkingAgentIdentifierType/text() = 'Agent']/premis:linkingAgentIdentifierValue";
	String fullNamePath = PREMISPath + "/premis:linkingAgentIdentifier[premis:linkingAgentIdentifierType/text() = 'Full Name']/premis:linkingAgentIdentifierValue";

	String linkingObjectIdentifierPath = PREMISPath + "/premis:linkingObjectIdentifier";

	XMLOutputter outputter = new XMLOutputter();


	System.out.println("doWritePremisRecord - start");

	FacesContext facesContext = FacesContext.getCurrentInstance();

	// JSFPortletUtil - Liferay provided class from util-bridges.jar
	ActionRequest request = (ActionRequest) JSFPortletUtil.getPortletRequest(facesContext);

	// load template file
	FileInputStream fIn = null;
	try {
	    fIn = new FileInputStream(getFoxmlTemplateFile());
	} catch (FileNotFoundException e) {
	    System.out.println("Template file not found");
	}

	FedoraCredentials credentials;
	CustomFedoraClient fc = null;
	try {
	    credentials = new FedoraCredentials(new URL(getFedoraURL()),
		    getFedoraUsername(),
		    getFedoraPassword());

	    fc = new CustomFedoraClient(credentials);
	} catch (MalformedURLException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	}

	BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));
	org.jdom.input.SAXBuilder jdomBuilder = new org.jdom.input.SAXBuilder();
	Document doc;

	try {
	    // Build FOXML if creating/modifying metadata
	    doc = jdomBuilder.build(reader);

	    List<Cookie> lCookies = getCookies();

	    // PREMIS event

	    // PRJ - 26/04/2011
	    // Rather than storing each individual PREMIS event in a separate version of the datastream, we need to:
	    // 1. Add the new PREMIS event to the existing PREMIS data.
	    // 2. Store the modified PREMIS data in a new version of the PREMIS datastream.
	    // 3. Remove the previous version of the PREMIS datastream.

	    FedoraResponse fResponse = null;

	    String sContent = "";
	    try {
		// Retrieve the existing fedora object.
		fResponse = (new CustomGetObjectXML(strPid)).execute(fc, lCookies);

		if (fResponse.getStatus() == 200) {
		    sContent = fResponse.getEntity(String.class);

		    // parse sContent to get PREMIS data
		    org.jdom.input.SAXBuilder jdomParser = new org.jdom.input.SAXBuilder();
		    try {
			Document premisDoc = jdomParser.build((new InputSource(new ByteArrayInputStream(sContent.getBytes("utf-8")))));
			// Retrieve the existing PREMIS data.
			Element premis = XPathGetNode(premisDoc, PREMISEventPath, nsList);
			// Add the new PREMIS event to the existing PREMIS data.
			premis.addContent(XPathGetNode(doc, PREMISEventPath, nsList).detach());
			// Store the modified PREMIS data in a new version of the PREMIS datastream.
			System.out.println("logging PREMIS event");
			ByteArrayOutputStream PREMIS = new ByteArrayOutputStream();
			outputter.output(premis, PREMIS);
			try {
			    ((CustomModifyDatastream) (new CustomModifyDatastream(strPid, "PREMIS")).content(PREMIS.toString())).execute(fc, lCookies);
			    // Remove the previous version of the PREMIS datastream.
			    ((CustomPurgeDatastream) (new CustomPurgeDatastream(strPid, "PREMIS")).endDT(XPathGetNode(premisDoc, PREMISDatastreamVersionPath, nsList).getAttributeValue("CREATED"))).execute(fc, lCookies);
			} catch (FedoraClientException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
			System.out.println("after PREMIS event.");

		    } catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("PREMIS JDOM Exception" + e.getMessage());
		    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("PREMIS IO Exception" + e.getMessage());
		    }
		}

	    } catch (FedoraClientException e) {
		System.out.println("Fedora Error: " + e.getMessage());
	    }
	} catch (org.jdom.JDOMException e) {
	    System.out.println("JDOM Exception: " + e);
	} catch (IOException e) {
	    System.out.println("IO Exception: " + e);
	}

	try {

	    List<Cookie> lCookies = getCookies();

	    // load PREMIS template file
	    FileInputStream fInPremis = null;
	    try {
		fInPremis = new FileInputStream(getPremisTemplateFile());
	    } catch (FileNotFoundException e) {
		System.out.println("Template file not found");
	    }

	    BufferedReader premisReader = new BufferedReader(new InputStreamReader(fInPremis));
	    doc = jdomBuilder.build(premisReader);

	    // PREMIS metadata
	    XPathNodeUpdater.create(doc, eventIdentifierPath, nsList).setContent("PREMIS:" + UUID.randomUUID().toString());

	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.000Z'"); // e.g. 2011-04-12T14:23:44.681Z
	    XPathNodeUpdater.create(doc, eventDateTimePath, nsList).setContent(sdf.format(cal.getTime()));

	    User u = null;
	    String sUserId = "";
	    String sUserName;
	    FacesContext face = FacesContext.getCurrentInstance();
	    ExternalContext externalContext = face.getExternalContext();
	    if (externalContext.getUserPrincipal() == null) {
		_log.info("current principal is null");
	    } else {
		sUserId = externalContext.getUserPrincipal().getName();
		Long id = Long.parseLong(sUserId);
		sUserId = "u" + sUserId;
		try {
		    u = UserLocalServiceUtil.getUserById(id);
		} catch (PortalException ex) {
		    _log.fatal(ex);
		} catch (SystemException ex) {
		    _log.fatal(ex);
		}
	    }
	    sUserName = u.getFullName();

	    XPathNodeUpdater.create(doc, agentPath, nsList).setContent(sUserId);
	    XPathNodeUpdater.create(doc, fullNamePath, nsList).setContent(sUserName);

	    outputter = new XMLOutputter();
	    //ByteArrayOutputStream PREMISEvent = new ByteArrayOutputStream();

	    String pidToUse = strPid;
	    System.out.println("modifying existing object.");

	    // Add a PREMIS event
	    XPathNodeUpdater.create(doc, eventTypePath, nsList).setContent(strEventType);
	    XPathNodeUpdater.create(doc, eventDetailPath, nsList).setContent(strEventDetail);
	    XPathNodeUpdater.create(doc, eventOutcomePath, nsList).setContent(strEventOutcome);
	    XPathNodeUpdater.create(doc, eventOutcomeDetailPath, nsList).setContent(strEventOutcomeDetail);
	    XPathNodeUpdater.create(doc, linkingObjectIdentifierPath, nsList).setContent(strPid);

	    System.out.println("logging PREMIS event");

	    FedoraResponse fResponse = null;

	    String sContent = "";
	    // Retrieve the existing fedora object.
	    fResponse = (new CustomGetObjectXML(strPid)).execute(fc, lCookies);

	    if (fResponse.getStatus() == 200) {
		sContent = fResponse.getEntity(String.class);

		// parse sContent to get PREMIS data
		org.jdom.input.SAXBuilder jdomParser = new org.jdom.input.SAXBuilder();
		try {
		    Document premisDoc = jdomParser.build((new InputSource(new ByteArrayInputStream(sContent.getBytes("utf-8")))));
		    // Retrieve the existing PREMIS data.
		    Element premis = XPathGetNode(premisDoc, PREMISEventPath, nsList);
		    // Add the new PREMIS event to the existing PREMIS data.
		    premis.addContent(XPathGetNode(doc, PREMISPath, nsList).detach());
		    // Store the modified PREMIS data in a new version of the PREMIS datastream.
		    ByteArrayOutputStream PREMIS = new ByteArrayOutputStream();
		    outputter.output(premis, PREMIS);
		    try {
			((CustomModifyDatastream) (new CustomModifyDatastream(pidToUse, "PREMIS")).content(PREMIS.toString())).execute(fc, lCookies);
			// Remove the previous version of the PREMIS datastream.
			((CustomPurgeDatastream) (new CustomPurgeDatastream(pidToUse, "PREMIS")).endDT(XPathGetNode(premisDoc, PREMISDatastreamVersionPath, nsList).getAttributeValue("CREATED"))).execute(fc, lCookies);
		    } catch (FedoraClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }

		} catch (JDOMException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    System.out.println("PREMIS1 JDOM Exception" + e.getMessage());
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    System.out.println("PREMIS1 IO Exception" + e.getMessage());
		}
	    }

	} catch (IOException ex) {
	    System.out.println("IO Error: " + ex);
	} catch (Exception ex) {
	    System.out.println("Error: " + ex);
	}
	System.out.println("doWritePremisRecord - end");

    }

    private List<Cookie> getCookies() {
	FacesContext facesContext = FacesContext.getCurrentInstance();

	// JSFPortletUtil - Liferay provided class from util-bridges.jar
	ActionRequest request = (ActionRequest) JSFPortletUtil.getPortletRequest(facesContext);
	javax.servlet.http.Cookie[] cookies = request.getCookies();
	List<Cookie> lCookies = new ArrayList<Cookie>();


	for (javax.servlet.http.Cookie c : cookies) {
	    lCookies.add(new Cookie(c.getName(), c.getValue(), c.getPath(), c.getDomain(), c.getVersion()));


	}

	return lCookies;



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
