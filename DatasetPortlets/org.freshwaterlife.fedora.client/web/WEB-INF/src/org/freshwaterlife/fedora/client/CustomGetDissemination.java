package org.freshwaterlife.fedora.client;

import java.util.List;

import javax.ws.rs.core.Cookie;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.request.GetDissemination;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import com.yourmediashelf.fedora.client.response.FedoraResponseImpl;

/**
* Builder for the GetDissemination method.
*
* @author Edwin Shin
*/
public class CustomGetDissemination extends GetDissemination {

    private final String pid;
    private final String sdefPid;
    private final String method;

    /**
    *
    * @param pid persistent identifier of the digital object
    * @param sdefPid persistent identifier of the sDef defining the methods
    * @param method method to invoke
    */
    public CustomGetDissemination(String pid, String sdefPid, String method) {
        super(pid, sdefPid, method);
        this.pid = pid;
        this.sdefPid = sdefPid;
        this.method = method;
    }
    
    /**
    * Add a parameter required by the method.
    *
    * @param key
    * @param value
    * @return this builder
    */
    public CustomGetDissemination methodParam(String key, String value) {
    	addQueryParam(key, value);
        return this;
    }

    public FedoraResponse execute(FedoraClient fedora, List<Cookie> cookies) throws FedoraClientException {
        WebResource wr = fedora.resource();
        String path = String.format("objects/%s/methods/%s/%s", pid, sdefPid, method);
        System.out.println("Path: " + path);
        WebResource.Builder builder = wr.path(path).queryParams(getQueryParams()).getRequestBuilder();
        
        for (Cookie c : cookies)
        {
        	//System.out.println("Cookie: " + c.getName());
        	builder = builder.cookie(c);
        }
        
        ClientResponse cr = builder.get(ClientResponse.class);
        return new FedoraResponseImpl(cr);
    }

}
