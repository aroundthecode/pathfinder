package org.aroundthecode.pathfinder.client.rest.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RestUtils {

	protected static final String USER_AGENT = "pathfinder-rest-client";
//	private static final Logger logger = LogManager.getLogger(RestUtils.class);
	private static final JSONParser jparser = new JSONParser();


	public static final String sendGet(String url)  throws IOException {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = null;
		StringBuffer response = new StringBuffer();
		try {
			in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;


			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
		}
		catch (Exception e) {
			System.err.println("sendGet - reading response " +e.getMessage());
		}
		finally{
			if(in!=null){
				in.close();
			}
		}

		return response.toString();

	}

	public static final String sendPost(String url, JSONObject body) throws IOException {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Content-Type","application/json");

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(body.toString());
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + body.toString());
		System.out.println("Response Code : " + responseCode);

		StringBuffer response = new StringBuffer();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
		} catch (Exception e) {
			System.err.println("sendPost - reading response " +e.getMessage());
		}
		finally{
			if(in!=null){
				in.close();
			}
		}

		return response.toString();

	}

	public static final JSONObject string2Json(String s) throws ParseException{
		return (JSONObject) jparser.parse(s);
	}
}
