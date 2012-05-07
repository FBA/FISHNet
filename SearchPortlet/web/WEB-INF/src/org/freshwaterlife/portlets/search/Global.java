/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freshwaterlife.portlets.search;

/**
 *
 * @author sfox
 */
public class Global {

    //all, websites, image, libcat, dataset
    public static String[] arrCores = {"", "liferay", "image", "libcat", "dataset", "imagearchive"};
    public static final int iNumOfCores = 6;
    public static final int EVERYTHING = 0;
    public static final int LIFERAYDOC = 1;
    public static final int IMAGE = 2;
    public static final int LIBCAT = 3;
    public static final int DATASET = 4;
    public static final int IMAGEARCHIVE = 5;
    //public static final int SPECIES = 5;
    public static final int iNumOfResultsPerPage = 20;
    public static String strSearchBoxText = "Search FreshwaterLife";
    public static final String strNoURLReturned = "nourl";
    public static final String strSolrServerProperty = "default.fwl.solrserver";
    public static final int iPageOffset = 2;
    public static final int iPageTotal = 5;
}
