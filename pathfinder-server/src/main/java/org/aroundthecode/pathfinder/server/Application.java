package org.aroundthecode.pathfinder.server;

import java.io.File;

import org.aroundthecode.pathfinder.server.configuration.ConfigurationManager;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.io.fs.FileUtils;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.ServerConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;

@SuppressWarnings("deprecation")
@SpringBootApplication
public class Application extends SpringBootServletInitializer implements CommandLineRunner {

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
			logger.error(e);
		}
		// end of Neo4j browser config

	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	public static void main(String[] args) throws Exception {
		FileUtils.deleteRecursively(new File(ConfigurationManager.getNeo4jDbPath()));
		SpringApplication.run(Application.class, args);
	}

}
