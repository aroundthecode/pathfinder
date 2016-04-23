package org.aroundthecode.pathfinder.maven.plugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.dependency.tree.TreeMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;
import org.aroundthecode.pathfinder.client.rest.PathfinderClient;
import org.aroundthecode.pathfinder.maven.plugin.treeserializers.PathfinderNodeVisitor;

@Mojo( name = "crawler", defaultPhase = LifecyclePhase.NONE )
public class PathFinderCrawlMojo extends TreeMojo
{

	@Component( role = MavenProjectBuilder.class )
	protected MavenProjectBuilder m_projectBuilder;

	/**
	 * Used to look up Artifacts in the remote repository.
	 */
	@Parameter( property = "component.org.apache.maven.artifact.factory.ArtifactFactory", readonly = true, required=true)
	protected ArtifactFactory factory;

	/**
	 * Used to look up Artifacts in the remote repository.
	 */
	@Parameter( property = "component.org.apache.maven.artifact.resolver.ArtifactResolver", readonly = true, required=true)
	protected ArtifactResolver artifactResolver;

	/**
	 * List of Remote Repositories used by the resolver
	 */
	@Parameter( property = "project.remoteArtifactRepositories", readonly = true, required=true)
	protected List remoteRepositories;

	/**
	 * Location of the local repository.
	 */
	@Parameter( property = "localRepository", readonly = true, required=true)

	protected ArtifactRepository localRepository;

	/**
	 * Neo4j rest protocol.
	 */
	@Parameter( property = "neo4j.protocol", defaultValue = "http" )
	private String neo4jProtocol;

	/**
	 * Neo4j rest domain.
	 */
	@Parameter( property = "neo4j.host", defaultValue = "localhost" )
	private String neo4jHost;

	/**
	 * Neo4j db port
	 */
	@Parameter( property = "neo4j.port", defaultValue = "8080" )
	private int neo4jPort;

	/**
	 * Neo4j base path
	 */
	@Parameter( property = "neo4j.path", defaultValue = "/" )
	private String neo4jPath;

	/**
	 * Artifact groupId to crawl.
	 */
	@Parameter( property = "crawler.groupId", defaultValue = "junit" )
	private String crawlerGroupId;

	/**
	 * Artifact artifactId to crawl.
	 */
	@Parameter( property = "crawler.artifactId", defaultValue = "junit" )
	private String crawlerArtifactId;

	/**
	 * Artifact version to crawl.
	 */
	@Parameter( property = "crawler.version", defaultValue = "4.12" )
	private String crawlerVersion;

	/**
	 * Artifact type to crawl.
	 */
	@Parameter( property = "crawler.type", defaultValue = "jar" )
	private String crawlerType;

	/**
	 * Artifact classifier to crawl.
	 */
	@Parameter( property = "crawler.classifier", defaultValue = "" )
	private String crawlerClassifier;

	/**
	 * Artifact scope to crawl.
	 */
	@Parameter( property = "crawler.scope", defaultValue = "compile" )
	private String crawlerScope;

	private MavenProject project = null;
	/**
	 * Override standard TreeMojo to retrieve project from parameters
	 */
	@Override
	public MavenProject getProject()
	{
		try {
			Artifact artifact = this.factory.createArtifact(
					crawlerGroupId, crawlerArtifactId, crawlerVersion,
					crawlerScope, crawlerType);
			artifactResolver.resolve(artifact, this.remoteRepositories,this.localRepository);
			//Build the project and get the result
			project = m_projectBuilder.buildFromRepository(artifact,this.remoteRepositories,this.localRepository);
		} catch (ArtifactResolutionException e) {
			getLog().error(e);
			project = null;
		} catch (ArtifactNotFoundException e) {
			getLog().error(e);
			project = null;
		} catch (ProjectBuildingException e) {
			getLog().error(e);
			project = null;
		}
		return project;

	}


	/**
	 * Override standard TreeMojo to provide only Pathfinder Visitors
	 */
	@Override
	public DependencyNodeVisitor getSerializingDependencyNodeVisitor( Writer writer )
	{

		DependencyNodeVisitor visitor=null;
		try {

			PathfinderClient client = new PathfinderClient(neo4jProtocol, neo4jHost, neo4jPort, neo4jPath);
			//visitor = new LogNodeVisitor(writer, getLog());
			visitor = new PathfinderNodeVisitor(writer, getLog(),client,project);

		} catch (IOException e) {
			getLog().error(e);
		}

		return visitor;
	}


	public void execute() throws MojoExecutionException, MojoFailureException
	{
		project = getProject();
		if(project == null){
			throw new MojoExecutionException("Failed to retrieve project to crawl!");
		}
		else{
			getLog().info("Project under analysis:"+project.getName());
			super.execute();
		}
	}
}
