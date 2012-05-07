/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freshwaterlife.portlets.speciespages.liferaytexts;

import com.sun.rave.web.ui.appbase.AbstractSessionBean;
import javax.faces.FacesException;
import com.sun.sql.rowset.CachedRowSetXImpl;
import java.sql.SQLException;

/**
 *
 * @author kearon
 */
public class LiferayTextsSessionBean extends AbstractSessionBean {
    private CachedRowSetXImpl tagsEntryRowSet;
    public boolean initialised = false;

    @Override
    public void init() {
        // Perform initializations inherited from our superclass
        super.init();
        // Perform application initialization that must complete
        // *before* managed components are initialized

        try {
            _init();
        } catch (Exception e) {
            log("taxonomySessionBean Initialization Failure", e);
            throw e instanceof FacesException ? (FacesException) e : new FacesException(e);
        }
    // Perform application initialization that must complete
    // *after* managed components are initialized
    }

    public void _init() {
         setTagsEntryRowSet(new CachedRowSetXImpl());
        try {
            getTagsEntryRowSet().setDataSourceName("jdbc/LiferayPool");
            getTagsEntryRowSet().setCommand("select entryID, name from TagsEntry");
            getTagsEntryRowSet().setTableName("TagsEntry");
            getTagsEntryRowSet().execute();
        } catch (SQLException sQLException) {
           log("Database initialisation error : ", sQLException);
        }
        this.initialised = true;
    }

    @Override
    public void destroy() {
        getTagsEntryRowSet().close();
    }

    /**
     * @return the tagsEntryRowSet
     */
    public CachedRowSetXImpl getTagsEntryRowSet() {
        return tagsEntryRowSet;
    }

    /**
     * @param tagsEntryRowSet the tagsEntryRowSet to set
     */
    public void setTagsEntryRowSet(CachedRowSetXImpl tagsEntryRowSet) {
        this.tagsEntryRowSet = tagsEntryRowSet;
    }
}