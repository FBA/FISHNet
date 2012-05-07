package org.freshwaterlife.portlets.ipc.testsend;

import org.freshwaterlife.portlets.ipc.TestPerson;
import javax.portlet.GenericPortlet;
import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import java.io.IOException;
import javax.portlet.PortletRequestDispatcher;
import javax.xml.namespace.QName;

/**
 * IPC_test_send Portlet Class
 */
public class IPC_test_send extends GenericPortlet {

    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        String testString_Data = request.getParameter("textValue");
        String firstname_data = request.getParameter("firstname");
        String surname_data = request.getParameter("surname");
        if (testString_Data != null && testString_Data.length() > 0) {
            try {
                QName qName = new javax.xml.namespace.QName("http://www.freshwaterlife.org/portlets/ipc", "testString", "qname_ns__");
                response.setEvent(qName, testString_Data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (surname_data != null || firstname_data != null) {
            try {
                TestPerson p = new TestPerson(firstname_data, surname_data);

                QName qName = new javax.xml.namespace.QName("http://www.freshwaterlife.org/portlets/ipc", "person", "qname_ns__");
                response.setEvent(qName, p);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        response.setContentType("text/html");
        PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/IPC_test_send_view.jsp");
        dispatcher.include(request, response);
    }
}
