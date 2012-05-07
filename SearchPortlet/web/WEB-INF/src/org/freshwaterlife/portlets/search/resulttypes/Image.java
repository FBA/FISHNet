/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freshwaterlife.portlets.search.resulttypes;

import org.freshwaterlife.portlets.search.Global;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author root
 */
public class Image extends BasicResultDoc implements Serializable {
    //urls used to construct thumb and link urls

    private static final String fowardUrlBase = "http://www.freshwaterlife.org/imagearchive/main.php?g2_itemId=";
    private static final String thumbUrlBase = "http://www.freshwaterlife.org/imagearchive/main.php?g2_view=core.DownloadItem&g2_itemId=";
    private String thumbid;
    private String thumbUrl;
    private String summary;


    public Image() {
        super();
    }

    public Image(HashMap values) {

        this.setDoctype(Global.IMAGE);
        //super(HashMap values-);
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

    }

    private void processKeywords(String kw) {
        String[] kwArr = kw.split(";");
        List<String> listTags = Arrays.asList(kwArr);
	ArrayList<String> alTags = new ArrayList<String>(listTags);
	this.setKeywords( alTags );
    }

    /**
     * @return the summary
     */
    @Override
    public String getSummary() {
        return summary;
    }

    /**
     * @param summary the summary to set
     */
    @Override
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * @return the thumbid
     */
    public String getThumbid() {
        return thumbid;
    }

    /**
     * @param thumbid the thumbid to set
     */
    public void setThumbid(String thumbid) {
        this.thumbid = thumbid;
        this.constructThumbUrl();
    }

    public void constructForwardUrl() {
        super.setForwardUrl(Image.fowardUrlBase + this.getId());
    }

    private void constructThumbUrl() {
        this.thumbUrl = Image.thumbUrlBase + this.getThumbid();
    }

    /**
     * @return the thumbUrl
     */
    public String getThumbUrl() {
        return thumbUrl;
    }

    /**
     * @param thumbUrl the thumbUrl to set
     */
    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    /**
     * @param id the id to set
     */
    @Override
    public void setId(String id) {
        super.setId(id);
        this.constructForwardUrl();
    }
}
