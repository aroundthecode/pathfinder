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

import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.dependency.tree.AbstractSerializingVisitor;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;


public class LogNodeVisitor extends AbstractSerializingVisitor implements
		DependencyNodeVisitor {

	private Log log;
	private String prj = null;

	public LogNodeVisitor(Writer writer, Log log) {
		super(writer);
		this.log = log;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean visit(DependencyNode node) {

		
		log.info("visiting "+node.toNodeString());
		
		if (node.getParent() == null || node.getParent() == node) {
			prj =  node.toNodeString();
			writer.write("Project is:[" + prj + "]\n");
			//STORE PRJ TO DB			
			
		}

		// Generate "currentNode -> Child" lines

		List<DependencyNode> children = node.getChildren();
		boolean out = true;
		for (Iterator<DependencyNode> it = children.iterator(); it.hasNext();) {
			String szFrom 	= node.toNodeString();
			String szTo 	= it.next().toNodeString();

			StringBuffer sb = new StringBuffer();
			sb.append(szFrom);
			sb.append(" requires ");
			sb.append(szTo);
			sb.append("\n");
			writer.println(sb.toString());
			
			//STORE SZFROM TO DB
			//STORE SZTO TO DB
			//STORE PRJ-FROM-TO TO DB
			
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
