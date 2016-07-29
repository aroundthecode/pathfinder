package org.aroundthecode.pathfinder.client.rest.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to manipulate Artifacts data
 * @author msacchetti
 *
 */
public class ArtifactUtils {

	/**
	 * Enum representation for dependencies types
	 */
	public enum Dependency {
		COMPILE, PROVIDED, RUNTIME, TEST,
		SYSTEM, IMPORT 
	}

	//groupId:artifactId:packaging:classifier:version
	public static final String EMPTYID = "::jar::";
	public static final String U = "uniqueId";
	public static final String G = "groupId";
	public static final String A = "artifactId";
	public static final String P = "packaging";
	public static final String C = "classifier";
	public static final String V = "version";
	
	public static final String PN = "parentNode";
	
	public static final String D = "dependencies";

	private ArtifactUtils() {
		throw new IllegalAccessError("Utility class");
	}
	
	public static final String getUniqueId(String groupId,String artifactId,String packaging,String classifier,String version){
		//https://maven.apache.org/pom.html
		//groupId:artifactId:packaging:classifier:version
		return groupId + ":" +
		artifactId + ":" + 
		packaging + ":" + 
		(classifier!=null?classifier:"") + ":" +
		version ;
	}

	public static final Map<String,String> splitUniqueId(String uniqueId){

		Map<String,String> out = new HashMap<String, String>();
		if(uniqueId!=null)
		{
			String[] tokens = uniqueId.split(":");
			if(tokens.length >= 4){
				out.put(G, tokens[0]);
				out.put(A, tokens[1]);
				out.put(P, tokens[2]);
				out.put(C, "");
				out.put(V, tokens[3]);
			}
			if(tokens.length == 5){
				out.put(C, tokens[3]);
				out.put(V, tokens[4]);

			} 
		}
		return out;

	}

}
