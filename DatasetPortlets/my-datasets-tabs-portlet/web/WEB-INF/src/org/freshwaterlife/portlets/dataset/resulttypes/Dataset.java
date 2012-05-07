package org.freshwaterlife.portlets.dataset.resulttypes;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.jsf.common.JSFPortletUtil;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.request.GetDatastream;
import com.yourmediashelf.fedora.client.request.GetDatastreamDissemination;
import com.yourmediashelf.fedora.client.response.DatastreamProfileResponse;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import com.yourmediashelf.fedora.generated.management.DatastreamProfile;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import javax.faces.context.FacesContext;
import javax.portlet.ActionRequest;
import javax.ws.rs.core.Cookie;

import org.freshwaterlife.fedora.client.CustomFedoraClient;
import org.freshwaterlife.fedora.client.CustomGetDatastreamDissemination;
import org.freshwaterlife.fedora.client.CustomGetObjectXML;
import org.freshwaterlife.fedora.client.CustomPurgeDatastream;
import org.freshwaterlife.fedora.client.CustomPurgeObject;
import org.freshwaterlife.portlets.dataset.Global;
import org.freshwaterlife.util.XPathNodeUpdater;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;

import java.util.logging.Logger;
import javax.faces.context.ExternalContext;

import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletResponse;
import net.sf.saxon.functions.Substring;
import org.freshwaterlife.fedora.client.CustomModifyDatastream;
import org.jdom.input.SAXBuilder;

import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;

/**
 * 
 * @author sfox
 */
public class Dataset extends BasicResultDoc implements Serializable {

    private static final long serialVersionUID = 1L;
    private String modsAbstract;
    private String modsSubjectTopic;
    private String submitText;
    private String datasetArticleURL;
    private String category;
    private String outcome;
    private String reviewerRoleterm;
    private boolean isDatasetOwnedByUser;
    protected String uploader;
    protected long uploaderUserId;
    private String forwardUrl; //from results if present, constructed in some cases - e.g. images, from other fields
    private String summary;
    private Long groupId;
    private String modified;

    public Dataset() {
	super();
    }

    public Dataset(HashMap values) {

	if (values.containsKey("mods.datasetCategory")) {
	    setCategory((String) values.get("mods.datasetCategory"));
	}
	if (values.containsKey("mods.datasetOutcome")) {
	    setOutcome((String) values.get("mods.datasetOutcome"));
	}

	setSubmitText("Apply for Digital Object Identifier (Publish the Dataset)");


    }

