package org.aroundthecode.pathfinder.client.rest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.Map;

import org.aroundthecode.pathfinder.client.rest.items.FilterItem;
import org.aroundthecode.pathfinder.client.rest.utils.ArtifactUtils;
import org.aroundthecode.pathfinder.client.rest.utils.RestUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

@SuppressWarnings("unchecked")
public class PathfinderClient {

	private static final int SLEEP = 10000;
	private String baseurl = "http://localhost:8080";

	public PathfinderClient(String protocol,String domain, int port, String path) throws IOException, InterruptedException {

		this.setBaseurl(protocol+"://"+domain+":"+port+path);

		System.err.print("testing connection ["+getBaseurl()+"]...");
		Socket socket = null;
		for (int i = 1; i <= 3; i++) {
			try {
				socket = new Socket();
				socket.connect(new InetSocketAddress(domain, port), 10);
				System.err.println("OK");
				break;
			} catch (IOException ex) {
				if(i<3){
					System.err.println("FAIL ["+i+"/3] Sleep 10 sec and retry");
					Thread.sleep(SLEEP);
				}
				else{
					System.err.println("FAIL ["+i+"/3] give up");
					throw new IOException(ex);
				}
			} 
			finally{
				if(socket!=null){
					socket.close();
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

	public String saveArtifact(String groupId,String artifactId,String packaging,String classifier,String version) throws IOException {

		JSONObject body = PathfinderClient.createJson(groupId, artifactId, packaging,classifier, version);
		return RestUtils.sendPost(getBaseurl() + "node/save", body);
	}

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

	public String createDependency(String uniqueIdFrom,String uniqueIdTo,String scope) throws IOException {

		JSONObject body = new JSONObject();
		body.put("from", uniqueIdFrom);
		body.put("to", uniqueIdTo);
		body.put("scope", scope);

		return RestUtils.sendPost(getBaseurl() + "node/depends", body);
	}

	public String addParent(String mainUniqueId,String parentUniqueId) throws IOException {

		JSONObject body = new JSONObject();
		body.put("main", mainUniqueId);
		body.put("parent", parentUniqueId);

		return RestUtils.sendPost(getBaseurl() + "node/parent", body);
	}

	public String query(String cypherQuery) throws IOException {

		JSONObject body = new JSONObject();
		body.put("q", cypherQuery);

		return RestUtils.sendPost(getBaseurl() + "/cypher/query", body);
	}

	public String filterAll(FilterItem f) throws IOException {

		StringBuffer sb = new StringBuffer("/query/filterall?");

		sb.append("gn1=").append(URLEncoder.encode(f.getFilterGN1(),"UTF-8")).append("&");
		sb.append("an1=").append(URLEncoder.encode(f.getFilterAN1(),"UTF-8")).append("&");
		sb.append("pn1=").append(URLEncoder.encode(f.getFilterPN1(),"UTF-8")).append("&");
		sb.append("cn1=").append(URLEncoder.encode(f.getFilterCN1(),"UTF-8")).append("&");
		sb.append("vn1=").append(URLEncoder.encode(f.getFilterVN1(),"UTF-8")).append("&");

		sb.append("gn2=").append(URLEncoder.encode(f.getFilterGN2(),"UTF-8")).append("&");
		sb.append("an2=").append(URLEncoder.encode(f.getFilterAN2(),"UTF-8")).append("&");
		sb.append("pn2=").append(URLEncoder.encode(f.getFilterPN2(),"UTF-8")).append("&");
		sb.append("cn2=").append(URLEncoder.encode(f.getFilterCN2(),"UTF-8")).append("&");
		sb.append("vn2=").append(URLEncoder.encode(f.getFilterVN2(),"UTF-8"));

		return RestUtils.sendGet(getBaseurl() + sb.toString() );
	}

	public String impact(int depth,String groupId,String artifactId,String packaging,String classifier,String version,FilterItem f) throws IOException {

		StringBuffer sb = new StringBuffer("/query/impact?");

		sb.append("d=").append(depth).append("&");
		sb.append("g=").append(URLEncoder.encode(groupId,"UTF-8")).append("&");
		sb.append("a=").append(URLEncoder.encode(artifactId,"UTF-8")).append("&");
		sb.append("p=").append(URLEncoder.encode(packaging,"UTF-8")).append("&");
		sb.append("c=").append(URLEncoder.encode(classifier,"UTF-8")).append("&");
		sb.append("v=").append(URLEncoder.encode(version,"UTF-8")).append("&");

		sb.append("gn1=").append(URLEncoder.encode(f.getFilterGN1(),"UTF-8")).append("&");
		sb.append("an1=").append(URLEncoder.encode(f.getFilterAN1(),"UTF-8")).append("&");
		sb.append("pn1=").append(URLEncoder.encode(f.getFilterPN1(),"UTF-8")).append("&");
		sb.append("cn1=").append(URLEncoder.encode(f.getFilterCN1(),"UTF-8")).append("&");
		sb.append("vn1=").append(URLEncoder.encode(f.getFilterVN1(),"UTF-8")).append("&");

		sb.append("gn2=").append(URLEncoder.encode(f.getFilterGN2(),"UTF-8")).append("&");
		sb.append("an2=").append(URLEncoder.encode(f.getFilterAN2(),"UTF-8")).append("&");
		sb.append("pn2=").append(URLEncoder.encode(f.getFilterPN2(),"UTF-8")).append("&");
		sb.append("cn2=").append(URLEncoder.encode(f.getFilterCN2(),"UTF-8")).append("&");
		sb.append("vn2=").append(URLEncoder.encode(f.getFilterVN2(),"UTF-8"));

		return RestUtils.sendGet(getBaseurl() + sb.toString() );
	}

	public JSONObject getArtifact(String uniqueId) throws IOException, ParseException {

		String response = RestUtils.sendGet(getBaseurl() + "node/get?id="+ uniqueId);
		return RestUtils.string2Json(response);
	}

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
