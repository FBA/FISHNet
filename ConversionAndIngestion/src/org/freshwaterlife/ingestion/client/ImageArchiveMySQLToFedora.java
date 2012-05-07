/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freshwaterlife.ingestion.client;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.ListIterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.freshwaterlife.conversion.FOXML.FOXMLConverter;
import org.freshwaterlife.conversion.XMLBatchExporter;
import org.freshwaterlife.conversion.XMLBatchImporter;
import org.freshwaterlife.conversion.XMLFileWriter;
import org.freshwaterlife.conversion.XSLTConverter;
import org.freshwaterlife.ingestion.FOXMLIngestor;
import org.jdom.Namespace;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author sfox
 */
public class ImageArchiveMySQLToFedora {

    public static void main(String[] args) {

	try {
	    Class.forName("com.mysql.jdbc.Driver");
	    String url = "jdbc:mysql://MYSQLSERVER:3306/gallery";
	    Connection con = DriverManager.getConnection(url, "USERNAME", "PASSWORD");
	    String strFieldPairValue = "";
	    //System.out.println("URL: " + url);
	    //System.out.println("Connection: " + con);

	    Statement stmt = null;
	    ResultSet rs = null;
	    //SQL query command
	    String SQL = "SELECT gcl_id,gcl_title,gcl_description,gcl_keywords,gcl_originationTimestamp,modified FROM gtb_item";

	    stmt = con.createStatement();
	    rs = stmt.executeQuery(SQL);
	    ArrayList resultlist = getResult(rs);


	    ListIterator listIterator = resultlist.listIterator();

	    while (listIterator.hasNext()) {

		int intCurrentresult = listIterator.nextIndex();
		Map map = (Map) listIterator.next();
		Map mergedmap = new HashMap();

		Object value = map.get("gcl_id");
		//System.out.println(value);

		//append the datamap fields

		SQL = "select gcl_field, gcl_value from gtb_admindatamap where gcl_itemId = " + value;
		rs = stmt.executeQuery(SQL);
		ArrayList datamaplist = getResult(rs);

		mergedmap.clear();
		mergedmap.putAll(map);
		if (datamaplist.size() > 0) {
		    ListIterator datamaplistIterator = datamaplist.listIterator();

		    while (datamaplistIterator.hasNext()) {
			Map pairs = (Map) datamaplistIterator.next();
			Iterator it = pairs.entrySet().iterator();
			while (it.hasNext()) {
			    Map.Entry valuepairs = (Map.Entry) it.next();
			    Map.Entry fieldpairs = (Map.Entry) it.next();

			    if (fieldpairs.getValue().equals("Max Freely Available Resolution")) {
				strFieldPairValue = "max_res";
			    } else if (fieldpairs.getValue().equals("FBA Usage Rights")) {
				strFieldPairValue = "fba_rights";
			    } else if (fieldpairs.getValue().equals("Request Processing")) {
				strFieldPairValue = "req_processing";
			    } else if (fieldpairs.getValue().equals("Modified By")) {
				strFieldPairValue = "modified_by";
			    } else if (fieldpairs.getValue().equals("Metadata Complete?")) {
				strFieldPairValue = "metadata_complete";
			    } else if (fieldpairs.getValue().equals("Reviewed By")) {
				strFieldPairValue = "metadata_reviewed_by";
			    } else {
				strFieldPairValue = "unknown";
			    }

			    mergedmap.put(strFieldPairValue, ((String) valuepairs.getValue()));

			}
		    }
		    resultlist.set(intCurrentresult, mergedmap);
		}

		//append the custom map fields

		SQL = "select gcl_field, gcl_value from gtb_customfieldmap where gcl_itemId = " + value;
		rs = stmt.executeQuery(SQL);
		ArrayList custommaplist = getResult(rs);

		if (custommaplist.size() > 0) {
		    ListIterator custommaplistIterator = custommaplist.listIterator();

		    while (custommaplistIterator.hasNext()) {
			Map pairs = (Map) custommaplistIterator.next();
			Iterator it = pairs.entrySet().iterator();
			while (it.hasNext()) {
			    Map.Entry valuepairs = (Map.Entry) it.next();
			    Map.Entry fieldpairs = (Map.Entry) it.next();

			    if (fieldpairs.getValue().equals("Geographic Keywords")) {
				strFieldPairValue = "geo_keywords";
			    } else if (fieldpairs.getValue().equals("Copyright Owner")) {
				strFieldPairValue = "copyright_owner";
			    } else if (fieldpairs.getValue().equals("Scientific Name")) {
				strFieldPairValue = "species_name";
			    } else if (fieldpairs.getValue().equals("Species Authority")) {
				strFieldPairValue = "species_authority";
			    } else if (fieldpairs.getValue().equals("Authority Date")) {
				strFieldPairValue = "authority_date";
			    } else if (fieldpairs.getValue().equals("Taxonomy requires further clarification")) {
				strFieldPairValue = "taxonomy_require_class";
			    } else if (fieldpairs.getValue().equals("Original Format")) {
				strFieldPairValue = "original_format";
			    } else if (fieldpairs.getValue().equals("Original Height (cm)")) {
				strFieldPairValue = "original_height_cm";
			    } else if (fieldpairs.getValue().equals("Original Width (cm)")) {
				strFieldPairValue = "original_width_cm";
			    } else if (fieldpairs.getValue().equals("Date taken/drawn")) {
				strFieldPairValue = "date_taken_drawn";
			    } else if (fieldpairs.getValue().equals("Artist Photographer")) {
				strFieldPairValue = "artist_photographer";
			    } else {
				strFieldPairValue = "unknown";
			    }
			    mergedmap.put(strFieldPairValue, (String) valuepairs.getValue());

			}
		    }
		    resultlist.set(intCurrentresult, mergedmap);
		}
		//add parent id
		SQL = "select  gcl_parentId from gtb_childentity where gcl_Id = " + value;
		rs = stmt.executeQuery(SQL);

		rs.next();
		String strParentId = rs.getString(1); //as theres only 1 column returned, and 1 parentid
		mergedmap.put("parent_id", strParentId);
	    }

	    con.close();

	    // now write out the xml file

	    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

	    //root elements
	    Document doc = docBuilder.newDocument();
	    Element rootElement = doc.createElement("ROOT");
	    doc.appendChild(rootElement);

	    //rows from resultlist

	    Iterator itr = resultlist.iterator();

	    while (itr.hasNext()) {
		Element rowElement = doc.createElement("row");
		rootElement.appendChild(rowElement);

		//add key and value pairs
		Map pairs = (Map) itr.next();
		Iterator it = pairs.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry valuepairs = (Map.Entry) it.next();
		    Element pairElement = doc.createElement("field");

		    pairElement.setAttribute("name", (String) valuepairs.getKey());

		    CDATASection cdsection = doc.createCDATASection((String) valuepairs.getValue());
		    pairElement.appendChild(cdsection);

		    rowElement.appendChild(pairElement);
		}

	    }

	    //write the content into xml file
	    System.out.println("Writing XML File");

	    args = new String[4];

	    args[0] = "C:/Users/sfox.FBAHQ/Documents/imagedata/imageArchiveOutput.xml";

	    String xmlInputFile = args[0];

	    TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    Transformer transformer = transformerFactory.newTransformer();
	    DOMSource source = new DOMSource(doc);
	    StreamResult result = new StreamResult(new File(xmlInputFile));
	    transformer.transform(source, result);

//XML to MODS

	    args[1] = "row";
	    args[2] = "C:/Users/sfox.FBAHQ/Documents/imagedata/imageArchiveOutput.mods.xml";
	    args[3] = "C:/Users/sfox.FBAHQ/Documents/imagedata/mods.xslt";

	    String xmlChildElement = args[1];
	    String modsOutputFile = args[2];
	    String modsXSLTFile = args[3];

	    // Writes XML to a file.
	    XMLFileWriter fw = new XMLFileWriter(modsOutputFile);

	    // Transforms CDS/ISIS record to MODS record and passes each one onto the supplied output source (XML Writer).
	    XSLTConverter modsConv = new XSLTConverter(fw, modsXSLTFile);

	    // Batches the elements that are sent to it and when limit is reached, send to the specified output.
	    XMLBatchExporter batch = new XMLBatchExporter(modsConv, "ROOT");

	    XMLBatchImporter imp = new XMLBatchImporter(batch, xmlChildElement, null);

	    imp.readFile(xmlInputFile);
	    

//MODS to Fedora

	    String modsInputFile = "DRIVE:/PATH/imageArchiveOutput.mods1.xml";
	    String modsChildElement = "row";
	    Namespace modsNamespace = null;//Namespace.getNamespace("mods", "http://www.loc.gov/mods/v3");
	    String foxmlTemplateFile = "DRIVE:/PATH/image-object-foxml-template.xml";
	    String fedoraURL = "http://SERVER:8080/fedora";
	    String fedoraUsername = "USERNAME";
	    String fedoraPassword = "PASSWORD";
	    String fedoraIngestFormat = "info:fedora/fedora-system:FOXML-1.1";
	    String fedoraLogMessage = "Image Catalogue Batch Ingest";

	    FOXMLIngestor ingest;
	    ingest = new FOXMLIngestor(fedoraURL, fedoraUsername, fedoraPassword, fedoraIngestFormat, fedoraLogMessage);

	    FOXMLConverter fox = new FOXMLConverter(ingest, foxmlTemplateFile);

	    imp = new XMLBatchImporter(fox, modsChildElement, modsNamespace);

	    imp.readFile(modsInputFile);


	} catch (Exception e) {
	    e.printStackTrace();
	}



    }

    /** Retrieves the results from the ResultSet and puts them in a List
     * of Maps.
     * @param rs The ResultSet to retrieve results from
     * @param query The query that has just been executed
     * @throws SQLException If there was a problem retrieving results
     * @return An ArrayList of Map objects, one Map per result record.
     */
    private static ArrayList getResult(ResultSet rs)
	    throws SQLException {
	String[] columnNames = getResultColumnNames(rs.getMetaData());

	ArrayList list = new ArrayList();
	while (rs.next()) {
	    Map map = new HashMap();
	    for (int i = 0; i < columnNames.length; i++) {
		map.put(columnNames[i], rs.getString(i + 1));
		//map.put(columnNames[i], rs.getObject(i + 1));
	    }
	    list.add(Collections.unmodifiableMap(map));
	}
	return list;
    }

    /**
     * Retrieves the column names for a result.
     * @param rsMeta The ResultSet metadata to retrieve names from
     * @throws SQLException If there was a problem retrieving results
     * @return An array of name Strings
     */
    private static String[] getResultColumnNames(ResultSetMetaData rsMeta) throws SQLException {
	String[] columnNames = new String[rsMeta.getColumnCount()];
	for (int i = 0; i < columnNames.length; i++) {
	    columnNames[i] = rsMeta.getColumnName(i + 1);
	    columnNames[i] = columnNames[i].toLowerCase();
	}
	return columnNames;
    }
}
