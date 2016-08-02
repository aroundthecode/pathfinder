package org.aroundthecode.pathfinder.server.entity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aroundthecode.pathfinder.client.rest.utils.ArtifactUtils;
import org.aroundthecode.pathfinder.client.rest.utils.ArtifactUtils.Dependency;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

/**
 * Neo4j Node Entity to map a maven artifact
 * @author msacchetti
 *
 */
@NodeEntity
public class Artifact {

	@GraphId Long id;
	private String uniqueId;
	@Fetch
	private String groupId = "";
	@Fetch
	private String artifactId = "";
	@Fetch
	private String packaging="jar";
	@Fetch
	private String classifier="";
	@Fetch
	private String version = "";
	@Fetch
	private Long timestamp = 1l;
	

	/**
	 * Relation for COMPILE scope
	 */
	@RelatedTo(type="COMPILE", direction=Direction.INCOMING)
	@Fetch public Set<Artifact> dependenciesCompile= new HashSet<Artifact>();

	/**
	 * Relation for PROVIDED scope
	 */
	@RelatedTo(type="PROVIDED", direction=Direction.INCOMING)
	@Fetch public Set<Artifact> dependenciesProvided= new HashSet<Artifact>();

	/**
	 * Relation for RUNTIME scope
	 */
	@RelatedTo(type="RUNTIME", direction=Direction.INCOMING)
	@Fetch public Set<Artifact> dependenciesRuntime= new HashSet<Artifact>();

	/**
	 * Relation for TEST scope
	 */
	@RelatedTo(type="TEST", direction=Direction.INCOMING)
	@Fetch public Set<Artifact> dependenciesTest= new HashSet<Artifact>();

	/**
	 * Relation for SYSTEM scope
	 */
	@RelatedTo(type="SYSTEM", direction=Direction.INCOMING)
	@Fetch public  Set<Artifact> dependenciesSystem= new HashSet<Artifact>();

	/**
	 * Relation for IMPORT scope
	 */
	@RelatedTo(type="IMPORT", direction=Direction.INCOMING)
	@Fetch public Set<Artifact> dependenciesImport= new HashSet<Artifact>();

	/**
	 * PARENT Relation
	 */
	@RelatedTo(type="PARENT", direction=Direction.OUTGOING)
	@Fetch public Artifact parentArtifact = null;

	/**
	 * Empty constructor, just for Spring Data
	 */
	public Artifact() {
		//nothing to do here, just neo4j compatibility 
	}

	/**
	 * Maps a generic Neo4j node to class Artifact via <b>uniqueId</b> attribute
	 * @param n Neo4j node with <b>uniqueId</b> attribute
	 */
	public Artifact(Node n){
		this(n.getProperty("uniqueId").toString());
	}

	/**
	 * Init class Artifact via <b>uniqueId</b> attribute
	 * @param uniqueId String representing <b>uniqueId</b> attribute, pattern groupId:artifacId:packaging:classifier:version
	 */
	public Artifact(String uniqueId) {
		setUniqueId(uniqueId);
		setTimestamp( System.currentTimeMillis() );
	}

	/**
	 * Initialize class Artifact via all maven artifact attribute
	 * @param groupId maven groupId string
	 * @param artifactId maven artifactId string
	 * @param version maven version string
	 * @param type maven type string
	 * @param classifier maven classifier string
	 */
	public Artifact(String groupId,String artifactId, String version,String type, String classifier) {
		setGroupId(groupId);
		setArtifactId(artifactId);
		setPackaging(type);
		setClassifier(classifier);
		setVersion(version);
		setTimestamp( System.currentTimeMillis() );
	}

	/**
	 * Setter for uniqueId attribute
	 * @param uniqueId String representing <b>uniqueId</b> attribute, pattern groupId:artifacId:packaging:classifier:version
	 */
	public void setUniqueId(String uniqueId) 
	{
		Map<String, String> map = ArtifactUtils.splitUniqueId(uniqueId);
		if(map.size()>0){
			setGroupId(		map.get(ArtifactUtils.G));
			setArtifactId(	map.get(ArtifactUtils.A));
			setPackaging(	map.get(ArtifactUtils.P));
			setClassifier(	map.get(ArtifactUtils.C));
			setVersion(		map.get(ArtifactUtils.V));
		}
	}

	/**
	 * Getter for parent artifact
	 * @return parent artifact
	 */
	public Artifact getParent(){
		return parentArtifact;
	}

	/**
	 * Setter for parent artifact
	 * @param a parent Artifact NodeEntity 
	 */
	public void hasParent(Artifact a){
		this.parentArtifact = a;
	}

	/**
	 * Add given node as current node dependency
	 * @param a Dependency node
	 * @param type Dependency type
	 */
	public void dependsOn(Artifact a,String type) {

		switch (Dependency.valueOf(type)) {
		case PROVIDED:
			dependenciesProvided.add(a);
			break;
		case RUNTIME:
			dependenciesRuntime.add(a);
			break;
		case TEST:
			dependenciesTest.add(a);
			break;
		case SYSTEM:
			dependenciesSystem.add(a);
			break;
		case IMPORT:
			dependenciesImport.add(a);
			break;
		case COMPILE:
		default:
			dependenciesCompile.add(a);
			break;
		}
	}

