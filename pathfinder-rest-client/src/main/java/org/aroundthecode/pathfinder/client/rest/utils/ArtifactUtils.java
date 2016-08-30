package org.aroundthecode.pathfinder.client.rest.utils;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Utility class to manipulate Artifacts data
 * @author msacchetti
 *
 */
public final class ArtifactUtils {

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
	public static final String T = "timestamp";
	public static final String PN = "parentNode";

	public static final String D = "dependencies";

	private ArtifactUtils() {
	}

	/**
	 * Format all information in an unique identifier for Maven artifact.
	 * See <a href="https://maven.apache.org/pom.html">https://maven.apache.org/pom.html</a> for details
	 * @param groupId Artifact groupId
	 * @param artifactId Artifact artifactId
	 * @param packaging Artifact packaging
	 * @param classifier Artifact classifier
	 * @param version Artifact version
	 * @return A string in form of groupId:artifactId:packaging:classifier:version
	 */
	public static final String getUniqueId(String groupId,String artifactId,String packaging,String classifier,String version){
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

	/**
	 * Return the JSON representation of the Artifact
	 * @param uniqueId Artifact unique identifier
	 * @param timestamp timestamp to be used in json
	 * @param parentUniqueId parent Artifact unique identifier, null if not applicable
	 * @return JSONObject representing Artifact 
	 */
	public static JSONObject artifactJSON(String uniqueId, Long timestamp, String parentUniqueId){
		return artifactJSON(uniqueId, timestamp, parentUniqueId,new JSONArray(),new JSONArray(),new JSONArray(),new JSONArray(),new JSONArray(),new JSONArray());
	}

	/**
	 * Return the JSON representation of the Artifact
	 * @param uniqueId Artifact unique identifier
	 * @param timestamp timestamp to be used in json
	 * @param parentUniqueId parent Artifact unique identifier, null if not applicable
	 * @param dependenciesUniqueIdCompile JSONArray of unique identifier for COMPILE dependencies
	 * @param dependenciesUniqueIdImport JSONArray of unique identifier for IMPORT dependencies
	 * @param dependenciesUniqueIdProvided JSONArray of unique identifier for PROVIDED dependencies
	 * @param dependenciesUniqueIdRuntime JSONArray of unique identifier for RUNTIME dependencies
	 * @param dependenciesUniqueIdSystem JSONArray of unique identifier for SYSTEM dependencies
	 * @param dependenciesUniqueIdTest JSONArray of unique identifier for TEST dependencies
	 * @return JSONObject representing Artifact 
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject artifactJSON(String uniqueId, Long timestamp, String parentUniqueId
			,JSONArray dependenciesUniqueIdCompile
			,JSONArray dependenciesUniqueIdImport
			,JSONArray dependenciesUniqueIdProvided
			,JSONArray dependenciesUniqueIdRuntime
			,JSONArray dependenciesUniqueIdSystem
			,JSONArray dependenciesUniqueIdTest
			){

		Map<String, String> map = splitUniqueId(uniqueId);

		JSONObject o = new JSONObject();
		o.put(U, uniqueId);
		o.put(G, map.get(G) );
		o.put(A, map.get(A) );
		o.put(V, map.get(V) );
		o.put(P, map.get(P) );
		o.put(C, map.get(C) );

		o.put(ArtifactUtils.T, timestamp.toString());

		if(parentUniqueId!=null){
			o.put(PN, parentUniqueId);
		}

		JSONObject od =  new JSONObject();
		od.put(Dependency.COMPILE.toString(), 	dependenciesUniqueIdCompile		);
		od.put(Dependency.IMPORT.toString(), 	dependenciesUniqueIdImport		);
		od.put(Dependency.PROVIDED.toString(), 	dependenciesUniqueIdProvided	);
		od.put(Dependency.RUNTIME.toString(), 	dependenciesUniqueIdRuntime		);
		od.put(Dependency.SYSTEM.toString(), 	dependenciesUniqueIdSystem		);
		od.put(Dependency.TEST.toString(), 		dependenciesUniqueIdTest		);

		o.put(ArtifactUtils.D, od);

		return o;
	}



}
