package org.freshwaterlife.conversion;

import java.io.*;
import java.util.List;
import java.util.Iterator;

import org.jdom.Namespace;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.JDOMException;

/**
 *
 * @author pjohnson
 */
public class XMLBatchImporter implements IXMLOutputSource {

    private IXMLOutputSource oOutput;
    private String sChildElement;
    private Namespace oNamespace;

    public XMLBatchImporter(IXMLOutputSource output, String childElement, Namespace ns) {
	this.oOutput = output;
	this.sChildElement = childElement;
	this.oNamespace = ns;
    }

    public XMLBatchImporter(IXMLOutputSource output) {
	this(output, null, null);
    }

    public void process(Element item) {
	this.process(item, "Library:");
    }

    public void process(Element item, String strPIDPrefix) {
	System.out.println("batch import");

	System.out.println(item.getChildren().size());
	System.out.println(item.getChildren().get(0).toString());

	List<Element> list;
	if (this.sChildElement != null) {
	    if (this.oNamespace != null) {
		list = item.getChildren(this.sChildElement, this.oNamespace);
	    } else {
		list = item.getChildren(this.sChildElement);
	    }

	    if (list.size() > 0) {
		Iterator<Element> itr = list.iterator();
		Element child;

		while (itr.hasNext()) {
		    child = itr.next();
		    this.oOutput.process((Element) child.clone(), strPIDPrefix);
		}
	    }
	} else {
	    this.oOutput.process((Element) item.clone());
	}

    }

    public void readFile(String inputFile) {
	// Read source file
	FileInputStream fIn;

	try {
	    fIn = new FileInputStream(inputFile);
	} catch (FileNotFoundException e) {
	    System.out.println("Input file not found");
	    return;
	}

	BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));

	// Buld up xml file...
	try {
	    SAXBuilder sb = new SAXBuilder();
	    Element el = sb.build(reader).getRootElement();
	    // Process XML File
	    process(el, "Image:");
	    this.oOutput.finished("Image:");
	} catch (JDOMException e) {
	    System.out.println("Invalid input file format" + e.getMessage());
	} catch (IOException e) {
	    System.out.println("Invalid input file format" + e.getMessage());
	}
    }

    public void finished()
    {
	this.finished("Library:");
    }

    public void finished(String strPIDPrefix) {
	this.oOutput.finished(strPIDPrefix);
    }
}
