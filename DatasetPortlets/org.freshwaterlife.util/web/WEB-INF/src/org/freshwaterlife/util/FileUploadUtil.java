package org.freshwaterlife.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;

public class FileUploadUtil {

    public static boolean isMultipart(final ActionRequest request) {
        return PortletFileUpload.isMultipartContent(request);
    }
	
    @SuppressWarnings("unchecked")
	public static List<FileItem> parseRequest(final ActionRequest request) 
            throws FileUploadException {
        FileItemFactory factory = new DiskFileItemFactory(); 
        PortletFileUpload upload = new PortletFileUpload(factory);
        return upload.parseRequest(request);
    }
	
    public static Map<String, String[]> getParameters(final List<FileItem> items) {
    	Map<String, String[]> result = new HashMap<String, String[]>();
        for (FileItem item : items) {
            if (item.isFormField()) {
            	String name = item.getFieldName();
                String value = item.getString();
                String[] values = result.get(name);

                if (values == null) {
                    // Not in parameter map yet, so add as new value.
                    result.put(name, new String[] { value });
                } else {
                    // Multiple field values, so add new value to existing array.
                    int length = values.length;
                    String[] newValues = new String[length + 1];
                    System.arraycopy(values, 0, newValues, 0, length);
                    newValues[length] = value;
                    result.put(name, newValues);
                }
            }
        }
        return result;
    }

    public static Map<String, FileItem> getFiles(final List<FileItem> items) {
            Map<String, FileItem> result = new HashMap<String, FileItem>();
        for (FileItem item : items) {
            if (!item.isFormField()) {
                result.put(item.getName(), item);
            }
        }
        return result;
    }
}