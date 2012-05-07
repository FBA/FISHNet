/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freshwaterlife.portlets.dataset.resulttypes;

import org.freshwaterlife.portlets.dataset.Global;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 
 * @author root
 */
public class Green extends Dataset implements Serializable {
    // urls used to construct thumb and link urls

    private static final String fowardUrlBase = "http://www.freshwaterlife.org/imagearchive/main.php?g2_itemId=";
    private static final String thumbUrlBase = "http://www.freshwaterlife.org/imagearchive/main.php?g2_view=core.DownloadItem&g2_itemId=";
    private String thumbid;
    private String thumbUrl;
    private String summary;
    private String datasetdisplayURL;

    public Green() {
	super();
    }

    public Green(HashMap values) {

	super(values);
	this.setDoctype(Global.GREEN);

	if (values.containsKey("uid")) {
	    this.setId((String) values.get("uid"));
	}
	if (values.containsKey("content")) {
	    this.setContent((String) values.get("content"));
	}
	if (values.containsKey("keywords")) {
	    processKeywords((String) values.get("keywords"));
	}
	if (values.containsKey("title")) {
	    this.setTitle((String) values.get("title"));
	}
	if (values.containsKey("summary")) {
	    this.setSummary((String) values.get("summary"));
	}
	if (values.containsKey("thumbid")) {
	    this.setThumbid((String) values.get("thumbid"));
	}
	if (values.containsKey("modified")) {
	    this.setModified((String) values.get("modified"));
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

	this.setStatus("Dataset is in the Green Category");
	this.setStatusImgURL("/resources/images/green.png");

	this.setDatasetdisplayURL(formatDatasetDisplayURL());

    }

    private void processKeywords(String kw) {
	String[] kwArr = kw.split(";");
	List<String> listTags = Arrays.asList(kwArr);
	ArrayList<String> alTags = new ArrayList<String>(listTags);
	this.setKeywords(alTags);
    }

    /**
     * @return the summary
     */
    @Override
    public String getSummary() {
	return summary;
    }

    /**
     * @param summary
     *            the summary to set
     */
    @Override
    public void setSummary(String summary) {
	this.summary = summary;
    }

    public String getThumbid() {
	return thumbid;
    }

    public void setThumbid(String thumbid) {
	this.thumbid = thumbid;
	this.constructThumbUrl();
    }

    public void constructForwardUrl() {
	super.setForwardUrl(Green.fowardUrlBase + this.getId());
    }

    private void constructThumbUrl() {
	this.thumbUrl = Green.thumbUrlBase + this.getThumbid();
    }

    /**
     * @return the thumbUrl
     */
    public String getThumbUrl() {
	return thumbUrl;
    }

    /**
     * @param thumbUrl
     *            the thumbUrl to set
     */
    public void setThumbUrl(String thumbUrl) {
	this.thumbUrl = thumbUrl;
    }

    public void setId(String id) {
	super.setId(id);
	this.constructForwardUrl();
    }

    public String formatDatasetDisplayURL() {

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
}
