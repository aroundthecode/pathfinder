package org.aroundthecode.pathfinder.client.rest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.aroundthecode.pathfinder.client.rest.utils.RestUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class PathfinderClient {

	private String baseurl = "http://localhost:8080";

	public PathfinderClient(String protocol,String domain, int port, String path) throws IOException {

		this.setBaseurl(protocol+"://"+domain+":"+port+path);

		System.err.print("testing connection ["+getBaseurl()+"]...");
		Socket socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(domain, port), 5);
			System.err.println("OK");
		} catch (IOException ex) {
			System.err.println("FAIL");
			throw new IOException(ex);
		} 
		finally{
			socket.close();
		}
	}

	public final String getBaseurl() {
		return baseurl;
	}

	public final void setBaseurl(String baseurl) {
		this.baseurl = baseurl;
	}

	public String saveArtifact(String groupId,String artifactId,String packaging,String classifier,String version) throws IOException {

		JSONObject body = createJson(groupId, artifactId, packaging,classifier, version);
		return RestUtils.sendPost(getBaseurl() + "node/save", body);
	}

	public JSONObject getArtifact(String uniqueId) throws IOException, ParseException {

		String response = RestUtils.sendGet(getBaseurl() + "node/get?id="+ uniqueId);
		return RestUtils.string2Json(response);
	}

	@SuppressWarnings("unchecked")
	private JSONObject createJson(String groupId, String artifactId,
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
