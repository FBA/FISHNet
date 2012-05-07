package org.freshwaterlife.portlets.library;

import static com.yourmediashelf.fedora.client.FedoraClient.getDissemination;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.ProcessAction;
import javax.portlet.ProcessEvent;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

import org.freshwaterlife.fedora.client.CustomFedoraClient;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.security.auth.AuthTokenUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.response.FedoraResponse;

public class DisplayPortlet extends MVCPortlet {

	@ProcessAction(name="showList")
    public void showList(ActionRequest request, ActionResponse response) throws PortletException, IOException {
            
		System.out.println("In ShowList Action - Display Portlet");
			
		HashMap<String,String> searchCriteria = new HashMap<String,String>();
			
		searchCriteria.put("field_name", request.getParameter("field_name"));
		searchCriteria.put("field_value", request.getParameter("field_value"));
		
		request.setAttribute("PID", request.getParameter("PID"));
		
        // Send the event using the appropriate QName
        response.setEvent(new QName("http:freshwaterlife.org/events", "LibraryCatalogue.DisplayList"), searchCriteria);
    }
	
	public void doView(RenderRequest request,RenderResponse response) throws PortletException,IOException {
        System.out.println("doView - Display Portlet");
        
        String sPID = (String)request.getAttribute("PID");
		System.out.println("doView - PID: " + sPID);
		if (sPID == null) {
			sPID = (String)request.getPortletSession().getAttribute("PID");
			request.getPortletSession().removeAttribute("PID");
		}
		if (sPID == null) {
			sPID = (String)request.getParameter("PID");
		}
		if (sPID != null) {
			response.setContentType("text/html");
		}
		
		if (_log.isInfoEnabled()) {
			_log.info("render " + request.getParameter("pid"));
		}

		//PrintWriter writer = response.getWriter();
		// use writer to render text
		FedoraResponse fResponse = null;
		
		CustomFedoraClient fedora;
		try
		{
			try {
		
				String baseUrl = "http://SERVER:8080/fedora";
	            String username = "USERNAME";
	            String password = "PASSWORD";
	            FedoraCredentials credentials = new FedoraCredentials(new URL(baseUrl), username, password);
	            fedora = new CustomFedoraClient(credentials);
	            
	            javax.servlet.http.Cookie[] cookies = request.getCookies();
	            
	            String headers = "";

	            HttpServletRequest req = (HttpServletRequest)request.getAttribute(PortletServlet.PORTLET_SERVLET_REQUEST);

	            String sPortletURL = "/web/guest/library2/-/catalogue/view/" + sPID + "/" + AuthTokenUtil.getToken(req);
	            
	            fResponse = getDissemination(sPID, "LibraryConfig:XSLT-SDEF", "getDocumentStyle1").methodParam("p_auth", sPortletURL).execute(fedora);
	            if (fResponse.getStatus() == 200)
	            {
	            	response.getWriter().println("<link rel=StyleSheet href=\"" + request.getContextPath() + "/html/css/libcat.css\" type=\"text/css\">" );
	            	response.getWriter().write(fResponse.getEntity(String.class));
	            	response.setContentType("text/html"); // or text/xml
	            }
	            
	        } catch (MalformedURLException e) {
	        	if (_log.isInfoEnabled()) {
	    			_log.info("Error with URL." + e.getMessage());
	    		}
	        } catch (FedoraClientException ex) {
	        	if (_log.isInfoEnabled()) {
	    			_log.info("Fedora Error: " + ex.getMessage());
	    		}
	        	response.getWriter().println("There was a problem retrieving the library catalogue item.");
	        }
	        
		} catch (IOException e) {
        	if (_log.isInfoEnabled()) {
    			_log.info("Error writing response: " + e.getMessage());
    		}
        }
		
		SessionMessages.clear(request);
        
    }
	
	@ProcessEvent(qname="{http:freshwaterlife.org/events}LibraryCatalogue.DisplayItem")
	public void processEvent(EventRequest request, EventResponse response)
	{
		System.out.println("Event Received - Display Item");
		
		String sPID = (String)request.getEvent().getValue();
		
		System.out.println("PID: " + sPID);
		request.getPortletSession().setAttribute("PID", sPID);
	}
	
	private static Log _log = LogFactoryUtil.getLog(DisplayPortlet.class);
}
