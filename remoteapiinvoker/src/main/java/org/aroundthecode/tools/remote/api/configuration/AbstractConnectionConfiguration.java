package org.aroundthecode.tools.remote.api.configuration;

import java.util.Locale;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * <p>Bean to map server configuration for HTTP call and authentications</p>
 * 
 * <p>It stores:
 * <ul>
 * <li><b>Protocols</b> and <b>port</b> by {@link AllowedProtocol} enum</li>
 * <li>Host <b>domain</b> name </li>
 * </ul>
 * </p>
 * @author michele.sacchetti
 *
 */
public abstract class AbstractConnectionConfiguration {
	
	/**
	 * Simple Logger
	 */
	private static final Logger log = LogManager.getLogger(AbstractConnectionConfiguration.class.getName());

	/**
	 * default charset to be used everywhere
	 */
	public static final String CHARSET_NAME = "UTF-8";
	
	/**
	 * commodity string for "application/json";
	 */
	public static final String APPLICATION_JSON = "application/json";
	
	private static ProtocolSocketFactory psf;
	
	static{
		try {
			psf = new EasySSLProtocolSocketFactory();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
	
	/**
	 * Public enum to store available protocols (actually HTTP and HTTPS) and their connections port
	 * @author michele.sacchetti
	 */
	public enum AllowedProtocol {
		HTTP("http","http",80,null),
		APP("app","http",8080,null),
		HTTPS("https","https",443, psf );

		private String name;
		private String protocol;
		private int port;
		private ProtocolSocketFactory socketFactory;

		private AllowedProtocol(String n,String p,int c,ProtocolSocketFactory sf) {
			name = n;
			protocol = p;
			port = c;
			socketFactory = sf;
		}

		/**
		 * Get protocol default port number
		 * @return
		 */
		public int getPort() {
			return port;
		}

		public String getName() {
			return name;
		}
		
		
		private ProtocolSocketFactory getSocketFactory() {
			return socketFactory;
		}

		public String getProtocol() {
			return protocol;
		}

		public static AllowedProtocol parse(String name){
			
			for(final AllowedProtocol ap : AllowedProtocol.values()) {
				if(name.equals(ap.getName())){
	            	return ap;
				}
	        }
			//fallback to default
			return AllowedProtocol.HTTP;
		}
		
	}

	
	private String domain = null;
	private AllowedProtocol protocol = AllowedProtocol.HTTP;
	
	/**
	 * Get domain to be used for all requests
	 * @return
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * Set domain to be used for all requests
	 * @return
	 */
	public final void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * Set {@link AllowedProtocol} to be used for all requests
	 * @return
	 */
	public final void setProtocol(AllowedProtocol protocol) {
		this.protocol = protocol;
	}
	
	/**
	 * Get {@link AllowedProtocol} to be used for all requests
	 * @return
	 */
	public String getProtocol() {
		return protocol.getProtocol().toLowerCase(Locale.getDefault());
	}	
	
	/**
	 * Get port to be used for all requests
	 * @return
	 */
	public int getPort() {
		return protocol.getPort();
	}
	
	/**
	 * Get {@link AllowedProtocol}
	 * @return
	 */
	protected AllowedProtocol getAllowedProtocol() {
		return protocol;
	}
	
	
	/**
	 * This method add configuration to {@link HttpClient} to manage protocols with customized ProtocolSocketFactory
	 * @param client {@link HttpClient} to be configured
	 */
	public void doSetAllowedProtocol(HttpClient client){
		
		if(getAllowedProtocol().getSocketFactory()!=null){
			getLog().info("Setting ProtocolSocketFactory for AllowedProtocol "+getProtocol() );
			Protocol myhttps = new Protocol(getProtocol(), getAllowedProtocol().getSocketFactory() , getPort());
			client.getHostConfiguration().setHost( getDomain(),getPort(), myhttps);
			Protocol.registerProtocol(getProtocol(), myhttps);
		}
		
	}
	
	/**
	 * Retrieve base url as protocol + domain
	 * @return base url
	 */
	public String getBaseUrl(){
		return getProtocol() + "://"+ getDomain();
	}

	/**
	 * Get Logger
	 * @return
	 */
	private Logger getLog() {
		return log;
	}
	
}
