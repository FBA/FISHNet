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
package org.freshwaterlife.portlets.dataset.display;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.ws.rs.core.Cookie;

import org.freshwaterlife.fedora.client.CustomFedoraDisplayClient;
import org.freshwaterlife.fedora.client.CustomGetDissemination;
import org.freshwaterlife.portlets.dataset.ApplicationBean;

import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.response.FedoraResponse;

/**
 * @author Paul Johnson
 */
public class UserBean implements Serializable {

    private ApplicationBean applicationBean;
    private static final long serialVersionUID = 1L;
    private String content;
    private String pid;
    private String edition;

    public UserBean() {

	// Gets the HttpServletRequest from the PortletServletRequest
	javax.servlet.http.HttpServletRequest request = (javax.servlet.http.HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("com.liferay.portal.kernel.servlet.PortletServletRequest"));

	// Gets the PID from the parameters
	String sPID = (String) request.getParameter("PID");
	System.out.println("PID: " + sPID);
	this.pid = sPID;

	// Gets the PID from the parameters
	String sEdition = (String) request.getParameter("edition");
	System.out.println("Edition: " + sEdition);
	this.edition = sEdition;
    }

    public String getContent() {
	FedoraResponse fResponse = null;

	CustomFedoraDisplayClient fedora;

	try {

	    javax.servlet.http.HttpServletRequest request = (javax.servlet.http.HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("com.liferay.portal.kernel.servlet.PortletServletRequest"));

	    FedoraCredentials credentials = new FedoraCredentials(new URL(getApplicationBean().getFedoraURL()), getApplicationBean().getFedoraUsername(), getApplicationBean().getFedoraPassword());
	    fedora = new CustomFedoraDisplayClient(credentials);

	    javax.servlet.http.Cookie[] cookies = request.getCookies();

	    List<Cookie> lCookies = new ArrayList<Cookie>();

	    for (javax.servlet.http.Cookie c : cookies) {
		lCookies.add(new Cookie(c.getName(), c.getValue(), c.getPath(), c.getDomain(), c.getVersion()));
	    }

	    String sContent = "";
	    fResponse = (new CustomGetDissemination(this.pid, "DatasetConfig:XSLT-SDEF", "getDocumentStyle1")).methodParam("edition", this.edition).execute(fedora, lCookies);
	    if (fResponse.getStatus() == 200) {
		sContent = fResponse.getEntity(String.class);
	    }

	    System.out.println("GOT FIRST DISSEMINATOR");
	    fResponse = (new CustomGetDissemination(this.pid, "DatasetConfig:XSLT-SDEF", "getDocumentStyle2")).execute(fedora, lCookies);
	    if (fResponse.getStatus() == 200) {
		sContent = sContent.replace("{RIGHTS_METADATA_FIELDS}", fResponse.getEntity(String.class));
	    }

	    fResponse = (new CustomGetDissemination(this.pid, "DatasetConfig:XSLT-SDEF", "getDocumentStyle3")).methodParam("edition", this.edition).execute(fedora, lCookies);
	    if (fResponse.getStatus() == 200) {
		//System.out.println(fResponse.getEntity(String.class));
		sContent = sContent.replace("{EDITIONS_LIST}", fResponse.getEntity(String.class));
	    }

	    this.content = sContent;

	} catch (MalformedURLException e) {
	    System.out.println("Error with URL." + e.getMessage());
	    this.content = "There was a problem retrieving the dataset.";
	} catch (FedoraClientException ex) {
	    System.out.println("Fedora Error: " + ex.getMessage());
	    this.content = "There was a problem retrieving the dataset.";
	}

	return this.content;
    }

    public void setContent(String sContent) {
	this.content = sContent;
    }

    public ApplicationBean getApplicationBean() {
	return applicationBean;
    }
    public void setApplicationBean(ApplicationBean applicationBean) {
	this.applicationBean = applicationBean;
    }
}
