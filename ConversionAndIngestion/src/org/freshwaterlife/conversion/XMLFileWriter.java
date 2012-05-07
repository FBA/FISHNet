package org.freshwaterlife.conversion;

import java.io.*;

import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.Element;

import org.freshwaterlife.conversion.IXMLOutputSource;

/**
 *
 * @author pjohnson
 */
public class XMLFileWriter implements IXMLOutputSource {

    private String sOutputFile;
    public int nCurrentFileNumber;

    public XMLFileWriter(String outputFile) {
	this.sOutputFile = outputFile;
	this.nCurrentFileNumber = 1;
    }

    public void process(Element item) {
	this.process(item, "Library:");
    }

    public void process(Element item, String strPIDPrefix) {
	try {
	    int nPos = this.sOutputFile.lastIndexOf(".");
	    String sFilename = this.sOutputFile.substring(0, nPos) + this.nCurrentFileNumber + this.sOutputFile.substring(nPos);
	    FileWriter fw = new FileWriter(sFilename);
	    XMLOutputter outp2 = new XMLOutputter();
	    outp2.setFormat(Format.getPrettyFormat());
	    //Document tempDoc = new Document();
	    //tempDoc.setRootElement(item);
	    outp2.output(item, fw);
	    this.nCurrentFileNumber++;
	    fw.close();
	} catch (IOException ex) {
	    System.out.println("Error writing file: " + ex.getMessage());
	} catch (Exception e) {
	    System.out.println("Error writing file: " + e.getMessage());
	}
    }
    public void finished()
    {
	this.finished("Library:");
    }

    public void finished(String strPIDPrefix) {
    }
}
