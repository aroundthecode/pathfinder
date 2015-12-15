package org.aroundthecode.pathfinder.server.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RestUtils {

	private final static String USER_AGENT = "pathfinder-rest-client";
	private static final Logger logger = LogManager.getLogger(RestUtils.class);
	private static JSONParser jparser = new JSONParser();


	public static String sendGet(String url)  throws IOException {

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
			logger.error("sendGet - reading response ",e);
		}
		finally{
			if(in!=null){
				in.close();
			}
		}

		return response.toString();

	}

	public static String sendPost(String url, JSONObject body) throws IOException {

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
			logger.error("sendPost - reading response ",e);
		}
		finally{
			if(in!=null){
				in.close();
			}
		}

		return response.toString();

	}

	public static JSONObject string2Json(String s) throws ParseException{
		return (JSONObject) jparser.parse(s);
	}
}
