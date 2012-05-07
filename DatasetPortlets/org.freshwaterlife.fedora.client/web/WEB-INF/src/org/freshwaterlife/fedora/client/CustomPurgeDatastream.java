package org.freshwaterlife.fedora.client;

import java.io.File;
import java.util.List;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.request.PurgeDatastream;
import com.yourmediashelf.fedora.client.response.PurgeDatastreamResponse;

/**
 * Builder for the AddDatastream method.
 *
 * @author Edwin Shin
 * @author Eric Liao
 *
 */
public class CustomPurgeDatastream extends PurgeDatastream {

    private final String pid;
    private final String dsId;

    /**
     * @param pid
     * persistent identifier of the digital object
     * @param dsId
     * datastream identifier
     */
    public CustomPurgeDatastream(String pid, String dsId) {
	super(pid, dsId);
	this.pid = pid;
	this.dsId = dsId;
    }

    public PurgeDatastreamResponse execute(FedoraClient fedora, List<Cookie> cookies) throws FedoraClientException {
	WebResource wr = fedora.resource();
	String path = String.format("objects/%s/datastreams/%s", pid, dsId);

	wr = wr.path(path).queryParams(getQueryParams());

	WebResource.Builder builder = wr.getRequestBuilder();

	for (Cookie c : cookies) {
	    builder = builder.cookie(c);
	}

	ClientResponse cr = builder.delete(ClientResponse.class);
	return null;
    }
}
