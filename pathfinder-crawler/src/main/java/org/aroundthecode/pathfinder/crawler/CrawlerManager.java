package org.aroundthecode.pathfinder.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.graph.selector.AndDependencySelector;
import org.eclipse.aether.util.graph.selector.ExclusionDependencySelector;
import org.eclipse.aether.util.graph.selector.OptionalDependencySelector;
import org.eclipse.aether.util.graph.selector.ScopeDependencySelector;

public class CrawlerManager {

	LocalRepository localRepo = null;
	RepositorySystem system = null;
	RepositorySystemSession session = null;
	List<RemoteRepository> repositories = new ArrayList<RemoteRepository>();

	public CrawlerManager(String localRepoPath) throws IOException {
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

	public List<Dependency> getDirectDependencies(Artifact artifact) throws DependencyResolutionException, DependencyCollectionException, ArtifactDescriptorException {

        RepositorySystem system = newRepositorySystem();

        RepositorySystemSession session = newSession( system );
        
        ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
        descriptorRequest.setArtifact( artifact );
        descriptorRequest.setRepositories(repositories );

        ArtifactDescriptorResult descriptorResult = system.readArtifactDescriptor( session, descriptorRequest );

        return descriptorResult.getDependencies();
        
    }

	



}
