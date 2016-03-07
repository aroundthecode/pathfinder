package org.aroundthecode.pathfinder.crawler;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.maven.DefaultArtifactFilterManager;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.artifact.filter.ScopeArtifactFilter;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.BuildingDependencyNodeVisitor;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;
import org.aroundthecode.pathfinder.client.rest.PathfinderClient;
import org.aroundthecode.pathfinder.maven.plugin.treeSerializers.PathfinderNodeVisitor;



public class test {

    private static Log log;
	private static DependencyGraphBuilder dependencyGraphBuilder;
	static MavenProject project = null;

	private static String serializeDependencyTree( DependencyNode rootNode )
	{
		StringWriter writer = new StringWriter();

		DependencyNodeVisitor visitor = getSerializingDependencyNodeVisitor( writer );

		// TODO: remove the need for this when the serializer can calculate last nodes from visitor calls only
		visitor = new BuildingDependencyNodeVisitor( visitor );
		rootNode.accept( visitor );
		return writer.toString();
	}

	public static DependencyNodeVisitor getSerializingDependencyNodeVisitor( Writer writer )
	{

		DependencyNodeVisitor visitor=null;
		try {
			PathfinderClient client = new PathfinderClient("http", "localhost", 8080, "/");
			//visitor = new LogNodeVisitor(writer, getLog());
			visitor = new PathfinderNodeVisitor(writer, getLog(),client,project);

		} catch (IOException e) {
			getLog().error(e);;
		}

		return visitor;
	}

	public static void main( String[] args )
			throws Exception
	{

		Model model = new Model();
		model.setGroupId( "org.aroundthecode.pathfinder" );
		model.setArtifactId( "pathfinder-server" );
		model.setVersion( "0.1.0-SNAPSHOT" );

		MavenProject project = new MavenProject(model);

		DefaultProjectBuildingRequest bpr = new DefaultProjectBuildingRequest();
		bpr.setProject(project);
		
		ArtifactFilter af = new ScopeArtifactFilter( "test" );

		DependencyNode rootNode = dependencyGraphBuilder.buildDependencyGraph( bpr,  af );
		serializeDependencyTree( rootNode );

	}

	
	public static Log getLog()
    {
        if ( log == null )
        {
            log = new SystemStreamLog();
        }

        return log;
    }
	
}
