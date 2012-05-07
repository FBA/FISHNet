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
package org.freshwaterlife.portlets.dataset.edit.bean;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.freshwaterlife.fedora.client.CustomAddDatastream;
import org.freshwaterlife.fedora.client.CustomFedoraClient;
import org.freshwaterlife.fedora.client.CustomGetObjectXML;
import org.freshwaterlife.fedora.client.CustomIngest;
import org.freshwaterlife.fedora.client.CustomPurgeDatastream;
import org.freshwaterlife.fedora.client.CustomUpload;
import org.freshwaterlife.fedora.client.CustomModifyDatastream;

import javax.ws.rs.core.Cookie;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.ActionRequest;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.xml.sax.InputSource;

import org.freshwaterlife.portlets.dataset.Global;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.util.bridges.jsf.common.JSFPortletUtil;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.request.GetDatastream;
import com.yourmediashelf.fedora.client.response.DatastreamProfileResponse;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import com.yourmediashelf.fedora.client.response.IngestResponse;
import com.yourmediashelf.fedora.client.response.ModifyDatastreamResponse;
import com.yourmediashelf.fedora.client.response.UploadResponse;
import com.yourmediashelf.fedora.generated.management.DatastreamProfile;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.freshwaterlife.fedora.client.CustomGetDatastreamDissemination;
import org.freshwaterlife.portlets.dataset.ApplicationBean;
import org.freshwaterlife.util.XPathNodeUpdater;

/**
 * @author Paul Johnson
 * @author Eric Liao
 */
public class UserBean implements Serializable {

    private ApplicationBean applicationBean;
    private String fedoraIngestFormat = "info:fedora/fedora-system:FOXML-1.1";
    private String fedoraLogMessage = "Dataset Upload Portlet Ingest";
    private static Log _log = LogFactoryUtil.getLog(UserBean.class);
    private static final long serialVersionUID = 1L;

    public UserBean() {
	populateTopicCategoryList();
	populateFrequencyOfUpdateList();
	this.resourceType = "dataset";
	this.showMetadata = "true";
	this.showUpload = "true";
	this.showSave = "true";
	this.showError = "false";
    }

    public List<SelectItem> getTopicCategoryList() {
	return topicCategoryList;
    }

    public void setTopicCategoryList(List<SelectItem> topicCategoryList) {
	this.topicCategoryList = topicCategoryList;
    }

    public List<SelectItem> getFrequencyOfUpdateList() {
	return frequencyOfUpdateList;
    }

    public void setFrequencyOfUpdateList(List<SelectItem> frequencyOfUpdateList) {
	this.frequencyOfUpdateList = frequencyOfUpdateList;
    }

    public String getOwnerName() {
	return ownerName;
    }

    public void setOwnerName(String ownerName) {
	this.ownerName = ownerName;
    }

    public String getContactPerson() {
	return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
	this.contactPerson = contactPerson;
    }

    public String getContactInstitution() {
	return contactInstitution;
    }

    public void setContactInstitution(String contactInstitution) {
	this.contactInstitution = contactInstitution;
    }

    public String getContactPhone() {
	return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
	this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
	return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
	this.contactEmail = contactEmail;
    }

    public String getOriginalCreator() {
	return originalCreator;
    }

    public void setOriginalCreator(String originalCreator) {
	this.originalCreator = originalCreator;
    }

    public String getDatasetTitle() {
	return datasetTitle;
    }

    public void setDatasetTitle(String datasetTitle) {
	this.datasetTitle = datasetTitle;
    }

    public String getDatasetDescription() {
	return datasetDescription;
    }

    public void setDatasetDescription(String datasetDescription) {
	this.datasetDescription = datasetDescription;
    }

    public List<String> getTopicCategory() {
	return topicCategory;
    }

    public void setTopicCategory(List<String> topicCategory) {
	this.topicCategory = topicCategory;
    }

    public String getKeywords() {
	return keywords;
    }

    public void setKeywords(String keywords) {
	this.keywords = keywords;
    }

    public String getGeographicKeywords() {
	return geographicKeywords;
    }

    public void setGeographicKeywords(String geographicKeywords) {
	this.geographicKeywords = geographicKeywords;
    }

    public String getWestBoundingLongitude() {
	return westBoundingLongitude;
    }

    public void setWestBoundingLongitude(String westBoundingLongitude) {
	this.westBoundingLongitude = westBoundingLongitude;
    }

    public String getEastBoundingLongitude() {
	return eastBoundingLongitude;
    }

    public void setEastBoundingLongitude(String eastBoundingLongitude) {
	this.eastBoundingLongitude = eastBoundingLongitude;
    }

    public String getNorthBoundingLatitude() {
	return northBoundingLatitude;
    }

    public void setNorthBoundingLatitude(String northBoundingLatitude) {
	this.northBoundingLatitude = northBoundingLatitude;
    }

    public String getSouthBoundingLatitude() {
	return southBoundingLatitude;
    }

    public void setSouthBoundingLatitude(String southBoundingLatitude) {
	this.southBoundingLatitude = southBoundingLatitude;
    }

    public String getSpatialReferenceSystem() {
	return spatialReferenceSystem;
    }

    public void setSpatialReferenceSystem(String spatialReferenceSystem) {
	this.spatialReferenceSystem = spatialReferenceSystem;
    }

    public String getFrequencyOfUpdate() {
	return frequencyOfUpdate;
    }

    public void setFrequencyOfUpdate(String frequencyOfUpdate) {
	this.frequencyOfUpdate = frequencyOfUpdate;
    }

    public String getTemporalExtentFrom() {
	return temporalExtentFrom;
    }

    public void setTemporalExtentFrom(String temporalExtentFrom) {
	this.temporalExtentFrom = temporalExtentFrom;
    }

    public String getTemporalExtentTo() {
	return temporalExtentTo;
    }

    public void setTemporalExtentTo(String temporalExtentTo) {
	this.temporalExtentTo = temporalExtentTo;
    }

    public String getDatasetReferenceDate() {
	return datasetReferenceDate;
    }

    public void setDatasetReferenceDate(String datasetReferenceDate) {
	this.datasetReferenceDate = datasetReferenceDate;
    }

    public String getLineage() {
	return lineage;
    }

    public void setLineage(String lineage) {
	this.lineage = lineage;
    }

    public String getResourceType() {
	return resourceType;
    }

    public void setResourceType(String resourceType) {
	this.resourceType = resourceType;
    }

    public String getResourceLocator() {
	return resourceLocator;
    }

    public void setResourceLocator(String resourceLocator) {
	this.resourceLocator = resourceLocator;
    }

    public String getUniqueResourceIdentifier() {
	return uniqueResourceIdentifier;
    }

    public void setUniqueResourceIdentifier(String uniqueResourceIdentifier) {
	this.uniqueResourceIdentifier = uniqueResourceIdentifier;
    }

    public String getDatasetLanguage() {
	return datasetLanguage;
    }

