/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freshwaterlife.portlets.search.process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.freshwaterlife.portlets.search.resulttypes.BasicResultDoc;
import org.freshwaterlife.portlets.search.resulttypes.Image;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author root
 */
public class ProcessResults {

    private String srcUrl;
    private ArrayList<HashMap> resultHashMaps;
    private ArrayList<BasicResultDoc> results;

    public ProcessResults(String url) {
        setSrcUrl(url);
    }

    public boolean process() {
        boolean success = false;

        //check if srcUrl is set
        if (srcUrl == null) {
            return false;
        }

        try {

            //get a jdom document built from xml at the src url
            SAXBuilder builder = new SAXBuilder();
            builder.setValidation(false);
            builder.setIgnoringElementContentWhitespace(true);
            Document src = builder.build(srcUrl);

            //retrieve the list of solr doc resultHashMaps
            Element root = src.getRootElement();
            Element result = root.getChild("result");
            List<Element> docs = result.getChildren("doc");

            //iterate over doc list, for each one put values into a HashMap and add this to a new list
            //then check doctype and create an appropriate result doc for each, add this to results array list
            this.resultHashMaps = new ArrayList(); //make sure we have resultHashMaps list to add to
            this.results = new ArrayList<BasicResultDoc>();
            
            Iterator dcs = docs.iterator();
            while(dcs.hasNext()){
                //HashMap ready for contents
                HashMap docVals = new HashMap();

                Element doc = (Element) dcs.next();
                List<Element> vals = doc.getChildren();
                //now iterate round the values of this doc
                Iterator v = vals.iterator();
                while (v.hasNext()){
                    Element val = (Element) v.next();
                    String key = val.getAttributeValue("name");
                    //get value, could be a str or an int
                    String value = val.getChildText("str");
                    if (value == null) value = val.getChildText("int");
                    if (value == null) value = "error retrieving value";

                    //add to hashmap
                    docVals.put(key, value);
                }

                this.resultHashMaps.add(docVals);

                //check doctype and create result object
                BasicResultDoc r = new Image();
                String doctype = (String) docVals.get("doctype");
                if (doctype.equalsIgnoreCase("image")){
                    r = new Image(docVals);
                }
                results.add(r);

            }

        } catch (JDOMException ex) {
            Logger.getLogger(ProcessResults.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(ProcessResults.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return success;
    }

    /**
     * @return the srcUrl
     */
    public String getSrcUrl() {
        return srcUrl;
    }

    /**
     * @param srcUrl the srcUrl to set
     */
    public void setSrcUrl(String srcUrl) {
        this.srcUrl = srcUrl;
    }

    /**
     * @return the resultHashMaps
     */
    public ArrayList<HashMap> getResultHashMaps() {
        return resultHashMaps;
    }

    /**
     * @param resultHashMaps the resultHashMaps to set
     */
    public void setResultHashMaps(ArrayList<HashMap> results) {
        this.resultHashMaps = results;
    }

    /**
     * @return the results
     */
    public ArrayList<BasicResultDoc> getResults() {
        return results;
    }

    /**
     * @param results the results to set
     */
    public void setResults(ArrayList<BasicResultDoc> results) {
        this.results = results;
    }
}
