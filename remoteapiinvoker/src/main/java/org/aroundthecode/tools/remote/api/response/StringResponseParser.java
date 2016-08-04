package org.aroundthecode.tools.remote.api.response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aroundthecode.tools.remote.api.configuration.AbstractConnectionConfiguration;

/**
 * {@link ResponseParser} implementation, all response is translated in a simple string
 * @author michele.sacchetti
 */
public class StringResponseParser implements ResponseParser {

	private static final Logger log = LogManager.getLogger(StringResponseParser.class.getName());

	/**
	 * Thread-local response data
	 */
	private ThreadLocal<String> response = new ThreadLocal<String>(); 
	
	private static final int BUF_SIZE = 1024;
	
	/**
	 * {@inheritDoc}
	 */
	public void parse(InputStream in) throws IOException {
		
		
		Writer writer = new StringWriter();
		 
        char[] buffer = new char[BUF_SIZE];
        Reader reader = new BufferedReader(new InputStreamReader(in, AbstractConnectionConfiguration.CHARSET_NAME));
        try {
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        
		} finally {
            try {
				reader.close();
			} catch (IOException e) {
				getLog().warn(e.getMessage());
			}
        }
        setResponse(writer.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	public String getResponse() {
		return response.get();
	}

	/**
	 * Set String response data
	 * @param response
	 */
	public void setResponse(String response) {
		this.response.set(response);
	}

	/**
	 * get Logger
	 * @return
	 */
	private Logger getLog() {
		return log;
	}

	
	
	

}
