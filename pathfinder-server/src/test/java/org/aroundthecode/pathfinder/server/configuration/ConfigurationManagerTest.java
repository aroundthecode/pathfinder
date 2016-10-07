package org.aroundthecode.pathfinder.server.configuration;

import static org.junit.Assert.*;

import org.junit.Test;

public class ConfigurationManagerTest {

	@Test
	public void testConfigurationManager() {
		
		assertNotNull(ConfigurationManager.getConfig("pathfinder.neo4j.db.port"));
		
		assertNotNull(ConfigurationManager.getNeo4jDbHost());
		assertNotNull(ConfigurationManager.getNeo4jDbPath());
		assertNotNull(ConfigurationManager.getNeo4jDbPort());
		assertNotNull(ConfigurationManager.getPathfinderHost());
		assertNotNull(ConfigurationManager.getPathfinderPath());
		assertNotNull(ConfigurationManager.getPathfinderPort());
		assertTrue(ConfigurationManager.getPathfinderPort()>0);
		assertNotNull(ConfigurationManager.getPathfinderProtocol());
		
	}

}
