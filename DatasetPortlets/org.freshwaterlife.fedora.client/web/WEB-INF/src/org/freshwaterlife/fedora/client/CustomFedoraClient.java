package org.freshwaterlife.fedora.client;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Cookie;

import com.sun.jersey.api.client.WebResource;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraCredentials;

public class CustomFedoraClient extends FedoraClient {

    private List<Cookie> cookies;

    public CustomFedoraClient(FedoraCredentials credentials) {
	super(credentials);
	cookies = new ArrayList<Cookie>();
    }

    public void addCookie(Cookie _cookie) {
	cookies.add(_cookie);
    }

    public WebResource resource(String uri) {
	WebResource wr = super.resource(uri);
	for (Cookie c : cookies) {
	    wr.cookie(c);
	}

	return wr;
    }
}
