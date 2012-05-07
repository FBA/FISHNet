/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freshwaterlife.portlets.dataset.process;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.freshwaterlife.portlets.dataset.Global;
import org.freshwaterlife.portlets.dataset.resulttypes.BasicResultDoc;
import org.freshwaterlife.portlets.dataset.resulttypes.Green;
import org.freshwaterlife.portlets.dataset.resulttypes.Orange;
import org.freshwaterlife.portlets.dataset.resulttypes.Red;
import org.freshwaterlife.portlets.dataset.resulttypes.RedOrange;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Attribute;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author root
 */
public class ProcessResults implements Serializable {

    private static final long serialVersionUID = 1L;
    private String srcUrl;
    private ArrayList<HashMap> resultHashMaps;
    private ArrayList<BasicResultDoc> results;
    private int iNumFound;
    private int iNumOfRedResults;
    private int iNumOfOrangeResults;
    private int iNumOfGreenResults;
    private int iNumOfRedOrangeResults;
    private int iNumOfDatasetResults;
    private int iNumOfReviewableResults;
    private int iNumOfRecommendedForDOIResults;

    //**************//
    // Constructors //
    //**************//
    public ProcessResults() {
	setSrcUrl("");
    }

    public ProcessResults(String url) {
	setSrcUrl(url);
    }

    //**************************************************************************
    public boolean process() {
	boolean success = false;
	iNumOfRedResults = 0;
	iNumOfOrangeResults = 0;
	iNumOfGreenResults = 0;
	iNumOfRedOrangeResults = 0;
	iNumOfDatasetResults = 0;
	iNumOfReviewableResults = 0;
	iNumOfRecommendedForDOIResults = 0;
	String value = "";

	//check if srcUrl is set
	if (srcUrl == null) {
	    this.results = new ArrayList<BasicResultDoc>();
	    return false;
	}

	try {
	    //get a jdom document built from xml at the src url
	    SAXBuilder builder = new SAXBuilder();
	    builder.setValidation(false);
	    builder.setIgnoringElementContentWhitespace(true);
	    Document src = builder.build(srcUrl);

	    Element root = src.getRootElement();

	    // Get and set totals.
	    try {
		List<Element> counts = root.getChildren("lst");
		Iterator countsIt = counts.iterator();
		while (countsIt.hasNext()) {
		    Element count = (Element) countsIt.next();
		    if (count.getAttributeValue("name").compareTo("facet_counts") == 0) {
			List<Element> fields = count.getChildren("lst");
			Iterator fieldsIt = fields.iterator();
			while (fieldsIt.hasNext()) {
			    Element field = (Element) fieldsIt.next();
			    if (field.getAttributeValue("name").compareTo("facet_fields") == 0) {
				List<Element> doctypes = field.getChildren("lst");
				Iterator doctypesIt = doctypes.iterator();
				while (doctypesIt.hasNext()) {
				    Element doctype = (Element) doctypesIt.next();
				    if (doctype.getAttributeValue("name").compareTo("doctype") == 0) {
					List<Element> totals = doctype.getChildren("int");
					Iterator totalsIt = totals.iterator();
					while (totalsIt.hasNext()) {
					    Element total = (Element) totalsIt.next();
					    if (total.getAttributeValue("name").compareTo("red") == 0) {
						iNumOfRedResults = Integer.parseInt(total.getValue());
					    } else if (total.getAttributeValue("name").compareTo("redorange") == 0) {
						iNumOfRedOrangeResults = Integer.parseInt(total.getValue());
					    } else if (total.getAttributeValue("name").compareTo("orange") == 0) {
						iNumOfOrangeResults = Integer.parseInt(total.getValue());
					    } else if (total.getAttributeValue("name").compareTo("green") == 0) {
						iNumOfGreenResults = Integer.parseInt(total.getValue());
					    } else if (total.getAttributeValue("name").compareTo("dataset") == 0) {
						iNumOfDatasetResults = Integer.parseInt(total.getValue());
					    } else if (total.getAttributeValue("name").compareTo("reviewable") == 0) {
						iNumOfReviewableResults = Integer.parseInt(total.getValue());
					    }
					}
					break; // exit while loop
				    }
				}
				break; // exit while loop
			    }
			}
			break; // exit while loop
		    }
		}
	    } catch (Exception e) {
		System.out.println("Error Getting Totals From Solr Result: " + e);
	    }

	    //retrieve the list of solr doc resultHashMaps
	    Element result = root.getChild("result");

	    List<Element> docs = result.getChildren("doc");

	    //total number of results back from solr
	    Attribute numFound = result.getAttribute("numFound");
	    this.setINumFound(numFound.getIntValue());

	    //iterate over doc list, for each one put values into a HashMap and add this to a new list
	    //then check doctype and create an appropriate result doc for each, add this to results array list
	    this.resultHashMaps = new ArrayList(); //make sure we have resultHashMaps list to add to
	    this.results = new ArrayList<BasicResultDoc>();

	    //Check if no results returned, and put suitable message entry in result list
	    if (docs.isEmpty()) {
		System.out.println("docs is empty");
	    } else {
		Iterator dcs = docs.iterator();
		while (dcs.hasNext()) {
		    //HashMap ready for contents
		    HashMap docVals = new HashMap();

		    Element doc = (Element) dcs.next();
		    List<Element> vals = doc.getChildren();
		    //now iterate round the values of this doc
		    Iterator v = vals.iterator();
		    while (v.hasNext()) {
			Element val = (Element) v.next();
			String key = val.getAttributeValue("name");

			// PJ - Found that solr sometimes returns <arr><str></str></arr>
			//      ... but sometimes just returns <str></str>.

			if (val.getName().compareTo("arr") == 0) {

			    //might contain multiple children values
			    List lChildList = val.getChildren();

			    if (lChildList.size() > 1) {
				String strTemp = "";
				Iterator c = lChildList.iterator();

				while (c.hasNext()) {
				    Element cVal = (Element) c.next();

				    strTemp += cVal.getValue() + ", ";
				}
				value = strTemp.substring(0, strTemp.lastIndexOf(","));
			    } else {
				//get value, could be a str or an int
				value = val.getChildText("str");
				if (value == null) {
				    value = val.getChildText("int");
				}
				if (value == null) {
				    value = val.getChildText("long");
				}
				if (value == null) {
				    value = "error retrieving value";
				}
			    }
			} else {
			    //get value, could be a str or an int
			    value = val.getText();
			    if (value == null) {
				value = "error retrieving value";
			    }
			}
			//add to hashmap
			docVals.put(key, value);
		    }

		    this.resultHashMaps.add(docVals);

		    //check doctype and create result object
		    BasicResultDoc r;
		    String doctype = (String) docVals.get("mods.datasetCategory");
		    if ((doctype != null) && (doctype.equalsIgnoreCase(Global.arrCategories[Global.GREEN]))) {
			r = new Green(docVals);
			results.add(r);
			iNumOfGreenResults++;
		    } else if (doctype.equalsIgnoreCase(Global.arrCategories[Global.RED])) {
			r = new Red(docVals);
			results.add(r);
			iNumOfRedResults++;
		    } else if (doctype.equalsIgnoreCase(Global.arrCategories[Global.ORANGE])) {
			r = new Orange(docVals);
			results.add(r);
			iNumOfOrangeResults++;
		    } else if (doctype.equalsIgnoreCase(Global.arrCategories[Global.REDORANGE])) {
			r = new RedOrange(docVals);
			results.add(r);
			iNumOfRedOrangeResults++;
		    }
		}
	    }
	} catch (JDOMException ex) {
	    System.out.println("JDOM Error: " + ex.getMessage());
	    Logger.getLogger(ProcessResults.class.getName()).log(Level.SEVERE, null, ex);
	    this.results = new ArrayList<BasicResultDoc>();
	    return false;
	} catch (IOException ex) {
	    System.out.println("IO Error: " + ex.getMessage());
	    Logger.getLogger(ProcessResults.class.getName()).log(Level.SEVERE, null, ex);
	    this.results = new ArrayList<BasicResultDoc>();
	    return false;
	}

	return success;
    }