	/**
	 * Getter for groupId attribute
	 * @return groupId attribute
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * Setter for groupId attribute
	 * @param groupId attribute value
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
		this.uniqueId = getUniqueId();
	}

	/**
	 * Getter for artifactId attribute
	 * @return artifactId attribute
	 */
	public String getArtifactId() {
		return artifactId;
	}

	/**
	 * Setter for artifactId attribute
	 * @param artifactId attribute value
	 */
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
		this.uniqueId = getUniqueId();
	}

	/**
	 * Getter for version attribute
	 * @return version attribute
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Setter for version attribute
	 * @param version attribute value
	 */
	public void setVersion(String version) {
		this.version = version;
		this.uniqueId = getUniqueId();
	}

	/**
	 * Getter for packaging attribute
	 * @return packaging attribute
	 */
	public String getPackaging() {
		return packaging;
	}

	/**
	 * Setter for packaging attribute
	 * @param packaging attribute value
	 */
	public void setPackaging(String packaging) {
		this.packaging = packaging;
		this.uniqueId = getUniqueId();
	}

	/**
	 * Getter for classifier attribute
	 * @return classifier attribute
	 */
	public String getClassifier() {
		return classifier;
	}

	/**
	 * Setter for classifier attribute
	 * @param classifier attribute value
	 */
	public void setClassifier(String classifier) {
		this.classifier = classifier;
		this.uniqueId = getUniqueId();
	}

	/**
	 * Getter for uniqueId attribute
	 * @return uniqueId attribute
	 */
	public String getUniqueId(){
		this.uniqueId = ArtifactUtils.getUniqueId(getGroupId(), getArtifactId(), getPackaging(), getClassifier(), getVersion());
		return this.uniqueId;
	}

	/**
	 * @return the timestamp
	 */
	public Long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * hashCode method
	 */
	@Override
	public int hashCode() {
		return id == null ? System.identityHashCode(this) : id.hashCode();
	}

	/**
	 * Return the JSON representation of the Artifact
	 * @return JSONObject representing Artifact
	 */
	@SuppressWarnings("unchecked")
	public JSONObject toJSON(){
		JSONObject o = new JSONObject();
		o.put(ArtifactUtils.U, getUniqueId());
		o.put(ArtifactUtils.G, getGroupId());
		o.put(ArtifactUtils.A, getArtifactId());
		o.put(ArtifactUtils.V, getVersion());
		o.put(ArtifactUtils.P, getPackaging());
		o.put(ArtifactUtils.C, getClassifier());
		
		o.put(ArtifactUtils.T, getTimestamp().toString());

		if(parentArtifact!=null && parentArtifact.getUniqueId()!=null){
			o.put(ArtifactUtils.PN, parentArtifact.getUniqueId());
		}

		JSONObject od =  new JSONObject();
		JSONArray d = new JSONArray();
		for (Artifact a : dependenciesCompile) {
			d.add( a.getUniqueId());
		}
		od.put(Dependency.COMPILE.toString(), d.clone());

		d.clear();
		for (Artifact a : dependenciesImport) {
			d.add( a.getUniqueId());
		}
		od.put(Dependency.IMPORT.toString(), d.clone());

		d.clear();
		for (Artifact a : dependenciesProvided) {
			d.add( a.getUniqueId());
		}
		od.put(Dependency.PROVIDED.toString(), d.clone());

		d.clear();
		for (Artifact a : dependenciesRuntime) {
			d.add( a.getUniqueId());
		}
		od.put(Dependency.RUNTIME.toString(), d.clone());

		d.clear();
		for (Artifact a : dependenciesSystem) {
			d.add( a.getUniqueId());
		}
		od.put(Dependency.SYSTEM.toString(), d.clone());

		d.clear();
		for (Artifact a : dependenciesTest) {
			d.add( a.getUniqueId());
		}
		od.put(Dependency.TEST.toString(), d.clone());

		o.put(ArtifactUtils.D, od);

		return o;
	}

	@Override
	public String toString() {
		return toJSON().toString();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;

		if (! (other instanceof Artifact)) 
			return false;

		return this.getUniqueId().equals(((Artifact)other).getUniqueId());
	}

	/**
	 * Convert Json object to Artifact Node
	 * @param o JSON representation of an artifact node
	 * @return Artifact Node
	 */
	public static Artifact parse(JSONObject o ){
		Artifact a = new Artifact(
				o.get(ArtifactUtils.G).toString(),
				o.get(ArtifactUtils.A).toString(),
				o.get(ArtifactUtils.V).toString(),
				o.get(ArtifactUtils.P).toString(),
				o.get(ArtifactUtils.C).toString()
				);
		
		if( o.get(ArtifactUtils.T) != null ){
			a.setTimestamp( Long.valueOf( o.get(ArtifactUtils.T).toString() ));
		}
		else{
			a.setTimestamp( Long.valueOf( Long.toString(System.currentTimeMillis()) ));
		}
		
		String pn = "" + o.get(ArtifactUtils.PN);
		if( pn != ""){
			a.hasParent( new Artifact(pn));
		}

		JSONObject deps = (JSONObject) o.get(ArtifactUtils.D);
		if(deps!=null){
			for (Dependency dep : Dependency.values()) 
			{
				JSONArray d = (JSONArray) deps.get(dep.toString());
				for(int i = 0; i< d.size(); i++ )
				{
					String uid = d.get(i).toString();
					Artifact da = new Artifact(uid);
					a.dependsOn(da, dep.name());
				}
			}
		}
		return a;
	}

}
