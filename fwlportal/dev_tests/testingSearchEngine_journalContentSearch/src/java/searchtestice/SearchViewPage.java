/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package searchtestice;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactory;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchEngine;
import com.liferay.portal.kernel.search.StringQueryImpl;
import com.liferay.portal.kernel.search.SearchEngineUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalContentSearch;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portlet.journal.service.persistence.JournalArticleFinderUtil;
import com.liferay.portlet.journal.service.JournalContentSearchLocalServiceUtil;
import com.liferay.portlet.journal.service.persistence.JournalContentSearchUtil;
import java.util.List;
import javax.faces.event.ActionEvent;

/**
 *
 * @author kmcnicol
 */
public class SearchViewPage {

    private String input;
    private String results = "I'm waiting...";

    //for testing
    private long groupId;
    private String articleId;

    public void useSearchEngine(ActionEvent event) {
        StringBuffer ret = new StringBuffer("Search Engine request received<br/>");

        StringQueryImpl query = new StringQueryImpl("test");
        try {
            //SearchEngine sEng = SearchEngineUtil.getSearchEngine();
            //sEng.
            Hits h = SearchEngineUtil.search(groupId, query, 1, 1);
            Document[] docs = h.getDocs();
            ret.append("search returned ").append(docs.length).append(" hits,br/>");
        } catch (SearchException se){
            ret.append("Search engine problem - SearchException: ").append(se.getMessage());
        }

        this.setResults(ret.toString());
    }

    public void useJournalUtil(ActionEvent event) {
        StringBuffer ret = new StringBuffer("processing journal button<br/>");

        //JournalArticleFinderUtil..countByKeywords(groupId, groupId, results, version, input, articleId, results, displayDateGT, displayDateLT, approved, expired, reviewDate);
        int rc = 99999;
        int fa = 99999;
        int grpCpub = 99999;
        int grpCpri = 99999;

        try {
            rc = JournalContentSearchUtil.countAll();
            ret.append("count all = ").append(rc).append("<br/>");

            List<JournalContentSearch> fall = JournalContentSearchUtil.findAll();
            fa = fall.size();
            ret.append("find all: ").append(fa).append("<br/>");

            grpCpub = JournalContentSearchUtil.countByG_P(groupId, false);
            grpCpri = JournalContentSearchUtil.countByG_P(groupId, true);

            DynamicQueryFactory dqf = DynamicQueryFactoryUtil.getDynamicQueryFactory();
            // dqf.
            // JournalContentSearchUtil.findWithDynamicQuery();

            ret.append("count by group ").append(groupId).append(" (public): ").append(grpCpub).append(" | (private): ").append(grpCpri).append("<br/>");


        } catch (SystemException se) {
            ret.append("journal button SystemException: ").append(se.getMessage());
        }

        this.setResults(ret.toString());

    //this.setResults(getArticle(this.getArticleId(), this.getGroupId()));
    }

    private void searchEngine() {
    }

    private String getArticle(String articleId, long groupId) {
        String ret = "hoping to get a journal article here";

        long grpId = groupId;
        String artId = articleId;
        double version = 1.0;

        try {
            JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(grpId, artId, version);
            String content = ja.getContent();
            if (content == null) {
                ret = "content was null";
            } else if (content.length() == 0) {
                ret = "content string length was 0";
            } else {
                ret = content;
            }
        } catch (PortalException pe) {
            ret = "PortalException: " + pe.getMessage();
        } catch (SystemException se) {
            ret = "SystemException: " + se.getMessage();
        }

        return ret;
    }

    /**
     * @return the input
     */
    public String getInput() {
        return input;
    }

    /**
     * @param input the input to set
     */
    public void setInput(String input) {
        this.input = input;
    }

    /**
     * @return the results
     */
    public String getResults() {
        return results;
    }

    /**
     * @param results the results to set
     */
    public void setResults(String results) {
        this.results = results;
    }

    /**
     * @return the groupId
     */
    public long getGroupId() {
        return groupId;
    }

    /**
     * @param groupId the groupId to set
     */
    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    /**
     * @return the articleId
     */
    public String getArticleId() {
        return articleId;
    }

    /**
     * @param articleId the articleId to set
     */
    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }
}
