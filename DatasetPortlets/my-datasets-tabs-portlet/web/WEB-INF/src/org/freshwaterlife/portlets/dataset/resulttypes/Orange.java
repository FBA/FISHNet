package org.freshwaterlife.portlets.dataset.resulttypes;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import java.io.Serializable;
import java.util.HashMap;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;

import org.freshwaterlife.portlets.dataset.Global;

/**
 * 
 * @author sfox
 */
public class Orange extends Dataset implements Serializable {

    private String modsAuthor;
    private String modsHostTitle;
    private String modsPublicationDate;
    private String modsExtVolume;
    private String modsExtPart;
    private String modsExtPages;
    private String modsHostAuthor;
    private String modsPublisher;
    private String modsPublisherLocation;
    private String modsShelfmark;
    private String modsDatasetCategory;
    private String libcatArticleURL;
    private ThemeDisplay themeDisplay;
    private PortletRequest portletRequest;
    private FacesContext context;
    private String libcatType;
    private String datasetdisplayURL;
    private String makeDatasetPublicText;

    public Orange() {
	super();
    }

    public Orange(HashMap values) {

	super(values);
	this.setDoctype(Global.ORANGE);

	if (values.containsKey("uid")) {
	    this.setId((String) values.get("uid"));
	}
	if (values.containsKey("PID")) {
	    this.setPid((String) values.get("PID"));
	}
	if (values.containsKey("mods.title")) {
	    this.setModsTitle((String) values.get("mods.title"));
	}
	if (values.containsKey("mods.author")) {
	    this.setModsAuthor((String) values.get("mods.author"));
	}
	if (values.containsKey("mods.host.title")) {
	    this.setModsHostTitle((String) values.get("mods.host.title"));
	}
	if (values.containsKey("mods.publication.date")) {
	    this.setModsPublicationDate((String) values.get("mods.publication.date"));
	}
	if (values.containsKey("mods.ext.volume")) {
	    this.setModsExtVolume((String) values.get("mods.ext.volume"));
	}
	if (values.containsKey("mods.ext.part")) {
	    this.setModsExtPart((String) values.get("mods.ext.part"));
	}
	if (values.containsKey("mods.ext.pages")) {
	    this.setModsExtPages((String) values.get("mods.ext.pages"));
	}

	if (values.containsKey("mods.host.author")) {
	    this.setModsHostAuthor((String) values.get("mods.host.author"));
	}

	if (values.containsKey("mods.publisher")) {
	    this.setModsPublisher((String) values.get("mods.publisher"));
	}

	if (values.containsKey("mods.publisher.location")) {
	    this.setModsPublisherLocation((String) values.get("mods.publisher.location"));
	}

	if (values.containsKey("mods.shelfmark")) {
	    this.setModsShelfmark((String) values.get("mods.shelfmark"));
	}

	if (values.containsKey("mods.reviewer.roleTerm")) {
	    this.setReviewerRoleterm((String) values.get("mods.reviewer.roleTerm"));
	}


	if (values.containsKey("lastModifiedDate")) {
	    String date = (String) values.get("lastModifiedDate");
	    String day = date.substring(0, 10);
	    String time = date.substring(11, 23);
	    String formattedDate = day + "</br>" + time;
	    this.setLastModifiedDate(formattedDate);
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

	this.setStatus("Dataset is in the Orange Category");
	this.setStatusImgURL("/resources/images/orange.png");
	this.setMakeDatasetPublicText("Make Dataset publicly available");

	this.setDatasetdisplayURL(formatDatasetDisplayURL());

    }

    /***********************/
    /* Getters and Setters */
    /***********************/
    public String getModsExtPages() {
	return modsExtPages;
    }

    public void setModsExtPages(String modsExtPages) {
	this.modsExtPages = modsExtPages;
    }

    public String getModsExtPart() {
	return modsExtPart;
    }

    public void setModsExtPart(String modsExtPart) {
	this.modsExtPart = modsExtPart;
    }

    public String getModsExtVolume() {
	return modsExtVolume;
    }

    public void setModsExtVolume(String modsExtVolume) {
	this.modsExtVolume = modsExtVolume;
    }

    public String getModsPublicationDate() {
	return modsPublicationDate;
    }

    public void setModsPublicationDate(String modsPublicationDate) {
	this.modsPublicationDate = modsPublicationDate;
    }

    public String getModsHostTitle() {
	return modsHostTitle;
    }

    public void setModsHostTitle(String modsHostTitle) {
	this.modsHostTitle = modsHostTitle;
    }

    public String getModsAuthor() {
	return modsAuthor;
    }

    public void setModsAuthor(String modsAuthor) {
	this.modsAuthor = modsAuthor;
    }

    public String getLibcatArticleURL() {
	return libcatArticleURL;
    }

    public void setLibcatArticleURL(String libcatArticleURL) {
	this.libcatArticleURL = libcatArticleURL;
    }

    public String getModsShelfmark() {
	return modsShelfmark;
    }

    public void setModsShelfmark(String modsShelfmark) {
	this.modsShelfmark = modsShelfmark;
    }

    public String getModsPublisherLocation() {
	return modsPublisherLocation;
    }

    public void setModsPublisherLocation(String modsPublisherLocation) {
	this.modsPublisherLocation = modsPublisherLocation;
    }

    public String getModsPublisher() {
	return modsPublisher;
    }

    public void setModsPublisher(String modsPublisher) {
	this.modsPublisher = modsPublisher;
    }

    public String getModsHostAuthor() {
	return modsHostAuthor;
    }

    public void setModsHostAuthor(String modsHostAuthor) {
	this.modsHostAuthor = modsHostAuthor;
    }

    public String getLibcatType() {
	return libcatType;
    }

    public void setLibcatType(String libcatType) {
	this.libcatType = libcatType;
    }

    public void setModsDatasetCategory(String modsDatasetCategory) {
	this.modsDatasetCategory = modsDatasetCategory;
    }

    public String getModsDatasetCategory() {
	return modsDatasetCategory;
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

    public void setMakeDatasetPublicText(String makeDatasetPublicText) {
	this.makeDatasetPublicText = makeDatasetPublicText;
    }

    public String getMakeDatasetPublicText() {
	return makeDatasetPublicText;
    }
}
