/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freshwaterlife.portlets.dataset.resulttypes;

import java.io.Serializable;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.util.bridges.jsf.common.JSFPortletUtil;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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
import javax.annotation.PostConstruct;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.freshwaterlife.fedora.client.CustomFedoraClient;
import org.freshwaterlife.fedora.client.CustomPurgeDatastream;
import org.freshwaterlife.fedora.client.CustomGetObjectXML;
import org.freshwaterlife.fedora.client.CustomModifyDatastream;
import org.freshwaterlife.util.XPathNodeUpdater;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.xml.sax.InputSource;
import javax.portlet.ActionRequest;
import javax.ws.rs.core.Cookie;
import org.freshwaterlife.portlets.dataset.ApplicationBean;

import org.freshwaterlife.portlets.dataset.Global;

/**
 * 
 * @author root
 */
public abstract class BasicResultDoc implements Serializable {

    private ApplicationBean applicationBean;
    private static final long serialVersionUID = 1L;
    // doctypes
    private String id; // maps to uid
    private String title; // maps to the name field and title field
    private int doctype; // use the static variables
    private ArrayList<String> keywords; // maps to tagentries
    private String forwardUrl; // from results if present, constructed in some
    // cases - e.g. images, from other fields
    private String summary;
    private String content;
    private String modified;
    protected String status;
    protected String modsTitle;
    protected String modsTitleEscaped;
    protected String statusImgURL;
    protected String lastModifiedDate;
    protected String pid;
    protected String uploaderScreenName;
    protected Log _log = LogFactoryUtil.getLog(BasicResultDoc.class);
    private String selectedReviewer;
    protected String solrURL;
    protected String fedoraURL;
    protected String fedoraUsername;
    protected String fedoraPassword;
    protected String premisTemplateFile;
    protected String foxmlTemplateFile;
    protected String keywordTemplateFile;
    protected String serverURL;
    public static final String PropertySolrServer = "default.fwl.solrserver";
    public static final String PropertyFedoraURL = "default.fwl.fedoraurl";
    public static final String PropertyFedoraUsername = "default.fwl.fedorausername";
    public static final String PropertyFedoraPassword = "default.fwl.fedorapassword";
    public static final String PropertyPremisTemplateFile = "default.fwl.premistemplatefile";
    public static final String PropertyFoxmlTemplateFile = "default.fwl.premisfoxmltemplatefile";
    public static final String PropertyKeywordTemplateFile = "default.fwl.keywordtemplatefile";
    public static final String PropertyServerURL = "default.fwl.serverurl";
    public static final String PropertyDoiMetadataXSLTFile = "default.fwl.doimetadataxsltfile";
    public static final String PropertyRestDOIEndpoint = "default.fwl.restdoiendpoint";
    public static final String PropertyRestMetadataEndpoint = "default.fwl.restmetadataendpoint";
    public static final String PropertyDOIusername = "default.fwl.doiusername";
    public static final String PropertyDOIpassword = "default.fwl.doipassword";
    public static final String PropertyDOIFBAPrefix = "default.fwl.doifbaprefix";
    public static final String PropertyDOIBLtestPrefix = "default.fwl.doibltestprefix";
    public static final String PropertyDOItestMode = "default.fwl.doitestmode";

