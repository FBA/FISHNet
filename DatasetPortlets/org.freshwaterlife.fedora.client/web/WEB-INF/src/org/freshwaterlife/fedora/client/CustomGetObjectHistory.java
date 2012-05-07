package org.freshwaterlife.fedora.client;

import java.util.List;

import javax.ws.rs.core.Cookie;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import com.yourmediashelf.fedora.client.response.FedoraResponseImpl;
import com.yourmediashelf.fedora.client.request.GetObjectHistory;

/**
 *
 * @author sfox
 */
public class CustomGetObjectHistory extends GetObjectHistory {

    private final String pid;

    public CustomGetObjectHistory(String pid) {
	super(pid);
	this.pid = pid;
    }

    public FedoraResponse execute(FedoraClient fedora, List<Cookie> cookies) throws FedoraClientException {

	WebResource wr = fedora.resource();
	String path = String.format("getObjectHistory/%s", pid);

	WebResource.Builder builder = wr.path(path).queryParams(getQueryParams()).getRequestBuilder();

	for (Cookie c : cookies) {
	    builder = builder.cookie(c);
	}

	ClientResponse cr = builder.get(ClientResponse.class);
	return new FedoraResponseImpl(cr);
    }
}
