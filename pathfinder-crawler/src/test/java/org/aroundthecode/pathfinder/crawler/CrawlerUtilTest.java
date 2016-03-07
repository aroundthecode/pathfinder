package org.aroundthecode.pathfinder.crawler;

import java.io.IOException;
import java.util.List;

import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.util.repository.AuthenticationBuilder;
import org.junit.Test;

public class CrawlerUtilTest {
	@Test
	public void test()  {

		CrawlerManager cm;
		try {
//			DefaultArtifact artifact = new DefaultArtifact( "org.aroundthecode.pathfinder:pathfinder-server:0.1.0-SNAPSHOT" );
//			DefaultArtifact artifact = new DefaultArtifact( "com.facilitylive:facilitylive-base:3.2.5-SNAPSHOT:pom" );
			DefaultArtifact artifact = new DefaultArtifact( "com.facilitylive:flive-portlet:5.3.2:war" );
			
			
			
			Authentication authentication = new AuthenticationBuilder()
			.addUsername("readonly")
			.addPassword("asdf10")
			.build();

//			RemoteRepository repo1 =new RemoteRepository.Builder("central", "default", "http://central.maven.org/maven2/").build();
			RemoteRepository repo2 = new RemoteRepository.Builder("facility", "default", "https://repo.facilitylive.int/artifactory/repo")
			.setAuthentication(authentication )
			.build();

			cm = new CrawlerManager("/Users/msacchetti/.m2/repository");
//			cm.addRepository(repo1);
			cm.addRepository(repo2);

			cm.setAnyDependencySelector();
			List<Dependency> deps = cm.getDirectDependencies(artifact);
			System.out.println("DEPS:"+deps.size());
			for ( Dependency dependency : deps )
	        {
	            System.out.println( dependency );
	        }
			
		} catch (IOException | DependencyResolutionException | DependencyCollectionException | ArtifactDescriptorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
