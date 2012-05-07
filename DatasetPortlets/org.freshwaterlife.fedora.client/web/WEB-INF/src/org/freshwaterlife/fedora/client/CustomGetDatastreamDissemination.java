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
package org.freshwaterlife.fedora.client;

import java.util.List;

import javax.ws.rs.core.Cookie;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.request.GetDatastreamDissemination;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import com.yourmediashelf.fedora.client.response.FedoraResponseImpl;

/**
 * Builder for the GetDatastreamDissemination method.
 *
 * @author Edwin Shin
 * @author Eric Liao
 */
public class CustomGetDatastreamDissemination extends GetDatastreamDissemination {

    private final String pid;
    private final String dsId;
    private String mimetype;

    /**
     * @param pid
     *        persistent identifier of the digital object
     */
    public CustomGetDatastreamDissemination(String pid, String dsId) {
	super(pid, dsId);
	this.pid = pid;
	this.dsId = dsId;
    }

    public GetDatastreamDissemination asOfDateTime(String asOfDateTime) {
	addQueryParam("asOfDateTime", asOfDateTime);
	return this;
    }

    public GetDatastreamDissemination download(boolean download) {
	addQueryParam("download", Boolean.toString(download));
	return this;
    }

    public FedoraResponse execute(FedoraClient fedora, List<Cookie> cookies) throws FedoraClientException {
	WebResource wr = fedora.resource();
	String path = String.format("objects/%s/datastreams/%s/content", pid, dsId);

	wr = wr.path(path).queryParams(getQueryParams());

	WebResource.Builder builder = wr.getRequestBuilder();

	for (Cookie c : cookies) {
	    builder = builder.cookie(c);
	}

	return new FedoraResponseImpl(builder.get(ClientResponse.class));
    }
}