    public BasicResultDoc() {

	this.doctype = Global.RED;
	this.title = "unknown";
	this.content = "unknown content";

	this.id = "placeholderId";

	try {
	    setSolrURL(PropsUtil.get(PropertySolrServer));
	    setFedoraURL(PropsUtil.get(PropertyFedoraURL));
	    setFedoraUsername(PropsUtil.get(PropertyFedoraUsername));
	    setFedoraPassword(PropsUtil.get(PropertyFedoraPassword));
	    setPremisTemplateFile(PropsUtil.get(PropertyPremisTemplateFile));
	    setFoxmlTemplateFile(PropsUtil.get(PropertyFoxmlTemplateFile));
	    setServerURL(PropsUtil.get(PropertyServerURL));
	    setKeywordTemplateFile(PropsUtil.get(PropertyKeywordTemplateFile));
	    /*
	    setDoiMetadataXSLTFile(PropsUtil.get(this.PropertyDoiMetadataXSLTFile));
	    setRestDOIEndpoint(PropsUtil.get(this.PropertyRestDOIEndpoint));
	    setRestMetadataEndpoint(PropsUtil.get(this.PropertyRestMetadataEndpoint));
	    setDOIusername(PropsUtil.get(this.PropertyDOIusername));
	    setDOIpassword(PropsUtil.get(this.PropertyDOIpassword));
	    setDOIFBAPrefix(PropsUtil.get(this.PropertyDOIFBAPrefix));
	    setDOIBLtestPrefix(PropsUtil.get(this.PropertyDOIBLtestPrefix));
	    if (PropsUtil.get(this.PropertyDOItestMode).matches("true")) {
	    setDOItestMode(true);
	    } else {
	    setDOItestMode(false);
	    }*/
	} catch (Exception e) {
	    setSolrURL("localhost");
	    setFedoraURL("localhost");
	    setFedoraUsername("");
	    setFedoraPassword("");
	    setPremisTemplateFile("/opt/liferay-portal-6.0.5/tomcat-6.0.26/conf/dataset-premis-template.xml");
	    setFoxmlTemplateFile("/opt/liferay-portal-6.0.5/tomcat-6.0.26/conf/dataset-object-foxml-template.xml");
	    setServerURL("localhost");
	    setKeywordTemplateFile("/opt/liferay-portal-6.0.5/tomcat-6.0.26/conf/dataset-keywords-template.xml");
	    /*
	    setDoiMetadataXSLTFile("/opt/liferay-portal-6.0.5/tomcat-6.0.26/conf/datacite-doi-metadata.xslt");
	    setRestDOIEndpoint("");
	    setRestMetadataEndpoint("");
	    setDOIusername("");
	    setDOIpassword("");
	    setDOIFBAPrefix("");
	    setDOIBLtestPrefix("");
	    setDOItestMode(true);*/

	    /*
	    setSolrURL(mysessionBean.getSolrURL());
	    setFedoraURL(mysessionBean.getFedoraURL());
	    setFedoraUsername(mysessionBean.getFedoraUsername());
	    setFedoraPassword(mysessionBean.getFedoraPassword());
	    setPremisTemplateFile(mysessionBean.getPremisTemplateFile());
	    setFoxmlTemplateFile(mysessionBean.getFoxmlTemplateFile());
	    setKeywordTemplateFile(mysessionBean.getKeywordTemplateFile());
	    setServerURL(mysessionBean.getServerURL());*/
	}
    }

    public BasicResultDoc(int doctype, String name, String content) {
	this.doctype = doctype;
	this.title = name;
	this.content = content;
	this.id = "placeholderId";

    }

    @PostConstruct
    public void init() {
	this.forwardUrl = getApplicationBean().getServerURL();

    }

