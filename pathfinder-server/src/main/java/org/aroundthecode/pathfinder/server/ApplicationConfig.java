package org.aroundthecode.pathfinder.server;

import org.aroundthecode.pathfinder.server.configuration.ConfigurationManager;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;

@Configuration
@EnableNeo4jRepositories(basePackages = "org.aroundthecode.pathfinder.server")
public class ApplicationConfig extends Neo4jConfiguration {

	public ApplicationConfig() {
		setBasePackage("org.aroundthecode.pathfinder.server");
	}

	@Bean(destroyMethod = "shutdown")
	GraphDatabaseService graphDatabaseService() {
		return new GraphDatabaseFactory().newEmbeddedDatabase(ConfigurationManager.getNeo4jDbPath());
	}
}