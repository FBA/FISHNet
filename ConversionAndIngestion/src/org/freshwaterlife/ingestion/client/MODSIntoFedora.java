package org.freshwaterlife.ingestion.client;

import java.net.MalformedURLException;
import org.jdom.Namespace;

import org.freshwaterlife.conversion.XMLBatchImporter;
import org.freshwaterlife.conversion.FOXML.FOXMLConverter;

import org.freshwaterlife.ingestion.FOXMLIngestor;

/**
 *
 * @author pjohnson
 */
public class MODSIntoFedora {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws MalformedURLException {

        String modsInputFile = "DRIVE:/PATH/All.MODS.xml";
        String modsChildElement = "mods";
        Namespace modsNamespace = Namespace.getNamespace("mods", "http://www.loc.gov/mods/v3");
        String foxmlTemplateFile = "DRIVE:/PATH/FOXML.Template.xml";
        String fedoraURL = "http://SERVER:8080/fedora";
        String fedoraUsername = "USERNAME";
        String fedoraPassword = "PASSWORD";
        String fedoraIngestFormat = "info:fedora/fedora-system:FOXML-1.1";
        String fedoraLogMessage = "Library Catalogue Batch Ingest";
        
        FOXMLIngestor ingest = new FOXMLIngestor(fedoraURL, fedoraUsername, fedoraPassword, fedoraIngestFormat, fedoraLogMessage);

        FOXMLConverter fox = new FOXMLConverter(ingest, foxmlTemplateFile);

        XMLBatchImporter imp = new XMLBatchImporter(fox, modsChildElement, modsNamespace);

        imp.readFile(modsInputFile);

    }
}
