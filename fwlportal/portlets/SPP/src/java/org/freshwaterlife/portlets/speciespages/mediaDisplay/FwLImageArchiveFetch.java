package org.freshwaterlife.portlets.speciespages.mediaDisplay;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mhaft
 */
public class FwLImageArchiveFetch {

    private String keyword;
    private int columns;
    private int maxItems;
    private static int timeout = 6000;

    public FwLImageArchiveFetch() {
        keyword = "";
        columns = 1;
        maxItems = 5;
    }
    public FwLImageArchiveFetch(String searchStr) {
        keyword = searchStr;
        columns = 1;
        maxItems = 5;
    }

    public static String stripWhite(String keyWord) {
        return keyWord.replaceAll("\\s+", "+");
    }

    public String getImages() throws Exception {

        String key = getKeyword();
        String keyClean = stripWhite(key);
        URL url = null;
        HttpURLConnection imgArchConn = null;

        BufferedReader in = null;
        InputStream stream;
        String results = "";

        try {
            url = new URL("http://www.freshwaterlife.org/imagearchive/mediaBlock.php?g2_itemId=7&mode=dynamic&g2_view=tags.VirtualAlbum&g2_tagName=" + keyClean + "&limit=" + maxItems + "&useThumb=1&column=" + columns + "&rss=1'");
            imgArchConn = (HttpURLConnection) url.openConnection();

            //set timeout
            imgArchConn.setReadTimeout(timeout);
            imgArchConn.setConnectTimeout(timeout);

            long start2 = System.currentTimeMillis();
            stream = imgArchConn.getInputStream();
            long start3 = System.currentTimeMillis();

            long res = start3 - start2;

            in = new BufferedReader(new InputStreamReader(stream));

            String inputLine;
            
            String errorCatch = "<link rel=\"alternate\" type=\"application/rss+xml\" title=\"Photo RSS\" href=\"http://www.freshwaterlife.org/imagearchive/mediaBlock.php?mode=mediaRss&amp;g2_itemIds=\"/>Array";

            while ((inputLine = in.readLine()) != null) {
                results = results + inputLine;
            }
            int compResults = results.compareTo(errorCatch);

            if (compResults == 0) {
                results = "<br/>Apologies.  No items match that search.";
            } else if (compResults > this.maxItems){
                results = results + getReadmore(keyClean);
            }

        } catch (Exception e) {
            results = results + "Apologies.  It appears that the Freshwater<em>Life</em> Image Archive is unavailable at this time." + e.getMessage() + e.getClass();
        }

        /* old way - before timeouts
        URL ImageArchive = new URL("http://www.freshwaterlife.org/imagearchive/mediaBlock.php?g2_itemId=7&mode=dynamic&g2_view=tags.VirtualAlbum&g2_tagName=" + keyClean + "&limit=" + maxItems + "&useThumb=1&column=" + columns + "&rss=1'");


        BufferedReader in = new BufferedReader(
        new InputStreamReader(
        ImageArchive.openStream()));
        in.

        String inputLine;
        String results = "";
        String errorCatch = "<link rel=\"alternate\" type=\"application/rss+xml\" title=\"Photo RSS\" href=\"http://www.freshwaterlife.org/imagearchive/mediaBlock.php?mode=mediaRss&amp;g2_itemIds=\"/>Array";

        while ((inputLine = in.readLine()) != null) {
        results = results + inputLine;
        }

        int compResults = results.compareTo(errorCatch);

        if (compResults == 0) {
            results = "We're sorry. There are currently no images of this item in the our Image Archive.";
        } else {
            results = results + getReadmore(keyClean);
        }

        in.close();
*/
        return results;
    }

    public String getReadmore(String keyClean) throws Exception {

        String spName = keyClean;
        String viewMore = "<a href=\"http://www.freshwaterlife.org/imagearchive/main.php?g2_view=tags.VirtualAlbum&g2_tagName=" + spName + "\">More...</a>";

        if (spName == null) {
            viewMore = "<a href=\"http://www.freshwaterlife.org/imagearchive/main.php?g2_view=tags.VirtualAlbum&g2_tagName=" + spName + "\">More...</a>";
        } else if (spName.length() == 0) {
            viewMore = "";
        }

        return viewMore;
    }

    /**
     * @return the keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * @param keyword the keyword to set
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * @return the columns
     */
    public int getColumns() {
        return columns;
    }

    /**
     * @param columns the columns to set
     */
    public void setColumns(int columns) {
        this.columns = columns;
    }

    /**
     * @return the maxItems
     */
    public int getMaxItems() {
        return maxItems;
    }

    /**
     * @param maxItems the maxItems to set
     */
    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }
}