    public String doDelete() {

	// get PID and do a purge + add note of 'User deletion on 27/01/2011'
	String datasetPID = this.getPid();
	// Gets the HttpServletRequest from the PortletServletRequest
	javax.servlet.http.HttpServletRequest request = (javax.servlet.http.HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("com.liferay.portal.kernel.servlet.PortletServletRequest"));

	FedoraCredentials credentials;
	CustomFedoraClient fc = null;
	try {
	    credentials = new FedoraCredentials(new URL(getFedoraURL()),
		    getFedoraUsername(),
		    getFedoraPassword());
	    fc = new CustomFedoraClient(credentials);

	    javax.servlet.http.Cookie[] cookies = request.getCookies();
	    List<Cookie> lCookies = new ArrayList<Cookie>();
	    for (javax.servlet.http.Cookie c : cookies) {
		lCookies.add(new Cookie(c.getName(), c.getValue(), c.getPath(),
			c.getDomain(), c.getVersion()));
	    }

	    // purge the fedora object
	    ((CustomPurgeObject) (new CustomPurgeObject(datasetPID))).execute(
		    fc, lCookies);

	} catch (MalformedURLException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	} catch (FedoraClientException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	System.out.println("Object: " + datasetPID + " has been deleted");

	return "submit";
    }

    public String doDownloadDataset() {
	FedoraCredentials credentials;
	CustomFedoraClient fedora = null;
	FedoraResponse fResponse = null;
	String datasetPID = this.getPid();
	List<Cookie> lCookies = getCookies();
	File file = null;
	//byte[] bytes = null;
	//HttpServletResponse servletResponse;
	PortletResponse portletResponse;
	try {
	    FacesContext facesContext = FacesContext.getCurrentInstance();
	    ExternalContext externalContext = facesContext.getExternalContext();
	    portletResponse = (PortletResponse) externalContext.getResponse();
	    //servletResponse = PortalUtil.getHttpServletResponse(portletResponse);

	    //createTempFile = fFileToReturn.createTempFile("OriginalContent", "xls");
	    credentials = new FedoraCredentials(new URL(getFedoraURL()),getFedoraUsername(),getFedoraPassword());
	    fedora = new CustomFedoraClient(credentials);
	    CustomGetDatastreamDissemination customGetDataDissem = (new CustomGetDatastreamDissemination(datasetPID, "originalContent"));
	    fResponse = customGetDataDissem.execute(fedora, lCookies);


	    DatastreamProfileResponse dspResponse;
	    dspResponse = new GetDatastream(datasetPID, "originalContent").format("xml").execute(fedora);
	    DatastreamProfile profile = dspResponse.getDatastreamProfile();

	    String strDsLabel = profile.getDsLabel();
	    String strDsMime = profile.getDsMIME();

	    if (strDsMime.isEmpty())
	    {
		strDsMime = "application/xls";
	    }

	    if (strDsLabel.isEmpty())
	    {
		strDsLabel = "original.xls";
	    }
	    if (fResponse.getStatus() == 200) {
		file = fResponse.getEntity(File.class);

		//servletResponse.setContentType("application/x-download");
//TODO: remove the hardcoded filename from code below
		//servletResponse.setHeader("content-disposition", "attachment;name=\"original.xls\"; filename=\"original.xls\"");
		//servletResponse.setContentLength((int) file.length());
		FacesContext context = FacesContext.getCurrentInstance();
		externalContext = context.getExternalContext();
		PortletSession psession = (PortletSession) externalContext.getSession(false);

		DataInputStream in = new DataInputStream(new FileInputStream(file));
		byte[] bytes = new byte[(int) file.length()];
		in.readFully(bytes);
		in.close();

		psession.setAttribute("DownloadData", bytes, PortletSession.APPLICATION_SCOPE);
		psession.setAttribute("DownloadContentType", strDsMime, PortletSession.APPLICATION_SCOPE);
		psession.setAttribute("DownloadFileName", strDsLabel, PortletSession.APPLICATION_SCOPE);

		//Invoke the servlet to 'give' the file to the browser
		externalContext.redirect(externalContext.getRequestContextPath() + "/download");

		//context.responseComplete();
	    }
	} catch (MalformedURLException ex) {
	    Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
	} catch (IOException ex) {
	    Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
	} catch (FedoraClientException ex) {
	    Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);

	}
	return "home";
    }

