package org.aroundthecode.tools.remote.api.configuration;

/**
 * Basic Configuration bean
 * @author michele.sacchetti
 *
 */
public class BasicConnectionConfiguration extends AbstractConnectionConfiguration {

	/**
	 * Base constructor
	 * @param protocol
	 * @param domain
	 */
	public BasicConnectionConfiguration(AllowedProtocol protocol, String domain){
		setProtocol(protocol);
		setDomain(domain);
	}
	
}
