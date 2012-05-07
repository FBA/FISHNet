package org.freshwaterlife.conversion;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

public class MergeXml {

    public static void main(String[] args) {
	String itemdataFilename = "C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/itemdata.xml";
	String admindataFilename = "C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/admindata.xml";
	String customdataFilename = "C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/customdata.xml";

	FileInputStream fInItems;
	FileInputStream fInAdmin;
	FileInputStream fInCustom;

	try {
	    fInItems = new FileInputStream(itemdataFilename);
	    fInAdmin = new FileInputStream(admindataFilename);
	    fInCustom = new FileInputStream(customdataFilename);
	} catch (FileNotFoundException e) {
	    System.out.println("Input file not found");
	    return;
	}

	BufferedReader readerItem = new BufferedReader(new InputStreamReader(fInItems));
	BufferedReader readerAdmin = new BufferedReader(new InputStreamReader(fInAdmin));
	BufferedReader readerCustom = new BufferedReader(new InputStreamReader(fInCustom));

	Element itemRows;
	Element adminRows;
	Element customRows;

	// Buld up xml file...
	try {
	    //System.out.println(readerItem.readLine());
	    SAXBuilder sb = new SAXBuilder();
	    Document doc = sb.build(readerItem);
	    //System.out.println(doc.hasRootElement());
	    //System.out.println(doc.getRootElement());
	    itemRows = doc.getRootElement();
	    adminRows = sb.build(readerAdmin).getRootElement();
	    customRows = sb.build(readerCustom).getRootElement();

	    List<Element> items;

	    items = itemRows.getChildren("row");
	    if (items.size() > 0) {

		Iterator<Element> itr = items.iterator();
		Element child;
		//System.out.println("Count: " + items.size());
		Element newItemRows = (Element) itemRows.clone();
		int ctr = 0;
		while (itr.hasNext()) {
		    HashMap<String, Element> hm = new HashMap<String, Element>();
		    hm.put("max_res", new Element("field").setAttribute("name", "max_res").setText(""));
		    hm.put("fba_rights", new Element("field").setAttribute("name", "fba_rights").setText(""));
		    hm.put("req_processing", new Element("field").setAttribute("name", "req_processing").setText(""));
		    hm.put("owner_charge", new Element("field").setAttribute("name", "owner_charge").setText(""));
		    hm.put("metadata_complete", new Element("field").setAttribute("name", "metadata_complete").setText(""));
		    hm.put("metadata_reviewed_by", new Element("field").setAttribute("name", "metadata_reviewed_by").setText(""));

		    hm.put("geo_keywords", new Element("field").setAttribute("name", "geo_keywords").setText(""));
		    hm.put("copyright_owner", new Element("field").setAttribute("name", "copyright_owner").setText(""));
		    hm.put("species_name", new Element("field").setAttribute("name", "species_name").setText(""));
		    hm.put("species_authority", new Element("field").setAttribute("name", "species_authority").setText(""));
		    hm.put("authority_date", new Element("field").setAttribute("name", "authority_date").setText(""));
		    hm.put("taxonomy_require_class", new Element("field").setAttribute("name", "taxonomy_require_class").setText(""));
		    hm.put("orig_format", new Element("field").setAttribute("name", "orig_format").setText(""));
		    hm.put("orig_height", new Element("field").setAttribute("name", "orig_height").setText(""));
		    hm.put("orig_width", new Element("field").setAttribute("name", "orig_width").setText(""));
		    hm.put("date_taken", new Element("field").setAttribute("name", "date_taken").setText(""));
		    hm.put("artist_photographer", new Element("field").setAttribute("name", "artist_photographer").setText(""));

		    child = itr.next();

		    // Get Row Id.
		    String itemId = ((Element) (child.getChildren("field").get(0))).getValue();

		    // Find Matching Row In Admin Items
		    String rowPath = "/ROOT/row[./field[@name = 'gcl_itemId' and text() = '" + itemId + "']]/field[@name != 'gcl_itemId' and text() != '']";
		    //String rowPath = "/ROOT/row[./field[@name = 'gcl_itemId' and text() = '" + itemId + "']]/field[(not(@name = following::field/@name) or text() != '') and (@name != 'gcl_itemId') and not(@name = preceding::field[text() != '']/@name)]";
		    ////String rowPath = "/ROOT/row[./field[@name = 'gcl_itemId' and text() = '" + itemId + "']]/field[(not(@name = following::row[./field[@name = 'gcl_itemId' and text() = '" + itemId + "']]/field/@name) or text() != '') and (@name != 'gcl_itemId') and not(@name = preceding::row[./field[@name = 'gcl_itemId' and text() = '" + itemId + "']]/field[text() != '']/@name)]";
		    //String rowPath = "/ROOT/row[./field[@name = 'gcl_itemId' and text() = '" + itemId + "']]/field[@name != 'gcl_itemId']";
		    //System.out.println(rowPath);
		    XPath xpath = XPath.newInstance(rowPath);
		    List fields = xpath.selectNodes(adminRows);

		    if (fields.size() > 0) {
			Iterator<Element> aItr = fields.iterator();
			Element field;

			while (aItr.hasNext()) {
			    field = aItr.next();
			    String value = field.getText();
			    field.setContent(new CDATA(value));
			    hm.put(field.getAttributeValue("name"), (Element) field.detach());
			    //System.out.println(field.getName() + field.getAttributeValue("name"));
			    //rw.addContent(field.detach());
			}
		    }

		    // Find Matching Row In Custom Items
		    //rowPath = "/ROOT/row[./field[@name = 'gcl_itemId' and text() = '" + itemId + "']]/field[@name != 'gcl_itemId' and text() != '']";
		    //rowPath = "/ROOT/row[./field[@name = 'gcl_itemId' and text() = '" + itemId + "']]/field[(not(@name = following::field/@name) or text() != '') and (@name != 'gcl_itemId') and not(@name = preceding::field[text() != '']/@name)]";
		    //rowPath = "/ROOT/row[./field[@name = 'gcl_itemId' and text() = '" + itemId + "']]/field[(not(@name = following::field[../field[@name = 'gcl_itemId' and text() = '" + itemId + "']]/@name) or text() != '') and (@name != 'gcl_itemId') and not(@name = preceding::field[../field[@name = 'gcl_itemId' and text() = '" + itemId + "']][text() != '']/@name)]";
		    ////rowPath = "/ROOT/row[./field[@name = 'gcl_itemId' and text() = '" + itemId + "']]/field[(not(@name = following::row[./field[@name = 'gcl_itemId' and text() = '" + itemId + "']]/field/@name) or text() != '') and (@name != 'gcl_itemId') and not(@name = preceding::row[./field[@name = 'gcl_itemId' and text() = '" + itemId + "']]/field[text() != '']/@name)]";
		    //rowPath = "/ROOT/row[./field[@name = 'gcl_itemId' and text() = '" + itemId + "']]/field[(not(@name = following::row[./field[@name = 'gcl_itemId' and text() = '" + itemId + "']]/field/@name) or text() != '') and (@name != 'gcl_itemId')]";
		    rowPath = "/ROOT/row[./field[@name = 'gcl_itemId' and text() = '" + itemId + "']]/field[@name != 'gcl_itemId' and text() != '']";
		    xpath = XPath.newInstance(rowPath);
		    fields = xpath.selectNodes(customRows);

		    if (fields.size() > 0) {
			Iterator<Element> aItr = fields.iterator();
			Element field;
			while (aItr.hasNext()) {
			    field = aItr.next();
			    String value = field.getText();
			    //System.out.println(value);
			    field.setContent(new CDATA(value));
			    hm.put(field.getAttributeValue("name"), (Element) field.detach());
			    //System.out.println(field.getName() + field.getAttributeValue("name"));
			    //rw.addContent(field.detach());
			}
		    }

		    Element rw = ((Element) newItemRows.getChildren("row").get(ctr));

		    // Add CData Element to all fields in itemData.
		    List<Element> flds = rw.getChildren("field");
		    Iterator<Element> fldItr = flds.iterator();
		    Element field;
		    while (fldItr.hasNext()) {
			field = fldItr.next();
			String value = field.getText();
			//System.out.println(value);
			field.setContent(new CDATA(value));
		    }

		    Collection<Element> c = hm.values();
		    Iterator<Element> itrC = c.iterator();
		    while (itrC.hasNext()) {
			rw.addContent((Element) itrC.next());
		    }

		    System.out.println("itemId: " + itemId);

		    //if (itemId.compareTo("2498") == 0) {
		    //	break;
		    //}
		    // Find Matching Row In Custom items
		    ctr++;
		}
		try {
		    //System.out.println(newItemRows.toString());
		    String sFilename = "C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/output.xml";
		    FileWriter fw = new FileWriter(sFilename);
		    XMLOutputter outp2 = new XMLOutputter();
		    outp2.setFormat(Format.getPrettyFormat());
		    //Document tempDoc = new Document();
		    //tempDoc.setRootElement(item);
		    outp2.output(newItemRows, fw);
		} catch (IOException ex) {
		    System.out.println("Error writing file: " + ex.getMessage());
		} catch (Exception e) {
		    System.out.println("Error writing file: " + e.getMessage());
		}
	    }
	    System.out.println("Finished");
	} catch (JDOMException e) {
	    System.out.println("JDOM Exception: " + e.getMessage());
	} catch (IOException e) {
	    System.out.println("IOEXception: " + e.getMessage());
	}
    }
}
