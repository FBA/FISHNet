package org.freshwaterlife.conversion.client;

import org.jdom.Namespace;

import org.freshwaterlife.conversion.XMLBatchExporter;
import org.freshwaterlife.conversion.XMLFileWriter;
import org.freshwaterlife.conversion.XMLBatchImporter;
import org.freshwaterlife.conversion.XSLTConverter;
import org.freshwaterlife.conversion.FOXML.FOXMLConverter;

/**
 *
 * @author pjohnson
 */
public class MODSToSOLR {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

    	if (args.length != 5)
    	{
    		System.out.println("Invalid number of arguments specified.");
    		System.out.println("Should be: modsInputFile maxRecordsPerFile foxmlTemplateFile solrXSLTFile solrOutputFile");
    	}
    	
    	String modsInputFile = args[0];//"C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_2.MODS/All.MODS.xml";
    	String modsChildElement = "mods";
        Namespace modsNamespace = Namespace.getNamespace("mods", "http://www.loc.gov/mods/v3");
        int maxRecordsPerFile = Integer.parseInt(args[1]);//500;
        String foxmlTemplateFile = args[2];//"C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_3.FOXML/FOXML.Template.xml";
        String foxmlRootElement = "foxmlobjects";
        String solrXSLTFile = args[3];//"C:/solr/conf/xslt/solr.xslt";
        String solrOutputFile = args[4];//"C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_4.SOLR/output/All.SOLR.Output.xml";

        // Writes XML to a file.
        XMLFileWriter fw = new XMLFileWriter(solrOutputFile);

        // Applies solr index xslt to foxml object. (Only used for testing purposes)
        XSLTConverter solrConv = new XSLTConverter(fw, solrXSLTFile);

        XMLBatchExporter foxmlBatch = new XMLBatchExporter(solrConv, maxRecordsPerFile, foxmlRootElement);

        FOXMLConverter fox = new FOXMLConverter(foxmlBatch, foxmlTemplateFile);

        XMLBatchImporter imp = new XMLBatchImporter(fox, modsChildElement, modsNamespace);

        imp.readFile(modsInputFile);

    }
}