    public String getCurrentOutcomeFromXML() {
	FedoraCredentials credentials;
	CustomFedoraClient fedora = null;
	FedoraResponse fResponse = null;

	String datasetPID = this.getPid();

	outcome = null;
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

		System.out.println("getCurrentOutcomeFromXML - start parsing descMetadata");

		org.jdom.input.SAXBuilder jdomParser = new org.jdom.input.SAXBuilder();
		try {
		    Document doc = jdomParser.build(new InputSource(new ByteArrayInputStream(sContent.getBytes("utf-8"))));
		    outcome = XPathGetNode(doc, Global.datasetOutcomePath, nsList).getText();
		} catch (JDOMException ex) {
		    Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
		    Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
		}
	    } else {
		System.out.println("getCurrentOutcomeFromXML FResponse Error");
	    }

	} catch (MalformedURLException ex) {
	    Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
	} catch (FedoraClientException ex) {
	    Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
	}
	return outcome;
    }

    public void writeOutcomeToXML(String new_outcome) {
	FedoraCredentials credentials;
	CustomFedoraClient fedora = null;
	FedoraResponse fResponse = null;

	XMLOutputter outputter = new XMLOutputter();
	String sContent = "";
	Map<String, Namespace> nsList = _buildNamespaceList();
	String datasetPID = this.getPid();
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

		XPathGetNode(doc, Global.datasetOutcomePath, nsList).setText(new_outcome);

		ByteArrayOutputStream descMetadata = new ByteArrayOutputStream();

		outputter.output(doc, descMetadata);
		((CustomModifyDatastream) (new CustomModifyDatastream(datasetPID, "descMetadata")).content(descMetadata.toString())).execute(fedora, lCookies);
	    } else {
		System.out.println("writeOutcomeToXML FResponse Error");
	    }

	} catch (MalformedURLException ex) {
	    Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
	} catch (JDOMException ex) {
	    Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
	} catch (IOException ex) {
	    Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
	} catch (FedoraClientException ex) {
	    Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /***********************/
    /* Getters and Setters */
    /***********************/
    public String getModsAbstract() {
	return modsAbstract;
    }

    public void setModsAbstract(String modsAbstract) {
	this.modsAbstract = modsAbstract;
    }

    public String getModsSubjectTopic() {
	return modsSubjectTopic;
    }

    public void setModsSubjectTopic(String modsSubjectTopic) {
	this.modsSubjectTopic = modsSubjectTopic;
    }

    public String getDatasetArticleURL() {
	return datasetArticleURL;
    }

    public void setDatasetArticleURL(String journalArticleURL) {
	this.datasetArticleURL = journalArticleURL;
    }

    public void setOutcome(String outcome) {
	this.outcome = outcome;
    }

    public String getOutcome() {
	return outcome;
    }

    public void setCategory(String category) {
	this.category = category;
    }

    public String getCategory() {
	return category;
    }

    public void setSubmitText(String submitText) {
	this.submitText = submitText;
    }

    public String getSubmitText() {
	return submitText;
    }

    public String getReviewerRoleterm() {
	return reviewerRoleterm;
    }

    public void setReviewerRoleterm(String reviewerRoleterm) {
	this.reviewerRoleterm = reviewerRoleterm;
    }

    public boolean isIsDatasetOwnedByUser() {

	User uLoggedInUser = getUser();
	long lLoggedInUserId = uLoggedInUser.getUserId();

	long lULoader = this.getUploaderUserId();

	if (lULoader == lLoggedInUserId) {
	    isDatasetOwnedByUser = true;
	} else {
	    isDatasetOwnedByUser = false;
	}
	return isDatasetOwnedByUser;
    }

    public void setIsDatasetOwnedByUser(boolean isDatasetOwnedByUser) {
	this.isDatasetOwnedByUser = isDatasetOwnedByUser;
    }

    public String getUploader() {
	return uploader;
    }

    public void setUploader(String uploader) {
	this.uploader = uploader;
    }

    public long getUploaderUserId() {
	return uploaderUserId;
    }

    public void setUploaderUserId(long uploaderUserId) {
	this.uploaderUserId = uploaderUserId;
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
	    credentials = new FedoraCredentials(new URL(getFedoraURL()), getFedoraUsername(), getFedoraPassword());
	    fc = new CustomFedoraClient(credentials);
	} catch (MalformedURLException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	}
	BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));
	SAXBuilder jdomBuilder = new SAXBuilder();
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
		    SAXBuilder jdomParser = new SAXBuilder();
		    try {
			Document premisDoc = jdomParser.build(new InputSource(new ByteArrayInputStream(sContent.getBytes("utf-8"))));
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
	} catch (JDOMException e) {
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
		SAXBuilder jdomParser = new SAXBuilder();
		try {
		    Document premisDoc = jdomParser.build(new InputSource(new ByteArrayInputStream(sContent.getBytes("utf-8"))));
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

    /**
     *
     */
    public String getForwardUrl() {
	return forwardUrl;
    }

    public void setForwardUrl(String forwardUrl) {
	this.forwardUrl = forwardUrl;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getSummary() {
	return summary;
    }

    public void setSummary(String summary) {
	this.summary = summary;
    }

    public String getModified() {
	return modified;
    }

    public void setModified(String modified) {
	this.modified = modified;
    }

    public String getModsTitle() {
	return modsTitle;
    }

    public void setModsTitle(String modsTitle) {
	this.modsTitle = modsTitle;
    }

    public void setModsTitleEscaped(String modsTitleEscaped) {
	this.modsTitleEscaped = modsTitleEscaped;
    }

    public String getModsTitleEscaped() {
	return modsTitleEscaped;
    }

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    public void setStatusImgURL(String statusImgURL) {
	this.statusImgURL = statusImgURL;
    }

    public String getStatusImgURL() {
	return statusImgURL;
    }

    public String getLastModifiedDate() {
	return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
	this.lastModifiedDate = lastModifiedDate;
    }

    public String getPid() {
	return pid;
    }

    public void setPid(String pid) {
	this.pid = pid;
    }

    public void setUploaderScreenName(String uploaderScreenName) {
	this.uploaderScreenName = uploaderScreenName;
    }

    public String getUploaderScreenName() {
	return uploaderScreenName;
    }
}
