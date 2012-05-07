package org.freshwaterlife.conversion;

import org.jdom.Element;

import org.freshwaterlife.conversion.IXMLOutputSource;

/**
 *
 * @author pjohnson
 */
public class XMLBatchExporter implements IXMLOutputSource {

    private IXMLOutputSource oOutput;
    private int nMaxRecordsPerFile;
    private int nCurrentItemNumber;
    private String sRootElement;
    private Element oRoot;

    public XMLBatchExporter(IXMLOutputSource output, int maxRecordsPerFile, String rootElement) {
	this.oOutput = output;
	this.nMaxRecordsPerFile = maxRecordsPerFile;
	this.nCurrentItemNumber = 0;
	this.sRootElement = rootElement;
	this.oRoot = new Element(this.sRootElement);
    }

    public XMLBatchExporter(IXMLOutputSource output, String rootElement) {
	this(output, 0, rootElement);
    }

    public void process(Element item) {
	this.process(item, "Library:");
    }

    public void process(Element item, String strPIDPrefix) {
	// Add item to root document.
	this.oRoot.addContent(item);

	this.nCurrentItemNumber++;

	System.out.println("Batch Export (" + this.sRootElement + "): Current Total: " + this.nCurrentItemNumber);

	// If we've reached the maximum number of items, save to file and create a new root document.
	if (this.nCurrentItemNumber == this.nMaxRecordsPerFile) {
	    System.out.println("Batch Export (" + this.sRootElement + "): Sending to Output");
	    this.oOutput.process(this.oRoot);
	    this.oRoot = new Element(this.sRootElement);
	    this.nCurrentItemNumber = 0;
	    //writeFile();
	}
    }

    public void finished()
    {
	this.finished("Library:");
    }


    public void finished(String strPIDPrefix) {
	if (this.nCurrentItemNumber > 0) {
	    this.oOutput.process(oRoot, strPIDPrefix);
	    this.oRoot = new Element(this.sRootElement);
	    this.nCurrentItemNumber = 0;
	    //writeFile();
	}
    }
}
