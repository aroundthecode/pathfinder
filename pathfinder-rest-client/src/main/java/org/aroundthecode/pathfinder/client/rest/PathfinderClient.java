package org.aroundthecode.pathfinder.client.rest;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aroundthecode.pathfinder.client.rest.items.FilterItem;
import org.aroundthecode.pathfinder.client.rest.manager.PathfinderUrlManager;
import org.aroundthecode.pathfinder.client.rest.manager.configuration.PathfinderConnectionConfiguration;
import org.aroundthecode.pathfinder.client.rest.utils.ArtifactUtils;
import org.aroundthecode.tools.remote.api.auth.Auth;
import org.aroundthecode.tools.remote.api.auth.NoAuth;
import org.aroundthecode.tools.remote.api.configuration.AbstractConnectionConfiguration;
import org.aroundthecode.tools.remote.api.configuration.AbstractConnectionConfiguration.AllowedProtocol;
import org.aroundthecode.tools.remote.api.response.EmptyResponseParser;
import org.aroundthecode.tools.remote.api.response.JsonArrayResponseParser;
import org.aroundthecode.tools.remote.api.response.JsonObjectResponseParser;
import org.aroundthecode.tools.remote.api.response.StringResponseParser;
import org.aroundthecode.tools.remote.api.response.TemporaryFileResponseParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * PathfinderClient wraps up Pathfinder server REST API calls in a client usable by java classes like Pathfinder maven plugin
 * @author msacchetti
 *
 */
@SuppressWarnings("unchecked")
public class PathfinderClient {

	private static final String CHARSET = "UTF-8";
	private static final int RETRY_TIMES = 3;
	private static final int RETRY_SLEEP = 10000;
	
	private static final int NOERROR = 299;
	private static final Logger log = LogManager.getLogger(PathfinderClient.class.getName());

	private NameValuePair[] headers = new NameValuePair[2];
	private PathfinderUrlManager um = null;

