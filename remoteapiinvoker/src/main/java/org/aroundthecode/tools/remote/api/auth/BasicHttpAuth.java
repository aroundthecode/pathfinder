package org.aroundthecode.tools.remote.api.auth;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.aroundthecode.tools.remote.api.configuration.AbstractConnectionConfiguration;

/**
 * Basic HTTP username and password authentication
 * @author michele.sacchetti
 *
 */
public class BasicHttpAuth implements Auth {

	private String username;
	private String pass;

	/**
	 * Basic constructor
	 * @param username User name used for login procedure
	 * @param pass Password used for login procedure
	 */
	public BasicHttpAuth(String username, String pass) {
		this.username = username;
		this.pass = pass;
	}

	/**
	 * {@inheritDoc}
	 */
	public void doAuth(AbstractConnectionConfiguration conf, HttpClient client, HttpMethod m) {
		
		client.getState().setCredentials(
				new AuthScope(conf.getDomain(), conf.getPort(), "realm"),
				new UsernamePasswordCredentials(getUsername(), getPass()));
		client.getParams().setAuthenticationPreemptive(true);
		m.setDoAuthentication(true);
	}

	/**
	 * User name used for login procedure
	 * @return
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * User name used for login procedure
	 * @return
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Password used for login procedure
	 * @return
	 */
	public String getPass() {
		return pass;
	}

	/**
	 * Password used for login procedure
	 * @return
	 */
	public void setPass(String pass) {
		this.pass = pass;
	}

}
