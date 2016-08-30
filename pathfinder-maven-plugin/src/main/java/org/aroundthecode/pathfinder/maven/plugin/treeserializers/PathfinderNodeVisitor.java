package org.aroundthecode.pathfinder.maven.plugin.treeserializers;


/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.dependency.tree.AbstractSerializingVisitor;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;
import org.aroundthecode.pathfinder.client.rest.PathfinderClient;
import org.aroundthecode.pathfinder.client.rest.utils.ArtifactUtils;
import org.json.simple.JSONArray;


public class PathfinderNodeVisitor extends AbstractSerializingVisitor implements
DependencyNodeVisitor {

	private Log log;
	private String prj = null;
	private MavenProject project = null;


	private JSONArray bulkArray = new JSONArray();
	private Long analysisTimestamp = null;

	public PathfinderNodeVisitor(Writer writer, Log log, MavenProject mavenProject) {
		super(writer);
		this.log = log;
		this.project = mavenProject;
		this.analysisTimestamp = System.currentTimeMillis();
	}

	/**
	 * @return the bulkArray
	 */
	public JSONArray getBulkArray() {
		return bulkArray;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(DependencyNode node) {

		JSONArray dependenciesUniqueIdCompile = new JSONArray();
		JSONArray dependenciesUniqueIdImport = new JSONArray();
		JSONArray dependenciesUniqueIdProvided = new JSONArray();
		JSONArray dependenciesUniqueIdRuntime = new JSONArray();
		JSONArray dependenciesUniqueIdSystem = new JSONArray();
		JSONArray dependenciesUniqueIdTest = new JSONArray();


		boolean out = true;

		log.debug("visiting "+node.toNodeString());

		/*
		 * Detect project node and eventually evaluate parent pom
		 */
		if (node.getParent() == null || node.getParent() == node) {
			prj =  node.toNodeString();
			writer.write("Project is:[" + prj + "]\n");

			Artifact parent = project.getParentArtifact();
			if(parent!=null){
				String parentId = getUniqueId(parent);
				writer.write("Parent project is:[" + parentId + "]\n");
				bulkArray.add( ArtifactUtils.artifactJSON(getUniqueId(node.getArtifact()), analysisTimestamp, parentId) );
			}

		}

		// Generate "currentNode -> Child" lines
		List<DependencyNode> children = node.getChildren();
		String szFrom = getUniqueId(node.getArtifact());
		for (Iterator<DependencyNode> child = children.iterator(); child.hasNext();) {
			DependencyNode c = child.next();

			String szTo = getUniqueId(c.getArtifact());
			String scope = c.getArtifact().getScope();

			//store node relation
			if(scope.contains(",")){
				writer.println("WARNING:multiple scopes detected into ["+scope+"], considering just first one");
				scope = scope.substring(0, scope.indexOf(','));
			}
			writer.println(szFrom+" --("+scope+")--> "+szTo);



			switch (ArtifactUtils.Dependency.valueOf(scope.toUpperCase())  ) {
			case COMPILE:
				dependenciesUniqueIdCompile.add(szTo);
				break;
			case PROVIDED:
				dependenciesUniqueIdProvided.add(szTo);
				break;
			case RUNTIME:
				dependenciesUniqueIdRuntime.add(szTo);
				break;
			case TEST:
				dependenciesUniqueIdTest.add(szTo);
				break;
			case SYSTEM:
				dependenciesUniqueIdSystem.add(szTo);
				break;
			case IMPORT:
				dependenciesUniqueIdImport.add(szTo);
				break;
			default:
				break;
			}


		}
		bulkArray.add(
				ArtifactUtils.artifactJSON(szFrom, analysisTimestamp, null, 
						dependenciesUniqueIdCompile, dependenciesUniqueIdImport, 
						dependenciesUniqueIdProvided, dependenciesUniqueIdRuntime, 
						dependenciesUniqueIdSystem, dependenciesUniqueIdTest)
				);


		return out;
	}

	private String getUniqueId(Artifact a){
		return ArtifactUtils.getUniqueId(a.getGroupId(), a.getArtifactId(), a.getType(), a.getClassifier(), a.getBaseVersion());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean endVisit(DependencyNode node) {
		return true;
	}

}
