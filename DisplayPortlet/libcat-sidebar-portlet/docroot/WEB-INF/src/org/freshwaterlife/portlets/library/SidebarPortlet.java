package org.freshwaterlife.portlets.library;

import java.io.IOException;
import java.util.HashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletException;
import javax.portlet.ProcessAction;
import javax.portlet.ProcessEvent;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.security.auth.AuthTokenUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

public class SidebarPortlet extends MVCPortlet {

	@ProcessAction(name="showItem")
    public void showItem(ActionRequest request, ActionResponse response) throws PortletException, IOException {
            
		System.out.println("In ShowItem Action");
			
		String sPID = request.getParameter("PID");
			
		// Send the event using the appropriate QName
        response.setEvent(new QName("http:freshwaterlife.org/events", "LibraryCatalogue.DisplayItem"), sPID);
        
    }
	
	public void doView(RenderRequest request,RenderResponse response) throws PortletException,IOException {
        System.out.println("doView - Sidebar Portlet");
        
        HashMap<String,String> searchCriteria = (HashMap<String,String>)request.getPortletSession().getAttribute("searchCriteria");
        request.getPortletSession().removeAttribute("searchCriteria");
        
        if (searchCriteria != null) {
        	response.getWriter().println("FieldName: " + searchCriteria.get("field_name"));
        	response.getWriter().println("FieldValue: " + searchCriteria.get("field_value"));
        	
        	HttpServletRequest req = (HttpServletRequest)request.getAttribute(PortletServlet.PORTLET_SERVLET_REQUEST);
        	String sAuthToken = AuthTokenUtil.getToken(req);
        	
        	response.getWriter().println("<p><a href=\"http://localhost:8080/web/guest/library2/-/catalogue-sidebar/view/Library:14d1f7b7-52db-43c7-a8dd-69fa6d790469/" + sAuthToken + "\">Item 1</a></p>");
        	response.getWriter().println("<p><a href=\"http://localhost:8080/web/guest/library2/-/catalogue-sidebar/view/Library:126230bb-9710-4220-a790-5823230c3972/" + sAuthToken + "\">Item 2</a></p>");
        }
        
        SessionMessages.clear(request);
    }
	
	@ProcessEvent(qname="{http:freshwaterlife.org/events}LibraryCatalogue.DisplayList")
	public void processEvent(EventRequest request, EventResponse response)
	{
		System.out.println("Event Received - Display List");
		
		HashMap<String,String> searchCriteria = (HashMap<String,String>)request.getEvent().getValue();
		
		System.out.println("Name: " + searchCriteria.get("field_name"));
		System.out.println("Value: " + searchCriteria.get("field_value"));
		
		request.getPortletSession().setAttribute("searchCriteria", searchCriteria);
	}
}
