package org.aroundthecode.tools.remote.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.params.HttpParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aroundthecode.tools.remote.api.auth.Auth;
import org.aroundthecode.tools.remote.api.auth.NoAuth;
import org.aroundthecode.tools.remote.api.configuration.AbstractConnectionConfiguration;
import org.aroundthecode.tools.remote.api.response.ResponseParser;
import org.aroundthecode.tools.remote.api.response.TemporaryFileResponseParser;

/**
 * <p>This class provides all shared functionality to perform POST/GET http calls to the server</p>
 * 
 * <p>Different kind of <i>ResponseParser</i> can be submitted to get proper response handling ad conversion in beans</p>
 * 
 * <p><i>Configuration</i> and <i>Authentications</i> beans are managed externally</p>
 * @see ResponseParser
 * @see AbstractConnectionConfiguration
 * @see Auth 
 * 
 * @author michele.sacchetti
 *
 */
public abstract class AbstractUrlManager {

	private static final int CLIENT_CONNECTION_TIMEOUT = 10 * 1000;
	private static final int CLIENT_SOCKET_TIMEOT = 30 * 1000;

	/**
	 * Available HTTP methods
	 */
	public enum HttpMethods {GET,POST,PUT,DELETE};

	/**
	 * Simple Logger
	 */
	private static Logger log = LogManager.getLogger(AbstractUrlManager.class.getName());


	/**
	 * Client to perform POST/GET calls
	 */
	private static final HttpClient CLIENT = new HttpClient();

