package org.aroundthecode.pathfinder.client.rest.utils;

import java.io.IOException;
import java.io.Reader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Utility class to perform POST and GET request to remote REST server
 * @author msacchetti
 *
 */
public class RestUtils {

	private static final JSONParser jparser = new JSONParser();

	private RestUtils() {
	}


	/**
	 * Utility method to convert a Reader representing a JSON to JSONObject 
	 * @param r Reader with JSON data
	 * @return JSONObject from give string
	 * @throws ParseException
	 * @throws IOException 
	 */
	public static final JSONObject string2JSONObject(Reader r) throws ParseException, IOException{
		return (JSONObject) jparser.parse(r);
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
	
	/**
	 * Utility method to convert a Reader representing a JSON to JSONArray 
	 * @param r Reader with JSON Array data
	 * @return JSONArray from give string
	 * @throws ParseException
	 * @throws IOException 
	 */
	public static final JSONArray string2JSONArray(Reader r) throws ParseException, IOException{
		return (JSONArray) jparser.parse(r);
	}
	
	/**
	 * Utility method to convert a String representing a JSON to JSONObject 
	 * @param s String with JSON data
	 * @return JSONObject from give string
	 * @throws ParseException
	 */
	public static final JSONArray string2JSONArray(String s) throws ParseException{
		return (JSONArray) jparser.parse(s);
	}


	
}
