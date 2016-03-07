package org.aroundthecode.pathfinder.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.filter.DependencyFilterUtils;
import org.eclipse.aether.util.graph.selector.AndDependencySelector;
import org.eclipse.aether.util.graph.selector.ExclusionDependencySelector;
import org.eclipse.aether.util.graph.selector.OptionalDependencySelector;
import org.eclipse.aether.util.graph.selector.ScopeDependencySelector;

public class CrawlerManagerOld {

	LocalRepository localRepo = null;
	RepositorySystem system = null;
	RepositorySystemSession session = null;
	List<RemoteRepository> repositories = new ArrayList<RemoteRepository>();

	public CrawlerManagerOld(String localRepoPath) throws IOException {
		localRepo = new LocalRepository( localRepoPath );
		system = newRepositorySystem();
		session = newSession(system);
	}

	public void addRepository(RemoteRepository repo){
		repositories.add(repo);
	}

	private RepositorySystem newRepositorySystem()
	{
		DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
		locator.addService( RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class );
		locator.addService( TransporterFactory.class, FileTransporterFactory.class );
		locator.addService( TransporterFactory.class, HttpTransporterFactory.class );
		return locator.getService( RepositorySystem.class );
	}

	public void setAnyDependencySelector(){
		DependencySelector depFilter = new AndDependencySelector(
				new ScopeDependencySelector( ),
				new OptionalDependencySelector(),
				new ExclusionDependencySelector()
				);
		addDependencySelector(depFilter);
	}

	public void addDependencySelector(DependencySelector depFilter){
		((DefaultRepositorySystemSession) session).setDependencySelector(depFilter);
	}

	private RepositorySystemSession newSession( RepositorySystem system )
	{
		DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
		session.setLocalRepositoryManager( system.newLocalRepositoryManager( session, localRepo ) );
		return session;
	}

	public void getDirectDependencies(Artifact artifact) throws DependencyResolutionException {

		DependencyFilter classpathFlter = DependencyFilterUtils.classpathFilter(JavaScopes.TEST);

		CollectRequest collectRequest = new CollectRequest();
		collectRequest.setRoot( new Dependency( artifact, JavaScopes.COMPILE ) );
		collectRequest.setRepositories( repositories );

		DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, classpathFlter);

		List<ArtifactResult> artifactResults =
				system.resolveDependencies(session, dependencyRequest).getArtifactResults();

		for (ArtifactResult artifactResult : artifactResults) {
			System.out.println( artifactResult.getRequest().getDependencyNode() );
		}
	}

	



}
