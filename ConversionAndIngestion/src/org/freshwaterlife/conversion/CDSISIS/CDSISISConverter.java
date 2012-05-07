package org.freshwaterlife.conversion.CDSISIS;

import java.io.*;

import java.util.List;
import java.util.Iterator;

import org.jdom.CDATA;
import org.jdom.Element;

import org.freshwaterlife.conversion.IXMLOutputSource;

/**
 *
 * @author pjohnson
 */
public class CDSISISConverter implements IXMLOutputSource {

    // Could either be a MODS Converter or a CDSISIS Batch Exporter.
    private IXMLOutputSource oOutput;
    private KeywordLookup oKeywordLookup;
    private String sInvalidElementsFile;
    private DataOutputStream fInvalidElements;
    private int nTotalsInvalidElements = 0;
    private String[] aDocTypeAElements = new String[]{"Mfn", "ANALYTIC_LEVEL_author_s_", "Author_role", "Corporate_author_s_", "Title", "Serial_Journal_title", "Date_of_publication", "Collation", "Other_pagination", "ISBN", "Annotation", "Windermere_shelfmark", "Dorset_shelfmark", "Category", "BL_index", "Wat__Auth_", "Indexing", "Number_of_copies", "Date_of_entry", "Document_type", "Keywords"};
    private String[] aDocTypeBElements = new String[]{"Mfn", "MONOGRAPHIC_LEVEL_author_s_", "Author_role", "Corporate_author_s_", "Title__monogr__", "Edition", "ISBN", "Imprint__Place__Publ___Date_", "Price", "Pages", "Other_pagination", "Annotation", "Windermere_shelfmark", "Dorset_shelfmark", "Category", "BL_index", "Wat__Auth_", "Indexing", "Number_of_copies", "Date_of_entry", "Document_type", "Keywords"};
    private String[] aDocTypeCElements = new String[]{"Mfn", "ANALYTIC_LEVEL_author_s_", "Corporate_author_s_", "Title", "Monographic_source_author_s_", "Corporate_source_author_s_", "Title__monogr__", "Imprint__Place__Publ___Date_", "Extent_of_chapter", "Other_pagination", "Annotation", "Windermere_shelfmark", "Dorset_shelfmark", "Category", "BL_index", "Wat__Auth_", "Indexing", "Number_of_copies", "Date_of_entry", "Document_type", "Keywords"};

    public CDSISISConverter(IXMLOutputSource output, KeywordLookup keywordLookup, String invalidElementsFile) {
	this.oOutput = output;
	this.oKeywordLookup = keywordLookup;
	this.sInvalidElementsFile = invalidElementsFile;
    }

    public void process(Element item) {
	this.process(item, "Library:");
    }

    public void process(Element item, String strPIDPrefix) {
	// Format CDSISIS Record.
	Element newRecord = (Element) item.clone();

	List<Element> list = item.getChildren();

	if (list.size() > 0) {
	    Iterator<Element> itr = list.iterator();

	    String sDocType = "";
	    String sMFN = "";
	    try {
		sDocType = ((Element) item.getChildren("Document_type").get(0)).getTextTrim().replace(".", "");
		sMFN = ((Element) item.getChildren("Mfn").get(0)).getTextTrim();
	    } catch (Exception ex) {
	    }
	    System.out.println("DocType: " + sDocType + " MFN: " + sMFN);

	    Element child;
	    int counter = 0;
	    //List<Element> keywordsXml = new ArrayList<Element>();
	    while (itr.hasNext()) {
		child = itr.next();

		// Check to see if this element is supposed to be present for its doctype (A, B or C).
		if (sDocType != "" && sMFN != "") {
		    if (!checkValidElement(child.getName(), sDocType)) {
			// Found element that shouldn't be in this DocType.
			storeInvalidElement(sMFN, sDocType, child.getName());
		    }
		}

		// Make sure there are Index items and the Keywords elements havent already been added previously.
		if (child.getName().compareTo("Indexing") == 0 && item.getChildren("Keywords").size() == 0) {
		    // Add Keyword Strings to element for indexing.
		    Element keywords = processIndex(child);
		    if (keywords != null) {
			newRecord.addContent(keywords);
		    } else {
			// Remove the Indexing element
			newRecord.removeContent((Element) newRecord.getChildren("Indexing").get(counter));
			counter--;
		    }

		    counter++;
		}


	    }

	}

	this.oOutput.process(newRecord);

    }

    private void storeInvalidElement(String mfn, String docType, String elementName) {
	System.out.println("Invalid Element (See Invalid Element xml file for details):");

	try {
	    if (this.nTotalsInvalidElements == 0) {
		createInvalidElementsFile();
	    }
	    this.nTotalsInvalidElements++;
	    this.fInvalidElements.writeBytes("Invalid Element. MFN: " + mfn + " DocType:" + docType + " Element: " + elementName + "\n");
	} catch (IOException ex) {
	    System.out.println("Could not write to error file");
	}
    }

    private void createInvalidElementsFile() {
	FileOutputStream fos;
	try {
	    fos = new FileOutputStream(this.sInvalidElementsFile);
	} catch (FileNotFoundException e) {
	    System.out.println("Errors file cannot be created");
	    return;
	}

	fInvalidElements = new DataOutputStream(fos);
    }

    private boolean checkValidElement(String elementName, String docType) {
	String[] aElements;
	if (docType.compareTo("A") == 0) {
	    aElements = this.aDocTypeAElements;
	} else if (docType.compareTo("B") == 0) {
	    aElements = this.aDocTypeBElements;
	} else if (docType.compareTo("C") == 0) {
	    aElements = this.aDocTypeCElements;
	} else {
	    return true;
	}

	for (String sElement : aElements) {
	    if (elementName.compareTo(sElement) == 0) {
		return true;
	    }
	}
	return false;
    }

    public void finished()
    {
	this.finished("Library:");
    }

    public void finished(String strPIDPrefix) {
	this.oOutput.finished(strPIDPrefix);
    }

    public Element processIndex(Element index) {
	String sIndex = index.getText().trim();
	if (sIndex.endsWith(".")) {
	    sIndex = sIndex.substring(0, sIndex.length() - 1);
	}

	String sKeywords = this.oKeywordLookup.getKeywords(sIndex);

	if (sKeywords != null && sKeywords.length() > 0) {
	    Element keywords = new Element("Keywords");
	    keywords.setAttribute("ID", sIndex);

	    String[] aKeywords = sKeywords.split(";");

	    for (String key : aKeywords) {
		Element temp = new Element("Keyword").setContent(new CDATA(key));
		keywords.addContent(temp);
	    }
	    return keywords;
	}

	return null;

    }
}
