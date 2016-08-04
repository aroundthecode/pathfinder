package org.aroundthecode.tools.remote.api.response;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.aroundthecode.tools.remote.api.configuration.AbstractConnectionConfiguration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

/**
 * {@link ResponseParser} implementation, response in converted into a {@link JSONArray} applying {@link JSONValue} parsing method
 * @author michele.sacchetti
 */
public class JsonArrayResponseParser implements ResponseParser {

	/**
	 * Thread-local response data
	 */
	private ThreadLocal<JSONArray> json = new ThreadLocal<JSONArray>(); 
	
	/**
	 * {@inheritDoc}
	 */
	public void parse(InputStream in) throws IOException {
			try {
				setJson((JSONArray) JSONValue.parseWithException( new InputStreamReader(in, AbstractConnectionConfiguration.CHARSET_NAME) ));
			} catch (ParseException e) {
				//just propagate
				throw new IOException(e);
			}
	}

	/**
	 * {@inheritDoc}
	 */
	public JSONArray getResponse() {
		return json.get();
	}

	/**
	 * Set {@link JSONObject} response data
	 */
	private void setJson(JSONArray json) {
		this.json.set(json);
	}

	
	
	

}
