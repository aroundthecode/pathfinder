package org.aroundthecode.pathfinder.client.rest.manager.configuration;

import org.aroundthecode.tools.remote.api.configuration.BasicConnectionConfiguration;

/**
 * Configuration for PathFinderUrlManager
 * @author msacchetti
 *
 */
public class PathfinderConnectionConfiguration extends BasicConnectionConfiguration {

	
	/**
	 * Context base URL to all other URLs
	 */
	public static final String BASE_URL =  "/";
	
	public static final String URL_CYPHER_QUERY 	= BASE_URL + "cypher/query";
	
	public static final String URL_QUERY_FILTERALL 	= BASE_URL + "query/filterall";
	public static final String URL_QUERY_IMPACT 	= BASE_URL + "query/impact";
	
	public static final String URL_NODE_GET 		= BASE_URL + "node/get";
	public static final String URL_NODE_PARENT 		= BASE_URL + "node/parent";
	public static final String URL_NODE_DEPENDS 	= BASE_URL + "node/depends";
	public static final String URL_NODE_SAVE 		= BASE_URL + "node/save";
	public static final String URL_NODE_DOWNLOAD 	= BASE_URL + "node/download";
	public static final String URL_NODE_UPLOAD 		= BASE_URL + "node/upload";
	public static final String URL_NODE_TRUNCATE 	= BASE_URL + "node/truncate";
	
	public static final String URL_CRAWLER_CRAWL 	= BASE_URL + "crawler/crawl";
	
	public PathfinderConnectionConfiguration(AllowedProtocol protocol,String domain) {
		super(protocol, domain);
		
	}
	
	@Override
	public String getDomain(){
		return super.getDomain() + ":" + getPort() ;
	}
}
