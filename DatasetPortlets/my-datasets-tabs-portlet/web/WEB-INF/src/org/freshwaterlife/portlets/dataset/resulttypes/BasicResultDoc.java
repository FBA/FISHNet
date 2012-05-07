/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freshwaterlife.portlets.dataset.resulttypes;

import java.io.Serializable;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.util.bridges.jsf.common.JSFPortletUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import javax.portlet.ActionRequest;
import javax.ws.rs.core.Cookie;
import org.freshwaterlife.portlets.dataset.Global;

/**
 *
 * @author root
 */
public abstract class BasicResultDoc implements Serializable {

    private static final long serialVersionUID = 1L;
    protected String id; //maps to uid
    private String title; //maps to the name field and title field
    private int doctype; //use the static variables
    private ArrayList<String> keywords; //maps to tagentries
    private String content;
    protected String status;
    protected String modsTitle;
    protected String modsTitleEscaped;
    protected String statusImgURL;
    protected String lastModifiedDate;
    protected String pid;
    protected String uploaderScreenName;
    protected Log _log = LogFactoryUtil.getLog(BasicResultDoc.class);
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
	    setSolrURL(PropsUtil.get(this.PropertySolrServer));
	    setFedoraURL(PropsUtil.get(this.PropertyFedoraURL));
	    setFedoraUsername(PropsUtil.get(this.PropertyFedoraUsername));
	    setFedoraPassword(PropsUtil.get(this.PropertyFedoraPassword));
	    setPremisTemplateFile(PropsUtil.get(this.PropertyPremisTemplateFile));
	    setFoxmlTemplateFile(PropsUtil.get(this.PropertyFoxmlTemplateFile));
	    setServerURL(PropsUtil.get(this.PropertyServerURL));
	    setKeywordTemplateFile(PropsUtil.get(this.PropertyKeywordTemplateFile));
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

    public Element XPathGetNode(Document doc, String path, Map<String, Namespace> ns) throws JDOMException {
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

    protected User getUser() {
	return ((ThemeDisplay) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(WebKeys.THEME_DISPLAY)).getUser();
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
     * @param title the title to set
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
     * @param keywords the keywords to set
     */
    public void setKeywords(ArrayList<String> keywords) {

	//do a bit of clean up
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
     * @param content the content to set
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

	    //ensure that the italics close tag hasn't been removed in truncation
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

	if (strBeforeText.contains("[i]")) //handle the customised italics tags
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