    //*********************//
    // Getters and Setters //
    //*********************//
    public String getSrcUrl() {
	return srcUrl;
    }

    public void setSrcUrl(String srcUrl) {
	this.srcUrl = srcUrl;
    }

    public ArrayList<HashMap> getResultHashMaps() {
	return resultHashMaps;
    }

    public void setResultHashMaps(ArrayList<HashMap> results) {
	this.resultHashMaps = results;
    }

    public ArrayList<BasicResultDoc> getResults() {
	return results;
    }

    public void setResults(ArrayList<BasicResultDoc> results) {
	this.results = results;
    }

    public int getINumFound() {
	return iNumFound;
    }

    public void setINumFound(int iNumFound) {
	this.iNumFound = iNumFound;
    }

    public int getINumOfOrangeResults() {
	return iNumOfOrangeResults;
    }

    public void setINumOfOrangeResults(int iNumOfOrangeResults) {
	this.iNumOfOrangeResults = iNumOfOrangeResults;
    }

    public int getINumOfRedResults() {
	return iNumOfRedResults;
    }

    public void setINumOfRedResults(int iNumOfRedResults) {
	this.iNumOfRedResults = iNumOfRedResults;
    }

    public int getINumOfRedOrangeResults() {
	return iNumOfRedOrangeResults;
    }

    public void setINumOfRedOrangeResults(int iNumOfRedOrangeResults) {
	this.iNumOfRedOrangeResults = iNumOfRedOrangeResults;
    }

    public int getINumOfGreenResults() {
	return iNumOfGreenResults;
    }

    public void setINumOfGreenResults(int iNumOfGreenResults) {
	this.iNumOfGreenResults = iNumOfGreenResults;
    }

    public int getINumOfDatasetResults() {
	return iNumOfDatasetResults;
    }

    public void setINumOfDatasetResults(int iNumOfDatasetResults) {
	this.iNumOfDatasetResults = iNumOfDatasetResults;
    }

    public int getINumOfReviewableResults() {
	return iNumOfReviewableResults;
    }

    public void setINumOfReviewableResults(int iNumOfReviewableResults) {
	this.iNumOfReviewableResults = iNumOfReviewableResults;
    }

    public int getINumOfRecommendedForDOIResults() {
	return iNumOfRecommendedForDOIResults;
    }

    public void setINumOfRecommendedForDOIResults(int iNumOfRecommendedForDOIResults) {
	this.iNumOfRecommendedForDOIResults = iNumOfRecommendedForDOIResults;
    }
}
