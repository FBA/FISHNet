/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freshwaterlife.portlets.dataset;

/**
 *
 * @author sfox
 */
public class Global {

    //all, red, orange, green, redorange
    public static String[] arrCategories = {"", "red", "orange", "green", "reviewable", "redorange"};
    public static final int ALLMYDATASETS = 0;
    public static final int RED = 1;
    public static final int ORANGE = 2;
    public static final int GREEN = 3;
    public static final int REVIEWABLE = 4;
    public static final int REDORANGE = 5;
    public static final String NOREVIEWER = "noreviewer";
    public static final int iNumOfResultsPerPage = 20;
    public static final String strNoURLReturned = "nourl";
    public static final int iPageOffset = 2;
    public static final int iPageTotal = 5;
    public static final String strDatasetReviewerRole = "Dataset Reviewer";
    public static final String strDatasetEditorRole = "Dataset Editor";
    public static final String datasetOutcomePath = "/mods:mods/mods:note[@type='admin' and ( text() = 'awaitingoutcome' or text() = 'approved' or text() = 'rejected' or text()='awaitingminting' or text()='doifailed' or text()='minted' or text()='awaitingmetadatconf' or text()='metadatafailed' or text()='metatdataattached' ) ]";
    public static final String datasetCategoryPath = "/mods:mods/mods:note[@type='admin' and ( text()='red' or text()='redorange' or text()='orange' or text()='green')]";
}
