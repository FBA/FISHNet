package org.freshwaterlife.fedora.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


import com.sun.jersey.api.client.WebResource;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraCredentials;

public class CustomFedoraDisplayClient extends FedoraClient {

    private Map<String, String> headers;

    public CustomFedoraDisplayClient(FedoraCredentials credentials) {
	super(credentials);
	headers = new HashMap<String, String>();
    }

    public void addHeader(String _name, String _value) {
	//this.resource().cookie(_cookie);
	headers.put(_name, _value);
    }

    public WebResource resource(String uri) {
	WebResource wr = super.resource(uri);

	Set<String> keys = headers.keySet();

	Iterator<String> iter = keys.iterator();
	while (iter.hasNext()) {
	    String key = (String) iter.next();
	    wr.header(key, headers.get(key));
	}

	return wr;
    }
}
