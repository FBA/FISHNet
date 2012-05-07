package org.freshwaterlife.fedora.client;

/**
 * Copyright (C) 2010 MediaShelf <http://www.yourmediashelf.com/>
 *
 * This file is part of fedora-client.
 *
 * fedora-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * fedora-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with fedora-client.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.List;

import javax.ws.rs.core.Cookie;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import com.yourmediashelf.fedora.client.response.FedoraResponseImpl;
import com.yourmediashelf.fedora.client.request.GetObjectXML;

/**
 * Builder for the GetObjectXML method.
 *
 * @author Edwin Shin
 * @author Eric Liao
 * @since 0.0.3
 */
public class CustomGetObjectXML extends GetObjectXML {

    private final String pid;

    /**
     * @param pid
     *        persistent identifier of the digital object, e.g. "demo:1".
     */
    public CustomGetObjectXML(String pid) {
    	super(pid);
        this.pid = pid;
    }

    public FedoraResponse execute(FedoraClient fedora, List<Cookie> cookies) throws FedoraClientException {
        
    	WebResource wr = fedora.resource();
    	String path = String.format("objects/%s/objectXML", pid);
    	
    	WebResource.Builder builder = wr.path(path).queryParams(getQueryParams()).getRequestBuilder();
        
        for (Cookie c : cookies)
        {
        	builder = builder.cookie(c);
        }                

        ClientResponse cr = builder.get(ClientResponse.class);
        return new FedoraResponseImpl(cr);
    }
}