    public void setDatasetLanguage(String datasetLanguage) {
	this.datasetLanguage = datasetLanguage;
    }

    public String getMetadataLanguage() {
	return metadataLanguage;
    }

    public void setMetadataLanguage(String metadataLanguage) {
	this.metadataLanguage = metadataLanguage;
    }

    public String getPid() {
	return pid;
    }

    public void setPid(String pid) {
	this.pid = pid;
    }

    public String getExistingPID() {
	return existingPID;
    }

    public void setExistingPID(String existingPID) {
	this.existingPID = existingPID;
    }

    public void setShowMetadata(String showMetadata) {
	this.showMetadata = showMetadata;
    }

    public String getShowMetadata() {
	return showMetadata;
    }

    public void setShowUpload(String showUpload) {
	this.showUpload = showUpload;
    }

    public String getShowUpload() {
	return showUpload;
    }

    public void setErrorMessage(String errorMessage) {
	this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
	return errorMessage;
    }

    public void setShowError(String showError) {
	this.showError = showError;
    }

    public String getShowError() {
	return showError;
    }

    public void setShowSave(String showSave) {
	this.showSave = showSave;
    }

    public String getShowSave() {
	return showSave;
    }

    private void populateTopicCategoryList() {
	topicCategoryList = new ArrayList<SelectItem>();
	topicCategoryList.add(new SelectItem("farming", "Farming"));
	topicCategoryList.add(new SelectItem("biota", "Biota"));
	topicCategoryList.add(new SelectItem("boundaries", "Boundaries"));
	topicCategoryList.add(new SelectItem("climatologyMeteorologyAtmosphere", "Climatology / Meteorology / Atmosphere"));
	topicCategoryList.add(new SelectItem("economy", "Economy"));
	topicCategoryList.add(new SelectItem("elevation", "Elevation"));
	topicCategoryList.add(new SelectItem("environment", "Environment"));
	topicCategoryList.add(new SelectItem("geoscientificInformation", "Geoscientific Information"));
	topicCategoryList.add(new SelectItem("health", "Health"));
	topicCategoryList.add(new SelectItem("imageryBaseMapsEarthCover", "Imagery / Base Maps / Earth Cover"));
	topicCategoryList.add(new SelectItem("intelligenceMilitary", "Intelligence / Military"));
	topicCategoryList.add(new SelectItem("inlandWaters", "Inland Waters"));
	topicCategoryList.add(new SelectItem("location", "Location"));
	topicCategoryList.add(new SelectItem("oceans", "Oceans"));
	topicCategoryList.add(new SelectItem("planningCadastre", "Planning / Cadastre"));
	topicCategoryList.add(new SelectItem("society", "Society"));
	topicCategoryList.add(new SelectItem("structure", "Structure"));
	topicCategoryList.add(new SelectItem("transportation", "Transportation"));
	topicCategoryList.add(new SelectItem("utilitiesCommunication", "Utilities / Communication"));
    }

    private void populateFrequencyOfUpdateList() {
	frequencyOfUpdateList = new ArrayList<SelectItem>();
	frequencyOfUpdateList.add(new SelectItem("", "Select One..."));
	frequencyOfUpdateList.add(new SelectItem("continual", "continual"));
	frequencyOfUpdateList.add(new SelectItem("daily", "daily"));
	frequencyOfUpdateList.add(new SelectItem("weekly", "weekly"));
	frequencyOfUpdateList.add(new SelectItem("fortnightly", "fortnightly"));
	frequencyOfUpdateList.add(new SelectItem("monthly", "monthly"));
	frequencyOfUpdateList.add(new SelectItem("quarterly", "quarterly"));
	frequencyOfUpdateList.add(new SelectItem("biannually", "biannually"));
	frequencyOfUpdateList.add(new SelectItem("annually", "annually"));
	frequencyOfUpdateList.add(new SelectItem("asNeeded", "asNeeded"));
	frequencyOfUpdateList.add(new SelectItem("irregular", "irregular"));
	frequencyOfUpdateList.add(new SelectItem("notPlanned", "notPlanned"));
	frequencyOfUpdateList.add(new SelectItem("unknown", "unknown"));
    }
    private List<SelectItem> topicCategoryList;
    private List<SelectItem> frequencyOfUpdateList;
    private String ownerName;
    private String contactPerson;
    private String contactInstitution;
    private String contactPhone;
    private String contactEmail;
    private String originalCreator;
    private String datasetTitle;
    private String datasetDescription;
    private List<String> topicCategory;
    private String keywords;
    private String geographicKeywords;
    private String westBoundingLongitude;
    private String eastBoundingLongitude;
    private String southBoundingLatitude;
    private String northBoundingLatitude;
    private String spatialReferenceSystem;
    private String frequencyOfUpdate;
    private String temporalExtentFrom;
    private String temporalExtentTo;
    private String datasetReferenceDate;
    private String lineage;
    private String datasetLanguage;
    private String metadataLanguage;
    private String uniqueResourceIdentifier;
    private String resourceLocator;
    private String resourceType;
    private String pid;
    private String existingPID;
    private String showMetadata;
    private String showUpload;
    private String showError;
    private String errorMessage;
    private String showSave;
    private String datasetCategory;

