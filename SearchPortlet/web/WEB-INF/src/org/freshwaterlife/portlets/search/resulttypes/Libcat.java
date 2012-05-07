package org.freshwaterlife.portlets.search.resulttypes;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import java.io.Serializable;
import java.util.HashMap;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import org.freshwaterlife.portlets.search.Global;

/**
 *
 * @author sfox
 */
public class Libcat extends BasicResultDoc implements Serializable {

    private String pid;
    private String modsAuthor;
    private String modsHostTitle;
    private String modsPublicationDate;
    private String modsExtVolume;
    private String modsExtPart;
    private String modsExtPages;
    private String modsHostAuthor;
    private String modsPublisher;
    private String modsPublisherLocation;
    private String modsShelfmark;
    private String libcatArticleURL;
    private ThemeDisplay themeDisplay;
    private PortletRequest portletRequest;
    private FacesContext context;
    private String libcatType;

    public Libcat() {
        super();
    }

    public Libcat(HashMap values) {
        this.setDoctype(Global.LIBCAT);
        //super(HashMap values-);

        if (values.containsKey("uid")) {
            this.setId((String) values.get("uid"));
        }
        if (values.containsKey("PID")) {
            this.setPid((String) values.get("PID"));
        }
        if (values.containsKey("mods.title")) {
            this.setModsTitle((String) values.get("mods.title"));
        }
        if (values.containsKey("mods.author")) {
            this.setModsAuthor((String) values.get("mods.author"));
        }
        if (values.containsKey("mods.host.title")) {
            this.setModsHostTitle((String) values.get("mods.host.title"));
        }
        if (values.containsKey("mods.publication.date")) {
            this.setModsPublicationDate((String) values.get("mods.publication.date"));
        }
        if (values.containsKey("mods.ext.volume")) {
            this.setModsExtVolume((String) values.get("mods.ext.volume"));
        }
        if (values.containsKey("mods.ext.part")) {
            this.setModsExtPart((String) values.get("mods.ext.part"));
        }
        if (values.containsKey("mods.ext.pages")) {
            this.setModsExtPages((String) values.get("mods.ext.pages"));
        }

        if (values.containsKey("mods.host.author")) {
            this.setModsHostAuthor((String) values.get("mods.host.author"));
        }

        if (values.containsKey("mods.publisher")) {
            this.setModsPublisher((String) values.get("mods.publisher"));
        }

        if (values.containsKey("mods.publisher.location")) {
            this.setModsPublisherLocation((String) values.get("mods.publisher.location"));
        }

        if (values.containsKey("mods.shelfmark")) {
            this.setModsShelfmark((String) values.get("mods.shelfmark"));
        }

        this.setLibcatArticleURL(formatLibcatArticleURL());

        this.evalLibcatType();
    }

    private String formatLibcatArticleURL() {
        String strArticleURL = Global.strNoURLReturned;
        String strHost;
        int iServerPort;
        String strScheme;

        try {
            this.context = FacesContext.getCurrentInstance();
            this.portletRequest = (PortletRequest) context.getExternalContext().getRequest();
            this.themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

        } catch (Exception e) {
        }

        strHost = portletRequest.getServerName();
        iServerPort = portletRequest.getServerPort();
        strScheme = portletRequest.getScheme();
        strArticleURL = strScheme + "://" + strHost + ":" + iServerPort + "/web/guest/library/-/catalogue/view/" + this.getPid();

        return strArticleURL;
    }

    private void evalLibcatType() {
        if (this.getModsHostTitle() == null) {
            this.setLibcatType("B");
        } else {
            if (this.getModsHostAuthor() == null) {
                this.setLibcatType("A");
            } else {
                this.setLibcatType("C");
            }
        }
    }

    /***********************/
    /* Getters and Setters */
    /***********************/
    public String getModsExtPages() {
        return modsExtPages;
    }

    private void setModsExtPages(String modsExtPages) {
        this.modsExtPages = modsExtPages;
    }

    public String getModsExtPart() {
        return modsExtPart;
    }

    private void setModsExtPart(String modsExtPart) {
        this.modsExtPart = modsExtPart;
    }

    public String getModsExtVolume() {
        return modsExtVolume;
    }

    private void setModsExtVolume(String modsExtVolume) {
        this.modsExtVolume = modsExtVolume;
    }

    public String getModsPublicationDate() {
        return modsPublicationDate;
    }

    private void setModsPublicationDate(String modsPublicationDate) {
        this.modsPublicationDate = modsPublicationDate;
    }

    public String getModsHostTitle() {
        return modsHostTitle;
    }

    private void setModsHostTitle(String modsHostTitle) {
        this.modsHostTitle = modsHostTitle;
    }

    public String getModsAuthor() {
        return modsAuthor;
    }

    private void setModsAuthor(String modsAuthor) {
        this.modsAuthor = modsAuthor;
    }

    public String getPid() {
        return pid;
    }

    private void setPid(String pid) {
        this.pid = pid;
    }

    public String getLibcatArticleURL() {
        return libcatArticleURL;
    }

    private void setLibcatArticleURL(String libcatArticleURL) {
        this.libcatArticleURL = libcatArticleURL;
    }

    public String getModsShelfmark() {
        return modsShelfmark;
    }

    private void setModsShelfmark(String modsShelfmark) {
        this.modsShelfmark = modsShelfmark;
    }

    public String getModsPublisherLocation() {
        return modsPublisherLocation;
    }

    private void setModsPublisherLocation(String modsPublisherLocation) {
        this.modsPublisherLocation = modsPublisherLocation;
    }

    public String getModsPublisher() {
        return modsPublisher;
    }

    private void setModsPublisher(String modsPublisher) {
        this.modsPublisher = modsPublisher;
    }

    public String getModsHostAuthor() {
        return modsHostAuthor;
    }

    private void setModsHostAuthor(String modsHostAuthor) {
        this.modsHostAuthor = modsHostAuthor;
    }

    public String getLibcatType() {
        return libcatType;
    }

    public void setLibcatType(String libcatType) {
        this.libcatType = libcatType;
    }
}
