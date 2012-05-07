/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freshwaterlife.portlets.speciespages.liferaytexts;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.sun.data.provider.impl.CachedRowSetDataProvider;
import java.sql.SQLException;
import com.sun.sql.rowset.CachedRowSetXImpl;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author kearon
 */
public class RetrieveArticles {

    private LiferayTextsSessionBean textsSessionBean;
    private CachedRowSetXImpl tagsEntryRowSet;
    private StringBuilder debugText = new StringBuilder("<b>Debug info:</b><br/>");
    private Long articleId;

    public RetrieveArticles(LiferayTextsSessionBean sessBean) {
        this.textsSessionBean = sessBean;
        // check to make sure the session bean is initialised properly
        if (!getTextsSessionBean().initialised) {
            getTextsSessionBean()._init();
        }
    }

    public String getArticle(String taxon, String audience, String structure, boolean mustBeApproved) {
        //html string for use in debugging
        StringBuilder htmlOutput = new StringBuilder();
        debugText.append("Executing queries for ").append(taxon);
        // various ids needed along the way to retrieve articles
        Long taxonTagID = null;
        Long audienceTagID = null;
        //articles separated into exact matches and those that are close enough...
        ArrayList<Long> firstChoiceArticles = new ArrayList();
        ArrayList<Long> secondChoiceArticles = new ArrayList();

        //Get the id's from the TagsEntry table
        //NB These methods will return the id of the first matching tag, so if there is more than one tag with the same string this
        //might lead to unexpected results....
        if (taxon != null && taxon.trim().length() > 0) {
            debugText.append("<br/>do we get to taxon tag retrieval?<br/>");
            ArrayList<Long> tIds = getTagID(taxon.trim());
            debugText.append("<br/>taxon ID array list: ").append(tIds.toString());

            if (!tIds.isEmpty()) {
                debugText.append("<br/>first taxon match: ");
                taxonTagID = tIds.get(0);
                debugText.append(taxonTagID.toString());
            }

        }

        if (audience != null && audience.trim().length() > 0) {
            ArrayList<Long> aIds = getTagID(audience);
            if (aIds.size() > 0) {
                audienceTagID = aIds.get(0);
                debugText.append(" - first audience match : ").append(audienceTagID.toString());
            }
        }


        //Get list of assets (articles / paragraphs) which are tagged with mathcing taxon tags
        ArrayList<Long> taxonAssets = null;
        if (taxonTagID != null) {
            taxonAssets = getAssetsForTaxon(taxonTagID);
            debugText.append("<br/>There are ").append(taxonAssets.size()).append(" articles tagged with ").append(taxon).append(" : ").append(taxonAssets.toString());
        } else {
            debugText.append("<br/><b>No paragraphs have been tagged with ").append(taxon).append("</b><br/>");
        }

        //Iterate over list of taxon refined assets, checking if they have been tagged for the correct audience
        //TODO - this bit needs testing with a large dataset with multiple audiences for each taxon.
        if (taxonAssets != null && taxonAssets.size() > 0) {

            //if for some reason there is no audience value set, then we can skip this bit
            //otherwise we have to sort out those articles we would prefer
            if (audienceTagID == null) {
                firstChoiceArticles = taxonAssets;
            } else {
                Iterator i = taxonAssets.iterator();
                while (i.hasNext()) {
                    Long artId = (Long) i.next();
                    if (audienceMatch(artId, audienceTagID)) {
                        firstChoiceArticles.add(artId);
                    } else {
                        secondChoiceArticles.add(artId);
                    }
                }
            }
        }

        //for the time being, iterate round all article id's retrieved, getting the appropriate table pk then the content and returning it as html
        //first choices:
        debugText.append("<br/>number of these articles for selected audience is ").append(firstChoiceArticles.size()).append(" : ").append(firstChoiceArticles.toString());
        htmlOutput.append("<br/><b>Here is the content of the list of first choice articles:</b><br/>");
        Iterator i = firstChoiceArticles.iterator();
        while (i.hasNext()) {
            Long artId = (Long) i.next();
            Long classPK = this.getJournalArticlePKfromAssetId(artId);
            htmlOutput.append("<br/>classPK: ").append(classPK);
            htmlOutput.append(this.getArticleContent(classPK, mustBeApproved));
        }
        debugText.append("<br/>number of these articles not for selected audience is ").append(secondChoiceArticles.size()).append(" : ").append(secondChoiceArticles.toString());
        htmlOutput.append("<br/><b>Here is the content of the list of second choice articles:</b><br/>");
        Iterator j = secondChoiceArticles.iterator();
        while (j.hasNext()) {
            Long artId = (Long) j.next();
            Long classPK = this.getJournalArticlePKfromAssetId(artId);
            htmlOutput.append("<br/>classPK: ").append(classPK);
            htmlOutput.append(this.getArticleContent(classPK, mustBeApproved));
        }

        

        debugText.append("<br/><b>Html Output:</b><br/>").append(htmlOutput);
        return debugText.toString();
    }

