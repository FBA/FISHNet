package org.freshwaterlife.portlets.dataset;

import com.liferay.portal.kernel.util.PropsUtil;
import java.io.Serializable;

/**
 *
 * @author sfox
 */
public class ApplicationBean implements Serializable {

    //Names of variables stored in the portals-ext.properties file
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
    private String solrURL;
    private String fedoraURL;
    private String fedoraUsername;
    private String fedoraPassword;
    private String premisTemplateFile;
    private String foxmlTemplateFile;
    private String keywordTemplateFile;
    private String serverURL;
    private String doiMetadataXSLTFile;
    private String restDOIEndpoint;
    private String restMetadataEndpoint;
    private String DOIusername;
    private String DOIpassword;
    private String DOIFBAPrefix;
    private String DOIBLtestPrefix;
    private Boolean DOItestMode;

    public ApplicationBean() {

	try {
	    setSolrURL(PropsUtil.get(this.PropertySolrServer));
	    setFedoraURL(PropsUtil.get(this.PropertyFedoraURL));
	    setFedoraUsername(PropsUtil.get(this.PropertyFedoraUsername));
	    setFedoraPassword(PropsUtil.get(this.PropertyFedoraPassword));
	    setPremisTemplateFile(PropsUtil.get(this.PropertyPremisTemplateFile));
	    setFoxmlTemplateFile(PropsUtil.get(this.PropertyFoxmlTemplateFile));
	    setServerURL(PropsUtil.get(this.PropertyServerURL));
	    setKeywordTemplateFile(PropsUtil.get(this.PropertyKeywordTemplateFile));
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
	    }
	} catch (Exception e) {
	    setSolrURL("localhost");
	    setFedoraURL("localhost");
	    setFedoraUsername("");
	    setFedoraPassword("");
	    setPremisTemplateFile("/opt/liferay-portal-6.0.5/tomcat-6.0.26/conf/dataset-premis-template.xml");
	    setFoxmlTemplateFile("/opt/liferay-portal-6.0.5/tomcat-6.0.26/conf/dataset-object-foxml-template.xml");
	    setServerURL("localhost");
	    setKeywordTemplateFile("/opt/liferay-portal-6.0.5/tomcat-6.0.26/conf/dataset-keywords-template.xml");
	    setDoiMetadataXSLTFile("/opt/liferay-portal-6.0.5/tomcat-6.0.26/conf/datacite-doi-metadata.xslt");
	    setRestDOIEndpoint("");
	    setRestMetadataEndpoint("");
	    setDOIusername("");
	    setDOIpassword("");
	    setDOIFBAPrefix("");
	    setDOIBLtestPrefix("");
	    setDOItestMode(true);

	    //TODO: Something needs doing here to flag up that the config variables are invalid//missing
	}
    }

    //*********************//
    // Getters and Setters //
    //*********************//
    public String getSolrURL() {
	return solrURL;
    }

    public void setSolrURL(String solrURL) {
	this.solrURL = solrURL;
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

    public String getKeywordTemplateFile() {
	return keywordTemplateFile;
    }

    public void setKeywordTemplateFile(String keywordTemplateFile) {
	this.keywordTemplateFile = keywordTemplateFile;
    }

    public String getDoiMetadataXSLTFile() {
	return doiMetadataXSLTFile;
    }

    public void setDoiMetadataXSLTFile(String doiMetadataXSLTFile) {
	this.doiMetadataXSLTFile = doiMetadataXSLTFile;
    }

    public String getDOIBLtestPrefix() {
	return DOIBLtestPrefix;
    }

    public void setDOIBLtestPrefix(String DOIBLtestPrefix) {
	this.DOIBLtestPrefix = DOIBLtestPrefix;
    }

    public String getDOIFBAPrefix() {
	return DOIFBAPrefix;
    }

    public void setDOIFBAPrefix(String DOIFBAPrefix) {
	this.DOIFBAPrefix = DOIFBAPrefix;
    }

    public String getDOIpassword() {
	return DOIpassword;
    }

    public void setDOIpassword(String DOIpassword) {
	this.DOIpassword = DOIpassword;
    }

    public Boolean getDOItestMode() {
	return DOItestMode;
    }

    public void setDOItestMode(Boolean DOItestMode) {
	this.DOItestMode = DOItestMode;
    }

    public String getDOIusername() {
	return DOIusername;
    }

    public void setDOIusername(String DOIusername) {
	this.DOIusername = DOIusername;
    }

    public String getRestDOIEndpoint() {
	return restDOIEndpoint;
    }

    public void setRestDOIEndpoint(String restDOIEndpoint) {
	this.restDOIEndpoint = restDOIEndpoint;
    }

    public String getRestMetadataEndpoint() {
	return restMetadataEndpoint;
    }

    public void setRestMetadataEndpoint(String restMetadataEndpoint) {
	this.restMetadataEndpoint = restMetadataEndpoint;
    }
}
