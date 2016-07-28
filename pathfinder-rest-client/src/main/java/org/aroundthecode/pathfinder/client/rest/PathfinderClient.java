package org.aroundthecode.pathfinder.client.rest;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aroundthecode.pathfinder.client.rest.items.FilterItem;
import org.aroundthecode.pathfinder.client.rest.utils.ArtifactUtils;
import org.aroundthecode.pathfinder.client.rest.utils.RestUtils;
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
	private String baseurl = "http://localhost:8080";
	private static final Logger log = LogManager.getLogger(PathfinderClient.class.getName());


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

		this.setBaseurl(protocol+"://"+domain+":"+port+path);
		log.warn("Testing connection [{}]...",getBaseurl());
		
		
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

	public final String getBaseurl() {
		return baseurl;
	}

	public final void setBaseurl(String baseurl) {
		this.baseurl = baseurl;
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
	public String saveArtifact(String groupId,String artifactId,String packaging,String classifier,String version) throws IOException {

		JSONObject body = PathfinderClient.createJson(groupId, artifactId, packaging,classifier, version);
		return RestUtils.sendPost(getBaseurl() + "node/save", body);
	}

	/**
	 * Invoke node/save method to Pathfinder server to save Artifact data
	 * @param uniqueId artifact unique ID groupId:artifacId:packaging:classifier:version  
	 * @return JSON String representation of saved artifact
	 * @throws IOException
	 */
	public String saveArtifact(String uniqueId) throws IOException {

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
	public String createDependency(String uniqueIdFrom,String uniqueIdTo,String scope) throws IOException {

		JSONObject body = new JSONObject();
		body.put("from", uniqueIdFrom);
		body.put("to", uniqueIdTo);
		body.put("scope", scope);

		return RestUtils.sendPost(getBaseurl() + "node/depends", body);
	}

	/**
	 * Invoke node/parent method to Pathfinder server to save parent relation between two Artifacts
	 * @param mainUniqueId artifact unique ID groupId:artifacId:packaging:classifier:version for main node
	 * @param parentUniqueId artifact unique ID groupId:artifacId:packaging:classifier:version for parent node
	 * @return JSON String representation of main artifact with new dependency
	 * @throws IOException
	 */
	public String addParent(String mainUniqueId,String parentUniqueId) throws IOException {

		JSONObject body = new JSONObject();
		body.put("main", mainUniqueId);
		body.put("parent", parentUniqueId);

		return RestUtils.sendPost(getBaseurl() + "node/parent", body);
	}

	/**
	 * Invoke /cypher/query method to Pathfinder server to perform a standard Neo4J cypher query
	 * @param cypherQuery cypher query to run
	 * @return String list, each line representing  nodeId-relationType-nodeId
	 * @throws IOException
	 */
	public String query(String cypherQuery) throws IOException {

		JSONObject body = new JSONObject();
		body.put("q", cypherQuery);

		return RestUtils.sendPost(getBaseurl() + "/cypher/query", body);
	}

	/**
	 * Invoke /query/filterall method to Pathfinder server to retrieve the full list of nodes filtered via <b>FilterItem</b> rules
	 * @param f FilterItem containing filtering rules
	 * @return JSON Array of JSON object providing Artifact and relations data
	 * @throws IOException
	 */
	public String filterAll(FilterItem f) throws IOException {

		StringBuilder sb = new StringBuilder("/query/filterall?");

		sb.append("gn1=").append(URLEncoder.encode(f.getFilterGN1(),CHARSET)).append("&");
		sb.append("an1=").append(URLEncoder.encode(f.getFilterAN1(),CHARSET)).append("&");
		sb.append("pn1=").append(URLEncoder.encode(f.getFilterPN1(),CHARSET)).append("&");
		sb.append("cn1=").append(URLEncoder.encode(f.getFilterCN1(),CHARSET)).append("&");
		sb.append("vn1=").append(URLEncoder.encode(f.getFilterVN1(),CHARSET)).append("&");

		sb.append("gn2=").append(URLEncoder.encode(f.getFilterGN2(),CHARSET)).append("&");
		sb.append("an2=").append(URLEncoder.encode(f.getFilterAN2(),CHARSET)).append("&");
		sb.append("pn2=").append(URLEncoder.encode(f.getFilterPN2(),CHARSET)).append("&");
		sb.append("cn2=").append(URLEncoder.encode(f.getFilterCN2(),CHARSET)).append("&");
		sb.append("vn2=").append(URLEncoder.encode(f.getFilterVN2(),CHARSET));

		return RestUtils.sendGet(getBaseurl() + sb.toString() );
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
	public String impact(int depth,String groupId,String artifactId,String packaging,String classifier,String version,FilterItem f) throws IOException {

		StringBuilder sb = new StringBuilder("/query/impact?");

		sb.append("d=").append(depth).append("&");
		sb.append("g=").append(URLEncoder.encode(groupId,CHARSET)).append("&");
		sb.append("a=").append(URLEncoder.encode(artifactId,CHARSET)).append("&");
		sb.append("p=").append(URLEncoder.encode(packaging,CHARSET)).append("&");
		sb.append("c=").append(URLEncoder.encode(classifier,CHARSET)).append("&");
		sb.append("v=").append(URLEncoder.encode(version,CHARSET)).append("&");

		sb.append("gn1=").append(URLEncoder.encode(f.getFilterGN1(),CHARSET)).append("&");
		sb.append("an1=").append(URLEncoder.encode(f.getFilterAN1(),CHARSET)).append("&");
		sb.append("pn1=").append(URLEncoder.encode(f.getFilterPN1(),CHARSET)).append("&");
		sb.append("cn1=").append(URLEncoder.encode(f.getFilterCN1(),CHARSET)).append("&");
		sb.append("vn1=").append(URLEncoder.encode(f.getFilterVN1(),CHARSET)).append("&");

		sb.append("gn2=").append(URLEncoder.encode(f.getFilterGN2(),CHARSET)).append("&");
		sb.append("an2=").append(URLEncoder.encode(f.getFilterAN2(),CHARSET)).append("&");
		sb.append("pn2=").append(URLEncoder.encode(f.getFilterPN2(),CHARSET)).append("&");
		sb.append("cn2=").append(URLEncoder.encode(f.getFilterCN2(),CHARSET)).append("&");
		sb.append("vn2=").append(URLEncoder.encode(f.getFilterVN2(),CHARSET));

		return RestUtils.sendGet(getBaseurl() + sb.toString() );
	}

	/**
	 * Invoke /node/get method to Pathfinder server to retrieve a node given its unique ID
	 * @param uniqueId artifact unique ID groupId:artifacId:packaging:classifier:version  
	 * @return JSON representation of given Artifact
	 * @throws IOException
	 * @throws ParseException
	 */
	public JSONObject getArtifact(String uniqueId) throws IOException, ParseException {

		String response = RestUtils.sendGet(getBaseurl() + "node/get?id="+ uniqueId);
		return RestUtils.string2Json(response);
	}
	
	/**
	 * Invoke /node/download to Pathfinder server to download the full project file
	 * @return File pointing to temporary download resource
	 * @throws IOException
	 */
	public File downloadProject() throws IOException{
		return RestUtils.downloadFile(getBaseurl() + "/node/download");
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

}
