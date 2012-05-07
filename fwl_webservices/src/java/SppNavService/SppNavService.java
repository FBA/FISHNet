/*
 * This class defines the webservice servlet that handles requests about the species navigation trees
 * url requests should be in the form base url (eg. new.freshwaterlife.org/webservices) followed by slash separated varibables.
 * the first variable defines the service required
 * subsequent variables pass parameters
 *
 * currently supported:
 * <base url>SppNav/trees - returns xml listing the navigation trees available
 * <base url>SppNav/tree - returns the top level of the default tree in xml format - note the id of the parent will be set to 0 to accomodate dhtmlx tree
 * <base url>SppNav/tree/#tree name# - returns the top level of #tree name#
 * <base url>SppNav/tree/#tree name#/#id# where #id# returns the requested node of #tree name# with it's immediate children.
 * <base url>SppNav/tree/#tree name#?id="#id#" where #id# accomodates the defualt dhtmlx tree request
 */
//TODO - Although I believe this should funtion correctly as a Restful Web Service from the client point of view, I haven't implemented any design patterns
package SppNavService;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author root
 */
public class SppNavService extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/xml;charset=UTF-8");

        StringBuilder rS = new StringBuilder("");

        //This bit is necessary for dhtmlx tree stuff
        //dhtmlx could be altered to make forward slash requests instead of ? pass ids.
        String id = request.getParameter("id");

        //trying stuff
        // get the url, remove the base URL bits, then parse the rest
        // pass extracted parameters to the appropriate method

        //trim the url to just the part we're interested in
        StringBuffer url = request.getRequestURL();
        int baseURL_x = url.indexOf("/SppNav") + 7; //calculate the index of the where the base url ends
        url.delete(0, baseURL_x); //remove the base url
        url.trimToSize();
        //remove first forward slash (after /SppNav to avoid later problems (such as just a / and nothing else) and empty bits to the array
        if (url.length() >= 1) {
            url.deleteCharAt(0);
        }

        //create an array split on the /'s
        String[] urlArr = url.toString().split("/");

        //now check for different URL patterns



        // check length of array to see what was passed
        switch (urlArr.length) {
            case 0: //this is just the base url - nothing has been passed so do the default behaviour
                rS = new StringBuilder(RetrieveTrees.getTree());
                break;
            case 1: //currently must be trees or tree
                if (urlArr[0].equals("trees")) {
                    rS = new StringBuilder(RetrieveTrees.getTreesList());
                } else if (urlArr[0].equals("tree")) {
                    rS = new StringBuilder(RetrieveTrees.getTree());
                }
                break;
            case 2: //currently can only be tree/#tree name# , tree/#tree name#?id=#id# or item/#id#
                if (urlArr[0].equals("tree")) {

                    if (id == null) {
                        rS = new StringBuilder(RetrieveTrees.getTree(urlArr[1]));
                    } else {
                        Integer node = new Integer(id);
                        rS = new StringBuilder(RetrieveTrees.getTree(urlArr[1], node.intValue()));
                    }
                } else if (urlArr[0].equals("item")) {
                    Integer item_id = new Integer(urlArr[1]);
                    rS = new StringBuilder(RetrieveTrees.getItem(item_id.intValue()));
                }
                break;
            case 3: //currently can only be tree/#tree name#/#id# or tree/#tree name#/update
                String treeName = urlArr[1];
                if ( "update".equalsIgnoreCase(urlArr[2]) ) {

                    rS = new StringBuilder(RecalculateChildren.recalculateChildren(treeName));

                } else {
                    Integer node = new Integer(urlArr[2]);
                    rS = new StringBuilder(RetrieveTrees.getTree(treeName, node.intValue()));
                }
                break;
            default:
                rS = new StringBuilder(RetrieveTrees.getTree());
                break;

        }

        PrintWriter out = response.getWriter();
        try {

            out.print(rS.toString());

        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
