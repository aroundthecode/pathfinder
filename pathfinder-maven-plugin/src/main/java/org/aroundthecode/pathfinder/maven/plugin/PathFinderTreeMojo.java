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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.dependency.tree.TreeMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;
import org.aroundthecode.pathfinder.client.rest.PathfinderClient;
import org.aroundthecode.pathfinder.maven.plugin.treeserializers.PathfinderNodeVisitor;
import org.json.simple.JSONArray;

@Mojo( name = "store-tree", defaultPhase = LifecyclePhase.NONE )
public class PathFinderTreeMojo extends TreeMojo
{
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


	private DependencyNodeVisitor visitor=null;

	/**
	 * Override standard TreeMojo to provide only Pathfinder Visitors
	 */
	@Override
	public DependencyNodeVisitor getSerializingDependencyNodeVisitor( Writer writer )
	{
		visitor = new PathfinderNodeVisitor(writer, getLog(),getProject());
		return visitor;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		super.execute();
		JSONArray data = ((PathfinderNodeVisitor)visitor).getBulkArray();
		getLog().info("DATA:"+data);
		PathfinderClient client = null;
		try {
			client = new PathfinderClient(neo4jProtocol, neo4jHost, neo4jPort, neo4jPath);
		} catch (IOException e) {
			getLog().error(e);
		}
		client.uploadProject(data);
	}

}
