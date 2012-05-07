package org.freshwaterlife.portlets.dataset.resulttypes;

import java.net.ProtocolException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.freshwaterlife.portlets.dataset.Global;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.xml.sax.InputSource;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.ActionRequest;
import javax.ws.rs.core.Cookie;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import com.liferay.util.bridges.jsf.common.JSFPortletUtil;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import org.freshwaterlife.fedora.client.CustomFedoraClient;
import org.freshwaterlife.fedora.client.CustomGetDatastreamDissemination;
import org.freshwaterlife.fedora.client.CustomGetObjectXML;
import org.freshwaterlife.fedora.client.CustomModifyDatastream;
import org.freshwaterlife.fedora.client.CustomPurgeDatastream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import javax.net.ssl.HttpsURLConnection;
import org.freshwaterlife.util.XPathNodeUpdater;

import sun.misc.BASE64Encoder;

/**
 * 
 * @author sfox
 */
public class RedOrange extends Dataset implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String groupId;
    private String articleId;
    private boolean isVisible;
    private String keywords;
    private String modsRightsCreator;
    private String modsRightsPhone;
    private String modsRightsInstitution;
    private String modsRightsContact;
    private String modsRightsOwner;
    private String ModsRightsEmail;
    private String modsSubjectTopic;
    private String modsAbstract;
    private String entryClassName;
    private String userId;
    private String rootEntryClassPK;
    private String submitText;
    private String datasetdisplayURL;
    private String uploader;
    private String assignedReviewer;
    private String reviewerName;
    private String reviewerUserId;
    private int doiReturnCode;
    private String doiReturnString;

    public RedOrange() {
	super();
    }

    public RedOrange(HashMap values) {

	super(values);

	this.setDoctype(Global.REDORANGE);

	if (values.containsKey("uid")) {
	    this.setId((String) values.get("uid"));
	}

	if (values.containsKey("mods.title")) {
	    String str = (String) values.get("mods.title");
	    this.setModsTitle(str);

	    //Escape the single and double quotes so that confirm dialog won't fail
	    str = str.replaceAll("'", "\\\\'");
	    str = str.replaceAll("\"", "\\\\\"");

	    this.setModsTitleEscaped(str);

	}

	if (values.containsKey("lastModifiedDate")) {
	    String date = (String) values.get("lastModifiedDate");
	    String day = date.substring(0, 10);
	    String time = date.substring(11, 23);
	    String formattedDate = day + "</br>" + time;
	    this.setLastModifiedDate(formattedDate);
	}

	if (values.containsKey("keywords")) {
	    this.setModKeywords((String) values.get("keywords"));
	}

	if (values.containsKey("mods.rights.creator")) {
	    this.setModsRightsCreator((String) values.get("mods.rights.creator"));
	}

	if (values.containsKey("PID")) {
	    this.setPid((String) values.get("PID"));
	}

	if (values.containsKey("mods.rights.phone")) {
	    this.setModsRightsPhone((String) values.get("mods.rights.phone"));
	}

	if (values.containsKey("mods.rights.institution")) {
	    this.setModsRightsInstitution((String) values.get("mods.rights.institution"));
	}

	if (values.containsKey("mods.rights.contact")) {
	    this.setModsRightsContact((String) values.get("mods.rights.contact"));
	}

	if (values.containsKey("mods.rights.owner")) {
	    this.setModsRightsOwner((String) values.get("mods.rights.owner"));
	}

	if (values.containsKey("mods.rights.email")) {
	    this.setModsRightsEmail((String) values.get("mods.rights.email"));
	}

	if (values.containsKey("mods.subject.topic")) {
	    this.setModsSubjectTopic((String) values.get("mods.subject.topic"));
	}

	if (values.containsKey("mods.abstract")) {
	    this.setModsAbstract((String) values.get("mods.abstract"));
	}

	if (values.containsKey("mods.reviewer.name")) {
	    this.setReviewerName((String) values.get("mods.reviewer.name"));
	}

	if (values.containsKey("mods.reviewer.userid")) {
	    this.setReviewerUserId((String) values.get("mods.reviewer.userid"));
	}

	if (values.containsKey("roleIds")) {
	    // parse to remove administrator
	    String[] roleIds = ((String) values.get("roleIds")).split(",");
	    String roleId = "";
	    for (String thisRole : roleIds) {
		if (!thisRole.equals("administrator")) {
		    roleId = thisRole.replaceAll("[^0-9]", "");
		}
	    }
	    User user;
	    try {
		user = UserLocalServiceUtil.getUserById(Integer.parseInt(roleId));
		this.setUploader(user.getFullName());
		this.setUploaderScreenName(user.getScreenName());

	    } catch (NumberFormatException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (PortalException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (SystemException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}

	this.setStatus("Dataset is in the Red-Orange Category");
	this.setStatusImgURL("/resources/images/red-orange.png");

	this.setDatasetdisplayURL(formatDatasetDisplayURL());

	this.setDoiReturnCode(0);
	this.setDoiReturnString("No DOI Return String");

    }

    public String doAssignReviewer() {

	System.out.println("doAssignReviewer - start");

	String sUserName = "";
	String sUserId = "";
	User u = null;

	sUserId = getSelectedReviewer();

	try {
	    u = UserLocalServiceUtil.getUserById(Long.parseLong(sUserId));
	} catch (PortalException ex) {
	    Logger.getLogger(RedOrange.class.getName()).log(Level.SEVERE, null, ex);
	} catch (SystemException ex) {
	    Logger.getLogger(RedOrange.class.getName()).log(Level.SEVERE, null, ex);
	}
	sUserName = u.getFullName();

	String datasetReviewerNamePath = "/mods:mods/mods:name/mods:namePart";
	String datasetReviewerIdPath = "/mods:mods/mods:name/mods:description";

	String datasetPID = this.getPid();
	Map<String, Namespace> nsList = _buildNamespaceList();
	FacesContext facesContext = FacesContext.getCurrentInstance();
	ActionRequest request = (ActionRequest) JSFPortletUtil.getPortletRequest(facesContext);
	FedoraCredentials credentials;
	CustomFedoraClient fedora = null;
	FedoraResponse fResponse = null;
	try {
    	    credentials = new FedoraCredentials(new URL(getApplicationBean().getFedoraURL()),
							getApplicationBean().getFedoraUsername(),
							getApplicationBean().getFedoraPassword());
	    fedora = new CustomFedoraClient(credentials);
	    javax.servlet.http.Cookie[] cookies = request.getCookies();
	    List<Cookie> lCookies = new ArrayList<Cookie>();
	    for (javax.servlet.http.Cookie c : cookies) {
		lCookies.add(new Cookie(c.getName(), c.getValue(), c.getPath(), c.getDomain(), c.getVersion()));
	    }
	    String sContent = "";
	    System.out.println("doAssignReviewer - start parsing descMetadata");
	    fResponse = (new CustomGetDatastreamDissemination(datasetPID, "descMetadata")).execute(fedora, lCookies);
	    if (fResponse.getStatus() == 200) {
		sContent = fResponse.getEntity(String.class);
		org.jdom.input.SAXBuilder jdomParser = new org.jdom.input.SAXBuilder();
		Document doc = jdomParser.build((new InputSource(new ByteArrayInputStream(sContent.getBytes("utf-8")))));

		XMLOutputter outputter = new XMLOutputter();

		this.reviewerName = XPathGetNode(doc, datasetReviewerNamePath, nsList).getText();
		this.reviewerUserId = XPathGetNode(doc, datasetReviewerIdPath, nsList).getText();
		System.out.println("Review Name: " + reviewerName);

		XPathGetNode(doc, datasetReviewerNamePath, nsList).setText(sUserName);
		XPathGetNode(doc, datasetReviewerIdPath, nsList).setText(sUserId);
		//Template reviewer name and user Id has now been populated.

		ByteArrayOutputStream descMetadata = new ByteArrayOutputStream();

		outputter.output(doc, descMetadata);
		((CustomModifyDatastream) (new CustomModifyDatastream(
			datasetPID, "descMetadata")).content(descMetadata.toString())).execute(fedora, lCookies);

		System.out.println("doAssignReviewer - logging reviewer event for review submission");
	    } else {
		System.out.println("doAssignReviewer - Error");
	    }
	} catch (FedoraClientException e) {
	    System.out.println("Fedora Error: " + e.getMessage());
	} catch (JDOMException e) {
	    System.out.println("JDOM Error: " + e.getMessage());
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (Exception e) {
	    System.out.println("Error: " + e.getMessage());
	}

	System.out.println("Object: " + datasetPID + " has been assigned reviewer: " + sUserName);

	return "assignreviewer";
    }

    public boolean hasAReviewer() {
	if (reviewerName.equals(Global.NOREVIEWERTEXT)) {
	    return false;
	} else {
	    return true;
	}
    }

    public String doRequestDOI() {

	InputStreamReader isr = null;
	int responseCode = 0;
	BufferedReader br = null;
	StringWriter sw = null;
	char[] buffer = null;
	int n = 0;
	Document doc = null;
	Element doiLinkAgentId = null;
	Element editionLinkAgentId = null;
	User user = null;
	User u = null;

	System.out.println("doRequestDOI - start");
	System.out.println("PID: " + this.pid);
	// TODO - Generate a DOI
	// TODO - Needs error checking for empty string
	int colonPos = this.pid.indexOf(':');
	String pidSuffix = this.pid.substring(colonPos + 1);
	String doi = getApplicationBean().getDOIBLtestPrefix() + "/" + pidSuffix;
	//String url = "[url]";
	String url = getApplicationBean().getServerURL() + "/web/guest/dataset-display/-/dataset-display/" + this.pid;
	//String url = "http://new.freshwaterlife.org/web/guest/dataset-display/-/dataset-display/0000a1a0-a1a5-42d7-a3ff-a4fb6c8c030d";
	// TODO - Generate edition based on previous DOI assignment (increment by 0.1)
	String edition = "";
	String currentDate = "";
	Element premis = null;
	String dtPremis = "";
	FedoraResponse fResponse = null;
	CustomFedoraClient fedora;
	FedoraCredentials credentials;
	String sContent = "";
	Calendar cal = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat(Global.strSimpleDateFormat); // e.g. 2011-04-12T14:23:44.681Z
	currentDate = sdf.format(cal.getTime());
	// Generate DOI Metadata in DataCite format
	ByteArrayOutputStream doiMetadata = new ByteArrayOutputStream();
	Map<String, Namespace> nsList = _buildNamespaceList();
	List<Cookie> lCookies = getCookies();
	try {
    	    credentials = new FedoraCredentials(new URL(getApplicationBean().getFedoraURL()),
							getApplicationBean().getFedoraUsername(),
							getApplicationBean().getFedoraPassword());
	} catch (MalformedURLException ex) {
	    Logger.getLogger(RedOrange.class.getName()).log(Level.SEVERE, null, ex);
	    return "doifailed";
	}
	fedora = new CustomFedoraClient(credentials);
	try {
	    fResponse = (new CustomGetObjectXML(this.getPid())).execute(fedora, lCookies);
	} catch (FedoraClientException ex) {
	    Logger.getLogger(RedOrange.class.getName()).log(Level.SEVERE, null, ex);
	    return "doifailed";
	}
	if (fResponse.getStatus() == 200) {
	    System.out.println("Response 200");
	    sContent = fResponse.getEntity(String.class);
	    System.out.println("Got Content");
	    // parse sContent to fill bean data
	    org.jdom.input.SAXBuilder jdomParser = new org.jdom.input.SAXBuilder();
	    System.out.println("doRequestDOI - start parsing xml");
	    doc = null;
	    try {
		doc = jdomParser.build(new InputSource(new ByteArrayInputStream(sContent.getBytes("utf-8"))));
	    } catch (JDOMException ex) {
		Logger.getLogger(RedOrange.class.getName()).log(Level.SEVERE, null, ex);
		return "doifailed";
	    } catch (IOException ex) {
		Logger.getLogger(RedOrange.class.getName()).log(Level.SEVERE, null, ex);
		return "doifailed";
	    }

	    System.out.println("doRequestDOI - parsed xml");
	    try {
		// Retrieve the existing PREMIS data - used later.
		premis = XPathGetNode(doc, Global.PREMISEventPath, nsList);
		dtPremis = XPathGetNode(doc, Global.PREMISDatastreamVersionPath, nsList).getAttributeValue("CREATED");
	    } catch (JDOMException ex) {
		Logger.getLogger(RedOrange.class.getName()).log(Level.SEVERE, null, ex);
		return "doifailed";
	    }
	    try {
		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		TransformerFactory e2 = TransformerFactory.newInstance();
		Templates templates = e2.newTemplates(new StreamSource(getApplicationBean().getDoiMetadataXSLTFile()));
		Transformer transformer = templates.newTransformer();
		//Document tempDoc = new Document();
		//tempDoc.setRootElement(item);
		JDOMSource source = new JDOMSource(doc.getRootElement());
		JDOMResult result = new JDOMResult();
		transformer.transform(source, result);
		//System.out.println(result.getDocument().toString());
		//System.out.println(((Element)result.getDocument().getRootElement()).getName());
		if (result.getDocument().hasRootElement()) {
		    XMLOutputter outputter = new XMLOutputter();
		    outputter.output(result.getDocument().getRootElement(), doiMetadata);
		}
	    } catch (Exception ex) {
		System.out.println("error: " + ex.getMessage());
		return "doifailed";
	    }
	}
	String metadata = "";
	metadata = doiMetadata.toString();
	if (metadata.equals("")) {
	    System.out.println("Could not generate DOI Metadata.");
	    return "doifailed";
	}

	System.out.println(metadata);
	// TODO - Call DOI APIs to send required metadata and MINT a DOI
	//Firstly send request to mint a doi
	String restMintDOIEndpoint;
	try {
	    restMintDOIEndpoint = getApplicationBean().getRestDOIEndpoint() + "?doi=" + URLEncoder.encode(doi, "UTF-8");
	    restMintDOIEndpoint += (url != null && url.trim().length() > 0) ? "&url=" + URLEncoder.encode(url, "UTF-8") : "";
	} catch (UnsupportedEncodingException ex) {
	    Logger.getLogger(RedOrange.class.getName()).log(Level.SEVERE, null, ex);
	    return "doifailed";
	}

	//restMintDOIEndpoint += "&testMode=" + Global.DOItestMode;
	URL uMintDOI;
	try {
	    uMintDOI = new URL(restMintDOIEndpoint);
	} catch (MalformedURLException ex) {
	    Logger.getLogger(RedOrange.class.getName()).log(Level.SEVERE, null, ex);
	    return "doifailed";
	}
	HttpsURLConnection conn;
	try {
	    conn = (HttpsURLConnection) uMintDOI.openConnection();
	} catch (IOException ex) {
	    Logger.getLogger(RedOrange.class.getName()).log(Level.SEVERE, null, ex);
	    return "doifailed";
	}
	try {
	    conn.setRequestMethod("POST");
	} catch (ProtocolException ex) {
	    Logger.getLogger(RedOrange.class.getName()).log(Level.SEVERE, null, ex);
	    return "doifailed";
	}
	conn.setUseCaches(false);
	conn.setDoInput(true);
	conn.setDoOutput(true);
	conn.setRequestProperty("Accept", "application/xml");
	conn.setRequestProperty("Content-Type", "application/xml;charset=UTF-8");
	BASE64Encoder enc = new sun.misc.BASE64Encoder();
	String encAuth = enc.encode((getApplicationBean().getDOIusername() + ":" + getApplicationBean().getDOIpassword()).getBytes());
	conn.setRequestProperty("Authorization", "Basic " + encAuth);
	try {
	    conn.connect();
	    responseCode = conn.getResponseCode();
	    if (responseCode >= 400) {
		isr = new InputStreamReader(conn.getErrorStream());
	    } else {
		isr = new InputStreamReader(conn.getInputStream());
	    }
	    br = new BufferedReader(isr);
	    sw = new StringWriter();
	    buffer = new char[1024];
	    while ((n = br.read(buffer)) != -1) {
		sw.write(buffer, 0, n);
	    }
	    System.out.println("Return Code:" + responseCode);
	    System.out.println("Return Body:" + sw.toString());
	    this.setDoiReturnCode(responseCode);
	    this.setDoiReturnString(sw.toString());
	    isr.close();
	    sw.close();
	    br.close();
	} catch (IOException ex) {
	    Logger.getLogger(RedOrange.class.getName()).log(Level.SEVERE, null, ex);
	    return "doifailed";
	}

	if (responseCode >= 400) {
	    return "doifailed";
	}
	// load PREMIS template file
	FileInputStream fInPremis = null;
	try {
	    fInPremis = new FileInputStream(getApplicationBean().getPremisTemplateFile());
	} catch (FileNotFoundException e) {
	    System.out.println("Template file not found");
	}
	BufferedReader premisReader = new BufferedReader(new InputStreamReader(fInPremis));
	org.jdom.input.SAXBuilder jdomBuilder = new org.jdom.input.SAXBuilder();
	try {
	    doc = jdomBuilder.build(premisReader);
	    // PREMIS metadata
	    XPathNodeUpdater.create(doc, Global.eventIdentifierPath, nsList).setContent("PREMIS:" + UUID.randomUUID().toString());
	    XPathNodeUpdater.create(doc, Global.eventDateTimePath, nsList).setContent(currentDate);
	} catch (JDOMException ex) {
	    Logger.getLogger(RedOrange.class.getName()).log(Level.SEVERE, null, ex);
	    return "doifailed";
	} catch (IOException ex) {
	    Logger.getLogger(RedOrange.class.getName()).log(Level.SEVERE, null, ex);
	    return "doifailed";
	}
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
	System.out.println("Got User name");
	System.out.println(sUserName);
	try {
	    XPathNodeUpdater.create(doc, Global.agentPath, nsList).setContent(sUserId);
	    XPathNodeUpdater.create(doc, Global.fullNamePath, nsList).setContent(sUserName);
	    System.out.println("6");
	    // Add a 'modify content' PREMIS event
	    XPathNodeUpdater.create(doc, Global.linkingObjectIdentifierPath, nsList).setContent(this.pid);
	    XPathNodeUpdater.create(doc, Global.eventTypePath, nsList).setContent("http://www.fba.org.uk/vocabulary/preservationEvents/assignDOI");
	    XPathNodeUpdater.create(doc, Global.eventDetailPath, nsList).setContent("Initiated minting DOI for dataset " + this.pid);
	    XPathNodeUpdater.create(doc, Global.eventOutcomePath, nsList).setContent("success");
	    XPathNodeUpdater.create(doc, Global.eventOutcomeDetailPath, nsList).setContent("requested the following DOI: " + doi);
	    System.out.println("1");
	    doiLinkAgentId = (Element) XPathNodeUpdater.create(doc, Global.linkingAgentIdentifierPath, nsList).getNode().detach();
	    editionLinkAgentId = (Element) doiLinkAgentId.clone();
	    System.out.println("2");
	    System.out.println("2a" + doiLinkAgentId.getName());
	    doiLinkAgentId.getChild("linkingAgentIdentifierType", nsList.get("premis")).setText("DOI");
	    doiLinkAgentId.getChild("linkingAgentIdentifierValue", nsList.get("premis")).setText(doi);
	    System.out.println("3");
	    XPathNodeUpdater.create(doc, Global.PREMISPath, nsList).getNode().addContent(doiLinkAgentId);
	    System.out.println("4");
	    editionLinkAgentId.getChild("linkingAgentIdentifierType", nsList.get("premis")).setText("Edition");
	    editionLinkAgentId.getChild("linkingAgentIdentifierValue", nsList.get("premis")).setText(edition);
	    System.out.println("5");
	    XPathNodeUpdater.create(doc, Global.PREMISPath, nsList).getNode().addContent(editionLinkAgentId);
	    System.out.println("created and populated new PREMIS event from template");
	} catch (JDOMException ex) {
	    Logger.getLogger(RedOrange.class.getName()).log(Level.SEVERE, null, ex);
	    return "doifailed";
	}

	if (premis != null) {
	    try {
		// Add the new PREMIS event to the existing PREMIS data.
		premis.addContent(XPathGetNode(doc, Global.PREMISPath, nsList).detach());
	    } catch (JDOMException ex) {
		Logger.getLogger(RedOrange.class.getName()).log(Level.SEVERE, null, ex);
		return "doifailed";
	    }
	    // Store the modified PREMIS data in a new version of the PREMIS datastream.
	    System.out.println("logging PREMIS event for minting DOI");
	    ByteArrayOutputStream PREMIS = new ByteArrayOutputStream();
	    XMLOutputter outputter = new XMLOutputter();
	    try {
		outputter.output(premis, PREMIS);
	    } catch (IOException ex) {
		Logger.getLogger(RedOrange.class.getName()).log(Level.SEVERE, null, ex);
		return "doifailed";
	    }
	    try {
		((CustomModifyDatastream) (new CustomModifyDatastream(this.pid, "PREMIS")).content(PREMIS.toString())).execute(fedora, lCookies);
		((CustomPurgeDatastream) (new CustomPurgeDatastream(this.pid, "PREMIS")).endDT(dtPremis)).execute(fedora, lCookies);
	    } catch (FedoraClientException ex) {
		Logger.getLogger(RedOrange.class.getName()).log(Level.SEVERE, null, ex);
		return "doifailed";
	    }
	    // Remove the previous version of the PREMIS datastream.
	    System.out.println("after PREMIS event.");
	} else {
	    // Should never be called as ingestion always creates PREMIS event
	    System.out.println("No Previous PREMIS Events saved. Need to create datastream");
	}
	String restPostMetadataEndpoint = getApplicationBean().getRestMetadataEndpoint(); // + "?testMode=" + Global.DOItestMode;
	DataOutputStream dos;
	URL uRestMetadataEndpoint;
	try {
	    uRestMetadataEndpoint = new URL(restPostMetadataEndpoint);

	} catch (MalformedURLException ex) {
	    Logger.getLogger(RedOrange.class.getName()).log(Level.SEVERE, null, ex);
	    return "doifailed";
	}
	try {
	    conn = (HttpsURLConnection) uRestMetadataEndpoint.openConnection();
	    conn.setRequestMethod("POST");
	    conn.setUseCaches(false);
	    conn.setDoInput(true);
	    conn.setDoOutput(true);
	    conn.setRequestProperty("Accept", "application/xml");
	    conn.setRequestProperty("Content-Type", "application/xml;charset=UTF-8");
	    enc = new sun.misc.BASE64Encoder();
	    encAuth = enc.encode((getApplicationBean().getDOIusername() + ":" + getApplicationBean().getDOIpassword()).getBytes());
	    conn.setRequestProperty("Authorization", "Basic " + encAuth);
	    dos = new DataOutputStream(conn.getOutputStream());
	    dos.writeBytes(metadata);
	    dos.close();
	    conn.connect();
	    responseCode = conn.getResponseCode();
	    if (responseCode >= 400) {
		isr = new InputStreamReader(conn.getErrorStream());
	    } else {
		isr = new InputStreamReader(conn.getInputStream());
	    }
	    br = new BufferedReader(isr);
	    sw = new StringWriter();
	    buffer = new char[1024];
	    while ((n = br.read(buffer)) != -1) {
		sw.write(buffer, 0, n);
	    }
	    System.out.println("Return Code:" + responseCode);
	    System.out.println("Return Body:" + sw.toString());
	    isr.close();
	    sw.close();
	    br.close();
	} catch (IOException ex) {
	    Logger.getLogger(RedOrange.class.getName()).log(Level.SEVERE, null, ex);
	    return "doifailed";
	}


	// Add DOI Assignment Metadata into PREMIS datastream
	// load PREMIS template file
	fInPremis = null;
	try {
	    fInPremis = new FileInputStream(getApplicationBean().getPremisTemplateFile());
	} catch (FileNotFoundException e) {
	    System.out.println("Template file not found");
	}
	premisReader = new BufferedReader(new InputStreamReader(fInPremis));
	jdomBuilder = new org.jdom.input.SAXBuilder();
	try {
	    doc = jdomBuilder.build(premisReader);
	    // PREMIS metadata
	    XPathNodeUpdater.create(doc, Global.eventIdentifierPath, nsList).setContent("PREMIS:" + UUID.randomUUID().toString());
	    XPathNodeUpdater.create(doc, Global.eventDateTimePath, nsList).setContent(currentDate);

	    sUserId = "";
	    sUserName = "";
	    face = FacesContext.getCurrentInstance();
	    externalContext = face.getExternalContext();
	    if (externalContext.getUserPrincipal() == null) {
		_log.info("current principal is null");
	    } else {
		sUserId = externalContext.getUserPrincipal().getName();
		Long id = Long.parseLong(sUserId);
		sUserId = "u" + sUserId;
		try {
		    user = UserLocalServiceUtil.getUserById(id);
		} catch (PortalException ex) {
		    _log.fatal(ex);
		} catch (SystemException ex) {
		    _log.fatal(ex);
		}
	    }
	    // Add a 'modify content' PREMIS event
	    XPathNodeUpdater.create(doc, Global.eventTypePath, nsList).setContent("http://www.fba.org.uk/vocabulary/preservationEvents/assignDOI");
	    XPathNodeUpdater.create(doc, Global.eventDetailPath, nsList).setContent("assigned DOI to dataset " + this.pid);
	    XPathNodeUpdater.create(doc, Global.eventOutcomePath, nsList).setContent("success");
	    XPathNodeUpdater.create(doc, Global.eventOutcomeDetailPath, nsList).setContent("assigned the following DOI: " + doi);
	    XPathNodeUpdater.create(doc, Global.linkingObjectIdentifierPath, nsList).setContent(this.pid);
	    System.out.println("1");
	    doiLinkAgentId = (Element) XPathNodeUpdater.create(doc, Global.linkingAgentIdentifierPath, nsList).getNode().detach();
	    editionLinkAgentId = (Element) doiLinkAgentId.clone();
	    System.out.println("2");
	    System.out.println("2a" + doiLinkAgentId.getName());
	    doiLinkAgentId.getChild("linkingAgentIdentifierType", nsList.get("premis")).setText("DOI");
	    doiLinkAgentId.getChild("linkingAgentIdentifierValue", nsList.get("premis")).setText(doi);
	    System.out.println("3");
	    XPathNodeUpdater.create(doc, Global.PREMISPath, nsList).getNode().addContent(doiLinkAgentId);
	    System.out.println("4");
	    editionLinkAgentId.getChild("linkingAgentIdentifierType", nsList.get("premis")).setText("Edition");
	    editionLinkAgentId.getChild("linkingAgentIdentifierValue", nsList.get("premis")).setText(edition);
	    System.out.println("5");
	    XPathNodeUpdater.create(doc, Global.PREMISPath, nsList).getNode().addContent(editionLinkAgentId);
	    System.out.println("created and populated new PREMIS event from template");
	    if (premis != null) {
		// Add the new PREMIS event to the existing PREMIS data.
		premis.addContent(XPathGetNode(doc, Global.PREMISPath, nsList).detach());
		// Store the modified PREMIS data in a new version of the PREMIS datastream.
		System.out.println("logging PREMIS event for modify metadata");
		ByteArrayOutputStream PREMIS = new ByteArrayOutputStream();
		XMLOutputter outputter = new XMLOutputter();
		try {
		    outputter.output(premis, PREMIS);
		} catch (IOException ex) {
		    Logger.getLogger(RedOrange.class.getName()).log(Level.SEVERE, null, ex);
		    return "doifailed";
		}
		try {
		    ((CustomModifyDatastream) (new CustomModifyDatastream(this.pid, "PREMIS")).content(PREMIS.toString())).execute(fedora, lCookies);
		    ((CustomPurgeDatastream) (new CustomPurgeDatastream(this.pid, "PREMIS")).endDT(dtPremis)).execute(fedora, lCookies);
		} catch (FedoraClientException ex) {
		    Logger.getLogger(RedOrange.class.getName()).log(Level.SEVERE, null, ex);
		    return "doifailed";
		}
		// Remove the previous version of the PREMIS datastream.
		System.out.println("after PREMIS event.");
	    } else {
		// Should never be called as ingestion always creates PREMIS event
		System.out.println("No Previous PREMIS Events saved. Need to create datastream");
	    }
	} catch (JDOMException ex) {
	    Logger.getLogger(RedOrange.class.getName()).log(Level.SEVERE, null, ex);
	    return "doifailed";
	} catch (IOException ex) {
	    Logger.getLogger(RedOrange.class.getName()).log(Level.SEVERE, null, ex);
	    return "doifailed";
	}
	// TODO - Move dataset from RedOrange to Orange category
	String category = getCurrentCategory();
	String new_category = null;
	String datasetPID = this.getPid();
	System.out.println("doRequestDOI category: " + category);
	//SFOX don't do this for now
	    /*
	if (category != null) {
	if (category.equals("redorange")) {
	// red -> orange
	new_category = "orange";
	}
	changeDatasetCategory(new_category);
	// Add a PREMIS event
	doWritePremisRecord(datasetPID,
	"http://www.fba.org.uk/vocabulary/preservationEvents/promotetoorange",
	"Promotion of dataset to orange",
	"success",
	"Successfully moved dataset to orange category",
	"");
	}
	 *
	 */
	return "doisuccessful";

    }

    /***********************/
    /* Getters and Setters */
    /***********************/
    public String getGroupId() {
	return groupId;
    }

    private void setGroupId(String groupId) {
	this.groupId = groupId;
    }

    public String getArticleId() {
	return articleId;
    }

    private void setArticleId(String articleId) {
	this.articleId = articleId;
    }

    public String getEntryClassName() {
	return entryClassName;
    }

    private void setEntryClassName(String entryClassName) {
	this.entryClassName = entryClassName;
    }

    public String getUserId() {
	return userId;
    }

    private void setUserId(String userId) {
	this.userId = userId;
    }

    public boolean getIsVisible() {
	return isVisible;
    }

    public boolean isIsVisible() {
	return isVisible;
    }

    public void setIsVisible(boolean isVisible) {
	this.isVisible = isVisible;
    }

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

    public String getModsRightsEmail() {
	return ModsRightsEmail;
    }

    public void setModsRightsEmail(String ModsRightsEmail) {
	this.ModsRightsEmail = ModsRightsEmail;
    }

    public String getModsRightsOwner() {
	return modsRightsOwner;
    }

    public void setModsRightsOwner(String modsRightsOwner) {
	this.modsRightsOwner = modsRightsOwner;
    }

    public String getModsRightsContact() {
	return modsRightsContact;
    }

    public void setModsRightsContact(String modsRightsContact) {
	this.modsRightsContact = modsRightsContact;
    }

    public String getModsRightsInstitution() {
	return modsRightsInstitution;
    }

    public void setModsRightsInstitution(String modsRightsInstitution) {
	this.modsRightsInstitution = modsRightsInstitution;
    }

    public String getModsRightsPhone() {
	return modsRightsPhone;
    }

    public void setModsRightsPhone(String modsRightsPhone) {
	this.modsRightsPhone = modsRightsPhone;
    }

    public String getModsRightsCreator() {
	return modsRightsCreator;
    }

    public void setModsRightsCreator(String modsRightsCreator) {
	this.modsRightsCreator = modsRightsCreator;
    }

    public String getModKeywords() {
	return keywords;
    }

    public void setModKeywords(String keywords) {
	this.keywords = keywords;
    }

    public String getRootEntryClassPK() {
	return rootEntryClassPK;
    }

    public void setRootEntryClassPK(String rootEntryClassPK) {
	this.rootEntryClassPK = rootEntryClassPK;
    }

    public String getSubmitText() {
	return submitText;
    }

    public void setSubmitText(String submitText) {
	this.submitText = submitText;
    }

    private String formatDatasetDisplayURL() {

	// /web/guest/dataset-display/-/dataset-display/<PID>

	String strDisplayURL = Global.strNoURLReturned;

	strDisplayURL = "/web/guest/dataset-display/-/dataset-display/"
		+ this.getPid();

	return strDisplayURL;
    }

    public void setDatasetdisplayURL(String datasetdisplayURL) {
	this.datasetdisplayURL = datasetdisplayURL;
    }

    public String getDatasetdisplayURL() {
	return datasetdisplayURL;
    }

    public void setUploader(String uploader) {
	this.uploader = uploader;
    }

    public String getUploader() {
	return uploader;
    }

    public String getAssignedReviewer() {
	return assignedReviewer;
    }

    public void setAssignedReviewer(String assignedReviewer) {
	this.assignedReviewer = assignedReviewer;
    }

    public String getReviewerName() {
	return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
	this.reviewerName = reviewerName;
    }

    public String getReviewerUserId() {
	return reviewerUserId;
    }

    public void setReviewerUserId(String reviewerUserId) {
	this.reviewerUserId = reviewerUserId;
    }

    public int getDoiReturnCode() {
	return doiReturnCode;
    }

    public void setDoiReturnCode(int doiReturnCode) {
	this.doiReturnCode = doiReturnCode;
    }

    public String getDoiReturnString() {
	return doiReturnString;
    }

    public void setDoiReturnString(String doiReturnString) {
	this.doiReturnString = doiReturnString;
    }
}
