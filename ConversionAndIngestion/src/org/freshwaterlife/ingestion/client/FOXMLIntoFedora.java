package org.freshwaterlife.ingestion.client;

import java.net.MalformedURLException;
import org.freshwaterlife.conversion.XMLBatchImporter;

import org.freshwaterlife.ingestion.FOXMLIngestor;

/**
 *
 * @author pjohnson
 */
public class FOXMLIntoFedora {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws MalformedURLException {

        String foxmlInputFile = "DRIVE:/PATH/All.FOXML.Output5.xml";
        String fedoraURL = "http://SERVER:8080/fedora";
        String fedoraUsername = "USERNAME";
        String fedoraPassword = "PASSWORD";
        String fedoraIngestFormat = "info:fedora/fedora-system:FOXML-1.1";
        String fedoraLogMessage = "Library Catalogue Batch Ingest";
        //String foxmlOutputFile = "DRIVE:/PATH/All.FOXML.Output.xml";
        //String foxmlTemplateFile = "DRIVE:/PATH/FOXML.Template.xml";

        FOXMLIngestor ingest = new FOXMLIngestor(fedoraURL, fedoraUsername, fedoraPassword, fedoraIngestFormat, fedoraLogMessage);

        XMLBatchImporter imp = new XMLBatchImporter(ingest);

        imp.readFile(foxmlInputFile);

    }
}