	/**
	 * Base constructor, provide connection parameters. 
	 * Upon creation the client will try to connect to check server availability.
	 * Upon connection failure it will attempt RETRY_TIMES times, with RETRY_SLEEP sleeping time.
	 * @param protocol http or https values allowed
	 * @param domain FQDN or IP to connect to
	 * @param port socket port Pathfinder server listen to
	 * @param path additional context path ("/" if not present)
	 * @throws IOException
	 */
	public PathfinderClient(String protocol,String domain, int port, String path) throws IOException {

		PathfinderConnectionConfiguration conf= new PathfinderConnectionConfiguration(
				AllowedProtocol.parse("app"),
				domain
				);
		Auth auth = new NoAuth();
		um = new PathfinderUrlManager(conf);

		um.setAuth( auth );
		headers[0] = new NameValuePair("Content-Type", AbstractConnectionConfiguration.APPLICATION_JSON);
		headers[1] = new NameValuePair("Accept", AbstractConnectionConfiguration.APPLICATION_JSON);		



		try(Socket socket = new Socket()){
			for (int i = 1; i <= RETRY_TIMES; i++) {
				try {
					socket.connect(new InetSocketAddress(domain, port), 10);
					log.warn("Connected");
					break;
				} catch (IOException ex) {
					if(i<3){
						log.error("Connection failed [{}/{}], will sleep {}ms and retry...",i,RETRY_TIMES,RETRY_SLEEP);
						try {
							Thread.sleep(RETRY_SLEEP);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}
					else{
						log.error("Connection failed [{}/{}], giving up sorry.",i,RETRY_TIMES);
						throw new IOException(ex);
					}
				} 
			}
		}


	}



	/**
	 * Invoke node/save method to Pathfinder server to save Artifact data
	 * @param groupId artifact GroupId
	 * @param artifactId artifact ArtifactId
	 * @param packaging artifact Packaging type
	 * @param classifier artifact classifier
	 * @param version artifact version
	 * @return JSON String representation of saved artifact
	 * @throws IOException
	 */
	public JSONObject saveArtifact(String groupId,String artifactId,String packaging,String classifier,String version) throws IOException {

		JSONObject resp = null;
		JsonObjectResponseParser jparser = new JsonObjectResponseParser();
		try {

			String json = createJson(groupId, artifactId, packaging, classifier, version).toString();
			um.getLog().debug("saveArtifact: [{}]",json);
			RequestEntity postData = getStringRequestEntity(json);

			int ret = um.doPost(PathfinderConnectionConfiguration.URL_NODE_SAVE, jparser,new NameValuePair[0],headers,  postData);
			if(ret!=HttpStatus.SC_OK){
				um.getLog().error("saveArtifact - Request failed, return status [{}]", ret);
			}
			else{
				resp = jparser.getResponse();
				um.getLog().debug("saveArtifact - response [{}]",resp);

			}
		} catch (IOException e) {
			um.getLog().error("saveArtifact", e);
		}

		return resp;

	}





	/**
	 * Invoke node/save method to Pathfinder server to save Artifact data
	 * @param uniqueId artifact unique ID groupId:artifacId:packaging:classifier:version  
	 * @return JSON String representation of saved artifact
	 * @throws IOException
	 */
	public JSONObject saveArtifact(String uniqueId) throws IOException {

		Map<String, String> map = ArtifactUtils.splitUniqueId(uniqueId);
		return saveArtifact(
				map.get(ArtifactUtils.G),
				map.get(ArtifactUtils.A),
				map.get(ArtifactUtils.P),
				map.get(ArtifactUtils.C),
				map.get(ArtifactUtils.V)
				);
	}

	/**
	 * Invoke node/depends method to Pathfinder server to save dependency relation between two Artifacts
	 * @param uniqueIdFrom artifact unique ID groupId:artifacId:packaging:classifier:version for source node
	 * @param uniqueIdTo artifact unique ID groupId:artifacId:packaging:classifier:version for destination node
	 * @param scope dependency scope 
	 * @return JSON String representation of source artifact with new dependency
	 * @throws IOException
	 */
	public JSONObject createDependency(String uniqueIdFrom,String uniqueIdTo,String scope) throws IOException {

		JSONObject resp = null;
		JsonObjectResponseParser jparser = new JsonObjectResponseParser();
		JSONObject body = new JSONObject();
		body.put("from", uniqueIdFrom);
		body.put("to", uniqueIdTo);
		body.put("scope", scope);

		um.getLog().debug("createDependency: [{}]",body.toString());
		RequestEntity postData = getStringRequestEntity(body.toString());

		int ret = um.doPost(PathfinderConnectionConfiguration.URL_NODE_DEPENDS, jparser,new NameValuePair[0],headers,  postData);
		if(ret!=HttpStatus.SC_OK){
			um.getLog().error("createDependency - Request failed, return status [{}]", ret);
		}
		else{
			resp = jparser.getResponse();
			um.getLog().debug("createDependency - response [{}]", resp);

		}

		return resp;

	}

	/**
	 * Invoke node/parent method to Pathfinder server to save parent relation between two Artifacts
	 * @param mainUniqueId artifact unique ID groupId:artifacId:packaging:classifier:version for main node
	 * @param parentUniqueId artifact unique ID groupId:artifacId:packaging:classifier:version for parent node
	 * @return JSON String representation of main artifact with new dependency
	 * @throws IOException
	 */
	public JSONObject addParent(String mainUniqueId,String parentUniqueId) throws IOException {

		JSONObject resp = null;
		JsonObjectResponseParser jparser = new JsonObjectResponseParser();
		JSONObject body = new JSONObject();
		body.put("main", mainUniqueId);
		body.put("parent", parentUniqueId);

		um.getLog().debug("addParent: [{}]",body.toString());
		RequestEntity postData = getStringRequestEntity(body.toString());

		int ret = um.doPost(PathfinderConnectionConfiguration.URL_NODE_PARENT, jparser,new NameValuePair[0],headers,  postData);
		if(ret!=HttpStatus.SC_OK){
			um.getLog().error("addParent - Request failed, return status [{}]", ret);
		}
		else{
			resp = jparser.getResponse();
			um.getLog().debug("addParent - response [{}]", resp);

		}

		return resp;
	}

	/**
	 * Invoke /cypher/query method to Pathfinder server to perform a standard Neo4J cypher query
	 * @param cypherQuery cypher query to run
	 * @return String list, each line representing  nodeId-relationType-nodeId
	 * @throws IOException
	 */
	public String query(String cypherQuery) throws IOException {

		String resp = null;
		StringResponseParser sparser = new StringResponseParser();
		JSONObject body = new JSONObject();
		body.put("q", cypherQuery);

		um.getLog().debug("query: [{}]",body.toString());
		RequestEntity postData = getStringRequestEntity(body.toString());

		int ret = um.doPost(PathfinderConnectionConfiguration.URL_CYPHER_QUERY, sparser,new NameValuePair[0],headers,  postData);
		if(ret!=HttpStatus.SC_OK){
			um.getLog().error("query - Request failed, return status [{}]", ret);
		}
		else{
			resp = sparser.getResponse();
			um.getLog().debug("query - response [{}]", resp);

		}

		return resp;
	}

	/**
	 * Invoke /query/filterall method to Pathfinder server to retrieve the full list of nodes filtered via <b>FilterItem</b> rules
	 * @param f FilterItem containing filtering rules
	 * @return JSON Array of JSON object providing Artifact and relations data
	 * @throws IOException
	 */
	public JSONArray filterAll(FilterItem f) throws IOException {

		NameValuePair[] filters = new NameValuePair[10];
		int i = 0;

		filters[i++] = new NameValuePair("gn1", URLEncoder.encode(f.getFilterGN1(),CHARSET));
		filters[i++] = new NameValuePair("an1", URLEncoder.encode(f.getFilterAN1(),CHARSET));
		filters[i++] = new NameValuePair("pn1", URLEncoder.encode(f.getFilterPN1(),CHARSET));
		filters[i++] = new NameValuePair("cn1", URLEncoder.encode(f.getFilterCN1(),CHARSET));
		filters[i++] = new NameValuePair("vn1", URLEncoder.encode(f.getFilterVN1(),CHARSET));

		filters[i++] = new NameValuePair("gn2", URLEncoder.encode(f.getFilterGN2(),CHARSET));
		filters[i++] = new NameValuePair("an2", URLEncoder.encode(f.getFilterAN2(),CHARSET));
		filters[i++] = new NameValuePair("pn2", URLEncoder.encode(f.getFilterPN2(),CHARSET));
		filters[i++] = new NameValuePair("cn2", URLEncoder.encode(f.getFilterCN2(),CHARSET));
		filters[i] = new NameValuePair("vn2", URLEncoder.encode(f.getFilterVN2(),CHARSET));

		JSONArray resp = null;
		JsonArrayResponseParser aparser = new JsonArrayResponseParser();
		int ret = um.doGet(PathfinderConnectionConfiguration.URL_QUERY_FILTERALL, aparser,filters,headers);
		if(ret!=HttpStatus.SC_OK){
			um.getLog().error("filterAll - Request failed, return status [{}]", ret);
		}
		else{
			resp = aparser.getResponse();
			um.getLog().debug("filterAll - response [{}]", resp);
		}

		return resp;
	}

	/**
	 * Invoke /query/impact method to Pathfinder server to retrieve the full list of nodes impacting provided Artifact filtered via <b>FilterItem</b> rules
	 * @param depth Impact search depth, this represents the number of relations hops the search will traverse before stopping
	 * @param groupId artifact GroupId
	 * @param artifactId artifact ArtifactId
	 * @param packaging artifact Packaging type
	 * @param classifier artifact classifier
	 * @param version artifact version
	 * @param f FilterItem containing filtering rules
	 * @return JSON Array of JSON object providing Artifact and relations data
	 * @throws IOException
	 */
	public JSONArray impact(int depth,String groupId,String artifactId,String packaging,String classifier,String version,FilterItem f) throws IOException {

		NameValuePair[] filters = new NameValuePair[16];
		int i = 0;

		filters[i++] = new NameValuePair("d", Integer.toString(depth) );
		filters[i++] = new NameValuePair("g", URLEncoder.encode(groupId,CHARSET));
		filters[i++] = new NameValuePair("a", URLEncoder.encode(artifactId,CHARSET));
		filters[i++] = new NameValuePair("p", URLEncoder.encode(packaging,CHARSET));
		filters[i++] = new NameValuePair("c", URLEncoder.encode(classifier,CHARSET));
		filters[i++] = new NameValuePair("v", URLEncoder.encode(version,CHARSET));

		filters[i++] = new NameValuePair("gn1", URLEncoder.encode(f.getFilterGN1(),CHARSET));
		filters[i++] = new NameValuePair("an1", URLEncoder.encode(f.getFilterAN1(),CHARSET));
		filters[i++] = new NameValuePair("pn1", URLEncoder.encode(f.getFilterPN1(),CHARSET));
		filters[i++] = new NameValuePair("cn1", URLEncoder.encode(f.getFilterCN1(),CHARSET));
		filters[i++] = new NameValuePair("vn1", URLEncoder.encode(f.getFilterVN1(),CHARSET));

		filters[i++] = new NameValuePair("gn2", URLEncoder.encode(f.getFilterGN2(),CHARSET));
		filters[i++] = new NameValuePair("an2", URLEncoder.encode(f.getFilterAN2(),CHARSET));
		filters[i++] = new NameValuePair("pn2", URLEncoder.encode(f.getFilterPN2(),CHARSET));
		filters[i++] = new NameValuePair("cn2", URLEncoder.encode(f.getFilterCN2(),CHARSET));
		filters[i] = new NameValuePair("vn2", URLEncoder.encode(f.getFilterVN2(),CHARSET));

		JSONArray resp = null;
		JsonArrayResponseParser aparser = new JsonArrayResponseParser();
		int ret = um.doGet(PathfinderConnectionConfiguration.URL_QUERY_IMPACT, aparser,filters,headers);
		if(ret!=HttpStatus.SC_OK){
			um.getLog().error("impact - Request failed, return status [{}]", ret);
		}
		else{
			resp = aparser.getResponse();
			um.getLog().debug("impact - response [{}]", resp);
		}

		return resp;
	}

	/**
	 * Invoke /node/get method to Pathfinder server to retrieve a node given its unique ID
	 * @param uniqueId artifact unique ID groupId:artifacId:packaging:classifier:version  
	 * @return JSON representation of given Artifact
	 * @throws IOException
	 * @throws ParseException
	 */
	public JSONObject getArtifact(String uniqueId) throws IOException {

		JSONObject resp = null;
		JsonObjectResponseParser jparser = new JsonObjectResponseParser();
		NameValuePair[] params = new NameValuePair[1];
		params[0] = new NameValuePair("id", uniqueId);
		
		int ret = um.doGet(PathfinderConnectionConfiguration.URL_NODE_GET, jparser,params,headers);
		if(ret!=HttpStatus.SC_OK){
			um.getLog().error("getArtifact - Request failed, return status [{}]", ret);
		}
		else{
			resp = jparser.getResponse();
			um.getLog().debug("getArtifact - response [{}]", resp);
		}

		return resp;
	}

	/**
	 * Invoke /node/download to Pathfinder server to download the full project file
	 * @return File pointing to temporary download resource
	 * @throws IOException
	 */
	public File downloadProject() throws IOException{
		
		File out = null;
		TemporaryFileResponseParser fparser = new TemporaryFileResponseParser("/tmp");
		try {
			int ret = um.downloadFile(PathfinderConnectionConfiguration.URL_NODE_DOWNLOAD, fparser, new NameValuePair[0], new NameValuePair[0]);
			if(ret>NOERROR){
				um.getLog().error("downloadProject - Request failed, return status" + ret);
			}
			else{
				out = fparser.getResponse();
				um.getLog().debug("downloadProject - response:"+out.getAbsolutePath());
			}
		} catch (IOException e) {
			um.getLog().error("downloadProject - request failed", e);
		}
		return out;

	}
	
	/**
	 * Invoke /node/upload to Pathfinder server to import a full project file
	 * @param json a JSONArray with full data to be imported
	 * @return a JSONObject containing summary of the operation
	 */
	public JSONObject uploadProject(JSONArray json) {

		JSONObject resp = null;
		JsonObjectResponseParser jparser = new JsonObjectResponseParser();
		try {

			um.getLog().debug("uploadProject: [{}]",json);
			RequestEntity postData = getStringRequestEntity(json.toString());

			int ret = um.doPost(PathfinderConnectionConfiguration.URL_NODE_UPLOAD, jparser,new NameValuePair[0],headers,  postData);
			if(ret!=HttpStatus.SC_OK){
				um.getLog().error("uploadProject - Request failed, return status [{}]", ret);
			}
			else{
				resp = jparser.getResponse();
				um.getLog().debug("uploadProject - response [{}]",resp);

			}
		} catch (IOException e) {
			um.getLog().error("uploadProject", e);
		}

		return resp;

	}
	
	/**
	 * Invoke /node/truncate to Pathfinder server to delete all nodes
	 */
	public void truncateProject() {

		EmptyResponseParser eparser = new EmptyResponseParser();
		try {

			int ret = um.doPost(PathfinderConnectionConfiguration.URL_NODE_TRUNCATE, eparser,new NameValuePair[0],headers,  null);
			if(ret!=HttpStatus.SC_OK){
				um.getLog().error("truncateProject - Request failed, return status [{}]", ret);
			}
		} catch (IOException e) {
			um.getLog().error("uploadProject", e);
		}

	}

	/**
	 * Utility method which creates a JSONObject containing minimal Artifact data to be passed via REST API invocation
	 * @param groupId artifact GroupId
	 * @param artifactId artifact ArtifactId
	 * @param packaging artifact Packaging type
	 * @param classifier artifact classifier
	 * @param version artifact version
	 * @return
	 */
	private static JSONObject createJson(String groupId, String artifactId,
			String packaging, String classifier, String version) {
		JSONObject body = new JSONObject();
		body.put("groupId", groupId);
		body.put("artifactId", artifactId);
		body.put("packaging", packaging);
		body.put("classifier", classifier);
		body.put("version", version);
		return body;
	}

	/**
	 * Convert a string to a RequestEntity ready to be submited as a POST
	 * @param data the string to be converted
	 * @return RequestEntity
	 * @throws UnsupportedEncodingException
	 */
	private static RequestEntity getStringRequestEntity(String data)
			throws UnsupportedEncodingException {
		return new StringRequestEntity(data ,
				AbstractConnectionConfiguration.APPLICATION_JSON,
				AbstractConnectionConfiguration.CHARSET_NAME);
	}

}
