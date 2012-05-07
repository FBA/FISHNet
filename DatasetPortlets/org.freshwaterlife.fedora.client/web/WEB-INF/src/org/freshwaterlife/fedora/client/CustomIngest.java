package org.freshwaterlife.fedora.client;

import java.io.File;
import java.util.List;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.request.Ingest;
import com.yourmediashelf.fedora.client.response.IngestResponse;

/**
 * Builder for the Ingest method.
 *
 * @author Edwin Shin
 * @author Paul Johnson
 */
public class CustomIngest extends Ingest {

    private final String pid;
    private Object content;

    /**
     *
     * @param pid the identified to assign or null to use a server-assigned pid
     */
    public CustomIngest(String pid) {
	super(pid);
	this.pid = pid;
    }

    /**
     * Constructor that will use a server-generated pid.
     */
    public CustomIngest() {
	this(null);
    }

    /**
     * The file to be ingested as a new object.
     *
     * @param content
     * @return this builder
     */
    public Ingest content(File content) {
	this.content = content;
	return this;
    }

    /**
     * The XML to be ingested as a new object.
     *
     * @param content
     * @return this builder
     */
    public Ingest content(String content) {
	this.content = content;
	return this;
    }

    public IngestResponse execute(FedoraClient fedora, List<Cookie> cookies) throws FedoraClientException {
	ClientResponse response = null;
	String path;
	if (pid == null || pid.isEmpty()) {
	    path = "objects/new";
	} else {
	    path = String.format("objects/%s", pid);
	}

	WebResource wr = fedora.resource().path(path).queryParams(getQueryParams());

	WebResource.Builder builder = wr.getRequestBuilder();

	for (Cookie c : cookies) {
	    builder = builder.cookie(c);
	}

	if (content == null) {
	    response = builder.post(ClientResponse.class);
	    System.out.println("No Content");
	} else if (content instanceof String) {
	    response = builder.type(MediaType.TEXT_XML_TYPE).post(ClientResponse.class, content);
	    System.out.println("String Content");
	} else if (content instanceof File) {
	    File f = (File) content;
	    response = builder.type(MediaType.TEXT_XML_TYPE).post(ClientResponse.class, f);
	    System.out.println("File Content");
	} else {
	    throw new IllegalArgumentException("unknown request content type");
	}
	return new IngestResponse(response);
    }
}
