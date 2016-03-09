package org.aroundthecode.pathfinder.server.crawler;

import java.io.File;
import java.util.Collections;
import java.util.Properties;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.aroundthecode.pathfinder.server.crawler.handler.JsonResponseHandler;
import org.json.simple.JSONObject;

public class CrawlerWrapper {

	private static String mvnHome = "/opt/apache-maven";
	private static Invoker invoker = new DefaultInvoker();
	static{
		String mHome = System.getenv("M2_HOME");
		if(mHome==null){
			System.err.println("No M2_HOME set, using ["+getMvnHome()+"]");
			mHome = getMvnHome();
		}

		invoker.setMavenHome( new File(mHome) );
	}

	public static JSONObject crawl(String groupId,String artifactId,String type,String classifier, String version){

		JsonResponseHandler jHandler = new JsonResponseHandler();
		InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile( new File( "./src/main/resources/embedder/pom.xml" ) );
		request.setGoals(  Collections.singletonList( "org.aroundthecode.pathfinder:pathfinder-maven-plugin:0.1.0-SNAPSHOT:crawler" ) );
		request.setOutputHandler(jHandler);

		Properties params = new Properties();
		params.setProperty("crawler.groupId", 		groupId);
		params.setProperty("crawler.artifactId", 	artifactId);
		params.setProperty("crawler.type", 			type);
		params.setProperty("crawler.classifier", 	classifier);
		params.setProperty("crawler.version", 		version);

		request.setProperties(params);

		InvocationResult result;
		try {
			result = invoker.execute( request );
			jHandler.setReturnStatus( result.getExitCode() );
			jHandler.setException( result.getExecutionException() );
		} 
		catch (MavenInvocationException e) {
			jHandler.setReturnStatus( -1 );
			jHandler.setException( e );
		}

		return jHandler.getJson();

	}

	public static String getMvnHome() {
		return mvnHome;
	}

	public void setMvnHome(String mvnHome) {
		this.mvnHome = mvnHome;
	}


}
