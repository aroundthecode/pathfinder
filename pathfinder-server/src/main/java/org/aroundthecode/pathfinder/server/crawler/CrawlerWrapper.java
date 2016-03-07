package org.aroundthecode.pathfinder.server.crawler;

import java.io.File;
import java.util.Collections;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

public class CrawlerWrapper {

//	static MavenCli cli = new MavenCli();
//	static String fd = new File(".").getAbsolutePath();
//	static{
//		fd += "/src/main/resources/embedder/";
//		System.setProperty("maven.multiModuleProjectDirectory", fd);
//	}
	
	static void crawl(String groupId,String artifactId,String packaginf,String classifier, String version) throws MavenInvocationException{
		
//		cli.doMain(
//				new String[] {"-Xmx1G","-XX:MaxPermSize=512m", "org.aroundthecode.pathfinder:pathfinder-maven-plugin:0.1.0-SNAPSHOT:crawler" },
//				fd, System.out, System.out);
		
		InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile( new File( "./src/main/resources/embedder/pom.xml" ) );
		request.setGoals(  Collections.singletonList( "org.aroundthecode.pathfinder:pathfinder-maven-plugin:0.1.0-SNAPSHOT:crawler" ) );
		Invoker invoker = new DefaultInvoker();
		invoker.setMavenHome( new File("/opt/apache-maven") );
		invoker.execute( request );
		
	}
	
	
}
