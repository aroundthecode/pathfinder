package org.aroundthecode.pathfinder.server;

import java.io.File;

import org.aroundthecode.pathfinder.server.configuration.ConfigurationManager;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.io.fs.FileUtils;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.ServerConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;

@SuppressWarnings("deprecation")
@SpringBootApplication
public class Application extends Neo4jConfiguration implements CommandLineRunner {

	@Configuration
	@EnableNeo4jRepositories(basePackages = "org.aroundthecode.pathfinder.server")
	static class ApplicationConfig extends Neo4jConfiguration {

		public ApplicationConfig() {
			setBasePackage("org.aroundthecode.pathfinder.server");
		}

		@Bean(destroyMethod = "shutdown")
		GraphDatabaseService graphDatabaseService() {
			return new GraphDatabaseFactory().newEmbeddedDatabase(ConfigurationManager.getNeo4jDbPath());
		}
	}

	@Autowired
	GraphDatabaseService db;

	@Override
	public void run(String... args) throws Exception {

		// used for Neo4j browser
		try {
			WrappingNeoServerBootstrapper neoServerBootstrapper;
			GraphDatabaseAPI api = (GraphDatabaseAPI) db;

			ServerConfigurator config = new ServerConfigurator(api);
			config.configuration().addProperty(Configurator.WEBSERVER_ADDRESS_PROPERTY_KEY, ConfigurationManager.getNeo4jDbHost());
			config.configuration().addProperty(Configurator.WEBSERVER_PORT_PROPERTY_KEY, ConfigurationManager.getNeo4jDbPort());

			neoServerBootstrapper = new WrappingNeoServerBootstrapper(api, config);
			neoServerBootstrapper.start();
		} catch(Exception e) {
			e.printStackTrace();
		}
		// end of Neo4j browser config

	}

	public static void main(String[] args) throws Exception {
		FileUtils.deleteRecursively(new File(ConfigurationManager.getNeo4jDbPath()));

		SpringApplication.run(Application.class, args);
	}

}
