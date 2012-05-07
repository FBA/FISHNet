/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package searchjsf.images;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import searchjsf.dbconnect.dbconnector;

/**
 *
 * @author mhaft
 */
public class Image {

    private String title;
    private String htmlTitle;
    private String thumbUrl;
    private long thumbId;
    private String midsizeUrl;
    private String fullsizeUrl;

    public Image(String title, String htmlTitle, String thumb_url, String midsize_url, String fullsize_url) {
        this.title = title;
        this.htmlTitle = htmlTitle;
        this.thumbUrl = thumb_url;
        this.midsizeUrl = midsize_url;
        this.fullsizeUrl = fullsize_url;
    }

    public Image(long gcl_derivativeSourceId) {
        try {
            Connection con = dbconnector.getJNDIConnection("jdbc/gallery");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT der_id, title, der_type FROM web_derivatives WHERE par_id = " + gcl_derivativeSourceId + " AND der_type=1;"); //get details, match by item id and derivative type = thumb (1)
            if (rs.next()) {
                this.title = rs.getString("title");
                this.htmlTitle = scrubBB(title);
                this.thumbId = rs.getLong("der_id");
                this.thumbUrl = "http://www.freshwaterlife.org/imagearchive/main.php?g2_view=core.DownloadItem&g2_itemId=" + thumbId;
                this.midsizeUrl = null;
                this.fullsizeUrl = null;
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(Image.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String scrubBB(String input) {
        String inputString = input;
        String output = inputString.replaceAll("\\[i\\]", "\\<i\\>");
        output = output.replaceAll("\\[/i\\]", "\\</i\\>");
        return output;
    }

    /*public Image getThumbAndMidSizeIds(long gcl_derivativeSourceId) {
    Image imgIds = null;
    try {
    Connection con = dbconnector.getJNDIConnection("jdbc/gallery");
    imgIds = new Image(gcl_derivativeSourceId);
    Statement stmt = con.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT der_id, title, der_type FROM web_derivatives WHERE par_id = " + gcl_derivativeSourceId + " ORDER BY der_type;");
    if (rs.next()) {
    imgIds.setThumbId(rs.getLong(1));
    }
    rs = stmt.executeQuery("SELECT gcl_title FROM gtb_item WHERE gcl_id = " + gcl_derivativeSourceId);
    if (rs.next()) {
    imgIds.setTitle(rs.getString(1));
    }
    System.out.println("getThumbIds: " + imgIds.getThumbId());
    System.out.println("getTitle: " + imgIds.getTitle());
    rs.close();
    stmt.close();
    con.close();
    } catch (SQLException ex) {
    Logger.getLogger(Image.class.getName()).log(Level.SEVERE, null, ex);
    }
    return imgIds;
    }*/
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the thumbUrl
     */
    public String getThumbUrl() {
        return thumbUrl;
    }

    /**
     * @param thumbUrl the thumbUrl to set
     */
    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    /**
     * @return the midsizeUrl
     */
    public String getMidsizeUrl() {
        return midsizeUrl;
    }

    /**
     * @param midsizeUrl the midsizeUrl to set
     */
    public void setMidsizeUrl(String midsizeUrl) {
        this.midsizeUrl = midsizeUrl;
    }

    /**
     * @return the fullsizeUrl
     */
    public String getFullsizeUrl() {
        return fullsizeUrl;
    }

    /**
     * @param fullsizeUrl the fullsizeUrl to set
     */
    public void setFullsizeUrl(String fullsizeUrl) {
        this.fullsizeUrl = fullsizeUrl;
    }

    /**
     * @return the thumbId
     */
    public long getThumbId() {
        return thumbId;
    }

    /**
     * @param thumbId the thumbId to set
     */
    public void setThumbId(long thumbIds) {
        this.thumbId = thumbIds;
    }

    /**
     * @return the htmlTitle
     */
    public String getHtmlTitle() {
        return htmlTitle;
    }

    /**
     * @param htmlTitle the htmlTitle to set
     */
    public void setHtmlTitle(String htmlTitle) {
        this.htmlTitle = htmlTitle;
    }
}
