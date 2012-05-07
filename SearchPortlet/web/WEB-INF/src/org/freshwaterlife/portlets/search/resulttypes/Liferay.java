package org.freshwaterlife.portlets.search.resulttypes;

import org.freshwaterlife.portlets.search.Global;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portlet.journal.service.JournalContentSearchLocalServiceUtil;
import com.liferay.portlet.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;
import com.liferay.portlet.blogs.model.BlogsEntry;
import com.liferay.portlet.bookmarks.model.BookmarksEntry;
import com.liferay.portlet.bookmarks.service.BookmarksEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.imagegallery.model.IGImage;
import com.liferay.portlet.imagegallery.service.IGImageLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.service.MBMessageLocalServiceUtil;
import com.liferay.portlet.wiki.model.WikiNode;
import com.liferay.portlet.wiki.model.WikiPage;
import com.liferay.portlet.wiki.model.WikiPageResource;
import com.liferay.portlet.wiki.service.WikiNodeLocalServiceUtil;
import com.liferay.portlet.wiki.service.WikiPageLocalServiceUtil;
import com.liferay.portlet.wiki.service.WikiPageResourceLocalServiceUtil;
import java.io.Serializable;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;

/**
 *
 * @author sfox
 */
public class Liferay extends BasicResultDoc implements Serializable {

    private String status;
    private String groupId;
    private String articleId;
    private String layoutFriendlyURL;
    private String layoutFullURL;
    private String strHost;
    private int iServerPort;
    private String strScheme;
    private PermissionChecker permissionChecker;
    private ThemeDisplay themeDisplay;
    private PortletRequest portletRequest;
    private FacesContext context;
    private boolean isVisible;
    private String entryClassName;
    private String userId;
    private String rootEntryClassPK;
    private String journalArticleURL;

    public Liferay() {
        super();
    }

    public Liferay(HashMap values) {
        this.setDoctype(Global.LIFERAYDOC);
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
        if (values.containsKey("modified")) {
            this.setModified((String) values.get("modified"));
        }
        if (values.containsKey("status")) {
            this.setStatus((String) values.get("status"));
        }
        if (values.containsKey("groupId")) {
            this.setGroupId((String) values.get("groupId"));
        }
        if (values.containsKey("entryClassPK")) {
            this.setArticleId((String) values.get("entryClassPK"));
        }
        if (values.containsKey("rootEntryClassPK")) {
            this.setRootEntryClassPK((String) values.get("rootEntryClassPK"));
        }
        if (values.containsKey("entryClassName")) {
            this.setEntryClassName((String) values.get("entryClassName"));
        }
        if (values.containsKey("userId")) {
            this.setUserId((String) values.get("userId"));
        }

        this.setJournalArticleURL(formatJournalArticleURL());
    }

