package org.aroundthecode.pathfinder.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.aroundthecode.pathfinder.client.rest.utils.ArtifactUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
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
import org.eclipse.aether.util.graph.visitor.FilteringDependencyVisitor;
import org.eclipse.aether.util.graph.visitor.TreeDependencyVisitor;

public class CrawlerManagerDirect {

	LocalRepository localRepo = null;
	RepositorySystem system = null;
	RepositorySystemSession session = null;
	List<RemoteRepository> repositories = new ArrayList<RemoteRepository>();
//	PathfinderClient client = null;

	public CrawlerManagerDirect(String localRepoPath) throws IOException {
		localRepo = new LocalRepository( localRepoPath );
		system = newRepositorySystem();
		session = newSession(system);
		repositories.add((new RemoteRepository.Builder("central", "default", "http://central.maven.org/maven2/")).build());
		//		repositories.add((new RemoteRepository.Builder("fl", "default", "https://repo.facilitylive.int/artifactory/repo/")).build());

//		client = new PathfinderClient("http", "localhost", 8080, "/");
	}

	private RepositorySystem newRepositorySystem()
	{
		DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
		locator.addService( RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class );
		locator.addService( TransporterFactory.class, FileTransporterFactory.class );
		locator.addService( TransporterFactory.class, HttpTransporterFactory.class );
		return locator.getService( RepositorySystem.class );
	}

	private RepositorySystemSession newSession( RepositorySystem system )
	{
		DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
		session.setLocalRepositoryManager( system.newLocalRepositoryManager( session, localRepo ) );
		
		DependencySelector depFilter = new AndDependencySelector(
	                    new ScopeDependencySelector(),// "provided" ),
	                    new OptionalDependencySelector(),
	                    new ExclusionDependencySelector()
	            );
		
		session.setDependencySelector(depFilter);
		return session;
	}

	public ArtifactDescriptorResult getArtifactDependencies(Artifact artifact) throws DependencyCollectionException, DependencyResolutionException, ArtifactDescriptorException{

		ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
		descriptorRequest.setArtifact( artifact );
		descriptorRequest.setRepositories( system.newResolutionRepositories(session, repositories ) );
		return system.readArtifactDescriptor( session, descriptorRequest );

	}

	public void getRecursiveDependencies(Artifact a,int maxDepth) throws DependencyCollectionException, DependencyResolutionException, ArtifactDescriptorException, IOException{

		List<Artifact> al = new ArrayList<Artifact>();
		List<String> allow = getMinimumArtifacts(a);
		//		al.add(a);
		//		System.out.println( 0 +" " + a );
		//		getRecursiveDependencies(a, 1, al,maxDepth+1,allow);
		//		System.out.println(allow.size());
	}

	/*
	public void getRecursiveDependencies(Artifact a, int depth,List<Artifact> al,int maxDepth,List<String> allow) throws DependencyCollectionException, DependencyResolutionException, ArtifactDescriptorException, IOException{
		ArtifactDescriptorResult descriptorResult = getArtifactDependencies(a);

		client.saveArtifact(getUniqueId(a));
		List<Dependency> l = descriptorResult.getDependencies();
		for ( Dependency dependency : l )
		{
			Artifact aa = dependency.getArtifact();
			if(!al.contains(aa) && depth < maxDepth && allow.contains(aa.toString()) ){
				for (int i = 0; i < depth; i++) {
					System.out.print("-");
				}
				System.out.println( depth +" " + dependency );
				client.saveArtifact(getUniqueId(aa));
				client.createDependency(getUniqueId(a), getUniqueId(aa), ArtifactUtils.Dependency.valueOf(dependency.getScope().toUpperCase()).toString());

				al.add(aa);
				allow.remove(aa.toString());
				getRecursiveDependencies(aa, depth+1,al,maxDepth,allow);
			}
		}
	}
	 */

	private List<String> getMinimumArtifacts(Artifact artifact) throws DependencyResolutionException, DependencyCollectionException{
		{

			DependencyFilter classpathFlter = DependencyFilterUtils.classpathFilter(JavaScopes.TEST);

			CollectRequest collectRequest = new CollectRequest();
			collectRequest.setRoot( new Dependency( artifact, JavaScopes.COMPILE ) );
			collectRequest.setRepositories( repositories );

			DependencyRequest dependencyRequest = new DependencyRequest( collectRequest, classpathFlter );

			List<ArtifactResult> artifactResults = system.resolveDependencies( session, dependencyRequest ).getArtifactResults();
			List<String> al = new ArrayList<String>();
			/*
	        //System.out.println("-->"+artifactResults.size() );
	        for ( ArtifactResult artifactResult : artifactResults )
	        {
	            System.out.println( artifactResult.getArtifact() );//+ " resolved to " + artifactResult.getArtifact().getFile() );
	            al.add(artifactResult.getArtifact().toString());
	        }
	        System.out.println("--------------------------------------" );
			 */

			CollectResult collectResult = system.collectDependencies(session, collectRequest);
			DependencyNode node = collectResult.getRoot();
			
			
			node.accept(new TreeDependencyVisitor(new FilteringDependencyVisitor(new DependencyVisitor() {
				String indent = "";
				@Override
				public boolean visitEnter(DependencyNode dependencyNode) {
					indent += "    ";
					System.out.println(indent + dependencyNode);
//					try {
//						System.out.println(indent + dependencyNode);//.getArtifact() + "("+ dependencyNode.getDependency().getScope()+")");
//						client.saveArtifact(getUniqueId(dependencyNode.getArtifact()));
//						for( DependencyNode d :dependencyNode.getChildren() ){
//							//System.out.println(dependencyNode + " -> " + d);
//							client.createDependency(getUniqueId(dependencyNode.getArtifact()), getUniqueId(d.getArtifact()), ArtifactUtils.Dependency.valueOf(d.getDependency().getScope().toUpperCase()).toString());
//						}
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					return true;
				}

				@Override
				public boolean visitLeave(DependencyNode dependencyNode) {
					indent = indent.substring(0, indent.length() - 4);
					return true;
				}
			}, classpathFlter)));

			return al;
		}
	}

	private String getUniqueId(Artifact a){
		return ArtifactUtils.getUniqueId(a.getGroupId(), a.getArtifactId(), a.getExtension(), a.getClassifier(), a.getBaseVersion());
	}


	private boolean contains(List<DependencyNode> seenList, DependencyNode dep)
	{
		for (DependencyNode node : seenList) {
			if (dep.getDependency().getArtifact().getArtifactId()
					.equals(node.getDependency().getArtifact().getArtifactId())) {
				return true;
			}
		}
		return false;
	}

}
