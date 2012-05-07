package org.freshwaterlife.portlets.dataset.resulttypes;

import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;

import javax.ws.rs.core.Cookie;

import org.freshwaterlife.fedora.client.CustomFedoraClient;
import org.freshwaterlife.fedora.client.CustomGetDatastreamDissemination;
import org.freshwaterlife.fedora.client.CustomModifyDatastream;
import org.freshwaterlife.fedora.client.CustomPurgeObject;
import org.freshwaterlife.portlets.dataset.Global;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
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

    public Dataset() {
	super();
    }

    public Dataset(HashMap values) {

	if (values.containsKey("mods.datasetCategory")) {
	    this.setCategory((String) values.get("mods.datasetCategory"));
	}

	this.setSubmitText("Apply for Digital Object Identifier (Publish the Dataset)");
    }

    public String doSubmit() {

	System.out.println("doSubmit - start");

	// get PID and modify descMetadata
	String datasetPID = this.getPid();
	String new_category = "";

	Map<String, Namespace> nsList = _buildNamespaceList();

	// JSFPortletUtil - Liferay provided class from util-bridges.jar

	FedoraCredentials credentials;
	CustomFedoraClient fedora = null;
	FedoraResponse fResponse = null;

	try {
    	    credentials = new FedoraCredentials(new URL(getApplicationBean().getFedoraURL()),
							getApplicationBean().getFedoraUsername(),
							getApplicationBean().getFedoraPassword());
	    fedora = new CustomFedoraClient(credentials);

	    List<Cookie> lCookies = getCookies();

	    String sContent = "";
	    fResponse = (new CustomGetDatastreamDissemination(datasetPID, "descMetadata")).execute(fedora, lCookies);
	    if (fResponse.getStatus() == 200) {
		sContent = fResponse.getEntity(String.class);

		System.out.println("doSubmit - start parsing descMetadata");

		org.jdom.input.SAXBuilder jdomParser = new org.jdom.input.SAXBuilder();
		Document doc = jdomParser.build((new InputSource(new ByteArrayInputStream(sContent.getBytes("utf-8")))));

		XMLOutputter outputter = new XMLOutputter();

		category = XPathGetNode(doc, Global.datasetCategoryPath, nsList).getText();
		System.out.println("category: " + category);

		if (category != null) {
		    if (category.equals("red")) {
			// red -> orange
			new_category = "orange";
		    } else if (category.equals("orange")) {
			// orange -> green
			new_category = "green";
		    } else if (category.equals("green")) {
			// green -> red
			new_category = "red";
		    }


		    XPathGetNode(doc, Global.datasetCategoryPath, nsList).setText(new_category);

		    ByteArrayOutputStream descMetadata = new ByteArrayOutputStream();

		    outputter.output(doc, descMetadata);
		    ((CustomModifyDatastream) (new CustomModifyDatastream(datasetPID, "descMetadata")).content(descMetadata.toString())).execute(fedora, lCookies);

		    // Add a 'submit for review' PREMIS event
		    doWritePremisRecord(datasetPID,
			    "http://www.fba.org.uk/vocabulary/preservationEvents/submit4Review",
			    "Submission of dataset to review process",
			    "success",
			    "Successfully submitted dataset to review process",
			    "");
		} else {
		    System.out.println("Error");
		}
	    }
	} catch (FedoraClientException e) {
	    // TODO Auto-generated catch block
	    System.out.println("Fedora Error: " + e.getMessage());
	} catch (JDOMException e) {
	    // TODO Auto-generated catch block
	    System.out.println("JDOM Error: " + e.getMessage());
	} catch (UnsupportedEncodingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    System.out.println("Error: " + e.getMessage());
	}

	System.out.println("Object: " + datasetPID + " is now in the "
		+ new_category);

	return "submit";
    }

    public String doSubmitForDOI() {
	System.out.println("doSubmitForDOI - start");
	return "submit";
    }

    public String doDelete() {

	// get PID and do a purge + add note of 'User deletion on 27/01/2011'
	String datasetPID = this.getPid();
	// Gets the HttpServletRequest from the PortletServletRequest
	javax.servlet.http.HttpServletRequest request = (javax.servlet.http.HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("com.liferay.portal.kernel.servlet.PortletServletRequest"));

	FedoraCredentials credentials;
	CustomFedoraClient fc = null;
	try {
    	    credentials = new FedoraCredentials(new URL(getApplicationBean().getFedoraURL()),
							getApplicationBean().getFedoraUsername(),
							getApplicationBean().getFedoraPassword());
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

    public void changeDatasetCategory(String new_category) {
	FedoraCredentials credentials;
	CustomFedoraClient fedora = null;
	FedoraResponse fResponse = null;
	String datasetPID = this.getPid();

	XMLOutputter outputter = new XMLOutputter();
	String sContent = "";
	Map<String, Namespace> nsList = _buildNamespaceList();
	String datasetCategoryPath = "/mods:mods/mods:note[@type='admin' and ( text()='red' or text()='redorange' or text()='orange' or text()='green')]";

	try {
    	    credentials = new FedoraCredentials(new URL(getApplicationBean().getFedoraURL()),
							getApplicationBean().getFedoraUsername(),
							getApplicationBean().getFedoraPassword());
	    fedora = new CustomFedoraClient(credentials);
	    List<Cookie> lCookies = getCookies();
	    fResponse = (new CustomGetDatastreamDissemination(datasetPID, "descMetadata")).execute(fedora, lCookies);
	    if (fResponse.getStatus() == 200) {
		sContent = fResponse.getEntity(String.class);

		org.jdom.input.SAXBuilder jdomParser = new org.jdom.input.SAXBuilder();
		Document doc = jdomParser.build(new InputSource(new ByteArrayInputStream(sContent.getBytes("utf-8"))));
		XPathGetNode(doc, datasetCategoryPath, nsList).setText(new_category);
		ByteArrayOutputStream descMetadata = new ByteArrayOutputStream();

		outputter.output(doc, descMetadata);
		((CustomModifyDatastream) (new CustomModifyDatastream(datasetPID, "descMetadata")).content(descMetadata.toString())).execute(fedora, lCookies);
	    } else {
		System.out.println("setCategory FResponse Error");
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

    public String getCurrentCategory() {
	FedoraCredentials credentials;
	CustomFedoraClient fedora = null;
	FedoraResponse fResponse = null;

	String datasetPID = this.getPid();
	String category = null;
	Map<String, Namespace> nsList = _buildNamespaceList();
	String sContent = "";
	List<Cookie> lCookies = getCookies();
	try {
    	    credentials = new FedoraCredentials(new URL(getApplicationBean().getFedoraURL()),
							getApplicationBean().getFedoraUsername(),
							getApplicationBean().getFedoraPassword());
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
		    Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
		    Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
		}
	    } else {
		System.out.println("getCurrentCategory FResponse Error");
	    }
	} catch (MalformedURLException ex) {
	    Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
	} catch (FedoraClientException ex) {
	    Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
	}

	return category;
    }

    public void setRevUserid(String new_revUserid) {
	FedoraCredentials credentials;
	CustomFedoraClient fedora = null;
	FedoraResponse fResponse = null;


	XMLOutputter outputter = new XMLOutputter();
	String sContent = "";
	Map<String, Namespace> nsList = _buildNamespaceList();
	String datasetCategoryPath = "/mods:mods/mods:name/mods:description";
	String datasetPID = this.getPid();

	try {
    	    credentials = new FedoraCredentials(new URL(getApplicationBean().getFedoraURL()),
							getApplicationBean().getFedoraUsername(),
							getApplicationBean().getFedoraPassword());
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
	    Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
	} catch (JDOMException ex) {
	    Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
	} catch (IOException ex) {
	    Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
	} catch (FedoraClientException ex) {
	    Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void setReviewer(String new_reviewer) {
	FedoraCredentials credentials;
	CustomFedoraClient fedora = null;
	FedoraResponse fResponse = null;


	XMLOutputter outputter = new XMLOutputter();
	String sContent = "";
	Map<String, Namespace> nsList = _buildNamespaceList();
	String datasetCategoryPath = "/mods:mods/mods:name/mods:namePart";
	String datasetPID = this.getPid();

	try {
    	    credentials = new FedoraCredentials(new URL(getApplicationBean().getFedoraURL()),
							getApplicationBean().getFedoraUsername(),
							getApplicationBean().getFedoraPassword());
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
	    Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
	} catch (JDOMException ex) {
	    Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
	} catch (IOException ex) {
	    Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
	} catch (FedoraClientException ex) {
	    Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public String getCurrentOutcomeFromXML() {
	FedoraCredentials credentials;
	CustomFedoraClient fedora = null;
	FedoraResponse fResponse = null;

	String datasetPID = this.getPid();

	String outcome = null;
	Map<String, Namespace> nsList = _buildNamespaceList();
	String sContent = "";
	List<Cookie> lCookies = getCookies();
	try {
    	    credentials = new FedoraCredentials(new URL(getApplicationBean().getFedoraURL()),
							getApplicationBean().getFedoraUsername(),
							getApplicationBean().getFedoraPassword());
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
    	    credentials = new FedoraCredentials(new URL(getApplicationBean().getFedoraURL()),
							getApplicationBean().getFedoraUsername(),
							getApplicationBean().getFedoraPassword());
	    fedora = new CustomFedoraClient(credentials);
	    List<Cookie> lCookies = getCookies();
	    fResponse = (new CustomGetDatastreamDissemination(datasetPID, "descMetadata")).execute(fedora, lCookies);
	    if (fResponse.getStatus() == 200) {
		sContent = fResponse.getEntity(String.class);

		org.jdom.input.SAXBuilder jdomParser = new org.jdom.input.SAXBuilder();
		Document doc = jdomParser.build(new InputSource(new ByteArrayInputStream(sContent.getBytes("utf-8"))));
		XPathGetNode(doc, Global.datasetCategoryPath, nsList).setText(new_outcome);
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

    private void setModsAbstract(String modsAbstract) {
	this.modsAbstract = modsAbstract;
    }

    public String getModsSubjectTopic() {
	return modsSubjectTopic;
    }

    private void setModsSubjectTopic(String modsSubjectTopic) {
	this.modsSubjectTopic = modsSubjectTopic;
    }

    public String getDatasetArticleURL() {
	return datasetArticleURL;
    }

    public void setDatasetArticleURL(String journalArticleURL) {
	this.datasetArticleURL = journalArticleURL;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    public String getStatus() {
	return status;
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
}
