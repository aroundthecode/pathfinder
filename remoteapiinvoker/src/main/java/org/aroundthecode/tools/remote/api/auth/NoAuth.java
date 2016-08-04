package org.aroundthecode.tools.remote.api.auth;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.aroundthecode.tools.remote.api.configuration.AbstractConnectionConfiguration;

/**
 * Class to skip authentication procedure
 * @author michele.sacchetti
 *
 */
public class NoAuth implements Auth {

	
	
	/**
	 * {@inheritDoc}
	 */
	public void doAuth(AbstractConnectionConfiguration conf, HttpClient client,HttpMethod m) {
	}

}
