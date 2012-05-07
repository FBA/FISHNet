/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freshwaterlife.portlets.search.resulttypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;

import org.freshwaterlife.portlets.dataset.display.my.Global;

/**
 *
 * @author root
 */
public abstract class BasicResultDoc implements Serializable {
    //doctypes
    private String id; //maps to uid
    private String title; //maps to the name field and title field
    private int doctype; //use the static variables
    private ArrayList<String> keywords; //maps to tagentries
    private String forwardUrl; //from results if present, constructed in some cases - e.g. images, from other fields
    private String summary;
    private Long groupId;
    private String content;
    private String modified;
    private String status;

    protected String modsTitle;

    public BasicResultDoc() {

        this.doctype = Global.IMAGE;
        this.title = "unknown";
        this.content = "unknown content";

        this.forwardUrl = "http://new.freshwaterlife.org";
        this.id = "placeholderId";

    }

    public BasicResultDoc(int doctype, String name, String content) {
        this.doctype = doctype;
        this.title = name;
        this.content = content;
        this.forwardUrl = "http://new.freshwaterlife.org";
        this.id = "placeholderId";

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

}
