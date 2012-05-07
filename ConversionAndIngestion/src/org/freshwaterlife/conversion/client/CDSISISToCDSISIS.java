package org.freshwaterlife.conversion.client;

import java.io.*;

import org.freshwaterlife.conversion.XMLFileWriter;
import org.freshwaterlife.conversion.XMLBatchExporter;
import org.freshwaterlife.conversion.CDSISIS.KeywordLookup;
import org.freshwaterlife.conversion.CDSISIS.CDSISISConverter;
import org.freshwaterlife.conversion.CDSISIS.CDSISISBatchImporter;

/**
 *
 * @author pjohnson
 */
public class CDSISISToCDSISIS {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

    	if (args.length != 6)
    	{
    		System.out.println("Invalid number of arguments specified.");
    		System.out.println("Should be: cdsisisOutputFile cdsisisInputFile cdsisisErrorFile cdsisisInvalidElementsFile keywords maxRecordsPerFile");
    	}
        String cdsisisOutputFile = args[0];//"C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_1.CDS.ISIS/All.CDSISIS.Output.xml";
        String cdsisisInputFile = args[1];//"C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_1.CDS.ISIS/All.CDSISIS.xml";
        String cdsisisErrorFile = args[2];//"C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_1.CDS.ISIS/All.Malformed.xml";
        String cdsisisInvalidElementsFile = args[3];//"C:/Documents and Settings/pjohnson/My Documents/My Dropbox/Public/_1.CDS.ISIS/All.Invalid.Elements.txt";
        String keywords = args[4];//"U:/LIS/NISC/FBACatNiscConverter/FBACatalogueKeywordCodes.csv";
        String rootElement = "DATABASE_FBA";
        int maxRecordsPerFile = Integer.parseInt(args[5]);//10000;

        XMLFileWriter fw = new XMLFileWriter(cdsisisOutputFile);

        XMLBatchExporter exp = new XMLBatchExporter(fw, maxRecordsPerFile, rootElement);

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

        CDSISISConverter cdsisisConv = new CDSISISConverter(exp, lookup, cdsisisInvalidElementsFile);

        CDSISISBatchImporter cdsisisImp = new CDSISISBatchImporter(cdsisisConv, cdsisisErrorFile);

        cdsisisImp.readFile(cdsisisInputFile);
    }
}
