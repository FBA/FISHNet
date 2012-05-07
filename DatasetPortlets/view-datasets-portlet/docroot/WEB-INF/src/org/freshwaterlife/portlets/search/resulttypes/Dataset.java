package org.freshwaterlife.portlets.search.resulttypes;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Random;

import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;

import org.freshwaterlife.portlets.dataset.display.my.Global;

/**
 *
 * @author sfox
 */
public class Dataset extends BasicResultDoc implements Serializable {

    private String pid;
    private String modsAbstract;
    private String modsSubjectTopic;
    private String status;
    private String statusImgURL;
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
		
		// for now: do a random 0 - 4 to demo status + status img
		Random generator = new Random();
		int status = generator.nextInt(4);
		switch (status) {
			case 0:
				// red			
				this.setStatus("Dataset is in the Red Category");
				this.setStatusImgURL("red.png");
				break;
			case 1:
				// red-orange
				this.setStatus("Dataset is under review going from Red to Orange Category");
				this.setStatusImgURL("red-orange.png");
				break;
			case 2:
				// orange
				this.setStatus("Dataset is in the Orange Category");
				this.setStatusImgURL("orange.png");
				break;
			case 3:
				// orange-green
				this.setStatus("Dataset is under review going from Orange to Green Category");
				this.setStatusImgURL("orange-green.png");
				break;				
			case 4:
				// green
				this.setStatus("Dataset is in the Green Category");
				this.setStatusImgURL("green.png");
				break;
		}		
	
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
		strArticleURL = strScheme + "://" + strHost + ":" + iServerPort + "/web/guest/dataset-display/-/dataset-display/" + this.getPid();
	
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

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatusImgURL(String statusImgURL) {
		this.statusImgURL = statusImgURL;
	}

	public String getStatusImgURL() {
		return statusImgURL;
	}
}
