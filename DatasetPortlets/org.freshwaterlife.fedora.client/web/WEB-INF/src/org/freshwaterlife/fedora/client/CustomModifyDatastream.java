package org.freshwaterlife.fedora.client;

import java.io.File;
import java.util.List;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.request.ModifyDatastream;
import com.yourmediashelf.fedora.client.response.ModifyDatastreamResponse;

/**
 * Builder for the AddDatastream method.
 *
 * @author Edwin Shin
 * @author Eric Liao
 *
 */
public class CustomModifyDatastream extends ModifyDatastream {

    private final String pid;
    private final String dsId;
    private Object content;
    private String mimetype;

    /**
     * @param pid
     * persistent identifier of the digital object
     * @param dsId
     * datastream identifier
     */
    public CustomModifyDatastream(String pid, String dsId) {
	super(pid, dsId);
	this.pid = pid;
	this.dsId = dsId;
    }
    public CustomModifyDatastream(String pid, String dsId, String contenttype) {
	super(pid, dsId);
	this.pid = pid;
	this.dsId = dsId;
	this.mimetype = contenttype;
    }

    public ModifyDatastream content(File content) {
	this.content = content;
	return this;
    }

    public ModifyDatastream content(String content) {
	this.content = content;
	return this;
    }

    public ModifyDatastreamResponse execute(FedoraClient fedora, List<Cookie> cookies, String contenttype) throws FedoraClientException {
	this.mimetype = contenttype;

	return this.execute(fedora, cookies);
    }


    public ModifyDatastreamResponse execute(FedoraClient fedora, List<Cookie> cookies) throws FedoraClientException {
	WebResource wr = fedora.resource();
	String path = String.format("objects/%s/datastreams/%s", pid, dsId);

	wr = wr.path(path).queryParams(getQueryParams());

	WebResource.Builder builder = wr.getRequestBuilder();

	for (Cookie c : cookies) {
	    builder = builder.cookie(c);
	}

	ClientResponse response = null;
	if (content == null) {
	    response = builder.put(ClientResponse.class);
	} else if (content instanceof String) {
	    response = builder.type(MediaType.TEXT_XML_TYPE).put(ClientResponse.class, content);
	} else if (content instanceof File) {
	    File f = (File) content;
	    //String mimeType = fedora.getMimeType(f);
	    String mimeType = this.mimetype;
	    response = builder.type(mimeType).put(ClientResponse.class, f);
	} else {
	    throw new IllegalArgumentException("unknown request content type");
	}
	return new ModifyDatastreamResponse(response);
    }
}
