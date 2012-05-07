package org.freshwaterlife.portlets.dataset.edit;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.filter.ActionRequestWrapper;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.freshwaterlife.util.FileUploadUtil;

public class FileUploadActionRequestWrapper extends ActionRequestWrapper {
	    
    private Map<String, String[]> parameters;
	
    public FileUploadActionRequestWrapper(final ActionRequest request) 
            throws FileUploadException {
        super(request);
        parameters = new HashMap<String, String[]>();
        if (FileUploadUtil.isMultipart(request)) {
            //logger.info("multipart");
            List<FileItem> items = FileUploadUtil.parseRequest(request);
            Map<String, FileItem> files = FileUploadUtil.getFiles(items);
            for (String name : files.keySet()) {
                setAttribute(files.get(name).getFieldName(), files.get(name));
                //logger.info("file " + files.get(name).getFieldName());
            }
            parameters = FileUploadUtil.getParameters(items);
        }
    }
	
    @Override
    public Map<String, String[]> getParameterMap() {
        return parameters;
    }
	
    @Override
    public String getParameter(final String name) {
        if (parameters.get(name) != null) {
            return parameters.get(name)[0];
        }
        return null;
    }

    @Override
    public String[] getParameterValues(final String name) {
        return parameters.get(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }
}