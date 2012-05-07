package org.freshwaterlife.conversion.client;

import java.io.*;

import org.jdom.Namespace;

import org.freshwaterlife.conversion.XMLFileWriter;
import org.freshwaterlife.conversion.XSLTConverter;
import org.freshwaterlife.conversion.XMLBatchExporter;
import org.freshwaterlife.conversion.XMLBatchImporter;
import org.freshwaterlife.conversion.CDSISIS.KeywordLookup;
import org.freshwaterlife.conversion.CDSISIS.CDSISISConverter;
import org.freshwaterlife.conversion.CDSISIS.CDSISISBatchImporter;
import org.freshwaterlife.conversion.FOXML.FOXMLConverter;

/**
 *
 * @author pjohnson
 */
public class CDSISISToFOXML {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

    	if (args.length != 8)
    	{
    		System.out.println("Invalid number of arguments specified.");
    		System.out.println("Should be: cdsisisInputFile cdsisisErrorFile cdsisisInvalidElementsFile keywords modsXSLTFile maxRecordsPerFile foxmlOutputFile foxmlTemplateFile");
    	}
    	
        String cdsisisInputFile = args[0];//"C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_1.CDS.ISIS/All.CDSISIS.xml";
        String cdsisisErrorFile = args[1];//"C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_1.CDS.ISIS/All.Malformed.xml";
        String cdsisisInvalidElementsFile = args[2];//"C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_1.CDS.ISIS/All.Invalid.Elements.txt";
        String keywords = args[3];//"U:/LIS/NISC/FBACatNiscConverter/FBACatalogueKeywordCodes.csv";
        String modsXSLTFile = args[4];//"C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_2.MODS/mods.xslt";
        String cdsisisRootElement = "DATABASE_FBA";
        String modsChildElement = "mods";
        Namespace modsNamespace = Namespace.getNamespace("mods", "http://www.loc.gov/mods/v3");
        int maxRecordsPerFile = Integer.parseInt(args[5]);//10000;
        String foxmlOutputFile = args[6];//"C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_3.FOXML/output/All.FOXML.Output.xml";
        String foxmlTemplateFile = args[7];//"C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_3.FOXML/FOXML.Template.xml";

        KeywordLookup lookup;
        try
        {
            lookup = new KeywordLookup(keywords);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Keyword file not found");
            return;
        }
        catch (IOException e)
        {
            System.out.println("General IO Exception");
            return;
        }

        // Writes XML to a file.
        XMLFileWriter fw = new XMLFileWriter(foxmlOutputFile);

        FOXMLConverter fox = new FOXMLConverter(fw, foxmlTemplateFile);
        
        XMLBatchImporter imp = new XMLBatchImporter(fox, modsChildElement, modsNamespace);

        // Transforms CDS/ISIS record to MODS record and passes each one onto the supplied output source (XML Writer).
        XSLTConverter modsConv = new XSLTConverter(imp, modsXSLTFile);

        // Batches the elements that are sent to it and when limit is reached, send to the specified output.
        XMLBatchExporter batch = new XMLBatchExporter(modsConv, maxRecordsPerFile, cdsisisRootElement);

        // Formats Raw CDS/ISIS records into correct format (Includes Indexing Records) and passes each one onto supplied (batched) MODS converter
        CDSISISConverter cdsisisConv = new CDSISISConverter(batch, lookup, cdsisisInvalidElementsFile);

        // Reads Raw CDS/ISIS records from input file and passes each one onto the supplied CDSISIS converter.
        CDSISISBatchImporter cdsisisImp = new CDSISISBatchImporter(cdsisisConv, cdsisisErrorFile);

        cdsisisImp.readFile(cdsisisInputFile);

    }
}
