package org.freshwaterlife.portlets.search.resulttypes;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import java.io.Serializable;
import java.util.HashMap;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import org.freshwaterlife.portlets.search.Global;

/**
 *
 * @author sfox
 */
public class Dataset extends BasicResultDoc implements Serializable {

    private String pid;
    private String modsAbstract;
    private String modsSubjectTopic;
    private PortletRequest portletRequest;
    private FacesContext context;
    private ThemeDisplay themeDisplay;
    private String datasetArticleURL;

    public Dataset() {
	super();
    }

    public Dataset(HashMap values) {
	this.setDoctype(Global.DATASET);
	if (values.containsKey("mods.title")) {
	    this.setModsTitle((String) values.get("mods.title"));
	}
	if (values.containsKey("mods.abstract")) {
	    this.setModsAbstract((String) values.get("mods.abstract"));
	}
	if (values.containsKey("mods.subject.topic")) {
	    this.setModsSubjectTopic((String) values.get("mods.subject.topic"));
	}
	if (values.containsKey("PID")) {
	    this.setPid((String) values.get("PID"));
	}

	this.setDatasetArticleURL(formatDatasetArticleURL());
    }

    private String formatDatasetArticleURL() {
	String strArticleURL = Global.strNoURLReturned;
	String strHost;
	int iServerPort;
	String strScheme;

	try {
	    this.context = FacesContext.getCurrentInstance();
	    this.portletRequest = (PortletRequest) context.getExternalContext().getRequest();
	    this.themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

	} catch (Exception e) {
	}

	strHost = portletRequest.getServerName();
	iServerPort = portletRequest.getServerPort();
	strScheme = portletRequest.getScheme();
	strArticleURL = strScheme + "://" + strHost + ":" + iServerPort + "/web/guest/dataset-display/-/dataset/view/" + this.getPid();

	return strArticleURL;
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

    public String getPid() {
	return pid;
    }

    private void setPid(String pid) {
	this.pid = pid;
    }

    public String getDatasetArticleURL() {
	return datasetArticleURL;
    }

    public void setDatasetArticleURL(String journalArticleURL) {
	this.datasetArticleURL = journalArticleURL;
    }
}
