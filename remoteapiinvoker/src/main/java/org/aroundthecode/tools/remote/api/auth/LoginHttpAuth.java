package org.aroundthecode.tools.remote.api.auth;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aroundthecode.tools.remote.api.configuration.AbstractConnectionConfiguration;

/**
 * This class provides Authentication performing a POST http call to an url providing username and password
 * 
 * Default <b>username</b> and <b>password</b> parameters are "user" and "pass", such value can be changed using public setters 
 * 
 * @author michele.sacchetti
 *
 */
public class LoginHttpAuth implements Auth {

	private static final int NOERROR = 299;

	/**
	 * Simple Logger
	 */
	private static final Logger log = LogManager.getLogger(LoginHttpAuth.class);

	/**
	 * Default login parameter name
	 */
	private static final String USER = "user";
	/**
	 * Default password parameter name
	 */
	private static final String PASS = "pass";
	
	private String username;
	private String pass;
	private String loginPage;
	private String userString;
	private String passString;

	/**
	 * Base constructor, default parameter for user name and password are used
	 * @param username user name used in login procedure
	 * @param password password used in login procedure
	 * @param loginPage login page to be used
	 */
	public LoginHttpAuth(String username, String password,String loginPage) {
		this(username,password,loginPage,USER,PASS);
	}
	
	/**
	 * Advanced constructor, login user name and password field must be specified
	 * @param username user name used in login procedure
	 * @param password password used in login procedure
	 * @param loginPage login page to be used
	 * @param userParamName user name field name
	 * @param passParamName password field name
	 */
	public LoginHttpAuth(String username, String password,String loginPage,String userParamName, String passParamName) {
		this.username = username;
		this.pass = password;
		this.loginPage = loginPage;
		this.userString = userParamName;
		this.passString = passParamName;
	}

	/**
	 * {@inheritDoc}
	 */
	public void doAuth(AbstractConnectionConfiguration conf, HttpClient client, HttpMethod m) throws AuthenticationException {
		
		try {
			
			String url = conf.getProtocol() + "://" + conf.getDomain() + getLoginPage();
			
			PostMethod loginCall = new PostMethod(url);
			
			NameValuePair userParam = new NameValuePair(getUserString(),getUsername());
			NameValuePair passParam = new NameValuePair(getPassString(),getPass());
			
			loginCall.addParameters( new NameValuePair[]{userParam,passParam});

			getLog().info("Auth user["+getUsername()+"] over url:["+loginCall.getURI().toString()+"]");
			
			int status = client.executeMethod(loginCall);
			
			if(status != HttpStatus.SC_MOVED_TEMPORARILY && status > NOERROR){
				throw new AuthenticationException("Error ["+status+"] invoking Auth user["+getUsername()+"] over url:["+loginCall.getURI().toString()+"]");
			}
			
			
			
		} catch (IOException e) {
			throw new AuthenticationException(e.getMessage(), e);
		}
		
	}

	/**
	 * Get User name used for login procedure
	 * @return
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Set User name used for login procedure
	 * @return
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Get Password used for login procedure
	 * @return
	 */
	public String getPass() {
		return pass;
	}

	/**
	 * Set Password used for login procedure
	 * @return
	 */
	public void setPass(String pass) {
		this.pass = pass;
	}

	/**
	 * Get page url path used for login procedure
	 * @return
	 */
	public String getLoginPage() {
		return loginPage;
	}

	/**
	 * Set page url path used for login procedure
	 * @return
	 */
	public void setLoginPage(String loginPage) {
		this.loginPage = loginPage;
	}

	/**
	 * Get logger
	 * @return
	 */
	public Logger getLog() {
		return log;
	}

	/**
	 * Get user name parameter used for login procedure
	 * @return
	 */
	public String getUserString() {
		return userString;
	}

	/**
	 * Set user name parameter used for login procedure
	 * @return
	 */
	public void setUserString(String userString) {
		this.userString = userString;
	}

	/**
	 * Get password parameter used for login procedure
	 * @return
	 */
	public String getPassString() {
		return passString;
	}

	/**
	 * Set password parameter used for login procedure
	 * @return
	 */
	public void setPassString(String passString) {
		this.passString = passString;
	}

}
