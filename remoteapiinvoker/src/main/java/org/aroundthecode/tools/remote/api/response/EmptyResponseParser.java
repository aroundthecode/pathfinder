package org.aroundthecode.tools.remote.api.response;

import java.io.IOException;
import java.io.InputStream;

/**
 * {@link ResponseParser} implementation, to be used for no-response method
 * 
 * @author michele.sacchetti
 */
public class EmptyResponseParser implements ResponseParser {

	/**
	 * {@inheritDoc}
	 */
	public void parse(InputStream in) throws IOException {
		// noting to do
	}

	/**
	 * {@inheritDoc}
	 */
	public String getResponse() {
		return "";
	}

}
