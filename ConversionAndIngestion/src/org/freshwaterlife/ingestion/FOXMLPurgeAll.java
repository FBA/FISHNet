package org.freshwaterlife.ingestion;

import java.io.IOException;
import java.util.ArrayList;

import org.fcrepo.client.FedoraClient;
import org.fcrepo.client.utility.AutoFinder;
import org.fcrepo.server.access.FedoraAPIA;
import org.fcrepo.server.management.FedoraAPIM;
import org.fcrepo.server.types.gen.ComparisonOperator;
import org.fcrepo.server.types.gen.Condition;
import org.fcrepo.server.types.gen.FieldSearchQuery;
import org.fcrepo.server.types.gen.FieldSearchResult;
import org.fcrepo.server.types.gen.ObjectFields;
import org.jdom.Element;

/**
*
* @author pjohnson
*/
public class FOXMLPurgeAll {

   private String sFedoraURL;
   private String sUsername;
   private String sPassword;
   private String sIngestFormat;
   private String sLogMessage;

   private static FedoraAPIM oAPIM;
   private static FedoraAPIA oAPIA;


   public FOXMLPurgeAll(String fedoraURL, String username, String password, String format, String message)
   {
       // TODO
       this.sFedoraURL = fedoraURL;
       this.sUsername = username;
       this.sPassword = password;
       this.sIngestFormat = format;
       this.sLogMessage = message;

       try
       {
           FedoraClient fc = new FedoraClient(this.sFedoraURL, this.sUsername, this.sPassword);
           oAPIM=fc.getAPIM();
           oAPIA=fc.getAPIA();
       }
       catch (Exception ex)
       {
           System.out.println("Could not connect to fedora repository" + ex.getMessage());
       }
   }

   public void process(String sField, String sMatch)
   {
       try
       {
           //XMLOutputter outp2 = new XMLOutputter();

           // prep
           //ByteArrayOutputStream out=new ByteArrayOutputStream();
           //outp2.output(item, out);
           //pipeStream(new StringReader(outp2.outputString(item)), out, 4096);

           // make the SOAP call on API-M using the connection stub
           //String pid = oAPIM.ingest(out.toByteArray(), this.sIngestFormat, this.sLogMessage);
           //String pid = oAPIM.purgeObject("Library:7d57e691-9d77-4f3c-a721-3b8af7c82afb", this.sLogMessage, false);
           ArrayList<Condition> conditionList = new ArrayList<Condition> ();

           Condition cond1 = new Condition();
           cond1.setProperty(sField);
           cond1.setOperator(ComparisonOperator.fromValue(ComparisonOperator._has));
           cond1.setValue(sMatch);
           conditionList.add(cond1);

           Condition[] c = new Condition[conditionList.size()];
           FieldSearchQuery query = new FieldSearchQuery();
           query.setConditions(conditionList.toArray(c));
           FieldSearchResult result  = new FieldSearchResult();
           int amount = 0;
           result = AutoFinder.findObjects(oAPIA, new String[]{"pid"}, 100, query);
           while (result.getResultList().length > 0)
           {
               ObjectFields[] resFields = result.getResultList();

               for (ObjectFields objF : resFields)
               {
                   System.out.println("Purging: " + objF.getPid());
                   oAPIM.purgeObject(objF.getPid(), this.sLogMessage, false);
               }

               amount += resFields.length;
               System.out.println("Total Results: " + resFields.length);

               result = AutoFinder.findObjects(oAPIA, new String[]{"pid"}, 100, query);
           }

           System.out.println("Grand Total Results: " + amount);
           //System.out.println("SOAP Request: ingest...");
           //System.out.println("SOAP Response: pid = " + pid);
       }
       catch (IOException ex)
       {
           System.out.println("Ingest Error: " + ex.getMessage());
       }
       
   }

   public void finished()
   {

   }
}
