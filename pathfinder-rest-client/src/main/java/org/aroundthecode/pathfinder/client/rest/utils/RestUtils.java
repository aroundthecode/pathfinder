package org.aroundthecode.pathfinder.client.rest.utils;

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

/**
 * Utility class to perform POST and GET request to remote REST server
 * @author msacchetti
 *
 */
public class RestUtils {

	protected static final String USER_AGENT = "pathfinder-rest-client";
	private static final Logger log = LogManager.getLogger(RestUtils.class.getName());
	private static final JSONParser jparser = new JSONParser();


	private RestUtils(){

	}

	/**
	 * Utility method to perform GET request to given URL
	 * @param url URL to be invoked
	 * @return plain text representation of response
	 * @throws IOException
	 */
	public static final String sendGet(String url)  throws IOException {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		if(log.isDebugEnabled()){
			log.debug("GET [" + url+"] " + responseCode);
		}

		StringBuilder response = new StringBuilder();

		try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())) ) {

			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
		}
		catch (Exception e) {
			log.error("sendGet - reading response " +e.getMessage(),e);
		}

		return response.toString();

	}

	/**
	 * Utility method to perform POST request to given URL
	 * @param url URL to be invoked
	 * @param body POST body data
	 * @return plain text representation of response
	 * @throws IOException
	 */
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
		if(log.isDebugEnabled()){
			log.debug("POST [" + url+"] body["+body.toString()+"]" + responseCode);
		}

		StringBuilder response = new StringBuilder();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())) ) {

			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
		}
		catch (Exception e) {
			log.error("sendPost - reading response " +e.getMessage(),e);
		}

		return response.toString();

	}

	/**
	 * Utility method to convert a String representing a JSON to JSONObject 
	 * @param s String with JSON data
	 * @return JSONObject from give string
	 * @throws ParseException
	 */
	public static final JSONObject string2Json(String s) throws ParseException{
		return (JSONObject) jparser.parse(s);
	}
}