    public void doLoad() {
	System.out.println("doLoad");

	Map<String, Namespace> nsList = _buildNamespaceList();

	String modsPath = "foxml:digitalObject/foxml:datastream[@ID='descMetadata']/foxml:datastreamVersion[last()]/foxml:xmlContent/mods:mods";
	String datasetTitlePath = modsPath + "/mods:titleInfo/mods:title";
	String datasetDescriptionPath = modsPath + "/mods:abstract";
	String dataIdentificationPath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification";

	// Keywords - From Template, Multiple
	String keywordsParentPath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords";

	// Geographic Keywords - MODS, Multiple
	String geographicKeywordsPath = modsPath + "/mods:subject";

	String westBoundingLongitudePath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:westBoundLongitude/gco:Decimal";
	String eastBoundingLongitudePath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:eastBoundLongitude/gco:Decimal";
	String northBoundingLatitudePath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:northBoundLatitude/gco:Decimal";
	String southBoundingLatitudePath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:southBoundLatitude/gco:Decimal";
	String spatialReferenceSystemPath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:referenceSystemInfo/gmd:MD_ReferenceSystem/gmd:referenceSystemIdentifier/gmd:RS_Identifier/gmd:code/gco:CharacterString";
	String frequencyOfUpdatePath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceMaintenance/gmd:MD_MaintenanceInformation/gmd:maintenanceAndUpdateFrequency/gmd:MD_MaintenanceFrequencyCode";

	// Temporal Extent
	String temporalExtentFromPath = modsPath + "/mods:originInfo/mods:dateOther[@point='start']";
	String temporalExtentToPath = modsPath + "/mods:originInfo/mods:dateOther[@point='end']";

	// Dataset Reference Date
	String datasetReferenceDatePath = modsPath + "/mods:originInfo/mods:dateIssued";

	String lineagePath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:statement/gco:CharacterString";

	// Resource Type - Does it need to be changed? Dataset is defined by default.
	String resourceTypePath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:hierarchyLevel/gmd:MD_ScopeCode";

	// Dataset Language
	String datasetLanguagePath = modsPath + "/mods:language/mods:languageTerm";

	String resourceLocatorPath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource/gmd:linkage/gmd:URL";
	String metadataLanguagePath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:language/gmd:LanguageCode";

	// Dataset Category
	String datasetCategoryPath = modsPath + "/mods:note[@type='admin']";

	modsPath = "foxml:digitalObject/foxml:datastream[@ID='rightsMetadata']/foxml:datastreamVersion[last()]/foxml:xmlContent/mods:mods";

	String ownerNamePath = modsPath + "/mods:name[mods:role/mods:roleTerm/text() = 'owner']/mods:namePart";
	String contactPersonPath = modsPath + "/mods:name[mods:role/mods:roleTerm/text() = 'contact']/mods:namePart";
	String contactInstitutionPath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:pointOfContact/gmd:CI_ResponsibleParty/gmd:organisationName/gco:CharacterString";
	String contactPhonePath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:pointOfContact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:voice/gco:CharacterString";
	String contactEmailPath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:pointOfContact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress/gco:CharacterString";
	String originalCreatorPath = modsPath + "/mods:name[mods:role/mods:roleTerm/text() = 'creator']/mods:namePart";

	// PREMIS event metadata
        /*
	String PREMISPath = "foxml:digitalObject/foxml:datastream[@ID='PREMIS']/foxml:datastreamVersion[last()]/foxml:xmlContent/premis:premis/premis:event";

	String eventIdentifierPath = PREMISPath + "/premis:eventIdentifier";
	String eventTypePath = PREMISPath + "/premis:eventType";
	String eventDateTimePath = PREMISPath + "/premis:eventDateTime";
	String eventDetailPath = PREMISPath + "/premis:eventDetail";

	String eventOutcomePath = PREMISPath + "/premis:eventOutcomeInformation/premis:eventOutcome";
	String eventOutcomeDetailPath = PREMISPath + "/premis:eventOutcomeInformation/premis:eventOutcomeDetail";

	String fedoraRolePath = PREMISPath + "/premis:linkingAgentIdentifier[premis:linkingAgentIdentifierType/text() = 'fedoraRole']/premis:linkingAgentIdentifierValue";
	String fullNamePath = PREMISPath + "/premis:linkingAgentIdentifier[premis:linkingAgentIdentifierType/text() = 'Full Name']/premis:linkingAgentIdentifierValue";
	String emailPath = PREMISPath + "/premis:linkingAgentIdentifier[premis:linkingAgentIdentifierType/text() = 'E-mail address']/premis:linkingAgentIdentifierValue";

	String linkingObjectIdentifierPath = PREMISPath + "/premis:linkingObjectIdentifier";
	 */

	// Gets the HttpServletRequest from the PortletServletRequest
	javax.servlet.http.HttpServletRequest request = (javax.servlet.http.HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("com.liferay.portal.kernel.servlet.PortletServletRequest"));

	// Gets the PID from the parameters
	String sPID = (String) request.getParameter("PID");
	System.out.println("PID: " + sPID);

	String action = (String) request.getParameter("myAction");
	System.out.println("action: " + action);

	if (sPID != null) {
	    this.existingPID = sPID;

	    FedoraResponse fResponse = null;
	    CustomFedoraClient fedora;

	    FedoraCredentials credentials;

	    if (action.equals("metadata")) {
		try {
		    credentials = new FedoraCredentials(new URL(getApplicationBean().getFedoraURL()), getApplicationBean().getFedoraUsername(), getApplicationBean().getFedoraPassword());
		    fedora = new CustomFedoraClient(credentials);

		    javax.servlet.http.Cookie[] cookies = request.getCookies();
		    List<Cookie> lCookies = new ArrayList<Cookie>();
		    for (javax.servlet.http.Cookie c : cookies) {
			lCookies.add(new Cookie(c.getName(), c.getValue(), c.getPath(), c.getDomain(), c.getVersion()));
		    }

		    String sContent = "";
		    fResponse = (new CustomGetObjectXML(sPID)).execute(fedora, lCookies);

		    if (fResponse.getStatus() == 200) {
			// modifying metadata, hide dataset section
			this.showMetadata = "true";
			this.showUpload = "false";
			this.showSave = "true";
			this.showError = "false";

			sContent = fResponse.getEntity(String.class);

			// parse sContent to fill bean data
			org.jdom.input.SAXBuilder jdomParser = new org.jdom.input.SAXBuilder();
			try {
			    System.out.println("doLoad - start parsing xml");
			    Document doc = jdomParser.build((new InputSource(new ByteArrayInputStream(sContent.getBytes("utf-8")))));

			    this.datasetTitle = XPathGetNode(doc, datasetTitlePath, nsList).getText();
			    this.datasetDescription = XPathGetNode(doc, datasetDescriptionPath, nsList).getText();

			    // Topic categories
			    this.topicCategory = new ArrayList<String>();
			    @SuppressWarnings("unchecked")
			    List<Element> list = XPathGetNode(doc, dataIdentificationPath, nsList).getChildren("topicCategory", nsList.get("gmd"));
			    //  /gmd:topicCategory/gmd:MD_TopicCategoryCode
			    if (list.size() > 0) {
				Iterator<Element> itr = list.iterator();
				Element ds;

				while (itr.hasNext()) {
				    ds = itr.next();
				    this.topicCategory.add(ds.getChildText("MD_TopicCategoryCode", nsList.get("gmd")));
				}
			    }

			    // Keywords - Multiple
			    Element keywordParentNode = XPathGetNode(doc, keywordsParentPath, nsList);
			    List<?> keywords1 = keywordParentNode.getChildren("keyword", nsList.get("gmd"));
			    String loadKeywords = "";
			    for (int i = 0; i < keywords1.size() - 1; i++) {
				loadKeywords += ((Element) keywords1.get(i)).getChildText("CharacterString", nsList.get("gco")) + ", ";
			    }
			    int lastKeyword = keywords1.size() - 1;
			    loadKeywords += ((Element) keywords1.get(lastKeyword)).getChildText("CharacterString", nsList.get("gco"));
			    this.keywords = loadKeywords;

			    // Geographic Keywords - Multiple
			    Element geographicNode = XPathGetNode(doc, geographicKeywordsPath, nsList);

			    List<?> keywords2 = geographicNode.getChildren();
			    String geoKeywords = "";
			    for (int i = 0; i < keywords2.size() - 1; i++) {
				Element gkw = (Element) keywords2.get(i);
				geoKeywords += gkw.getText() + ", ";
			    }
			    lastKeyword = keywords2.size() - 1;
			    geoKeywords += ((Element) keywords2.get(lastKeyword)).getText();
			    this.geographicKeywords = geoKeywords;

			    // Bounding box
			    this.westBoundingLongitude = XPathGetNode(doc, westBoundingLongitudePath, nsList).getText();
			    this.eastBoundingLongitude = XPathGetNode(doc, eastBoundingLongitudePath, nsList).getText();
			    this.northBoundingLatitude = XPathGetNode(doc, northBoundingLatitudePath, nsList).getText();
			    this.southBoundingLatitude = XPathGetNode(doc, southBoundingLatitudePath, nsList).getText();
			    this.spatialReferenceSystem = XPathGetNode(doc, spatialReferenceSystemPath, nsList).getText();
			    this.frequencyOfUpdate = XPathGetNode(doc, frequencyOfUpdatePath, nsList).getText();

			    // Temporal Extent
			    this.temporalExtentFrom = XPathGetNode(doc, temporalExtentFromPath, nsList).getText();
			    this.temporalExtentTo = XPathGetNode(doc, temporalExtentToPath, nsList).getText();

			    this.datasetReferenceDate = XPathGetNode(doc, datasetReferenceDatePath, nsList).getText();
			    this.lineage = XPathGetNode(doc, lineagePath, nsList).getText();
			    this.resourceType = XPathGetNode(doc, resourceTypePath, nsList).getText();
			    this.datasetLanguage = XPathGetNode(doc, datasetLanguagePath, nsList).getText();
			    this.resourceLocator = XPathGetNode(doc, resourceLocatorPath, nsList).getText();
			    this.metadataLanguage = XPathGetNode(doc, metadataLanguagePath, nsList).getText();

			    // Dataset category
			    this.setDatasetCategory(XPathGetNode(doc, datasetCategoryPath, nsList).getText());

			    // Rights Metadata
			    this.ownerName = XPathGetNode(doc, ownerNamePath, nsList).getText();
			    this.contactPerson = XPathGetNode(doc, contactPersonPath, nsList).getText();
			    this.contactInstitution = XPathGetNode(doc, contactInstitutionPath, nsList).getText();
			    this.contactPhone = XPathGetNode(doc, contactPhonePath, nsList).getText();
			    this.contactEmail = XPathGetNode(doc, contactEmailPath, nsList).getText();
			    this.originalCreator = XPathGetNode(doc, originalCreatorPath, nsList).getText();

			    System.out.println("doLoad - complete parsing xml");

			} catch (JDOMException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			} catch (IOException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
		    }
		} catch (MalformedURLException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		} catch (FedoraClientException e) {
		    System.out.println("Fedora Error: " + e.getMessage());
		    this.showMetadata = "false";
		    this.showUpload = "false";
		    this.showSave = "false";
		    this.showError = "true";
		    this.setErrorMessage("There was a problem retrieving the dataset.");
		}
	    } else if (action.equals("upload")) {
		// modifying dataset, hide metadata section
		this.showMetadata = "false";
		this.showUpload = "true";
		this.showSave = "true";
		this.showError = "false";
	    }
	} else {
	    // creating new record
	    this.existingPID = "NEW";
	    this.showMetadata = "true";
	    this.showUpload = "true";
	    this.setShowSave("true");
	    this.showError = "false";
	}

	System.out.println("doLoad - end");
    }

    public String doSave() {

	FedoraCredentials credentials;
	CustomFedoraClient fedora = null;
	FedoraResponse fResponse = null;
	ModifyDatastreamResponse mdsResponse = null;
	Map<String, Namespace> nsList = _buildNamespaceList();
	String descMetadataPath = "foxml:digitalObject/foxml:datastream[@ID='descMetadata']/foxml:datastreamVersion[last()]/foxml:xmlContent/mods:mods";
	String rightsMetadataPath = "foxml:digitalObject/foxml:datastream[@ID='rightsMetadata']/foxml:datastreamVersion[last()]/foxml:xmlContent/mods:mods";
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

	System.out.println("doSave - start");

	FacesContext facesContext = FacesContext.getCurrentInstance();

	// JSFPortletUtil - Liferay provided class from util-bridges.jar
	ActionRequest request = (ActionRequest) JSFPortletUtil.getPortletRequest(facesContext);
	File originalUploadedFile = null;
	String extension = null;
	String filename = null;

	// load template file
	FileInputStream fIn = null;
	try {
	    fIn = new FileInputStream(getApplicationBean().getFoxmlTemplateFile());
	} catch (FileNotFoundException e) {
	    System.out.println("Template file not found");
	    return "back";
	}

	credentials = null;
	CustomFedoraClient fc = null;
	try {
	    credentials = new FedoraCredentials(new URL(getApplicationBean().getFedoraURL()), getApplicationBean().getFedoraUsername(), getApplicationBean().getFedoraPassword());
	    fc = new CustomFedoraClient(credentials);
	} catch (MalformedURLException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	    return "back";
	}

	BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));
	org.jdom.input.SAXBuilder jdomBuilder = new org.jdom.input.SAXBuilder();
	Document doc;

