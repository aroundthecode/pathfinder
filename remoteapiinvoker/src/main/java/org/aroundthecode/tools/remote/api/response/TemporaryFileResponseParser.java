package org.aroundthecode.tools.remote.api.response;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * {@link ResponseParser} implementation, save response stream to a temporary file provided its reference
 * @author michele.sacchetti
 */
public class TemporaryFileResponseParser implements ResponseParser {

	private static final int BUFSIZE = 1024;

	private static final Logger log = LogManager.getLogger(TemporaryFileResponseParser.class.getName());

	/**
	 * Thread-local response data
	 */
	private ThreadLocal<File> response = new ThreadLocal<File>(); 
	private ThreadLocal<String> tmpPath = new ThreadLocal<String>(); 
	private ThreadLocal<String> filename = new ThreadLocal<String>(); 

	/**
	 * Save response stream to a temporary file provided its reference
	 * @param tempStoragePath : temporary file path
	 */
	public TemporaryFileResponseParser(String tempStoragePath){
		tmpPath.set(tempStoragePath);
	}


	/**
	 * {@inheritDoc}
	 */
	public void parse(InputStream in) throws IOException {
		setResponse(in);
	}

	/**
	 * {@inheritDoc}
	 */
	public File getResponse() {
		return response.get();
	}

	/**
	 * Set String response data
	 * @param response
	 */
	public void setResponse(InputStream response) {

		String file = (getStoragePath() + "/" + getFilename() ).replaceAll("//", "/");
		File f = new File(file);
		f.deleteOnExit();
		BufferedInputStream bis = null;
		OutputStream out = null;
		try {
			bis = new BufferedInputStream(response);
			out = new BufferedOutputStream( new FileOutputStream(f) );
			byte[] buffer = new byte[BUFSIZE];
			int count = -1;
			while ((count = bis.read(buffer)) != -1) {
				out.write(buffer, 0, count);
			}
		} catch (FileNotFoundException e) {
			getLog().error(this.getClass().getName() + " : " + e.getMessage());
		} catch (IOException e) {
			getLog().error(this.getClass().getName() + " : " + e.getMessage());
		}finally{
			try {
				if(out!=null){
					out.flush();
					out.close();
				}
				if(bis!=null){
					bis.close();
				}
				response.close();
			} catch (IOException e) {
				getLog().error(this.getClass().getName() + " : " + e.getMessage());
			}
		}

		this.response.set(f);
	}

	/**
	 * get Logger
	 * @return
	 */
	private Logger getLog() {
		return log;
	}

	/**
	 * Get temporary storage path
	 * @return temporary storage path
	 */
	public String getStoragePath() {
		return tmpPath.get();
	}

	/**
	 * Set temporary storage path
	 * @param storage path
	 */
	public void setStoragePath(String tmpPath) {
		this.tmpPath.set(tmpPath);
	}

	/**
	 * Retrieve file name
	 * @return file name
	 */
	public String getFilename() {
		return filename.get();
	}

	/**
	 * Set file name
	 * @param file name
	 */
	public void setFilename(String filename) {
		this.filename.set(filename);
	}

}