    public String getArticleContent(Long articleId, boolean approved) {
        StringBuilder cont = new StringBuilder("no content to retrieve");

        debugText.append("<br/>getArticleContent(").append(articleId).append(", ").append(approved).append(")");
        //debugText.append(" -- manual overide 10212, true");

        try {
            JournalArticle article = JournalArticleLocalServiceUtil.getLatestArticle(articleId, approved);//(articleId, approved);//
            cont = new StringBuilder();
            String approvedBy = article.getApprovedByUserName();
            String title = article.getTitle();
            String xmlContent = this.parseXml(article.getContent());
            String structureId = article.getStructureId();
            cont.append("<br/>Title: ").append(title).append("<br/>Approved By: ").append("<br/>Structure id: ").append(structureId).append(approvedBy).append("<br/>").append(xmlContent);
        } catch (PortalException pEx) {
            cont.append("<br/>PortalException retrieving matching article: ").append(pEx.getLocalizedMessage());
        } catch (SystemException sEx) {
            cont.append("<br/>SystemException retrieving matching article: ").append(sEx.getLocalizedMessage());
        }

        return cont.toString();
    }

    public String parseXml(String xmlString) {
        StringBuilder htmlRet = new StringBuilder();

        //create a jdom document out of our input string
        SAXBuilder sb = new SAXBuilder();
        Document doc;
        try {
            doc = sb.build(new StringReader(xmlString));
        } catch (JDOMException jex) {
            return "<br/>an exception occurred parsing the xml into jdom: " + jex.getMessage();
        } catch (IOException iex) {
            return "<br/>an exception occurred reading the xml string for parsing in jdom: " + iex.getMessage();
        }

        //locate the content element
        Element root = doc.getRootElement();
        Element cont = root.getChild("static-content");

        //and get the content
        htmlRet.append(cont.getText());

        return htmlRet.toString();
    }

    public ArrayList<Long> getTagID(String keyword) {
        ArrayList<Long> ids = new ArrayList();

        //for testing:
        //Long l = new Long(9999);
        //ids.add(l);

        CachedRowSetDataProvider tagsDataProvider = new CachedRowSetDataProvider();
        setTagsEntryRowSet(getTextsSessionBean().getTagsEntryRowSet());
        debugText.append("rowset: ").append(this.tagsEntryRowSet).append("<br/>");

        try {
            tagsEntryRowSet.setCommand("select  entryId, name from TagsEntry where name = ?");
            tagsEntryRowSet.setTableName("TagsEntry");
            tagsEntryRowSet.setObject(1, keyword);
            tagsDataProvider.setCachedRowSet(tagsEntryRowSet);
            tagsDataProvider.refresh();
        } catch (SQLException sEx) {
            debugText.append("Database query error : ").append(sEx);
        }

        //Iterate round the results
        boolean hasNext = tagsDataProvider.cursorFirst();
        while (hasNext) {
            Long entryID = (Long) tagsDataProvider.getValue("entryID");
            String name = (String) tagsDataProvider.getValue("name");

            ids.add(entryID);

            //            debugText.append("--").append(name).append(" : ").append(entryID.toString()).append("--");

            hasNext = tagsDataProvider.cursorNext();
        }


        return ids;
    }

