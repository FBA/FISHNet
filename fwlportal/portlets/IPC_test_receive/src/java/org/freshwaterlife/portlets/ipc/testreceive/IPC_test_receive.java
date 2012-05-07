package org.freshwaterlife.portlets.ipc.testreceive;

import javax.portlet.GenericPortlet;
import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import java.io.IOException;
import java.io.PrintWriter;
import org.freshwaterlife.portlets.ipc.TestPerson;

/**
 * IPC_test_receive Portlet Class
 */
public class IPC_test_receive extends GenericPortlet {

    private String textValue = "waiting for something";
    private TestPerson person = new TestPerson();

    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
    }

    @Override
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        writer.println("<h2>IPC (Different WARs) text string received :</h2>" + this.getTextValue());
        writer.println("<h2>Person object received : </h2>" + this.person.getFirstname() + " " + this.person.getSurname());
    }

    @javax.portlet.ProcessEvent(qname = "{http://www.freshwaterlife.org/portlets/ipc}testString")
    public void handleProcesstestStringEvent(javax.portlet.EventRequest request, javax.portlet.EventResponse response) throws javax.portlet.PortletException, java.io.IOException {
        javax.portlet.Event event = request.getEvent();
        java.lang.String value = (java.lang.String) event.getValue();
        //response.setRenderParameter("key",value);
        request.getPortletSession().setAttribute("key", value);

        this.setTextValue(value);
    }

    @javax.portlet.ProcessEvent(qname = "{http://www.freshwaterlife.org/portlets/ipc}person")
    public void handleProcessPersonEvent(javax.portlet.EventRequest request, javax.portlet.EventResponse response) throws javax.portlet.PortletException, java.io.IOException {
        javax.portlet.Event event = request.getEvent();
        TestPerson p = (TestPerson) event.getValue();
        person = p;
    }

    /**
     * @return the textValue
     */
    public String getTextValue() {
        return textValue;
    }

    /**
     * @param textValue the textValue to set
     */
    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    /**
     * @return the person
     */
    public TestPerson getPerson() {
        return person;
}

    /**
     * @param person the person to set
     */
    public void setPerson(TestPerson person) {
        this.person = person;
    }
}