    private String formatJournalArticleURL() {

        /* This checks if the object is viewable by the current user and, if so, works out
         * the original url for the search results. The "visible" flag variable is set for each one.
         */

        Long lGroupId;
        Long lArticleId;
        Long lUserId;
        String strJournalURL = Global.strNoURLReturned;

        JournalContentSearchLocalServiceUtil jcslu = new JournalContentSearchLocalServiceUtil();
        LayoutLocalServiceUtil llsu = new LayoutLocalServiceUtil();
        List<Long> listLayouts = new ArrayList<Long>();
        Layout layLayout;
        User user;
        int iCount = 0;
        this.isVisible = false;

        try {
            this.context = FacesContext.getCurrentInstance();
            this.portletRequest = (PortletRequest) context.getExternalContext().getRequest();
            this.themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            lGroupId = Long.decode(this.getGroupId());
        } catch (NumberFormatException n) {
            lGroupId = 0l; //zero long, not letter O
        }

        try {
            lArticleId = Long.decode(this.getArticleId());
        } catch (NumberFormatException n) {
            lArticleId = 0l; //zero long, not letter O
        }

        try {
            lUserId = Long.decode(this.getUserId());
        } catch (NumberFormatException n) {
            lUserId = 0l; //zero long, not letter O
        }


        if ((lGroupId != 0) && (lArticleId != 0)) {
            try {
                try {
                    permissionChecker = themeDisplay.getPermissionChecker();
                    this.setIsVisible(permissionChecker.hasPermission(lGroupId, this.entryClassName, this.rootEntryClassPK, ActionKeys.VIEW));
                    if (this.isIsVisible()) {
                        if (getEntryClassName().equalsIgnoreCase(JournalArticle.class.getName())) {
                            iCount = jcslu.getLayoutIdsCount(lGroupId, false, articleId);
                            listLayouts = jcslu.getLayoutIds(lGroupId, false, articleId);
                            if (iCount > 0) {
                                layLayout = llsu.getLayout(lGroupId, false, listLayouts.get(0));
                                layoutFriendlyURL = PortalUtil.getLayoutFriendlyURL(layLayout, themeDisplay);
                                layoutFullURL = PortalUtil.getLayoutActualURL(layLayout, themeDisplay.getPathMain());
                                strHost = portletRequest.getServerName();
                                iServerPort = portletRequest.getServerPort();
                                strScheme = portletRequest.getScheme();

                                if (layoutFullURL.startsWith("http://")) {
                                    strJournalURL = layoutFullURL;
                                } else {
                                    if (strHost.equalsIgnoreCase("localhost")) {
                                        strJournalURL = strScheme + "://" + strHost + ":" + iServerPort + layoutFriendlyURL;
                                    } else {
                                        strJournalURL = layoutFriendlyURL;
                                    }
                                }
                            }
                        } else if (getEntryClassName().equalsIgnoreCase(BlogsEntry.class.getName())) {
                            BlogsEntry entry = BlogsEntryLocalServiceUtil.getEntry(lArticleId);

                            Group trgtGroup = GroupLocalServiceUtil.getGroup(lGroupId);
                            String strFrUrl = trgtGroup.getFriendlyURL();
                            String strUrlPath;
                            user = UserLocalServiceUtil.getUser(lUserId);

                            if (strFrUrl.equalsIgnoreCase("/fishnet")) {
                                strUrlPath = "/home/-/blogs/";
                            } else if (strFrUrl.equalsIgnoreCase("/fishlink")) {
                                strUrlPath = "/home/-/blogs/";
                            } else if (strFrUrl.equalsIgnoreCase("/freshwater-biological-association")) {
                                strUrlPath = "/home/-/blogs/";
                            } else {
                                strUrlPath = "/blog/-/blogs/";
                            }
                            layoutFriendlyURL = "/web" + strFrUrl + strUrlPath + entry.getUrlTitle();

                            strHost = portletRequest.getServerName();
                            iServerPort = portletRequest.getServerPort();
                            strScheme = portletRequest.getScheme();

                            strJournalURL = strScheme + "://" + strHost + ":" + iServerPort + layoutFriendlyURL;
                        } else if (getEntryClassName().equalsIgnoreCase(WikiPage.class.getName())) {
                            WikiPageResource pageResource = WikiPageResourceLocalServiceUtil.getPageResource(lArticleId);

                            WikiPage wikiPage = WikiPageLocalServiceUtil.getPage(pageResource.getNodeId(), pageResource.getTitle());
                            WikiNode wikiNode = WikiNodeLocalServiceUtil.getNode(pageResource.getNodeId());
                            String strWikiNodeName = wikiNode.getName();
                            String strWikiTitle = wikiPage.getTitle();
                            Long lPageGroupId = wikiPage.getGroupId();
                            Group trgtGroup = GroupLocalServiceUtil.getGroup(lPageGroupId);
                            String strFrUrl = trgtGroup.getFriendlyURL();

                            strHost = portletRequest.getServerName();
                            iServerPort = portletRequest.getServerPort();
                            strScheme = portletRequest.getScheme();
//Extremely nasty hack!
                            if (strFrUrl.equalsIgnoreCase("/tera")) {
                                String strReplacedTitle = strWikiTitle.replaceAll("\\s", "\\+");
                                layoutFriendlyURL = "/web" + strFrUrl + "/home?p_p_id=54_INSTANCE_rU18&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=1&_54_INSTANCE_rU18_struts_action=%2Fwiki_display%2Fview&_54_INSTANCE_rU18_nodeName=" + strWikiNodeName + "&_54_INSTANCE_rU18_title=" + strReplacedTitle;
                            } else if (strFrUrl.equalsIgnoreCase("/fwl")) {
                                layoutFriendlyURL = "/web" + strFrUrl + "/wiki/-/wiki/" + strWikiNodeName + "/" + strWikiTitle;
                            } else if (strFrUrl.equalsIgnoreCase("/fishlink")) {
                                layoutFriendlyURL = "/web" + strFrUrl + strFrUrl + "-wiki/-/wiki/" + strWikiNodeName + "/" + strWikiTitle;
                            } else {
                                layoutFriendlyURL = "/freshwater-wiki/-/wiki/" + strWikiNodeName + "/" + strWikiTitle;
                            }
//</hack>
                            strJournalURL = strScheme + "://" + strHost + ":" + iServerPort + layoutFriendlyURL;

                        } else if (getEntryClassName().equalsIgnoreCase(DLFileEntry.class.getName())) {
                            DLFileEntry fileEntry = DLFileEntryLocalServiceUtil.getFileEntry(lArticleId);
                            lGroupId = fileEntry.getGroupId();

                            AssetEntry assetEntry = AssetEntryLocalServiceUtil.getEntry(getEntryClassName(), lArticleId);
                            Long lEntryId = assetEntry.getEntryId();

                            strHost = portletRequest.getServerName();
                            iServerPort = portletRequest.getServerPort();
                            strScheme = portletRequest.getScheme();
// Another hack
                            layoutFriendlyURL = "/fwl/documents/-/asset_publisher/8Ztl/document/id/" + lEntryId;
                            strJournalURL = strScheme + "://" + strHost + ":" + iServerPort + "/web" + layoutFriendlyURL;
                            if (fileEntry.getTitle().isEmpty())
                            {
                                this.setTitle("From Document Library");
                            }
                            else
                            {
                                this.setTitle(fileEntry.getTitle());
                            }
//

                        } else if (getEntryClassName().equalsIgnoreCase(IGImage.class.getName())) {
                            IGImage image = IGImageLocalServiceUtil.getImage(lArticleId);

                            strHost = portletRequest.getServerName();
                            iServerPort = portletRequest.getServerPort();
                            strScheme = portletRequest.getScheme();

                            layoutFriendlyURL = "igimage.fba.org.uk";
                            //if (layoutFullURL.startsWith("http://")) {
                            //strJournalURL = layoutFullURL;
                            //} else {
                            strJournalURL = strScheme + "://" + strHost + ":" + iServerPort + layoutFriendlyURL;
                            //}
                            //PortletURL viewImageURL = new PortletURLImpl(request, PortletKeys.IMAGE_GALLERY, plid, PortletRequest.RENDER_PHASE);

                            //viewImageURL.setWindowState(WindowState.MAXIMIZED);

                            //viewImageURL.setParameter("struts_action", "/image_gallery/view");
                            //viewImageURL.setParameter("folderId", String.valueOf(image.getFolderId()));

                        } else if (getEntryClassName().equalsIgnoreCase(MBMessage.class.getName())) {
                            MBMessage message = MBMessageLocalServiceUtil.getMessage(lArticleId);
                            String aHref = "<a href=" + themeDisplay.getPathMain() + "/message_boards/find_message?messageId=" + message.getMessageId() + ">";
                            strHost = portletRequest.getServerName();
                            iServerPort = portletRequest.getServerPort();
                            strScheme = portletRequest.getScheme();

                            layoutFriendlyURL = "mbmessage.fba.org.uk";
                            //if (layoutFullURL.startsWith("http://")) {
                            //strJournalURL = layoutFullURL;
                            //} else {
                            strJournalURL = strScheme + "://" + strHost + ":" + iServerPort + layoutFriendlyURL;
                            //}

                        } else if (getEntryClassName().equalsIgnoreCase(BookmarksEntry.class.getName())) {
                            BookmarksEntry entry = BookmarksEntryLocalServiceUtil.getEntry(lArticleId);

                            String entryURL = themeDisplay.getPathMain() + "/bookmarks/open_entry?entryId=" + entry.getEntryId();

                            strHost = portletRequest.getServerName();
                            iServerPort = portletRequest.getServerPort();
                            strScheme = portletRequest.getScheme();

                            layoutFriendlyURL = "bookmarks.fba.org.uk";
                            //if (layoutFullURL.startsWith("http://")) {
                            //strJournalURL = layoutFullURL;
                            //} else {
                            strJournalURL = strScheme + "://" + strHost + ":" + iServerPort + layoutFriendlyURL;
                            //}

                        }


                    }
                } catch (PortalException ex) {
                    Logger.getLogger(Liferay.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SystemException ex) {
                Logger.getLogger(Liferay.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return strJournalURL;
    }

    /***********************/
    /* Getters and Setters */
    /***********************/
    public String getStatus() {
        return status;
    }

    private void setStatus(String status) {
        this.status = status;
    }

    public String getGroupId() {
        return groupId;
    }

    private void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    private void setJournalArticleURL(String journalArticleURL) {
        this.journalArticleURL = journalArticleURL;
    }

    public String getJournalArticleURL() {
        return journalArticleURL;
    }

    public String getArticleId() {
        return articleId;
    }

    private void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getEntryClassName() {
        return entryClassName;
    }

    private void setEntryClassName(String entryClassName) {
        this.entryClassName = entryClassName;
    }

    public String getUserId() {
        return userId;
    }

    private void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean getIsVisible() {
        return isVisible;
    }

    public boolean isIsVisible() {
        return isVisible;
    }

    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public String getRootEntryClassPK() {
        return rootEntryClassPK;
    }

    private void setRootEntryClassPK(String rootEntryClassPK) {
        this.rootEntryClassPK = rootEntryClassPK;
    }
}
