package org.aroundthecode.pathfinder.server.crawler.handler;

import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.json.simple.JSONObject;


public class JsonResponseHandler implements InvocationOutputHandler{

	private StringBuffer response = null;
	private int ret = -1;
	private String exception = "";
	
	public JsonResponseHandler() {
		response = new StringBuffer();
		setReturnStatus(-1);
	}
	
	@Override
	public void consumeLine(String line) {
		response.append(line).append("\n");
		
	}

	private int getReturnStatus() {
		return ret;
	}

	public void setReturnStatus(int ret) {
		this.ret = ret;
	}

	public void setException(Exception executionException) {
		if(executionException!=null){
			setException( executionException.getMessage() );
		}
	}

	private String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}
	
	private String getResponse() {
		return response.toString();
	}

	@SuppressWarnings("unchecked")
	public JSONObject getJson(){
		JSONObject ret = new JSONObject();
		ret.put("response", getResponse());
		ret.put("return", getReturnStatus());
		ret.put("exception", getException());
		return ret;
	}


	
	

}
