package org.freshwaterlife.portlets.dataset.edit;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import org.apache.commons.fileupload.FileUploadException;
import org.freshwaterlife.util.FileUploadUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class FacesPortlet extends com.sun.faces.portlet.FacesPortlet {

    private static final Log logger = LogFactoryUtil.getLog(FacesPortlet.class);

    public FacesPortlet() {
	super();
	//logger.info("init");
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse response)
	    throws IOException, PortletException {

	ActionRequest myRequest = request;


	if (FileUploadUtil.isMultipart(request)) {
	    //logger.info("multipart!");
	    try {
		myRequest = new FileUploadActionRequestWrapper(request);
	    } catch (FileUploadException e) {
		logger.error(e.getMessage());
	    }
	}

	super.processAction(myRequest, response);

    }
}
