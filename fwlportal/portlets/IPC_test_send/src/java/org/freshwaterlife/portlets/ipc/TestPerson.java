/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.freshwaterlife.portlets.ipc;

import java.io.Serializable;

/**
 *
 * @author root
 */
public class TestPerson implements Serializable {
    private String firstname;
    private String surname;

    public TestPerson(){
        this.firstname="unknown";
        this.surname="unknown";
    }
    public TestPerson(String forename, String lastname){
        this.firstname = forename;
        this.surname = lastname;
    }

    /**
     * @return the firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * @param firstname the firstname to set
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * @return the surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * @param surname the surname to set
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

}
