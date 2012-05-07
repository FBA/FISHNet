package org.freshwaterlife.conversion.client;

import org.jdom.Namespace;

import org.freshwaterlife.conversion.XMLFileWriter;
import org.freshwaterlife.conversion.XMLBatchImporter;
import org.freshwaterlife.conversion.FOXML.FOXMLConverter;

/**
 *
 * @author pjohnson
 */
public class MODSToFOXML {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

    	if (args.length != 3)
    	{
    		System.out.println("Invalid number of arguments specified.");
    		System.out.println("Should be: modsInputFile foxmlOutputFile foxmlTemplateFile");
    	}
    	
        String modsInputFile = args[0];//"C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_2.MODS/All.MODS.xml";
        String modsChildElement = "mods";
        Namespace modsNamespace = Namespace.getNamespace("mods", "http://www.loc.gov/mods/v3");
        String foxmlOutputFile = args[1];//"C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_3.FOXML/All.FOXML.Output.xml";
        String foxmlTemplateFile = args[2];//"C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_3.FOXML/FOXML.Template.xml";

        // Writes XML to a file.
        XMLFileWriter fw = new XMLFileWriter(foxmlOutputFile);

        FOXMLConverter fox = new FOXMLConverter(fw, foxmlTemplateFile);

        XMLBatchImporter imp = new XMLBatchImporter(fox, modsChildElement, modsNamespace);

        imp.readFile(modsInputFile);

    }
}
