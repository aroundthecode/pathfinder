package org.aroundthecode.tools.remote.api.auth;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.aroundthecode.tools.remote.api.configuration.AbstractConnectionConfiguration;

/**
 * Interface for generic authentication method.
 * @author michele.sacchetti
 *
 */
public interface Auth {

	/**
	 * Authentication Method, it will be invoked before calling the real request to the remote server
	 * @param conf server connection configuration bean
	 * @param client {@link HttpClient} used for authenticated calls
	 * @param m {@link HttpMethod} to be authenticated
	 * @throws AuthenticationException raised if any part of the authentication would fail
	 */
	void doAuth(AbstractConnectionConfiguration conf, HttpClient client, HttpMethod m) throws AuthenticationException;
	
}
