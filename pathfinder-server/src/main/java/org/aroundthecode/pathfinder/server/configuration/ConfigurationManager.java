package org.aroundthecode.pathfinder.server.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Class to wrap properties configuration files access
 * @author msacchetti
 *
 */
public class ConfigurationManager {

	private static final String PATHFINDER_PATH = "pathfinder.path";
	private static final String PATHFINDER_PORT = "pathfinder.port";
	private static final String PATHFINDER_HOST = "pathfinder.host";
	private static final String PATHFINDER_PROTOCOL = "pathfinder.protocol";
	private static final String PATHFINDER_NEO4J_DB_ENABLE = "pathfinder.neo4j.db.enable";
	private static final String PATHFINDER_NEO4J_DB_PATH = "pathfinder.neo4j.db.path";
	private static final String PATHFINDER_NEO4J_DB_PORT = "pathfinder.neo4j.db.port";
	private static final String PATHFINDER_NEO4J_DB_HOST = "pathfinder.neo4j.db.host";
	private static final String CONFIG = "config/pathfinder.properties";
	private final static Properties p = new Properties();
	private static final Logger log = LogManager.getLogger(ConfigurationManager.class.getName());


	private ConfigurationManager() {
		throw new IllegalAccessError("Utility class");
	}

	static{
		try {
			InputStream is = ConfigurationManager.class.getClassLoader().getResourceAsStream(CONFIG);
			p.load(is);
			log.info("Loaded [{}]",CONFIG);
		} catch (IOException e) {
			log.error(e.getMessage());
			log.error("Error loading [{}]",CONFIG);
		}
	}

	/**
	 * @return pathfinder.protocol value
	 */
	public static String getPathfinderProtocol(){
		return getConfig(PATHFINDER_PROTOCOL);
	}

	/**
	 * @return pathfinder.host value
	 */
	public static String getPathfinderHost(){
		return getConfig(PATHFINDER_HOST);
	}

	/**
	 * @return pathfinder.port value
	 */
	public static int getPathfinderPort(){
		return Integer.parseInt( getConfig(PATHFINDER_PORT) );
	}

	/**
	 * @return pathfinder.path value
	 */
	public static String getPathfinderPath(){
		return getConfig(PATHFINDER_PATH);
	}

	/**
	 * @return pathfinder.neo4j.db.host value
	 */
	public static String getNeo4jDbHost(){
		return getConfig(PATHFINDER_NEO4J_DB_HOST);
	}

	/**
	 * @return pathfinder.neo4j.db.port value
	 */
	public static String getNeo4jDbPort(){
		return getConfig(PATHFINDER_NEO4J_DB_PORT);
	}

	/**
	 * @return pathfinder.neo4j.db.path value
	 */
	public static String getNeo4jDbPath(){
		return getConfig(PATHFINDER_NEO4J_DB_PATH);
	}

	/**
	 * @return pathfinder.neo4j.db.enable value
	 */
	public static Boolean isNeo4jDbEnable(){
		return Boolean.valueOf( getConfig(PATHFINDER_NEO4J_DB_ENABLE) );
	}

	protected static String getConfig(String key){
		return p.getProperty(key);
	}

}
