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
package org.freshwaterlife.portlets.search;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Role;
import java.io.Serializable;
import org.freshwaterlife.portlets.search.process.ProcessResults;
import org.freshwaterlife.portlets.search.resulttypes.BasicResultDoc;
import org.freshwaterlife.portlets.search.resulttypes.Image;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.theme.ThemeDisplay;
import java.util.ArrayList;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import java.util.Iterator;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;

/**
 * @author Kearon McNicol
 */
public class UserBean implements Serializable {

    private ApplicationBean applicationBean;
    private String qValue;
    private String requestUrl;
    private int numOfLiferayResults;
    private int numOfImageResults;
    private int numOfImageArchiveResults;
    private int numOfLibcatResults;
    private int numOfDatasetResults;
    private int numOfAllResults;
    private int currentTab;
    private int currentPage;
    private int totalPages;
    private ArrayList<BasicResultDoc> searchResults;
    private ArrayList<Integer> pageIndex = new ArrayList<Integer>();

    public UserBean() {
	this.qValue = Global.strSearchBoxText;
	this.currentTab = 0;
	this.currentPage = 1;
    }

    private void calculateTotalPages() {

	switch (this.currentTab) {
	    case (0): {
		this.totalPages = ((int) (Math.ceil((double) numOfAllResults / Global.iNumOfResultsPerPage)));
		break;
	    }
	    case (1): {
		this.totalPages = ((int) (Math.ceil((double) numOfLiferayResults / Global.iNumOfResultsPerPage)));
		break;
	    }
	    case (2): {
		this.totalPages = ((int) (Math.ceil((double) numOfImageResults / Global.iNumOfResultsPerPage)));
		break;
	    }
	    case (3): {
		this.totalPages = ((int) (Math.ceil((double) numOfLibcatResults / Global.iNumOfResultsPerPage)));
		break;
	    }
	    case (4): {
		this.totalPages = ((int) (Math.ceil((double) numOfDatasetResults / Global.iNumOfResultsPerPage)));
		break;
	    }
	    case (5): {
		this.totalPages = ((int) (Math.ceil((double) numOfImageArchiveResults / Global.iNumOfResultsPerPage)));
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
	if (this.qValue != null && this.qValue.length() > 0) {
	    //create empty results with some info in case something goes wrong
	    Image blank = new Image();
	    blank.setTitle("No results found or error occurred");

	    ArrayList<BasicResultDoc> tmpResults = new ArrayList<BasicResultDoc>();
	    tmpResults.add(blank);

	    try {
		String strDoctypeQuery = "";
		String strRolesQuery = "";
		String strQueryText = "";

		String strDocType = Global.arrCores[this.currentTab];

		if (strDocType.compareTo("") != 0) {
		    strDoctypeQuery = "(doctype:" + strDocType + ") AND ";
		}

		switch (this.currentTab) {
		    case Global.LIFERAYDOC: {
			//restrict the liferay results to what is viewable by current user
			strRolesQuery = _getLiferayRoleQuery();
			strQueryText = "(*:* -templateId:[* TO *]) AND (keywords:" + this.getqValue() + " OR assetTagNames:" + this.getqValue() + ")";
			break;
		    }
		    case Global.IMAGE: {
			strQueryText = "(keywords:" + this.getqValue() + " OR assetTagNames:" + this.getqValue() + ")";
			break;
		    }
		    case Global.IMAGEARCHIVE: {
			strQueryText = "(keywords:" + this.getqValue() + " OR assetTagNames:" + this.getqValue() + ")";
			break;
		    }
		    case Global.LIBCAT: {
			strQueryText = "(keywords:" + this.getqValue() + ")";
			break;
		    }
		    case Global.DATASET: {
			strQueryText = "(keywords:" + this.getqValue() + ")";
			break;
		    }
		    default: {
			//ensure that if items are liferay, the user has the correct role to view them.
			strRolesQuery = "NOT(NOT" + _getLiferayRoleQuery() + " (doctype:" + Global.arrCores[Global.LIFERAYDOC] + ")) AND ";
			strQueryText = "(*:* -templateId:[* TO *]) AND (keywords:" + this.getqValue() + " OR assetTagNames:" + this.getqValue() + ")";
		    }
		}

		this.requestUrl = getApplicationBean().getSolrUrl() + "/select/?q=(" + strDoctypeQuery + strRolesQuery + strQueryText + ")&version=2.2&start=" + (this.currentPage - 1) * Global.iNumOfResultsPerPage + "&rows=" + Global.iNumOfResultsPerPage + "&indent=on&facet=on&facet.field=doctype";
		ProcessResults pr = new ProcessResults(requestUrl);

		//check if search string is blank
		if (this.getqValue() != null && this.qValue.length() > 0) {
		    pr.process();
		}

		tmpResults = pr.getResults();
		if (updateTotals) {
		    numOfLiferayResults = pr.getINumOfLiferayResults();
		    numOfImageResults = pr.getINumOfImageResults();
		    numOfImageArchiveResults = pr.getINumOfImageArchiveResults();
		    numOfLibcatResults = pr.getINumOfLibcatResults();
		    numOfDatasetResults = pr.getINumOfDatasetResults();
		    numOfAllResults = pr.getINumFound();
		}

	    } catch (Exception e) {
		numOfLiferayResults = 0;
		numOfImageResults = 0;
		numOfImageArchiveResults = 0;
		numOfLibcatResults = 0;
		numOfDatasetResults = 0;
		numOfAllResults = 0;
	    }
	    this.calculateTotalPages();
	    this.setSearchResults(tmpResults);
	    this.populatePageIndex(tmpResults.size()); //so 0 results can be handled
	}

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

    private String _getLiferayRoleQuery() {
	long lUserId;
	long lDefaultUserId;
	List<Role> lrUserRoles;

	PortletRequest portletRequest;
	FacesContext context;
	ThemeDisplay themeDisplay;
	PermissionChecker permissionChecker;
	Iterator it;
	Role role;
	String strTempRolesQuery = "";

	try {
	    context = FacesContext.getCurrentInstance();
	    portletRequest = (PortletRequest) context.getExternalContext().getRequest();
	    themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
	    permissionChecker = themeDisplay.getPermissionChecker();
	    lUserId = permissionChecker.getUserId();
	    permissionChecker.getCompanyId();

	    lrUserRoles = RoleLocalServiceUtil.getUserRoles(lUserId);

	    lDefaultUserId = UserLocalServiceUtil.getDefaultUserId(permissionChecker.getCompanyId());

	    if (lUserId != lDefaultUserId) {
		List<Role> lrDefaultUserRoles;
		//Don't forget the "guest" account if user is logged in
		lrDefaultUserRoles = RoleLocalServiceUtil.getUserRoles(lDefaultUserId);

		lrUserRoles.addAll(lrDefaultUserRoles);
	    }

	    if (!lrUserRoles.isEmpty()) {
		boolean boolRolesFound = false;
		it = lrUserRoles.iterator();
		strTempRolesQuery = "(roleId:(";
		while (it.hasNext()) {
		    boolRolesFound = true;
		    role = (Role) it.next();
		    strTempRolesQuery += role.getRoleId() + " OR ";
		}
		if (boolRolesFound) {
		    //get rid of the extra " OR "
		    strTempRolesQuery = strTempRolesQuery.substring(0, strTempRolesQuery.length() - 4);
		}
		strTempRolesQuery += ")) AND ";
	    }
	} catch (Exception e) {
	    System.out.println(e);
	}

	return strTempRolesQuery;
    }

    //*** Getters and Setters ***
    public ArrayList<Integer> getPageIndex() {
	return pageIndex;
    }

    public void setPageIndex(ArrayList<Integer> pageIndex) {
	this.pageIndex = pageIndex;
    }

    public ApplicationBean getApplicationBean() {
	return applicationBean;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
	this.applicationBean = applicationBean;
    }

    public String getqValue() {
	return qValue;
    }

    public void setqValue(String qValue) {
	this.qValue = qValue;
    }

    public int getNumOfImageResults() {
	return numOfImageResults;
    }

    public void setNumOfImageResults(int iNumOfImageResults) {
	this.numOfImageResults = iNumOfImageResults;
    }

    public int getNumOfImageArchiveResults() {
	return numOfImageArchiveResults;
    }

    public void setNumOfImageArchiveResults(int iNumOfImageArchiveResults) {
	this.numOfImageArchiveResults = iNumOfImageArchiveResults;
    }

    public int getNumOfLiferayResults() {
	return numOfLiferayResults;
    }

    public void setNumOfLiferayResults(int iNumOfLiferayResults) {
	this.numOfLiferayResults = iNumOfLiferayResults;
    }

    public int getNumOfLibcatResults() {
	return numOfLibcatResults;
    }

    public void setNumOfLibcatResults(int numOfLibcatResults) {
	this.numOfLibcatResults = numOfLibcatResults;
    }

    public int getNumOfDatasetResults() {
	return numOfDatasetResults;
    }

    public void setNumOfDatasetResults(int numOfDatasetResults) {
	this.numOfDatasetResults = numOfDatasetResults;
    }

    public int getNumOfAllResults() {
	return numOfAllResults;
    }

    public void setNumOfAllResults(int numOfAllResults) {
	this.numOfAllResults = numOfAllResults;
    }

    public int getNumOfCurrentTabResults() {
	switch (this.currentTab) {
	    case (Global.LIFERAYDOC): {
		return getNumOfLiferayResults();
	    }
	    case (Global.IMAGE): {
		return getNumOfImageResults();
	    }
	    case (Global.LIBCAT): {
		return getNumOfLibcatResults();
	    }
	    case (Global.DATASET): {
		return getNumOfDatasetResults();
	    }
	    case (Global.IMAGEARCHIVE): {
		return getNumOfImageArchiveResults();
	    }
	}
	return getNumOfAllResults();
    }

    public ArrayList<BasicResultDoc> getSearchResults() {
	return searchResults;
    }

    public void setSearchResults(ArrayList<BasicResultDoc> results) {
	this.searchResults = results;
    }

    public void setCurrentTab(int tab) {
	this.currentTab = tab;
    }

    public int getCurrentTab() {
	return this.currentTab;
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
}