	static {
		HttpParams httpParams = CLIENT.getParams();
		httpParams.setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, CLIENT_CONNECTION_TIMEOUT);
		httpParams.setIntParameter(HttpConnectionParams.SO_TIMEOUT, CLIENT_SOCKET_TIMEOT);
	}

	/**
	 * Bean handling connection configurations like domain, protocol and port
	 */
	private AbstractConnectionConfiguration configuration;

	/**
	 * Bean used to perform authentication on server
	 */
	private Auth auth = new NoAuth();

	/**
	 * Default constructor
	 * @param conf a {@link AbstractConnectionConfiguration} bean to manage connection informations
	 */
	public AbstractUrlManager(AbstractConnectionConfiguration conf) {
		configuration = conf;
		getConfiguration().doSetAllowedProtocol(getClient());
	}


	/**
	 * Method to provide the final URL to the client
	 */
	public abstract String getUrlPath(); 

	/**
	 * Build up final url as: [protocol]://[domain][url]
	 * @return
	 */
	public String getFullUrl(String prefix){
		return getConfiguration().getProtocol() + "://" + getConfiguration().getDomain() +prefix +  getUrlPath();
	}


	/**
	 * Invokes <b>GET</b> method over server 
	 * @param restUrl additional path to be added to base one
	 * @param parser {@link ResponseParser} to be used to parse server response
	 * @param params an array of {@link NameValuePair} to be used as additional parameters	 
	 * @param headers an array of {@link NameValuePair} to be used as additional request headers
	 * @return GET return status
	 * @throws IOException any exception which can be thrown by both URL invoker and response parsing
	 */
	public int doGet(String restUrl, ResponseParser parser, NameValuePair[] params, NameValuePair[] headers) throws IOException{
		return invoke(restUrl, parser, params,headers, HttpMethods.GET,null);
	}

	/**
	 * Invokes <b>GET</b> method over server 
	 * @param parser {@link ResponseParser} to be used to parse server response
	 * @param params an array of {@link NameValuePair} to be used as additional parameters	 
	 * @param headers an array of {@link NameValuePair} to be used as additional request headers
	 * @return GET return status
	 * @throws IOException any exception which can be thrown by both URL invoker and response parsing
	 */
	public int doGet( ResponseParser parser, NameValuePair[] params, NameValuePair[] headers) throws IOException{
		return doGet("", parser, params,headers);
	}

	/**
	 * Invokes <b>GET</b> method over server 
	 * @param restUrl additional path to be added to base one
	 * @param parser {@link ResponseParser} to be used to parse server response
	 * @param params an array of {@link NameValuePair} to be used as additional parameters
	 * @return GET return status
	 * @throws IOException any exception which can be thrown by both URL invoker and response parsing
	 */
	public int doGet(String restUrl, ResponseParser parser, NameValuePair[] params) throws IOException{
		return doGet( restUrl, parser, params, new NameValuePair[0]);
	}

	/**
	 * Invokes <b>GET</b> method over server 
	 * @param parser {@link ResponseParser} to be used to parse server response
	 * @param params an array of {@link NameValuePair} to be used as additional parameters
	 * @return GET return status
	 * @throws IOException any exception which can be thrown by both URL invoker and response parsing
	 */
	public int doGet( ResponseParser parser, NameValuePair[] params) throws IOException{
		return doGet( "", parser, params, new NameValuePair[0]);
	}

	/**
	 * Invokes <b>GET</b> method over server with no additional parameters 
	 * @param parser {@link ResponseParser} to be used to parse server response
	 * @return GET return status
	 * @throws IOException any exception which can be thrown by both URL invoker and response parsing
	 */
	public int doGet( ResponseParser parser) throws IOException{
		return doGet( "", parser, new NameValuePair[0], new NameValuePair[0]);
	}

	/**
	 * Invokes <b>POST</b> method over server 
	 * @param restUrl additional path to be added to base one
	 * @param parser {@link ResponseParser} to be used to parse server response
	 * @param params an array of {@link NameValuePair} to be used as additional parameters
	 * @param headers an array of {@link NameValuePair} to be used as additional request headers
	 * @return POST return status
	 * @throws IOException any exception which can be thrown by both URL invoker and response parsing
	 */
	public int doPost(String restUrl, ResponseParser parser, NameValuePair[] params, NameValuePair[] headers, RequestEntity postData) throws IOException{
		return invoke(restUrl, parser, params, headers, HttpMethods.POST,  postData);
	}

	/**
	 * Invokes <b>POST</b> method over server 
	 * @param parser {@link ResponseParser} to be used to parse server response
	 * @param params an array of {@link NameValuePair} to be used as additional parameters
	 * @param headers an array of {@link NameValuePair} to be used as additional request headers
	 * @return POST return status
	 * @throws IOException any exception which can be thrown by both URL invoker and response parsing
	 */
	public int doPost(ResponseParser parser, NameValuePair[] params, NameValuePair[] headers, RequestEntity postData) throws IOException{
		return doPost("", parser,params, new NameValuePair[0],  postData);
	}

	/**
	 * Invokes <b>POST</b> method over server 
	 * @param restUrl additional path to be added to base one
	 * @param parser {@link ResponseParser} to be used to parse server response
	 * @param params an array of {@link NameValuePair} to be used as additional parameters
	 * @return POST return status
	 * @throws IOException any exception which can be thrown by both URL invoker and response parsing
	 */
	public int doPost(String restUrl,ResponseParser parser, NameValuePair[] params, RequestEntity postData) throws IOException{
		return doPost(restUrl, parser,params, new NameValuePair[0],  postData);
	}

	/**
	 * Invokes <b>POST</b> method over server 
	 * @param parser {@link ResponseParser} to be used to parse server response
	 * @param params an array of {@link NameValuePair} to be used as additional parameters
	 * @return POST return status
	 * @throws IOException any exception which can be thrown by both URL invoker and response parsing
	 */
	public int doPost(ResponseParser parser, NameValuePair[] params, RequestEntity postData) throws IOException{
		return doPost("", parser,params, new NameValuePair[0], postData);
	}

	/**
	 * Invokes <b>POST</b> method over server with no additional parameters 
	 * @param parser {@link ResponseParser} to be used to parse server response
	 * @param params an array of {@link NameValuePair} to be used as additional parameters
	 * @return POST return status
	 * @throws IOException any exception which can be thrown by both URL invoker and response parsing
	 */
	public int doPost(ResponseParser parser, RequestEntity postData) throws IOException{
		return doPost("", parser, new NameValuePair[0], new NameValuePair[0],postData);
	}



	/**
	 * Invokes <b>PUT</b> method over server 
	 * @param restUrl additional path to be added to base one
	 * @param parser {@link ResponseParser} to be used to parse server response
	 * @param headers an array of {@link NameValuePair} to be used as additional request headers
	 * @return PUT return status
	 * @throws IOException any exception which can be thrown by both URL invoker and response parsing
	 */
	public int doPut(String restUrl, ResponseParser parser, NameValuePair[] headers, RequestEntity putData) throws IOException{
		return invoke(restUrl, parser, new NameValuePair[0], headers, HttpMethods.PUT,  putData);
	}

	/**
	 * Invokes <b>PUT</b> method over server 
	 * @param parser {@link ResponseParser} to be used to parse server response
	 * @param headers an array of {@link NameValuePair} to be used as additional request headers
	 * @return PUT return status
	 * @throws IOException any exception which can be thrown by both URL invoker and response parsing
	 */
	public int doPut(ResponseParser parser, NameValuePair[] headers, RequestEntity putData) throws IOException{
		return doPut("", parser, new NameValuePair[0],  putData);
	}

	/**
	 * Invokes <b>PUT</b> method over server 
	 * @param restUrl additional path to be added to base one
	 * @param parser {@link ResponseParser} to be used to parse server response
	 * @param params an array of {@link NameValuePair} to be used as additional parameters
	 * @return PUT return status
	 * @throws IOException any exception which can be thrown by both URL invoker and response parsing
	 */
	public int doPut(String restUrl,ResponseParser parser, RequestEntity putData) throws IOException{
		return doPut(restUrl, parser, new NameValuePair[0],  putData);
	}

	/**
	 * Invokes <b>PUT</b> method over server with no additional parameters 
	 * @param parser {@link ResponseParser} to be used to parse server response
	 * @param params an array of {@link NameValuePair} to be used as additional parameters
	 * @return PUT return status
	 * @throws IOException any exception which can be thrown by both URL invoker and response parsing
	 */
	public int doPut(ResponseParser parser, RequestEntity putData) throws IOException{
		return doPut("", parser, new NameValuePair[0],putData);
	}

	/**
	 * Invokes <b>PUT</b> method over server with no additional parameters 
	 * @param parser {@link ResponseParser} to be used to parse server response
	 * @param params an array of {@link NameValuePair} to be used as additional parameters
	 * @return PUT return status
	 * @throws IOException any exception which can be thrown by both URL invoker and response parsing
	 */
	public int doDelete(String restUrl,ResponseParser parser) throws IOException{
		return invoke(restUrl, parser, new NameValuePair[0], new NameValuePair[0], HttpMethods.DELETE, null);
	}

	/**
	 * Perform file download and uses provided {@link TemporaryFileResponseParser} to provide access to file
	 * @param url to download
	 * @param parser {@link TemporaryFileResponseParser} to provide access to file
	 * @param params array of {@link NameValuePair} to be used as additional parameters
	 * @param headers array of {@link NameValuePair} to be used as additional headers
	 * @return GET status
	 * @throws IOException
	 */
	public int downloadFile(String url,TemporaryFileResponseParser parser,NameValuePair[] params, NameValuePair[] headers) throws IOException
	{
		String filename = url.substring(url.lastIndexOf('/')+1);
		parser.setFilename(filename);
		return invoke(url,parser, params, headers, HttpMethods.GET, null);
	}
	
	/**
	 * Perform file upload
	 * @param url Upload url
	 * @param parser{@link ResponseParser} to be used to parse server response
	 * @param params an array of {@link NameValuePair} to be used as additional parameters
	 * @param headers array of {@link NameValuePair} to be used as additional headers
	 * @param file file to upload
	 * @return return status
	 * @throws IOException
	 */
	public int uploadFile(String url,ResponseParser parser,NameValuePair[] params, NameValuePair[] headers,File file) throws IOException{
		
		Part[] parts = { new FilePart("file", file) };
		HttpMethodParams hmparams = new HttpMethodParams();
		
		for(NameValuePair nvp : params){
			hmparams.setParameter(nvp.getName(), nvp.getValue() );
		}
		
		RequestEntity data = new MultipartRequestEntity(parts, hmparams);
     
		return invoke(url, parser, params, headers, HttpMethods.POST, data);
		
	}
	
	
	
	/**
	 * <p>Invokes <b>GET/POST/PUT</b> method over server.</p>
	 * <p>It uses {@link Auth} bean to apply authentication over the invocation.</p>
	 * <p>It uses {@link ResponseParser} to parse server response.</p>
	 * <br/>
	 * @param restUrl additional path to be added to base one
	 * @param parser {@link ResponseParser} to be used to parse server response
	 * @param params an array of {@link NameValuePair} to be used as additional parameters
	 * @param isPost trigger POST or GET methods
	 * @return GET return status
	 * @throws IOException any exception which can be thrown by both URL invoker and response parsing
	 */	
	protected int invoke(String restUrl, ResponseParser parser, NameValuePair[] params, NameValuePair[] headers,HttpMethods hm, RequestEntity bodyData) throws IOException{

		String url = getFullUrl(restUrl) ;
		InputStream in = null;
		int status = 0;
		HttpMethod m = null;
		try {
			switch (hm) {
			case GET:
				m = new GetMethod(url);
				((GetMethod)m).setQueryString(params);
				getLog().info("QueryString:["+m.getQueryString()+"]");	
				break;
			case POST:
				m = new PostMethod(url);
				((PostMethod)m).addParameters(params);
				for (NameValuePair nvp : params) {
					getLog().info("POST params:["+nvp.getName()+"="+nvp.getValue()+"]");
				}
				if(bodyData!=null){
					((PostMethod)m).setRequestEntity(bodyData);
				}
				break;
			case PUT:
				m = new PutMethod(url);
				if(bodyData!=null){
					((PutMethod)m).setRequestEntity(bodyData);
				}
				break;
			case DELETE:
				m = new DeleteMethod(url);
				break;
			default:
				//this should never happen
				throw new IOException("Not implemented Http Method:"+hm.toString());
			}


			for (NameValuePair nvp : headers) {
				getLog().info("Header data:["+nvp.getName()+"="+nvp.getValue()+"]");
				m.setRequestHeader(nvp.getName(), nvp.getValue());
			}

			getLog().info("url:["+m.getURI().toString()+"]");

			status = executeHttpMethod(m);
			in = m.getResponseBodyAsStream();
			parser.parse(in);
		}
		finally{
			if(m!=null){
				m.releaseConnection();
			}
			try {
				if(in != null) {
					in.close();
				}
			} catch (IOException e) {
				getLog().info("Error closing stream",e);
			}
		}	
		return status;
	}

	/**
	 * @param m
	 * @return
	 * @throws AuthenticationException
	 * @throws IOException
	 * @throws HttpException
	 */
	protected int executeHttpMethod(HttpMethod m)
			throws IOException {
		int status;
		getAuth().doAuth(getConfiguration(), getClient(), m);
		status = getClient().executeMethod(m);

		checkStatus(status,getLog());

		getLog().info("return status:["+status+"]");
		return status;
	}
	
	/**
	 * Checks HTTP status response and throws {@link HttpException} if needed 
	 * @param status HTTP status response
	 * @throws HttpException thrown on unsuccessful states
	 */
	public static void checkStatus(int status, Logger l) throws HttpException {
		switch (status) {
		case HttpStatus.SC_NOT_FOUND:
			l.error("Page not found:"+status);
			throw new HttpException("Page not found");
		case HttpStatus.SC_UNAUTHORIZED:
			l.error("Unauthorized:"+status);
			throw new HttpException("Unauthorized");
		case HttpStatus.SC_BAD_REQUEST:
			l.error("Bad Request:"+status);
			throw new HttpException("Bad Request");
		case HttpStatus.SC_OK:
		case HttpStatus.SC_MOVED_TEMPORARILY:
		case HttpStatus.SC_NO_CONTENT:
			l.debug("OK Return Status:"+status);
			break;
		case HttpStatus.SC_CREATED:
			l.debug("Created return status:"+status);
			break;
		default:
			l.warn("Unhandled status:"+status);
		}

	}


	/**
	 * Set Authentication bean
	 * @return
	 */
	public Auth getAuth() {
		return auth;
	}

	/**
	 * Set Authentication bean
	 * @param auth
	 */
	public void setAuth(Auth auth) {
		this.auth = auth;
	}

	/**
	 * Get configuration bean
	 * @return
	 */
	public AbstractConnectionConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * Set configuration bean
	 * @return
	 */
	public void setConfiguration(AbstractConnectionConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Get Logger
	 * @return
	 */
	public Logger getLog() {
		return log;
	}


	/**
	 * Get {@link HttpClient} used for requests
	 * @return
	 */
	private static HttpClient getClient() {
		return CLIENT;
	}


}
