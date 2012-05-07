package org.freshwaterlife.portlets.search.resulttypes;

import org.freshwaterlife.portlets.search.Global;
import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author sfox
 */
public class Species extends BasicResultDoc implements Serializable {

    private String authority;
    private String rank;
    private String source;

    public Species() {
	super();
    }

    /**
     * Get the value of source
     *
     * @return the value of source
     */
    public String getSource() {
	return source;
    }

    /**
     * Set the value of source
     *
     * @param source new value of source
     */
    public void setSource(String source) {
	this.source = source;
    }

    /**
     * Get the value of rank
     *
     * @return the value of rank
     */
    public String getRank() {
	return rank;
    }

    /**
     * Set the value of rank
     *
     * @param rank new value of rank
     */
    public void setRank(String rank) {
	this.rank = rank;
    }

    /**
     * Get the value of authority
     *
     * @return the value of authority
     */
    public String getAuthority() {
	return authority;
    }

    /**
     * Set the value of authority
     *
     * @param authority new value of authority
     */
    public void setAuthority(String authority) {
	this.authority = authority;
    }

        /**
     * @return the content
     */


    public Species(HashMap values) {
//	this.setDoctype(Global.SPECIES);
	//super(HashMap values-);
	if (values.containsKey("uid")) {
	    this.setId((String) values.get("uid"));
	}
	if (values.containsKey("content")) {
	    this.setContent((String) values.get("content"));
	}
	if (values.containsKey("title")) {
	    this.setTitle((String) values.get("title"));
	}

	if (values.containsKey("authority")) {
	    this.setAuthority((String) values.get("authority"));
	}

	if (values.containsKey("rank")) {
	    this.setRank((String) values.get("rank"));
	}
	if (values.containsKey("source")) {
	    this.setSource((String) values.get("source"));
	}
    }
}


