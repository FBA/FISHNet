package org.freshwaterlife.ingestion.client;

import java.io.*;

import org.jdom.Namespace;

import org.freshwaterlife.conversion.XSLTConverter;
import org.freshwaterlife.conversion.XMLBatchExporter;
import org.freshwaterlife.conversion.XMLBatchImporter;
import org.freshwaterlife.conversion.CDSISIS.KeywordLookup;
import org.freshwaterlife.conversion.CDSISIS.CDSISISConverter;
import org.freshwaterlife.conversion.CDSISIS.CDSISISBatchImporter;
import org.freshwaterlife.conversion.FOXML.FOXMLConverter;

import org.freshwaterlife.ingestion.FOXMLIngestor;
import org.freshwaterlife.ingestion.FOXMLPurgeAll;

/**
 *
 * @author pjohnson
 */
public class PurgeFedoraObjects {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

    	if (args.length != 7)
    	{
    		System.out.println("Invalid number of arguments specified.");
    		System.out.println("Should be: fedoraURL fedoraUsername fedoraPassword fedoraIngestFormat fedoraLogMessage fieldName fieldValue");
    	}
    	
        String fedoraURL = args[0];//"http://localhost:8080/fedora";
        String fedoraUsername = args[1];//"fedoraAdmin";
        String fedoraPassword = args[2];//"112233";
        String fedoraIngestFormat = args[3];//"info:fedora/fedora-system:FOXML-1.1";
        String fedoraLogMessage = args[4];//"Library Catalogue Batch Ingest";
        
        String fieldName = args[5];//"info:fedora/fedora-system:FOXML-1.1";
        String fieldValue = args[6];//"info:fedora/fedora-system:FOXML-1.1";
        
        FOXMLPurgeAll purge = new FOXMLPurgeAll(fedoraURL, fedoraUsername, fedoraPassword, fedoraIngestFormat, fedoraLogMessage);

        purge.process(fieldName, fieldValue);

    }
}
