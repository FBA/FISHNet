/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freshwaterlife.portlets.search.resulttypes;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author root
 */
public abstract class BasicResultDoc {
    //doctypes
    public static final int LIFERAYDOC = 1;
    public static final int IMAGE = 2;

    private String id; //maps to uid
    private String title; //maps to the name field and title field
    private int doctype; //use the static variables
    private ArrayList<String> keywords; //maps to tagentries
    private String content;
    private String forwardUrl; //from results if present, constructed in some cases - e.g. images, from other fields

    public BasicResultDoc() {
    }

    public BasicResultDoc(int doctype, String name, String content) {
        this.doctype = doctype;
        this.title = name;
        this.content = content;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the doctype
     */
    public int getDoctype() {
        return doctype;
    }

    /**
     * @param doctype the doctype to set
     */
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
        while (i.hasNext()){
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

    /**
     * @return the forwardUrl
     */
    public String getForwardUrl() {
        return forwardUrl;
    }

    /**
     * @param forwardUrl the forwardUrl to set
     */
    public void setForwardUrl(String forwardUrl) {
        this.forwardUrl = forwardUrl;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
}