    public void doWritePremisRecord(String strLinkingObjectIdentifier,
	    String strEventType,
	    String strEventDetail,
	    String strEventOutcome,
	    String strEventOutcomeDetail,
	    String strLinkingAgentIdentifier) {
	Map<String, Namespace> nsList = _buildNamespaceList();

	XMLOutputter outputter = new XMLOutputter();


	System.out.println("doWritePremisRecord - start");

	FacesContext facesContext = FacesContext.getCurrentInstance();

	// JSFPortletUtil - Liferay provided class from util-bridges.jar
	ActionRequest request = (ActionRequest) JSFPortletUtil.getPortletRequest(facesContext);

	// load template file
	FileInputStream fIn = null;
	try {
	    fIn = new FileInputStream(getApplicationBean().getFoxmlTemplateFile());
	} catch (FileNotFoundException e) {
	    System.out.println("Template file not found");
	}

	FedoraCredentials credentials;
	CustomFedoraClient fc = null;
	try {
	    credentials = new FedoraCredentials(new URL(getApplicationBean().getFedoraURL()),
		    getApplicationBean().getFedoraUsername(),
		    getApplicationBean().getFedoraPassword());
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
		fResponse = (new CustomGetObjectXML(strLinkingObjectIdentifier)).execute(fc, lCookies);

		if (fResponse.getStatus() == 200) {
		    sContent = fResponse.getEntity(String.class);

		    // parse sContent to get PREMIS data
		    org.jdom.input.SAXBuilder jdomParser = new org.jdom.input.SAXBuilder();
		    try {
			Document premisDoc = jdomParser.build((new InputSource(new ByteArrayInputStream(sContent.getBytes("utf-8")))));
			// Retrieve the existing PREMIS data.
			Element premis = XPathGetNode(premisDoc, Global.PREMISEventPath, nsList);
			// Add the new PREMIS event to the existing PREMIS data.
			premis.addContent(XPathGetNode(doc, Global.PREMISEventPath, nsList).detach());
			// Store the modified PREMIS data in a new version of the PREMIS datastream.
			System.out.println("logging PREMIS event");
			ByteArrayOutputStream PREMIS = new ByteArrayOutputStream();
			outputter.output(premis, PREMIS);
			try {
			    ((CustomModifyDatastream) (new CustomModifyDatastream(strLinkingObjectIdentifier, "PREMIS")).content(PREMIS.toString())).execute(fc, lCookies);
			    // Remove the previous version of the PREMIS datastream.
			    ((CustomPurgeDatastream) (new CustomPurgeDatastream(strLinkingObjectIdentifier, "PREMIS")).endDT(XPathGetNode(premisDoc, Global.PREMISDatastreamVersionPath, nsList).getAttributeValue("CREATED"))).execute(fc, lCookies);
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
		fInPremis = new FileInputStream(getApplicationBean().getPremisTemplateFile());
	    } catch (FileNotFoundException e) {
		System.out.println("Template file not found");
	    }

	    BufferedReader premisReader = new BufferedReader(new InputStreamReader(fInPremis));
	    doc = jdomBuilder.build(premisReader);

	    // PREMIS metadata
	    XPathNodeUpdater.create(doc, Global.eventIdentifierPath, nsList).setContent("PREMIS:" + UUID.randomUUID().toString());

	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(Global.strSimpleDateFormat); // e.g. 2011-04-12T14:23:44.681Z
	    XPathNodeUpdater.create(doc, Global.eventDateTimePath, nsList).setContent(sdf.format(cal.getTime()));

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

	    XPathNodeUpdater.create(doc, Global.agentPath, nsList).setContent(sUserId);
	    XPathNodeUpdater.create(doc, Global.fullNamePath, nsList).setContent(sUserName);

	    outputter = new XMLOutputter();
	    //ByteArrayOutputStream PREMISEvent = new ByteArrayOutputStream();

	    String pidToUse = strLinkingObjectIdentifier;
	    System.out.println("modifying existing object.");

	    // Add a PREMIS event
	    XPathNodeUpdater.create(doc, Global.eventTypePath, nsList).setContent(strEventType);
	    XPathNodeUpdater.create(doc, Global.eventDetailPath, nsList).setContent(strEventDetail);
	    XPathNodeUpdater.create(doc, Global.eventOutcomePath, nsList).setContent(strEventOutcome);
	    XPathNodeUpdater.create(doc, Global.eventOutcomeDetailPath, nsList).setContent(strEventOutcomeDetail);
	    XPathNodeUpdater.create(doc, Global.linkingAgentIdentifierPath, nsList).setContent(strLinkingAgentIdentifier);
	    XPathNodeUpdater.create(doc, Global.linkingObjectIdentifierPath, nsList).setContent(strLinkingObjectIdentifier);

	    System.out.println("logging PREMIS event");

	    FedoraResponse fResponse = null;

	    String sContent = "";
	    // Retrieve the existing fedora object.
	    fResponse = (new CustomGetObjectXML(strLinkingObjectIdentifier)).execute(fc, lCookies);

	    if (fResponse.getStatus() == 200) {
		sContent = fResponse.getEntity(String.class);

		// parse sContent to get PREMIS data
		org.jdom.input.SAXBuilder jdomParser = new org.jdom.input.SAXBuilder();
		try {
		    Document premisDoc = jdomParser.build((new InputSource(new ByteArrayInputStream(sContent.getBytes("utf-8")))));
		    // Retrieve the existing PREMIS data.
		    Element premis = XPathGetNode(premisDoc, Global.PREMISEventPath, nsList);
		    // Add the new PREMIS event to the existing PREMIS data.
		    premis.addContent(XPathGetNode(doc, Global.PREMISPath, nsList).detach());
		    // Store the modified PREMIS data in a new version of the PREMIS datastream.
		    ByteArrayOutputStream PREMIS = new ByteArrayOutputStream();
		    outputter.output(premis, PREMIS);
		    try {
			((CustomModifyDatastream) (new CustomModifyDatastream(pidToUse, "PREMIS")).content(PREMIS.toString())).execute(fc, lCookies);
			// Remove the previous version of the PREMIS datastream.
			((CustomPurgeDatastream) (new CustomPurgeDatastream(pidToUse, "PREMIS")).endDT(XPathGetNode(premisDoc, Global.PREMISDatastreamVersionPath, nsList).getAttributeValue("CREATED"))).execute(fc, lCookies);
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

    protected Map<String, Namespace> _buildNamespaceList() {
	Map<String, Namespace> nsList = new HashMap<String, Namespace>();
	nsList.put("mods", Namespace.getNamespace("mods", "http://www.loc.gov/mods/v3"));
	nsList.put("premis", Namespace.getNamespace("premis", "http://www.loc.gov/standards/premis"));

	return nsList;
    }

    protected List<Cookie> getCookies() {
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

    /**
     * @return the title
     */
    public String getTitle() {
	String strRetString = title;
	strRetString = replaceItalicsTags(strRetString);
	return strRetString;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
	this.title = title;
    }

    public int getDoctype() {
	return doctype;
    }

    public void setDoctype(int doctype) {
	this.doctype = doctype;
    }

    /**
     * @return the keywords
     */
    public ArrayList<String> getKeywords() {
	return keywords;
    }

    /**
     * @param keywords
     *            the keywords to set
     */
    public void setKeywords(ArrayList<String> keywords) {

	// do a bit of clean up
	Iterator i = keywords.iterator();
	ArrayList<String> tags = new ArrayList<String>();
	while (i.hasNext()) {
	    String s = (String) i.next();
	    s = s.trim();
	    s = s.toUpperCase();
	    tags.add(s);
	}

	this.keywords = tags;
    }

    /**
     * @return the content
     */
    public String getContent() {
	return content;
    }

    /**
     * @param content
     *            the content to set
     */
    public void setContent(String content) {
	this.content = content;
    }

    public String getContentSubstring() {

	int iCharsRequired = 200;
	String retString = content;

	retString = replaceItalicsTags(retString);

	if (retString.length() > iCharsRequired) {
	    retString = retString.substring(0, iCharsRequired);

	    // ensure that the italics close tag hasn't been removed in
	    // truncation
	    if (countIndexOf(retString, "<i>") > countIndexOf(retString, "</i>")) {
		retString += "</i>...";
	    } else {
		retString += "...";
	    }
	}
	return retString;
    }

    private String replaceItalicsTags(String strBeforeText) {
	String strAfterText = strBeforeText;

	if (strBeforeText.contains("[i]")) // handle the customised italics tags
	{
	    strAfterText = strBeforeText.replace("[i]", "<i>");
	    strAfterText = strAfterText.replace("[/i]", "</i>");
	}

	return strAfterText;
    }

    private int countIndexOf(String content, String search) {
	int ctr = -1;
	int total = 0;
	while (true) {
	    if (ctr == -1) {
		ctr = content.indexOf(search);
	    } else {
		ctr = content.indexOf(search, ctr);
	    }

	    if (ctr == -1) {
		break;
	    } else {
		total++;
		ctr += search.length();
	    }
	}
	return total;
    }

    /***********************/
    /* Getters and Setters */
    /***********************/
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

    public String getSelectedReviewer() {
	return selectedReviewer;
    }

    public void setSelectedReviewer(String selectedReviewer) {
	this.selectedReviewer = selectedReviewer;
    }

    public ApplicationBean getApplicationBean() {
	return applicationBean;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
	this.applicationBean = applicationBean;
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

    public String getSolrURL() {
	return this.solrURL;
    }

    public void setSolrURL(String solrURL) {
	this.solrURL = solrURL;
    }
}
