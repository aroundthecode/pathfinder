package org.aroundthecode.pathfinder.server.crawler;

import java.io.File;
import java.util.Collections;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

public class CrawlerWrapper {

	private static String mvnHome = "/opt/apache-maven";
	
	public static void crawl(String groupId,String artifactId,String packaginf,String classifier, String version) throws MavenInvocationException{
		
		InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile( new File( "./src/main/resources/embedder/pom.xml" ) );
		request.setGoals(  Collections.singletonList( "org.aroundthecode.pathfinder:pathfinder-maven-plugin:0.1.0-SNAPSHOT:crawler" ) );
		Invoker invoker = new DefaultInvoker();
		
		String mHome = System.getenv("M2_HOME");
		if(mHome==null){
			System.err.println("No M2_HOME set, using ["+getMvnHome()+"]");
			mHome = getMvnHome();
		}
		
		invoker.setMavenHome( new File(mHome) );
		invoker.execute( request );
		
	}

	public static String getMvnHome() {
		return mvnHome;
	}

	public void setMvnHome(String mvnHome) {
		this.mvnHome = mvnHome;
	}
	
	
}
