package org.aroundthecode.tools.remote.api.response;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface to implement generic HTTP response parsers
 * @author michele.sacchetti
 */ 
public interface ResponseParser {

	/**
	 * Method responsible of parsing response data and format them in desired output format
	 * @param in response {@link InputStream}
	 * @throws IOException raised if parsing error occurs
	 */
	void parse(InputStream in) throws IOException;

	/**
	 * Method returning response in desired format
	 * @return Object representation of response data, Return type may change depending on implementation
	 */
	Object getResponse();
	
}
