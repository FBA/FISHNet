package org.freshwaterlife.portlets.dataset.resulttypes;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import java.io.Serializable;
import java.util.HashMap;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;

import org.freshwaterlife.portlets.dataset.Global;

/**
 * 
 * @author sfox
 */
public class Orange extends Dataset implements Serializable {

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
	private String keywords;
	private String modsRightsCreator;
	private String modsRightsPhone;
	private String modsRightsInstitution;
	private String modsRightsContact;
	private String modsRightsOwner;
	private String ModsRightsEmail;
	private String modsSubjectTopic;
	private String modsAbstract;
	private String entryClassName;
	private String userId;
	private String rootEntryClassPK;
	private String submitText;
	private String datasetdisplayURL;
	private String uploader;
	private String makeDatasetPublicText;


	public Orange() {
		super();
	}

	public Orange(HashMap values) {

		super(values);
		this.setDoctype(Global.ORANGE);

		if (values.containsKey("uid")) {
			this.setId((String) values.get("uid"));
		}

		if (values.containsKey("mods.title")) {
			this.setModsTitle((String) values.get("mods.title"));
		}
		if (values.containsKey("lastModifiedDate")) {
			String date = (String) values.get("lastModifiedDate");
			String day = date.substring(0, 10);
			String time = date.substring(11, 23);
			String formattedDate = day + "</br>" + time;
			this.setLastModifiedDate(formattedDate);
		}

		if (values.containsKey("keywords")) {
			this.setModKeywords((String) values.get("keywords"));
		}

		if (values.containsKey("mods.rights.creator")) {
			this.setModsRightsCreator((String) values
					.get("mods.rights.creator"));
		}

		if (values.containsKey("PID")) {
			this.setPid((String) values.get("PID"));
		}

		if (values.containsKey("mods.rights.phone")) {
			this.setModsRightsPhone((String) values.get("mods.rights.phone"));
		}

		if (values.containsKey("mods.rights.institution")) {
			this.setModsRightsInstitution((String) values
					.get("mods.rights.institution"));
		}

		if (values.containsKey("mods.rights.contact")) {
			this.setModsRightsContact((String) values
					.get("mods.rights.contact"));
		}

		if (values.containsKey("mods.rights.owner")) {
			this.setModsRightsOwner((String) values.get("mods.rights.owner"));
		}

		if (values.containsKey("mods.rights.email")) {
			this.setModsRightsEmail((String) values.get("mods.rights.email"));
		}

		if (values.containsKey("mods.subject.topic")) {
			this.setModsSubjectTopic((String) values.get("mods.subject.topic"));
		}

		if (values.containsKey("mods.abstract")) {
			this.setModsAbstract((String) values.get("mods.abstract"));
		}
		
		if (values.containsKey("roleIds")) {
			// parse to remove administrator
			String[] roleIds = ((String) values.get("roleIds")).split(",");
			String roleId = "";
			for (String thisRole : roleIds) {
				if (!thisRole.equals("administrator")) {
					roleId = thisRole.replaceAll("[^0-9]", "");
				}
			}
			User user;
			try {
				user = UserLocalServiceUtil.getUserById(Integer.parseInt(roleId));
				this.setUploader(user.getFullName());
				this.setUploaderScreenName(user.getScreenName());

			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PortalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		this.setStatus("Dataset is in the Orange Category");
		this.setStatusImgURL("/resources/images/orange.png");
		
		this.setMakeDatasetPublicText("Make Dataset publicly available");


		this.setDatasetdisplayURL(formatDatasetDisplayURL());

	}

	/***********************/
	/* Getters and Setters */
	/***********************/
	public String getGroupId() {
		return groupId;
	}

	private void setGroupId(String groupId) {
		this.groupId = groupId;
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

	public String getModsAbstract() {
		return modsAbstract;
	}

	public void setModsAbstract(String modsAbstract) {
		this.modsAbstract = modsAbstract;
	}

	public String getModsSubjectTopic() {
		return modsSubjectTopic;
	}

	public void setModsSubjectTopic(String modsSubjectTopic) {
		this.modsSubjectTopic = modsSubjectTopic;
	}

	public String getModsRightsEmail() {
		return ModsRightsEmail;
	}

	public void setModsRightsEmail(String ModsRightsEmail) {
		this.ModsRightsEmail = ModsRightsEmail;
	}

	public String getModsRightsOwner() {
		return modsRightsOwner;
	}

	public void setModsRightsOwner(String modsRightsOwner) {
		this.modsRightsOwner = modsRightsOwner;
	}

	public String getModsRightsContact() {
		return modsRightsContact;
	}

	public void setModsRightsContact(String modsRightsContact) {
		this.modsRightsContact = modsRightsContact;
	}

	public String getModsRightsInstitution() {
		return modsRightsInstitution;
	}
	public void setModsRightsInstitution(String modsRightsInstitution) {
		this.modsRightsInstitution = modsRightsInstitution;
	}
	public String getModsRightsPhone() {
		return modsRightsPhone;
	}

	public void setModsRightsPhone(String modsRightsPhone) {
		this.modsRightsPhone = modsRightsPhone;
	}

	public String getModsRightsCreator() {
		return modsRightsCreator;
	}

	public void setModsRightsCreator(String modsRightsCreator) {
		this.modsRightsCreator = modsRightsCreator;
	}

	public String getModKeywords() {
		return keywords;
	}

	public void setModKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getRootEntryClassPK() {
		return rootEntryClassPK;
	}

	public void setRootEntryClassPK(String rootEntryClassPK) {
		this.rootEntryClassPK = rootEntryClassPK;
	}

	

	private String formatDatasetDisplayURL() {

		// /web/guest/dataset-display/-/dataset-display/<PID>

		String strDisplayURL = Global.strNoURLReturned;

		strDisplayURL = "/web/guest/dataset-display/-/dataset-display/"
				+ this.getPid();

		return strDisplayURL;
	}

	public void setDatasetdisplayURL(String datasetdisplayURL) {
		this.datasetdisplayURL = datasetdisplayURL;
	}

	public String getDatasetdisplayURL() {
		return datasetdisplayURL;
	}

	public void setUploader(String uploader) {
		this.uploader = uploader;
	}

	public String getUploader() {
		return uploader;
	}

	public void setMakeDatasetPublicText(String makeDatasetPublicText) {
		this.makeDatasetPublicText = makeDatasetPublicText;
	}

	public String getMakeDatasetPublicText() {
		return makeDatasetPublicText;
	}

}
