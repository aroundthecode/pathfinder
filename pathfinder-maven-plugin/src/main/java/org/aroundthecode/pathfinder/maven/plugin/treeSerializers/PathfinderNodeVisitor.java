package org.aroundthecode.pathfinder.maven.plugin.treeSerializers;


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

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.dependency.tree.AbstractSerializingVisitor;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;
import org.aroundthecode.pathfinder.client.rest.PathfinderClient;
import org.aroundthecode.pathfinder.client.rest.utils.ArtifactUtils;
import org.aroundthecode.pathfinder.client.rest.utils.ArtifactUtils.Dependency;


public class PathfinderNodeVisitor extends AbstractSerializingVisitor implements
		DependencyNodeVisitor {

	private Log log;
	private String prj = null;
	PathfinderClient client = null;

	public PathfinderNodeVisitor(Writer writer, Log log,PathfinderClient client) {
		super(writer);
		this.log = log;
		this.client = client;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean visit(DependencyNode node) {

		boolean out = true;
		
		log.info("visiting "+node.toNodeString());
		
		if (node.getParent() == null || node.getParent() == node) {
			prj =  node.toNodeString();
			writer.write("Project is:[" + prj + "]\n");
			//STORE PRJ TO DB		
			out = saveNode(node);
			
		}

		// Generate "currentNode -> Child" lines

		List<DependencyNode> children = node.getChildren();
		for (Iterator<DependencyNode> child = children.iterator(); child.hasNext();) {
			DependencyNode c = child.next();

			String szFrom = getUniqueId(node.getArtifact());
			String szTo = getUniqueId(c.getArtifact());
			String scope = c.getArtifact().getScope();
			writer.println(szFrom+" --("+scope+")--> "+szTo);

			//STORE SZFROM TO DB
			out = saveNode(node);
			//STORE SZTO TO DB
			out = saveNode(c);
			//STORE PRJ-FROM-TO TO DB
			try {
				client.createDependency(szFrom, szTo, Dependency.valueOf(scope.toUpperCase()).toString());
			} catch (IOException e) {
				log.error("error creating dependency:"+e.getMessage());
			}
			
		}

		return out;
	}
	
	private String getUniqueId(Artifact a){
		return ArtifactUtils.getUniqueId(a.getGroupId(), a.getArtifactId(), a.getType(), a.getClassifier(), a.getVersion());
	}

	private boolean saveNode(DependencyNode node) {
		boolean out = true;
		try {
			client.saveArtifact(getUniqueId(node.getArtifact() ));
		} catch (IOException e) {
			log.error("error saving node "+prj);
			out = false;
		}
		return out;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean endVisit(DependencyNode node) {
		return true;
	}

}
