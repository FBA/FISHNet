package org.freshwaterlife.ingestion;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
//import org.fcrepo.client.FedoraClient;
//import org.fcrepo.server.management.FedoraAPIM;
//import org.fcrepo.server.access.FedoraAPIA;
import org.freshwaterlife.conversion.CustomFedoraClient;
import org.freshwaterlife.conversion.IXMLOutputSource;

import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.response.IngestResponse;

import static com.yourmediashelf.fedora.client.FedoraClient.purgeObject;
import static com.yourmediashelf.fedora.client.FedoraClient.ingest;

/**
 *
 * @author pjohnson
 */
public class FOXMLIngestor implements IXMLOutputSource {

    private String sIngestFormat;
    private String sLogMessage;
    private CustomFedoraClient oFedora;
    private Boolean updateExistingObject;

    public FOXMLIngestor(String fedoraURL, String username, String password, String format, String message) throws MalformedURLException {
	this(new CustomFedoraClient(new FedoraCredentials(new URL(fedoraURL), username, password)), format, message);
    }

    public FOXMLIngestor(CustomFedoraClient fc, String format, String message) {
	try {
	    this.oFedora = fc;
	    this.sIngestFormat = format;
	    this.sLogMessage = message;
	    //oAPIM=fc.getAPIM();
	    //System.out.println("getting APIA");
	    //oAPIA=fc.getAPIA();
	    //System.out.println("finished getting APIs");
	} catch (Exception ex) {
	    System.out.println("Could not connect to fedora repository" + ex.getMessage());
	}
    }

    public void setUpdateExistingObject(Boolean _update) {
	this.updateExistingObject = _update;
    }

    public void process(Element item) {
	this.process(item, "Library:");
    }

    public void process(Element item, String strPIDPrefix) {
	try {
	    try {
		if (this.updateExistingObject) {
		    // If this object already exists then delete it first.
		    ////oAPIM.purgeObject(item.getAttributeValue("PID"), "Remove", false);
		    purgeObject(item.getAttributeValue("PID")).execute(this.oFedora);
		}
	    } catch (Exception e) {
	    }

	    XMLOutputter outp2 = new XMLOutputter();

	    // prep
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    outp2.output(item, out);
	    //pipeStream(new StringReader(outp2.outputString(item)), out, 4096);

	    // make the SOAP call on API-M using the connection stub
	    IngestResponse response = ingest().content(out.toString()).format(this.sIngestFormat).logMessage(this.sLogMessage).execute(this.oFedora);
	    String pid = response.getPid();
	    ////String pid = oAPIM.ingest(out.toByteArray(), this.sIngestFormat, this.sLogMessage);
	    //String pid = oAPIM.purgeObject("Library:7d57e691-9d77-4f3c-a721-3b8af7c82afb", this.sLogMessage, false);
	    //ArrayList<Condition> conditionList = new ArrayList<Condition> ();

	    //Condition cond1 = new Condition();
	    //cond1.setProperty("pid");
	    //cond1.setOperator(ComparisonOperator.fromValue(ComparisonOperator._has));
	    //cond1.setValue("Library:*");
	    //conditionList.add(cond1);

	    //Condition[] c = new Condition[conditionList.size()];
	    //FieldSearchQuery query = new FieldSearchQuery();
	    //query.setConditions(conditionList.toArray(c));
	    //FieldSearchResult result  = new FieldSearchResult();
	    //int amount = 0;
	    //result = AutoFinder.findObjects(oAPIA, new String[]{"pid"}, 100, query);
	    //while (result.getResultList().length > 0)
	    //{
	    //    ObjectFields[] resFields = result.getResultList();

	    //    for (ObjectFields objF : resFields)
	    //    {
	    //        System.out.println("Purging: " + objF.getPid());
	    //        oAPIM.purgeObject(objF.getPid(), this.sLogMessage, false);
	    //    }

	    //    amount += resFields.length;
	    //    System.out.println("Total Results: " + resFields.length);

	    //    result = AutoFinder.findObjects(oAPIA, new String[]{"pid"}, 100, query);
	    //}

	    //System.out.println("Grand Total Results: " + amount);
	    System.out.println("SOAP Request: ingest...");
	    System.out.println("SOAP Response: pid = " + pid);
	} catch (IOException ex) {
	    System.out.println("Ingest Error: " + ex.getMessage());
	} catch (FedoraClientException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    public void finished()
    {
	this.finished("Library:");
    }

    public void finished(String strPIDPrefix) {
    }
}
