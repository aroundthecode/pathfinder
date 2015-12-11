package org.aroundthecode.pathfinder.maven.plugin;

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

import org.apache.maven.plugin.dependency.tree.TreeMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;
import org.aroundthecode.pathfinder.maven.plugin.treeSerializers.LogNodeVisitor;

/**
 * This Mojo is an extension of base dependency:tree {@link TreeMojo}
 * Its purpose is to parse dependency tree and store all dependencies and their relationship on database
 * 
 * @author <a href="mailto:michele.sacchetti@gmail.com">Michele Sacchetti</a>
 * 
 * @extendsPlugin dependency
 * @extendsGoal tree
 * @goal store-tree
 * @phase compile
 * @requiresDependencyResolution test
 * 
 */
//@Mojo( name = "store-tree", defaultPhase = LifecyclePhase.COMPILE )

public class PathFinderMojo extends TreeMojo{
	
	/**
     * Neo4j rest URL.
     * @parameter property="neo4j.url" default-value="localhost"
     */
	private String neo4jUrl;
	
	/**
     * Neo4j db port
     * @parameter property="neo4j.port" default-value="8686"
     */
	private String neo4jPort;
	
	/**
	 * Override standard TreeMojo to provide only Pathfinder Visitors
	 */
	@Override
    public DependencyNodeVisitor getSerializingDependencyNodeVisitor( Writer writer )
    {
            return new LogNodeVisitor( writer,getLog() );
    }
	
}
