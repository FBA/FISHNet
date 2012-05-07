package org.freshwaterlife.portlets.speciespages.ipctests;

import org.freshwaterlife.portlets.speciespages.*;
import javax.faces.event.ActionEvent;

/**
 * Page bean that backs the ipc test send page
 * @author KMcNicol
 */
public class SendPageBean {

    //String to back the input box
    private String input = "";

    //variable for linking to the instance of the coordinator bean in use by this portlet
    private Coordinator coordinator;

    /**
     * default constructore
     */
    public SendPageBean(){
    }

    /**
     * @return the input
     */
    public String getInput() {
        return input;
    }

    /**
     * @param input the input to set
     */
    public void setInput(String input) {
        this.input = input;
    }

    /**
     * back the set button - pass the variable to the coordinator for updating
     * @param event
     */
    public void setValue(ActionEvent event){
        coordinator.setIpcTestString(input);
    }

    /**
     * @return the coordinator
     * Used by JSF framework to set the managed property
     */
    public Coordinator getCoordinator() {
        return coordinator;
    }

    /**
     * @param coordinator the coordinator to set
     * Used by JSF framework to set the managed property
     */
    public void setCoordinator(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

}
