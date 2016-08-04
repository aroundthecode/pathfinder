package org.aroundthecode.tools.remote.api.auth;

import static org.junit.Assert.fail;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.aroundthecode.tools.remote.api.configuration.AbstractConnectionConfiguration.AllowedProtocol;
import org.aroundthecode.tools.remote.api.configuration.BasicConnectionConfiguration;
import org.junit.Test;

public class LoginHttpAuthTest {

	
	/**
	 * Tests authentication via httpbin fake services
	 */
	@Test
	public void test() {
		Auth auth = new BasicHttpAuth("user", "passwd");
		BasicConnectionConfiguration conf = new BasicConnectionConfiguration(AllowedProtocol.HTTP, "httpbin.org");
		
		HttpClient client = new HttpClient();
		conf.doSetAllowedProtocol(client);
		
		String url = conf.getProtocol() + "://" + conf.getDomain() + "/basic-auth/user/passwd";
		GetMethod gm = new GetMethod(url);
		
		try {
			auth.doAuth(conf, client, gm);
		} catch (AuthenticationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
