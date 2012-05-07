package org.freshwaterlife.portlets;

import java.net.*;
import java.io.*;

/**
 *
 * Open a URL and retrieve the contents
 *
 * @author Kearon McNicol
 *
 */
public class UrlContentReader {

    private String urlString = "http://localhost:8080/";
    private String content;

    public UrlContentReader(String url) {
        this.urlString = url;
    }

    public String retrieveContent() throws MalformedURLException, IOException {
        URL url = new URL(this.urlString);

        // open the urlString stream, wrap it an a few "readers"
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder retrieved = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            retrieved.append(line);
        }
        // close our reader
        reader.close();

        content = retrieved.toString();
        return content;
    }

    /**
     * @return the urlString
     */
    public String getUrl() {
        return urlString;
    }

    /**
     * @param urlString the urlString to set
     */
    public void setUrl(String url) {
        this.urlString = url;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }
}
