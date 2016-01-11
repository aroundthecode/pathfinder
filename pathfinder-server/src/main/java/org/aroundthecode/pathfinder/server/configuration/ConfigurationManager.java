package org.aroundthecode.pathfinder.server.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class ConfigurationManager {
	
	private static final String PATHFINDER_PATH = "pathfinder.path";
	private static final String PATHFINDER_PORT = "pathfinder.port";
	private static final String PATHFINDER_HOST = "pathfinder.host";
	private static final String PATHFINDER_PROTOCOL = "pathfinder.protocol";
	private static final String PATHFINDER_NEO4J_DB_PATH = "pathfinder.neo4j.db.path";
	private static final String PATHFINDER_NEO4J_DB_PORT = "pathfinder.neo4j.db.port";
	private static final String PATHFINDER_NEO4J_DB_HOST = "pathfinder.neo4j.db.host";
	private static final String CONFIG = "config/pathfinder.properties";
	private final static Properties p = new Properties();
	private static final Logger log = LogManager.getLogger(ConfigurationManager.class.getName());
	
	static{
		try {
			InputStream is = ConfigurationManager.class.getClassLoader().getResourceAsStream(CONFIG);
			p.load(is);
			log.info("Loaded [{}]",CONFIG);
		} catch (IOException e) {
			log.error("Error loading [{}]",CONFIG);
		}
	}
	
	
	public static String getPathfinderProtocol(){
		return getConfig(PATHFINDER_PROTOCOL);
	}
	
	public static String getPathfinderHost(){
		return getConfig(PATHFINDER_HOST);
	}
	
	public static int getPathfinderPort(){
		return Integer.parseInt( getConfig(PATHFINDER_PORT) );
	}
	
	public static String getPathfinderPath(){
		return getConfig(PATHFINDER_PATH);
	}
	
	public static String getNeo4jDbHost(){
		return getConfig(PATHFINDER_NEO4J_DB_HOST);
	}
	
	public static String getNeo4jDbPort(){
		return getConfig(PATHFINDER_NEO4J_DB_PORT);
	}
	
	public static String getNeo4jDbPath(){
		return getConfig(PATHFINDER_NEO4J_DB_PATH);
	}
	
	public static String getConfig(String key){
		return p.getProperty(key);
	}

}
