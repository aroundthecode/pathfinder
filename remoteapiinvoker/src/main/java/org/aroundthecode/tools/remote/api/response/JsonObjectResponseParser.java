package org.aroundthecode.tools.remote.api.response;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.aroundthecode.tools.remote.api.configuration.AbstractConnectionConfiguration;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

/**
 * {@link ResponseParser} implementation, response in converted into a {@link JSONObject} applying {@link JSONValue} parsing method
 * @author michele.sacchetti
 */
public class JsonObjectResponseParser implements ResponseParser {

	/**
	 * Thread-local response data
	 */
	private ThreadLocal<JSONObject> json = new ThreadLocal<JSONObject>(); 
	
	/**
	 * {@inheritDoc}
	 */
	public void parse(InputStream in) throws IOException {
			try {
				setJson((JSONObject) JSONValue.parseWithException( new InputStreamReader(in, AbstractConnectionConfiguration.CHARSET_NAME) ));
			} catch (ParseException e) {
				//just propagate
				throw new IOException(e);
			}
	}

	/**
	 * {@inheritDoc}
	 */
	public JSONObject getResponse() {
		return json.get();
	}

	/**
	 * Set {@link JSONObject} response data
	 */
	private void setJson(JSONObject json) {
		this.json.set(json);
	}

	
	
	

}
