package org.freshwaterlife.fedora.client;

import java.io.File;
import java.util.List;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.request.AddDatastream;
import com.yourmediashelf.fedora.client.response.AddDatastreamResponse;

/**
 * Builder for the AddDatastream method.
 *
 * @author Edwin Shin
 * @author Paul Johnson
 *
 */
public class CustomAddDatastream extends AddDatastream {

    private final String pid;
    private final String dsId;
    private Object content;

    /**
     * @param pid
     * persistent identifier of the digital object
     * @param dsId
     * datastream identifier
     */
    public CustomAddDatastream(String pid, String dsId) {
	super(pid, dsId);
	this.pid = pid;
	this.dsId = dsId;
    }

    public AddDatastreamResponse execute(FedoraClient fedora, List<Cookie> cookies) throws FedoraClientException {
	// special handling for RELS-EXT and RELS-INT
	if (dsId.equals("RELS-EXT") || dsId.equals("RELS-INT")) {
	    String mimeType = getFirstQueryParam("mimeType");
	    if (mimeType == null) {
		addQueryParam("mimeType", "application/rdf+xml");
	    }

	    String formatURI = getFirstQueryParam("formatURI");
	    if (formatURI == null) {
		if (dsId.equals("RELS-EXT")) {
		    addQueryParam("formatURI", "info:fedora/fedora-system:FedoraRELSExt-1.0");
		} else if (dsId.equals("RELS-INT")) {
		    addQueryParam("formatURI", "info:fedora/fedora-system:FedoraRELSInt-1.0");
		}
	    }
	}

	WebResource wr = fedora.resource();
	String path = String.format("objects/%s/datastreams/%s", pid, dsId);

	wr = wr.path(path).queryParams(getQueryParams());

	WebResource.Builder builder = wr.getRequestBuilder();

	for (Cookie c : cookies) {
	    //System.out.println("Cookie: " + c.getName());
	    builder = builder.cookie(c);
	}

	ClientResponse response = null;
	String mimeType = getFirstQueryParam("mimeType");
	MediaType mediaType = null;
	if (content == null) {
	    response = builder.post(ClientResponse.class);
	} else if (content instanceof String) {
	    if (mimeType == null) {
		mediaType = MediaType.TEXT_XML_TYPE;
	    } else {
		mediaType = MediaType.valueOf(mimeType);
	    }
	    response = builder.type(mediaType).post(ClientResponse.class, content);
	} else if (content instanceof File) {
	    File f = (File) content;
	    if (mimeType == null) {
		mediaType = MediaType.valueOf(fedora.getMimeType(f));
	    } else {
		mediaType = MediaType.valueOf(mimeType);
	    }
	    response = builder.type(mediaType).post(ClientResponse.class, f);
	} else {
	    throw new IllegalArgumentException("unknown request content type");
	}
	return new AddDatastreamResponse(response);
    }
}
