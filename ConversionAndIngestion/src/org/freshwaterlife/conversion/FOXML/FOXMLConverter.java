package org.freshwaterlife.conversion.FOXML;

import java.io.*;
import java.security.interfaces.DSAKey;
import java.util.UUID;
import java.util.List;
import java.util.Iterator;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Namespace;
import org.jdom.Element;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import org.freshwaterlife.conversion.IXMLOutputSource;

/**
 *
 * @author pjohnson
 */
public class FOXMLConverter implements IXMLOutputSource {

    private IXMLOutputSource oOutput;
    private Document oTemplateDoc;
    private Namespace foxmlNS;

    public FOXMLConverter(IXMLOutputSource output, String templateFile) {
	this.oOutput = output;

	// load template file
	FileInputStream fIn;
	try {
	    fIn = new FileInputStream(templateFile);
	} catch (FileNotFoundException e) {
	    System.out.println("Template file not found");
	    return;
	}

	BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));

	SAXBuilder sb = new SAXBuilder();
	try {
	    this.oTemplateDoc = sb.build(reader);
	} catch (JDOMException e) {
	    return;
	} catch (IOException e) {
	    return;
	}

	this.foxmlNS = Namespace.getNamespace("foxml", "info:fedora/fedora-system:def/foxml#");
    }

    public void process(Element item)
    {
	this.process(item, "Library:");
    }
    
    public void process(Element item, String strPIDPrefix) {
	Document newDoc = (Document) this.oTemplateDoc.clone();

	List<Element> list = newDoc.getRootElement().getChildren("datastream", this.foxmlNS);
	String sPID = strPIDPrefix + UUID.randomUUID().toString();
	newDoc.getRootElement().setAttribute("PID", sPID);
	System.out.println("Number of datastreams: " + list.size());

	if (list.size() > 0) {
	    Iterator<Element> itr = list.iterator();
	    Element ds;

	    Namespace oaidcNS = Namespace.getNamespace("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
	    Namespace dcNS = Namespace.getNamespace("dc", "http://purl.org/dc/elements/1.1/");
	    Namespace rdfNS = Namespace.getNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");

	    //List<Element> keywordsXml = new ArrayList<Element>();
	    while (itr.hasNext()) {
		try {
		    ds = itr.next();
		    if (ds.getAttributeValue("ID").compareTo("descMetadata") == 0) {
			System.out.println("Found datastream");
			ds.getChild("datastreamVersion", this.foxmlNS).getChild("xmlContent", this.foxmlNS).addContent(item);
		    } else if (ds.getAttributeValue("ID").compareTo("DC") == 0) {
			System.out.println("Found datastream: DC");
			ds.getChild("datastreamVersion", this.foxmlNS).getChild("xmlContent", this.foxmlNS).getChild("dc", oaidcNS).getChild("identifier", dcNS).setText(sPID);
		    } else if (ds.getAttributeValue("ID").compareTo("RELS-EXT") == 0) {
			System.out.println("Found datastream: RELS-EXT");
			Attribute att = new Attribute("about", "info:fedora/" + sPID, rdfNS);
			ds.getChild("datastreamVersion", this.foxmlNS).getChild("xmlContent", this.foxmlNS).getChild("RDF", rdfNS).getChild("Description", rdfNS).setAttribute(att);
		    } else {
			System.out.println("ID: " + ds.getAttributeValue("ID"));
		    }
		} catch (Exception e) {
		    System.out.println("Exception: " + e.getMessage() + e.getCause() + e.getStackTrace().toString());
		}
	    }
	}

	List<Element> rowDatastreams = item.getChildren("datastream", this.foxmlNS); //found row
	//handle 6 DS's
	System.out.println("Number of row's datastreams: " + rowDatastreams.size());

	if (rowDatastreams.size() > 0) {
	    Iterator<Element> itrRow = rowDatastreams.iterator();
	    Element dsRow;
	    try {
		while (itrRow.hasNext()) {
		    dsRow = itrRow.next();
		    System.out.println("Found row datastream");
		    newDoc.getRootElement().addContent((Element)dsRow.clone());
		}
	    } catch (Exception e) {
		System.out.println("Exception: " + e.getMessage() + e.getCause() + e.getStackTrace().toString());
	    }
	}

	System.out.println("Sending to output");
	this.oOutput.process(
		(Element) newDoc.getRootElement().clone(), strPIDPrefix);
	System.out.println(
		"Back from output");

    }

    public void finished()
    {
	this.finished("Library:");
    }

    public void finished(String strPIDPrefix) {
	this.oOutput.finished(strPIDPrefix);
    }
}
