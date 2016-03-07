package org.aroundthecode.pathfinder.crawler;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.project.DefaultMavenProjectBuilder;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.shared.artifact.filter.ScopeArtifactFilter;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.filter.AncestorOrSelfDependencyNodeFilter;
import org.apache.maven.shared.dependency.graph.filter.AndDependencyNodeFilter;
import org.apache.maven.shared.dependency.graph.filter.DependencyNodeFilter;
import org.apache.maven.shared.dependency.graph.internal.DefaultDependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.traversal.BuildingDependencyNodeVisitor;
import org.apache.maven.shared.dependency.graph.traversal.CollectingDependencyNodeVisitor;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;
import org.apache.maven.shared.dependency.graph.traversal.FilteringDependencyNodeVisitor;
import org.apache.maven.shared.dependency.graph.traversal.SerializingDependencyNodeVisitor;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;

public class CrawlerManagerMaven2 {
	
	public void getDirectDependencies() throws ProjectBuildingException, DependencyGraphBuilderException {
		VersionRange vr = VersionRange.createFromVersion("0.1.0-SNAPSHOT");
		ArtifactHandler ah = new DefaultArtifactHandler();
		Artifact a = new DefaultArtifact("org.aroundthecode.pathfinder", "pathfinder-server", vr, "compile", "jar", "",  ah);
		DefaultMavenProjectBuilder mpb = new DefaultMavenProjectBuilder();
		mpb.enableLogging( new ConsoleLogger(Logger.LEVEL_DEBUG, this.getClass().getName()));
		
		ArtifactRepository repo1 = new DefaultArtifactRepository("maven-central", "http://repo1.maven.org/maven2/", new DefaultRepositoryLayout() );
		ArtifactRepository repolocal = new DefaultArtifactRepository( "local", new File("/Users/msacchetti/.m2/repository").toURI().toString(), new DefaultRepositoryLayout() );
		
		MavenProject project = mpb.buildFromRepository(a, Arrays.asList(	repo1 ),repolocal);
		DependencyGraphBuilder dependencyGraphBuilder = new DefaultDependencyGraphBuilder();
		ArtifactFilter filter = new ScopeArtifactFilter();
		DependencyNode rootNode = dependencyGraphBuilder.buildDependencyGraph(project , filter);
		
		String dependencyTreeString = serializeDependencyTree( rootNode );

	}	
	private String serializeDependencyTree( DependencyNode rootNode )
    {
        StringWriter writer = new StringWriter();

        DependencyNodeVisitor visitor = new SerializingDependencyNodeVisitor( writer, SerializingDependencyNodeVisitor.WHITESPACE_TOKENS );

        // TODO: remove the need for this when the serializer can calculate last nodes from visitor calls only
        visitor = new BuildingDependencyNodeVisitor( visitor );
        
        List<DependencyNodeFilter> filters = new ArrayList<DependencyNodeFilter>();
        DependencyNodeFilter filter = new AndDependencyNodeFilter( filters );

        if ( filter != null )
        {
            CollectingDependencyNodeVisitor collectingVisitor = new CollectingDependencyNodeVisitor();
            DependencyNodeVisitor firstPassVisitor = new FilteringDependencyNodeVisitor( collectingVisitor, filter );
            rootNode.accept( firstPassVisitor );

            DependencyNodeFilter secondPassFilter =
                new AncestorOrSelfDependencyNodeFilter( collectingVisitor.getNodes() );
            visitor = new FilteringDependencyNodeVisitor( visitor, secondPassFilter );
        }

        rootNode.accept( visitor );

        return writer.toString();
    }
	



}