	try {
	    if (this.showMetadata.equals("true")) {
		// Build FOXML if creating/modifying metadata
		doc = jdomBuilder.build(reader);
		if (_buildFOXMLDocument(doc)) {
		    System.out.println("built FOXML successfully.");

		    System.out.println("existing PID: " + this.existingPID);

		    if (!this.existingPID.equals("NEW")) {
			javax.servlet.http.Cookie[] cookies = request.getCookies();
			List<Cookie> lCookies = new ArrayList<Cookie>();

			for (javax.servlet.http.Cookie c : cookies) {
			    lCookies.add(new Cookie(c.getName(), c.getValue(), c.getPath(), c.getDomain(), c.getVersion()));
			}

			// rightsMetadata
			System.out.println("modifying existing rightsMetadata.");
			XMLOutputter outputter = new XMLOutputter();
			ByteArrayOutputStream rightsMetadata = new ByteArrayOutputStream();
			outputter.output(XPathGetNode(doc, rightsMetadataPath, nsList), rightsMetadata);

			try {
			    ((CustomModifyDatastream) (new CustomModifyDatastream(this.existingPID, "rightsMetadata")).content(rightsMetadata.toString())).execute(fc, lCookies);
			} catch (FedoraClientException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			    System.out.println("modifying existing rightsMetadata failed.");
			    return "back";
			}

			// descMetadata
			System.out.println("modifying existing descMetadata.");
			ByteArrayOutputStream descMetadata = new ByteArrayOutputStream();
			outputter.output(XPathGetNode(doc, descMetadataPath, nsList), descMetadata);

			try {
			    ((CustomModifyDatastream) (new CustomModifyDatastream(this.existingPID, "descMetadata")).content(descMetadata.toString())).execute(fc, lCookies);
			} catch (FedoraClientException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			    System.out.println("modifying existing descMetadata failed.");
			    return "back";
			}

			// PREMIS event

			// PRJ - 26/04/2011
			// Rather than storing each individual PREMIS event in a separate version of the datastream, we need to:
			// 1. Add the new PREMIS event to the existing PREMIS data.
			// 2. Store the modified PREMIS data in a new version of the PREMIS datastream.
			// 3. Remove the previous version of the PREMIS datastream.

			fResponse = null;

			String sContent = "";
			try {
			    // Retrieve the existing fedora object.
			    fResponse = (new CustomGetObjectXML(this.existingPID)).execute(fc, lCookies);

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
				    System.out.println("logging PREMIS event for modify metadata");
				    ByteArrayOutputStream PREMIS = new ByteArrayOutputStream();
				    outputter.output(premis, PREMIS);
				    try {
					((CustomModifyDatastream) (new CustomModifyDatastream(this.existingPID, "PREMIS")).content(PREMIS.toString())).execute(fc, lCookies);
					// Remove the previous version of the PREMIS datastream.
					((CustomPurgeDatastream) (new CustomPurgeDatastream(this.existingPID, "PREMIS")).endDT(XPathGetNode(premisDoc, PREMISDatastreamVersionPath, nsList).getAttributeValue("CREATED"))).execute(fc, lCookies);
				    } catch (FedoraClientException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "back";
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
		    } else {
			if (!_ingestIntoFedora(doc)) {
			    System.out.println("FOXML ingest failed.");
			    return "back";
			}
			System.out.println("ingested FOXML successfully.");
		    }
		} else {
		    System.out.println("failed building FOXML.");
		    return "back";
		}
	    }
	} catch (org.jdom.JDOMException e) {
	    System.out.println("JDOM Exception: " + e);
	    return "back";
	} catch (IOException e) {
	    System.out.println("IO Exception: " + e);
	    return "back";
	}

	if (this.showUpload.equals("true")) {
	    // FileItem is from commons-fileupload.jar

	    FileItem item = (FileItem) request.getAttribute("resumeFile");

	    System.out.println("File name: " + item.getName());
	    System.out.println("IsInMemory: " + item.isInMemory());
	    System.out.println("IsFormField: " + item.isFormField());
//TODO: check for blanks in filename?
	    int dotIndex = item.getName().lastIndexOf('.');
	    if (-1 != dotIndex) {
		extension = item.getName().substring(dotIndex);
		int lastSlashPos = item.getName().lastIndexOf('\\');
		filename = item.getName().substring(lastSlashPos + 1, dotIndex);
	    }
	    // Upload the file
	    if (item.getSize() > 0) {
		System.out.println("uploading file");
		try {
		    if (this.existingPID.equals("NEW")) {
			if (!this.showMetadata.equals("true")) {
			    System.out.println("assigning new PID");
			    this.pid = "Dataset:" + UUID.randomUUID().toString();
			}
		    } else {
			System.out.println("using existing PID");
			this.pid = this.existingPID;
		    }
		    //File uploadedFile = File.createTempFile(this.pid.replace(":", ""), ".tmp");
		    File uploadedFile = File.createTempFile(this.pid.replace(":", ""), extension);

		    System.out.println("starting writing file");
		    item.write(uploadedFile);
		    System.out.println("finished writing file");

		    javax.servlet.http.Cookie[] cookies = request.getCookies();
		    List<Cookie> lCookies = new ArrayList<Cookie>();

		    for (javax.servlet.http.Cookie c : cookies) {
			lCookies.add(new Cookie(c.getName(), c.getValue(), c.getPath(), c.getDomain(), c.getVersion()));
		    }
		    //TODO: This has been commented out as it was causing a 405 error, and isn't needed until SSO is in and running
		    //UploadResponse response = (new CustomUpload(uploadedFile)).execute(fc, lCookies);
		    UploadResponse response = (new CustomUpload(uploadedFile)).execute(fc);


		    // load PREMIS template file
		    FileInputStream fInPremis = null;
		    try {
			fInPremis = new FileInputStream(getApplicationBean().getPremisTemplateFile());
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

		    XMLOutputter outputter = new XMLOutputter();
		    //ByteArrayOutputStream PREMISEvent = new ByteArrayOutputStream();

		    String pidToUse = this.pid;
		    if (!this.existingPID.equals("NEW")) {
			boolean bDataStreamExists = false;
			System.out.println("modifying existing object.");

			pidToUse = this.existingPID;

			credentials = new FedoraCredentials(new URL(getApplicationBean().getFedoraURL()), getApplicationBean().getFedoraUsername(), getApplicationBean().getFedoraPassword());
			fedora = new CustomFedoraClient(credentials);
			try {
			    fResponse = (new CustomGetDatastreamDissemination(pidToUse, "originalContent")).execute(fedora, lCookies);
			    if (fResponse.getStatus() == 200) {
				bDataStreamExists = true;
			    } else if (fResponse.getStatus() == 404) {
				bDataStreamExists = false;
			    }
			} catch (Exception ex) {
			    //Am catching this exception here, so that the condition
			    //statement below can handle the existence/non existence of the datastream
			    bDataStreamExists = false;
			    System.out.println("No originalContent datastream exists for this dataset so will create it now");
			}
			if (bDataStreamExists) {

			    String contentType = item.getContentType();

			    CustomModifyDatastream cmsOriginalContent = (new CustomModifyDatastream(this.existingPID, "originalContent",contentType));

			    CustomModifyDatastream cmsUploadLoc = (CustomModifyDatastream) cmsOriginalContent.
				    dsLabel(filename + extension).
				    content(uploadedFile);
	
			    mdsResponse = cmsUploadLoc.execute(fc, lCookies);


			} else {
			    // The datastream for originalContent doesn't exist, so create it
			    ((CustomAddDatastream) (new CustomAddDatastream(this.pid, "originalContent")).altIDs(new ArrayList<String>()).
				    //dsLabel("Original Content").
				    dsLabel(filename + extension).
				    versionable(true).
				    mimeType(item.getContentType()).
				    dsLocation(response.getUploadLocation()).
				    controlGroup("M").
				    dsState("A").
				    logMessage("initial upload")).execute(fc, lCookies);
			}

			// Add a 'modify content' PREMIS event
			XPathNodeUpdater.create(doc, eventTypePath, nsList).setContent("http://www.fba.org.uk/vocabulary/preservationEvents/modifyContent");
			XPathNodeUpdater.create(doc, eventDetailPath, nsList).setContent("modifying object content for " + this.existingPID);
			XPathNodeUpdater.create(doc, eventOutcomePath, nsList).setContent("success");
			XPathNodeUpdater.create(doc, eventOutcomeDetailPath, nsList).setContent(this.existingPID + "'s content modified");
			XPathNodeUpdater.create(doc, linkingObjectIdentifierPath, nsList).setContent(this.existingPID);

			System.out.println("logging PREMIS event for modify content");
			//outputter.output(doc, PREMISEvent);
			//((CustomModifyDatastream)(new CustomModifyDatastream(this.existingPID, "PREMIS")).
			//		content(PREMISEvent.toString())).execute(fc, lCookies);
		    } else {
			String strDsLabel = filename + extension;

			if (strDsLabel.isEmpty()) {
			    strDsLabel = "Original Content";
			}
			((CustomAddDatastream) (new CustomAddDatastream(this.pid, "originalContent")).altIDs(new ArrayList<String>()).
				dsLabel(strDsLabel).
				versionable(true).
				mimeType(item.getContentType()).
				dsLocation(response.getUploadLocation()).
				controlGroup("M").
				dsState("A").
				logMessage("initial upload")).execute(fc, lCookies);

			// Add a 'ingestion' PREMIS event
			XPathNodeUpdater.create(doc, eventTypePath, nsList).setContent("http://id.loc.gov/vocabulary/preservationEvents/ingestion");
			XPathNodeUpdater.create(doc, eventDetailPath, nsList).setContent("ingestion of dataset content");
			XPathNodeUpdater.create(doc, eventOutcomePath, nsList).setContent("success");
			XPathNodeUpdater.create(doc, eventOutcomeDetailPath, nsList).setContent("ingestion content as datastream of PID: " + this.pid);
			XPathNodeUpdater.create(doc, linkingObjectIdentifierPath, nsList).setContent(this.pid);

			System.out.println("logging PREMIS event for new content");
			//outputter.output(doc, PREMISEvent);
			//((CustomModifyDatastream)(new CustomModifyDatastream(this.pid, "PREMIS")).
			//		content(PREMISEvent.toString())).execute(fc, lCookies);
		    }



		    fResponse = null;

		    String sContent = "";
		    try {
			// Retrieve the existing fedora object.
			fResponse = (new CustomGetObjectXML(pidToUse)).execute(fc, lCookies);

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
				    return "back";
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
		    } catch (FedoraClientException e) {
			System.out.println("Fedora Error: " + e.getMessage());
		    }

		} catch (IOException ex) {
		    System.out.println("IO Error: " + ex);
		    return "back";
		} catch (Exception ex) {
		    System.out.println("Error: " + ex);
		    return "back";
		}
	    }
	}
	System.out.println("doSave - end");

	FacesContext face = FacesContext.getCurrentInstance();
	try {
	    // redirects to the My Datasets Portlet
	    face.getExternalContext().redirect("/web/guest/my-datasets");
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    System.out.println("Exception redirecting to my datasets: " + e.getMessage());
	    e.printStackTrace();
	}

	return "back";
    }

    public Element XPathGetNode(Document doc, String path, Map<String, Namespace> ns) throws JDOMException {
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
    }

    private Map<String, Namespace> _buildNamespaceList() {

	Map<String, Namespace> nsList = new HashMap<String, Namespace>();

	nsList.put("mods", Namespace.getNamespace("mods", "http://www.loc.gov/mods/v3"));
	nsList.put("gmd", Namespace.getNamespace("gmd", "http://www.isotc211.org/2005/gmd"));
	nsList.put("gco", Namespace.getNamespace("gco", "http://www.isotc211.org/2005/gco"));
	nsList.put("oaidc", Namespace.getNamespace("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/"));
	nsList.put("dc", Namespace.getNamespace("dc", "http://purl.org/dc/elements/1.1/"));
	nsList.put("rdf", Namespace.getNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"));
	nsList.put("policy", Namespace.getNamespace("policy", "urn:oasis:names:tc:xacml:1.0:policy"));
	nsList.put("premis", Namespace.getNamespace("premis", "http://www.loc.gov/standards/premis"));
	nsList.put("foxml", Namespace.getNamespace("foxml", "info:fedora/fedora-system:def/foxml#"));

	return nsList;
    }

    private boolean _buildFOXMLDocument(Document doc) {
	Map<String, Namespace> nsList = _buildNamespaceList();

	String pidPath1 = "foxml:digitalObject";
	String pidPath2 = "foxml:digitalObject/foxml:datastream[@ID='DC']/foxml:datastreamVersion/foxml:xmlContent/oai_dc:dc/dc:identifier";
	String pidPath3 = "foxml:digitalObject/foxml:datastream[@ID='RELS-EXT']/foxml:datastreamVersion/foxml:xmlContent/rdf:RDF/rdf:Description";

	String modsPath = "foxml:digitalObject/foxml:datastream[@ID='descMetadata']/foxml:datastreamVersion/foxml:xmlContent/mods:mods";

	String datasetTitlePath = modsPath + "/mods:titleInfo/mods:title";
	System.out.println("datasetTitlePath: " + datasetTitlePath);

	String datasetDescriptionPath = modsPath + "/mods:abstract";

	String descModsPath = "foxml:digitalObject/foxml:datastream[@ID='descMetadata']/foxml:datastreamVersion/foxml:xmlContent/mods:mods";

	String dataIdentificationPath = descModsPath + "/mods:extension/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification";

	// Keywords - From Template, Multiple
	String keywordsParentPath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords";
	String keywordsPath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword[last()]/gco:CharacterString";

	// Geographic Keywords - MODS, Multiple
	String geographicKeywordsPath = modsPath + "/mods:subject";

	String westBoundingLongitudePath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:westBoundLongitude/gco:Decimal";
	String eastBoundingLongitudePath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:eastBoundLongitude/gco:Decimal";
	String northBoundingLatitudePath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:northBoundLatitude/gco:Decimal";
	String southBoundingLatitudePath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:southBoundLatitude/gco:Decimal";
	String spatialReferenceSystemPath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:referenceSystemInfo/gmd:MD_ReferenceSystem/gmd:referenceSystemIdentifier/gmd:RS_Identifier/gmd:code/gco:CharacterString";
	String frequencyOfUpdatePath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceMaintenance/gmd:MD_MaintenanceInformation/gmd:maintenanceAndUpdateFrequency/gmd:MD_MaintenanceFrequencyCode";

	// Temporal Extent
	String temporalExtentFromPath = modsPath + "/mods:originInfo/mods:dateOther[@point='start']";
	String temporalExtentToPath = modsPath + "/mods:originInfo/mods:dateOther[@point='end']";

	// Dataset Reference Date
	String datasetReferenceDatePath = modsPath + "/mods:originInfo/mods:dateIssued";

	String lineagePath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:statement/gco:CharacterString";

	// Unique Resource Identifier - MODS or GEMINI?
	String uniqueResourceIdentifierPath = modsPath + "/mods:identifier[@type='uri']";

	// Resource Type - Does it need to be changed? Dataset is defined by default.
	String resourceTypePath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:hierarchyLevel/gmd:MD_ScopeCode";

	// Dataset Language
	String datasetLanguagePath = modsPath + "/mods:language/mods:languageTerm";

	String resourceLocatorPath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource/gmd:linkage/gmd:URL";
	String metadataLanguagePath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:language/gmd:LanguageCode";

	// Dataset Category
	String datasetCategoryPath = modsPath + "/mods:note[@type='admin']";

	String policyPath = "foxml:digitalObject/foxml:datastream[@ID='POLICY']/foxml:datastreamVersion/foxml:xmlContent/policy:Policy";

	String policyMessagePath = policyPath + "/policy:Description";
	String policyPIDPath = policyPath + "/policy:Target/policy:Resources/policy:Resource/policy:ResourceMatch/policy:AttributeValue";
	String policyUserIdStringBagPath = policyPath + "/policy:Rule[@RuleId='1']/policy:Condition/policy:Apply";
	String policyUserIdPath = policyPath + "/policy:Rule[@RuleId='1']/policy:Condition/policy:Apply/policy:AttributeValue";

	modsPath = "foxml:digitalObject/foxml:datastream[@ID='rightsMetadata']/foxml:datastreamVersion/foxml:xmlContent/mods:mods";

	String ownerNamePath = modsPath + "/mods:name[mods:role/mods:roleTerm/text() = 'owner']/mods:namePart";
	String contactPersonPath = modsPath + "/mods:name[mods:role/mods:roleTerm/text() = 'contact']/mods:namePart";
	String contactInstitutionPath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:pointOfContact/gmd:CI_ResponsibleParty/gmd:organisationName/gco:CharacterString";
	String contactPhonePath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:pointOfContact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:voice/gco:CharacterString";
	String contactEmailPath = modsPath + "/mods:extension/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:pointOfContact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress/gco:CharacterString";
	String originalCreatorPath = modsPath + "/mods:name[mods:role/mods:roleTerm/text() = 'creator']/mods:namePart";

	// PREMIS event metadata
	// Use Fedora versionable datastream to keep history of PREMIS
	String PREMISPath = "foxml:digitalObject/foxml:datastream[@ID='PREMIS']/foxml:datastreamVersion/foxml:xmlContent/premis:premis/premis:event";
	String eventIdentifierPath = PREMISPath + "/premis:eventIdentifier";
	String eventTypePath = PREMISPath + "/premis:eventType";
	String eventDateTimePath = PREMISPath + "/premis:eventDateTime";
	String eventDetailPath = PREMISPath + "/premis:eventDetail";

	String eventOutcomePath = PREMISPath + "/premis:eventOutcomeInformation/premis:eventOutcome";
	String eventOutcomeDetailPath = PREMISPath + "/premis:eventOutcomeInformation/premis:eventOutcomeDetail";

	String agentPath = PREMISPath + "/premis:linkingAgentIdentifier[premis:linkingAgentIdentifierType/text() = 'Agent']/premis:linkingAgentIdentifierValue";
	String fullNamePath = PREMISPath + "/premis:linkingAgentIdentifier[premis:linkingAgentIdentifierType/text() = 'Full Name']/premis:linkingAgentIdentifierValue";

	String linkingObjectIdentifierPath = PREMISPath + "/premis:linkingObjectIdentifier";

	if (this.existingPID.equals("NEW")) {
	    System.out.println("building new object");
	    this.pid = "Dataset:" + UUID.randomUUID().toString();
	} else {
	    System.out.println("modifying existing object");
	    this.pid = this.existingPID;
	}

	// URI to dataset display portlet.
	this.uniqueResourceIdentifier = getApplicationBean().getServerURL() + "/web/guest/dataset-display/-/dataset-display/" + this.pid;
	String policyMessage = "This is an object-specific policy stored inside the " + this.pid + " digital object as a POLICY datastream.";
	this.datasetCategory = "red";

	try {

	    XPathNodeUpdater.create(doc, pidPath1).addAttribute("PID", this.pid);
	    XPathNodeUpdater.create(doc, pidPath2, nsList).setContent(this.pid);
	    XPathNodeUpdater.create(doc, pidPath3, nsList).addAttribute("about", "info:fedora/" + this.pid, (Namespace) nsList.get("rdf"));

	    XPathNodeUpdater.create(doc, datasetTitlePath, nsList).setContent(this.datasetTitle);
	    XPathNodeUpdater.create(doc, datasetDescriptionPath, nsList).setContent(this.datasetDescription);

	    for (String tc : this.topicCategory) {
		try {
		    Element tcEl = new Element("topicCategory", nsList.get("gmd"));
		    tcEl.addContent(new Element("MD_TopicCategoryCode", nsList.get("gmd")).setText(tc));
		    XPathNodeUpdater.create(doc, dataIdentificationPath, nsList).addChildItem(tcEl);
		} catch (Exception e) {
		}
	    }

	    // Keywords - Multiple
	    String[] foxmlKeywords = this.keywords.split(",");
	    for (String kw : foxmlKeywords) {
		kw = kw.trim();

		FileInputStream fIn = null;
		try {
		    fIn = new FileInputStream(getApplicationBean().getKeywordTemplateFile());
		} catch (FileNotFoundException e) {
		    System.out.println("Keyword Template file not found");
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));

		org.jdom.input.SAXBuilder jdomBuilder = new org.jdom.input.SAXBuilder();
		try {
		    Document kwDoc = jdomBuilder.build(reader);
		    XPathNodeUpdater.create(doc, keywordsParentPath, nsList).addChildItem(kwDoc.detachRootElement());
		    XPathNodeUpdater.create(doc, keywordsPath, nsList).setContent(kw);
		} catch (org.jdom.JDOMException e) {
		    System.out.println("JDOM Exception: " + e);
		} catch (IOException e) {
		    System.out.println("IO Exception: " + e);
		}
	    }

	    // Geographic Keywords - Multiple
	    String[] gKeywords = this.geographicKeywords.split(",");
	    for (String gkw : gKeywords) {
		gkw = gkw.trim();

		try {
		    Element gkwEl = new Element("topic", nsList.get("mods")).setText(gkw);
		    XPathNodeUpdater.create(doc, geographicKeywordsPath, nsList).addChildItem(gkwEl);
		} catch (org.jdom.JDOMException e) {
		    System.out.println("JDOM Exception: " + e);
		}
	    }

	    XPathNodeUpdater.create(doc, westBoundingLongitudePath, nsList).setContent(this.westBoundingLongitude);
	    XPathNodeUpdater.create(doc, eastBoundingLongitudePath, nsList).setContent(this.eastBoundingLongitude);
	    XPathNodeUpdater.create(doc, northBoundingLatitudePath, nsList).setContent(this.northBoundingLatitude);
	    XPathNodeUpdater.create(doc, southBoundingLatitudePath, nsList).setContent(this.southBoundingLatitude);
	    XPathNodeUpdater.create(doc, spatialReferenceSystemPath, nsList).setContent(this.spatialReferenceSystem);
	    XPathNodeUpdater.create(doc, frequencyOfUpdatePath, nsList).setContent(this.frequencyOfUpdate);

	    // Temporal Extent
	    XPathNodeUpdater.create(doc, temporalExtentFromPath, nsList).setContent(this.temporalExtentFrom);
	    XPathNodeUpdater.create(doc, temporalExtentToPath, nsList).setContent(this.temporalExtentTo);

	    // Dataset Reference Date
	    XPathNodeUpdater.create(doc, datasetReferenceDatePath, nsList).setContent(this.datasetReferenceDate);

	    XPathNodeUpdater.create(doc, lineagePath, nsList).setContent(this.lineage);

	    // Unique Resource Identifier - MODS or GEMINI?
	    //XPathNodeUpdater.create(doc, uniqueResourceIdentifierPath, nsList).setContent(this.uniqueResourceIdentifier);

	    // Resource Type - Does it need to be changed? Dataset is defined by default.
	    XPathNodeUpdater.create(doc, resourceTypePath, nsList).setContent(this.resourceType);

	    // Dataset Language
	    // TODO - Need to only include this element if a value is specified by user.
	    XPathNodeUpdater.create(doc, datasetLanguagePath, nsList).setContent(this.datasetLanguage);

	    XPathNodeUpdater.create(doc, resourceLocatorPath, nsList).setContent(this.resourceLocator);
	    XPathNodeUpdater.create(doc, metadataLanguagePath, nsList).setContent(this.metadataLanguage);

	    // Dataset Category
	    XPathNodeUpdater.create(doc, datasetCategoryPath, nsList).setContent(this.datasetCategory);

	    XPathNodeUpdater.create(doc, ownerNamePath, nsList).setContent(this.ownerName);
	    XPathNodeUpdater.create(doc, contactPersonPath, nsList).setContent(this.contactPerson);
	    XPathNodeUpdater.create(doc, contactInstitutionPath, nsList).setContent(this.contactInstitution);
	    XPathNodeUpdater.create(doc, contactPhonePath, nsList).setContent(this.contactPhone);
	    XPathNodeUpdater.create(doc, contactEmailPath, nsList).setContent(this.contactEmail);
	    XPathNodeUpdater.create(doc, originalCreatorPath, nsList).setContent(this.originalCreator);

	    XPathNodeUpdater.create(doc, policyPath, nsList).addAttribute("PolicyId", this.pid);
	    XPathNodeUpdater.create(doc, policyPIDPath, nsList).setContent(this.pid);
	    XPathNodeUpdater.create(doc, policyMessagePath, nsList).setContent(policyMessage);

	    User u = null;
	    String sUserId = "";
	    String sUserName;
	    String sUserEmail;
	    FacesContext fc = FacesContext.getCurrentInstance();
	    ExternalContext externalContext = fc.getExternalContext();
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
	    sUserEmail = u.getEmailAddress();
	    System.out.println("User Id: " + sUserId);
	    System.out.println("User Name: " + sUserName);
	    System.out.println("User Email: " + sUserEmail);

	    try {
		Element node = XPathNodeUpdater.create(doc, policyUserIdPath, nsList).getNode();
		node = (Element) node.clone();
		node = node.setText(sUserId);
		XPathNodeUpdater.create(doc, policyUserIdStringBagPath, nsList).addChildItem(node);
	    } catch (org.jdom.JDOMException e) {
		System.out.println("JDOM Exception: " + e);
		return false;
	    }

	    System.out.println("wrote policies");

	    // PREMIS metadata
	    XPathNodeUpdater.create(doc, eventIdentifierPath, nsList).setContent("PREMIS:" + UUID.randomUUID().toString());

	    if (this.existingPID.equals("NEW")) {
		System.out.println("logging PREMIS event for new object");
		XPathNodeUpdater.create(doc, eventTypePath, nsList).setContent("http://id.loc.gov/vocabulary/preservationEvents/ingestion");
		XPathNodeUpdater.create(doc, eventDetailPath, nsList).setContent("ingest dataset to FISHNet repository");
		XPathNodeUpdater.create(doc, eventOutcomePath, nsList).setContent("success");
		XPathNodeUpdater.create(doc, eventOutcomeDetailPath, nsList).setContent("ingested as PID: " + this.pid);
	    } else {
		System.out.println("logging PREMIS event for modify object");
		XPathNodeUpdater.create(doc, eventTypePath, nsList).setContent("http://www.fba.org.uk/vocabulary/preservationEvents/modifyMetadata");
		XPathNodeUpdater.create(doc, eventDetailPath, nsList).setContent("modifying object metadata for " + this.pid);
		XPathNodeUpdater.create(doc, eventOutcomePath, nsList).setContent("success");
		XPathNodeUpdater.create(doc, eventOutcomeDetailPath, nsList).setContent(this.pid + "'s metadata modified");
	    }

	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.000Z'"); // e.g. 2011-04-12T14:23:44.681Z
	    XPathNodeUpdater.create(doc, eventDateTimePath, nsList).setContent(sdf.format(cal.getTime()));
	    XPathNodeUpdater.create(doc, agentPath, nsList).setContent(sUserId);
	    XPathNodeUpdater.create(doc, fullNamePath, nsList).setContent(sUserName);

	    XPathNodeUpdater.create(doc, linkingObjectIdentifierPath, nsList).setContent(this.pid);

	} catch (org.jdom.JDOMException e) {
	    System.out.println("JDOM Exception: " + e);
	    return false;
	}

	return true;
    }

    private boolean _ingestIntoFedora(Document doc) {
	FedoraCredentials credentials;
	try {
	    credentials = new FedoraCredentials(new URL(getApplicationBean().getFedoraURL()), getApplicationBean().getFedoraUsername(), getApplicationBean().getFedoraPassword());

	    CustomFedoraClient fc = new CustomFedoraClient(credentials);

	    FacesContext facesContext = FacesContext.getCurrentInstance();

	    // JSFPortletUtil - Liferay provided class from util-bridges.jar
	    ActionRequest request = (ActionRequest) JSFPortletUtil.getPortletRequest(facesContext);

	    javax.servlet.http.Cookie[] cookies = request.getCookies();

	    List<Cookie> lCookies = new ArrayList<Cookie>();

	    for (javax.servlet.http.Cookie c : cookies) {
		lCookies.add(new Cookie(c.getName(), c.getValue(), c.getPath(), c.getDomain(), c.getVersion()));
	    }

	    XMLOutputter outp2 = new XMLOutputter();

	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    try {
		outp2.output(doc.getRootElement(), out);
		//System.out.println(out.toString());
		IngestResponse response = ((CustomIngest) (new CustomIngest(this.pid)).content(out.toString()).
			format(this.fedoraIngestFormat).logMessage(this.fedoraLogMessage)).execute(fc, lCookies);
		String pid = response.getPid();
		this.pid = pid;
		System.out.println("Ingested PID: " + pid);
		return true;
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		System.out.println("Ingest IOException: ");
		e.printStackTrace();
		return false;
	    } catch (FedoraClientException e) {
		// TODO Auto-generated catch block
		System.out.println("Ingest FedoraClientException: ");
		e.printStackTrace();
		return false;
	    }
	} catch (MalformedURLException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	    return false;
	}

    }

    public void setDatasetCategory(String datasetCategory) {
	this.datasetCategory = datasetCategory;
    }

    public String getDatasetCategory() {
	return datasetCategory;
    }

    public ApplicationBean getApplicationBean() {
	return applicationBean;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
	this.applicationBean = applicationBean;
    }
}