    public ArrayList<Long> getAssetsForTaxon(Long taxonTagID) {
        ArrayList<Long> iDs = new ArrayList();
        CachedRowSetDataProvider tagsDataProvider = new CachedRowSetDataProvider();
        setTagsEntryRowSet(getTextsSessionBean().getTagsEntryRowSet());
        try {
            tagsEntryRowSet.setCommand("select  assetId, entryId from TagsAssets_TagsEntries where entryId = ?");
            tagsEntryRowSet.setTableName("TagsAssets_TagsEntries");
            tagsEntryRowSet.setObject(1, taxonTagID);
            tagsDataProvider.setCachedRowSet(tagsEntryRowSet);
            tagsDataProvider.refresh();
        } catch (SQLException sEx) {
            debugText.append("Database query error : ").append(sEx);
        }
        //Iterate round the results
        boolean hasNext = tagsDataProvider.cursorFirst();
        while (hasNext) {
            Long assetId = (Long) tagsDataProvider.getValue("assetId");
            iDs.add(assetId);
            hasNext = tagsDataProvider.cursorNext();
        }
        return iDs;
    }

    public Long getJournalArticlePKfromAssetId(Long assetId) {
        Long pk = null;
        CachedRowSetDataProvider tagsDataProvider = new CachedRowSetDataProvider();
        setTagsEntryRowSet(getTextsSessionBean().getTagsEntryRowSet());
        try {
            tagsEntryRowSet.setCommand("select  assetId, classPK from TagsAsset where assetId = ?");
            tagsEntryRowSet.setTableName("TagsAsset");
            tagsEntryRowSet.setObject(1, assetId);
            tagsDataProvider.setCachedRowSet(tagsEntryRowSet);
            tagsDataProvider.refresh();
        } catch (SQLException sEx) {
            debugText.append("Database query error : ").append(sEx);
        }
        //Iterate round the results
        boolean hasNext = tagsDataProvider.cursorFirst();
        while (hasNext) {
            pk = (Long) tagsDataProvider.getValue("classPK");
            hasNext = tagsDataProvider.cursorNext();
        }
        return pk;
    }

    public boolean audienceMatch(Long artId, Long audience) {
        CachedRowSetDataProvider tagsDataProvider = new CachedRowSetDataProvider();
        setTagsEntryRowSet(getTextsSessionBean().getTagsEntryRowSet());
        try {
            tagsEntryRowSet.setCommand("select  assetId, entryId from TagsAssets_TagsEntries where assetId = ?");
            tagsEntryRowSet.setTableName("TagsAssets_TagsEntries");
            tagsEntryRowSet.setObject(1, artId);
            tagsDataProvider.setCachedRowSet(tagsEntryRowSet);
            tagsDataProvider.refresh();
        } catch (SQLException sEx) {
            debugText.append("Database query error : ").append(sEx);
        }
        //Iterate round the results
        boolean hasNext = tagsDataProvider.cursorFirst();
        while (hasNext) {
            Long tagId = (Long) tagsDataProvider.getValue("entryId");
            if (tagId.longValue() == audience.longValue()) {
                return true;
            }
            hasNext = tagsDataProvider.cursorNext();
        }
        return false;
    }

    /**
     * @return the articleId
     */
    public Long getArticleId() {
        return articleId;
    }

    /**
     * @param articleId the articleId to set
     */
    public void setArticleId(Long articleId) {
        this.articleId = articleId;
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

    /**
     * @param textsSessionBean the textsSessionBean to set
     */
    public void setTextsSessionBean(LiferayTextsSessionBean textsSessionBean) {
        this.textsSessionBean = textsSessionBean;
    }

    protected LiferayTextsSessionBean getTextsSessionBean() {
        return this.textsSessionBean;
    //return (LiferayTextsSessionBean) getBean("textsSessionBean");
    }
    /*
    protected Object getBean(String name) {
    FacesContext fc = FacesContext.getCurrentInstance();
    return fc.getApplication().getVariableResolver().resolveVariable(fc, name);
    }
     */
}