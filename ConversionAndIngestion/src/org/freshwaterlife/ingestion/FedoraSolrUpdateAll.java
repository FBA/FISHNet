package org.freshwaterlife.ingestion;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
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
public class FedoraSolrUpdateAll {

   private String sFedoraURL;
   private String sUsername;
   private String sPassword;
   private String sSolrUpdateURL;
   
   private static FedoraAPIM oAPIM;
   private static FedoraAPIA oAPIA;


   public FedoraSolrUpdateAll(String fedoraURL, String username, String password, String solrUpdateURL)
   {
       // TODO
       this.sFedoraURL = fedoraURL;
       this.sUsername = username;
       this.sPassword = password;
       this.sSolrUpdateURL = solrUpdateURL;

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
           result = AutoFinder.findObjects(oAPIA, new String[]{"pid"}, 1000, query);
           while (result.getResultList().length > 0)
           {
        	   ObjectFields[] resFields = result.getResultList();

               for (ObjectFields objF : resFields)
               {
            	   
            	    //URL urlObj = new URL("http://172.16.0.92:8080/solr/core_libcat/fedora?pid=" + objF.getPid() + "&action=savePid");
            	   URL urlObj = new URL("http://localhost:8080/solr/fedora?pid=" + objF.getPid() + "&action=savePid");

	       			HttpURLConnection urlc = (HttpURLConnection)urlObj.openConnection();
	
	       			urlc.setDoOutput(true);
	       			urlc.setRequestMethod("GET");
	       			//urlc.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
	
	       			//String[] cookieNames = _getCookieNames(serviceUrl);
	
	       			//_setCookieProperty(request, urlc, cookieNames);
	
	       			OutputStreamWriter osw = new OutputStreamWriter(urlc.getOutputStream());
	
	       			osw.write("dummy");
	
	       			osw.flush();
	
	       			int responseCode = urlc.getResponseCode();
            	    System.out.println("Response: " + responseCode);
            	   
            	   
            	   
            	   
                   /*System.out.println("Updating: " + objF.getPid());
                   //oAPIM.purgeObject(objF.getPid(), this.sLogMessage, false);
                   System.out.println(this.sSolrUpdateURL + objF.getPid());
                   URL url = new URL("http://localhost:8080/solr/fedora?pid=" + objF.getPid() + "&action=savePid");
                   URLConnection conn = url.openConnection ();
                   conn.setDoOutput(true);
       			   OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                   osw.write("dummy");
                   osw.flush();
                   int responseCode = conn.getResponseCode();*/
               }

               amount += resFields.length;
               System.out.println("Total Results: " + resFields.length);

               if (result.getListSession() != null)
               {
            	   System.out.println("Session: " + result.getListSession());
            	   System.out.println("Session: " + result.getListSession().getToken());
            	   result = AutoFinder.resumeFindObjects(oAPIA, result.getListSession().getToken());
               }
               else
               {
            	   break;
               }
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
