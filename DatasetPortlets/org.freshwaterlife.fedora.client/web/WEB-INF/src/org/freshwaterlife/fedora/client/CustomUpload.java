package org.freshwaterlife.fedora.client;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.request.Upload;
import com.yourmediashelf.fedora.client.response.UploadResponse;

/**
 * Builder for the Upload method.
 *
 * <p>The Fedora REST API upload method was introduced in Fedora 3.4</p>
 *
 * @author Edwin Shin
 * @author Paul Johnson
 *
 * @see "http://www.fedora-commons.org/jira/browse/FCREPO-687"
 */
public class CustomUpload extends Upload {

    private final org.slf4j.Logger logger =
	    org.slf4j.LoggerFactory.getLogger(this.getClass());
    private final File file;

    /**
     * @param file
     * the file to upload
     */
    public CustomUpload(File file) {
	super(file);
	this.file = file;
    }

    public UploadResponse execute(FedoraClient fedora, List<Cookie> cookies) throws FedoraClientException {
	ClientResponse response = null;
	String path = String.format("upload");
	WebResource wr = fedora.resource().path(path);

	MediaType mediaType = MediaType.valueOf(fedora.getMimeType(file));
	MultiPart multiPart = new FormDataMultiPart().bodyPart(new FileDataBodyPart("file", file, mediaType));

	// Check for a 302 (expected if baseUrl is http but Fedora is configured
	// to require SSL
	response = wr.head();

	if (response.getStatus() == 302) {
	    URI newLocation = response.getLocation();
	    logger.warn("302 status for upload request: " + newLocation);
	    wr = fedora.resource(newLocation.toString());
	}

	WebResource.Builder builder = wr.getRequestBuilder();

	builder = wr.queryParams(getQueryParams()).type(MediaType.MULTIPART_FORM_DATA_TYPE);

	//TODO: This has been commented out as it was causing a 405 error, and isn't needed until SSO is in and running
	
	//for (Cookie c : cookies) {
	//  builder = builder.cookie(c);
	//}

	response = builder.post(ClientResponse.class, multiPart);

	return new UploadResponse(response);
    }
}
