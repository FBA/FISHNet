package org.freshwaterlife.fedora.client;

import java.util.List;

import javax.ws.rs.core.Cookie;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.request.PurgeObject;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import com.yourmediashelf.fedora.client.response.FedoraResponseImpl;

/**
* Builder for the PurgeObject method.
*
* @author Edwin Shin
* @author Paul Johnson
*/
public class CustomPurgeObject extends PurgeObject {
	private final String pid;

    /**
*
* @param pid the identifier of the object to delete.
*/
    public CustomPurgeObject(String pid) {
        super(pid);
        this.pid = pid;
    }

    public FedoraResponse execute(FedoraClient fedora, List<Cookie> cookies) throws FedoraClientException {
        String path = String.format("objects/%s", this.pid);

        WebResource wr = fedora.resource().path(path).queryParams(getQueryParams());
        
        WebResource.Builder builder = wr.getRequestBuilder();
        
        for (Cookie c : cookies)
        {        	
        	builder = builder.cookie(c);
        }
        
        return new FedoraResponseImpl(builder.delete(ClientResponse.class));
    }
}
