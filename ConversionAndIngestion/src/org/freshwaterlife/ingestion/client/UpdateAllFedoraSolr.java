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

import org.freshwaterlife.ingestion.FedoraSolrUpdateAll;

/**
 *
 * @author pjohnson
 */
public class UpdateAllFedoraSolr {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

    	if (args.length != 6)
    	{
    		System.out.println("Invalid number of arguments specified.");
    		System.out.println("Should be: fedoraURL fedoraUsername fedoraPassword fieldName fieldValue");
    	}
    	
        String fedoraURL = args[0];//"http://SERVER:8080/fedora";
        System.out.println(fedoraURL);
        String fedoraUsername = args[1];//"USERNAME";
        System.out.println(fedoraUsername);
        String fedoraPassword = args[2];//"PASSWORD";
        System.out.println(fedoraPassword);
        String fieldName = args[3];//"info:fedora/fedora-system:FOXML-1.1";
        System.out.println(fieldName);
        String fieldValue = args[4];//"info:fedora/fedora-system:FOXML-1.1";
        System.out.println(fieldValue);
        
        String solrUpdateUrl = "http://SOLRSERVER:8080/solr/fedora?action=savePid&pid=";
        System.out.println(solrUpdateUrl);
        
        FedoraSolrUpdateAll update = new FedoraSolrUpdateAll(fedoraURL, fedoraUsername, fedoraPassword, solrUpdateUrl);

        update.process(fieldName, fieldValue);

    }
}
