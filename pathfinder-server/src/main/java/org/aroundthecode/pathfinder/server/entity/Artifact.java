package org.aroundthecode.pathfinder.server.entity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aroundthecode.pathfinder.client.rest.utils.ArtifactUtils;
import org.aroundthecode.pathfinder.client.rest.utils.ArtifactUtils.Dependency;
import org.json.simple.JSONObject;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;


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

	public Artifact() {
	}

	public Artifact(String uniqueId) {
		setUniqueId(uniqueId);
	}

	public Artifact(String groupId,String artifactId, String version,String type, String classifier) {
		setGroupId(groupId);
		setArtifactId(artifactId);
		setPackaging(type);
		setClassifier(classifier);
		setVersion(version);
	}

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

	@RelatedTo(type="COMPILE", direction=Direction.INCOMING)
	public @Fetch Set<Artifact> dependenciesCompile= new HashSet<Artifact>();

	@RelatedTo(type="PROVIDED", direction=Direction.INCOMING)
	public @Fetch Set<Artifact> dependenciesProvided= new HashSet<Artifact>();

	@RelatedTo(type="RUNTIME", direction=Direction.INCOMING)
	public @Fetch Set<Artifact> dependenciesRuntime= new HashSet<Artifact>();

	@RelatedTo(type="TEST", direction=Direction.INCOMING)
	public @Fetch Set<Artifact> dependenciesTest= new HashSet<Artifact>();

	@RelatedTo(type="SYSTEM", direction=Direction.INCOMING)
	public @Fetch Set<Artifact> dependenciesSystem= new HashSet<Artifact>();

	@RelatedTo(type="IMPORT", direction=Direction.INCOMING)
	public @Fetch Set<Artifact> dependenciesImport= new HashSet<Artifact>();

	@RelatedTo(type="PARENT", direction=Direction.OUTGOING)
	public @Fetch Artifact parentArtifact = null;

	public void hasParent(Artifact a){
		this.parentArtifact = a;
	}

	public void dependsOn(Artifact a,String type) {

		switch (Dependency.valueOf(type)) {
		default:
		case COMPILE:
			dependenciesCompile.add(a);
			break;
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
		}
	}

	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
		this.uniqueId = getUniqueId();
	}

	public String getArtifactId() {
		return artifactId;
	}
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
		this.uniqueId = getUniqueId();
	}

	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
		this.uniqueId = getUniqueId();
	}

	public String getPackaging() {
		return packaging;
	}
	public void setPackaging(String packaging) {
		this.packaging = packaging;
		this.uniqueId = getUniqueId();
	}

	public String getClassifier() {
		return classifier;
	}
	public void setClassifier(String classifier) {
		this.classifier = classifier;
		this.uniqueId = getUniqueId();
	}

	public String getUniqueId(){
		return ArtifactUtils.getUniqueId(getGroupId(), getArtifactId(), getPackaging(), getClassifier(), getVersion());
	}

	@Override
	public int hashCode() {
		return id == null ? System.identityHashCode(this) : id.hashCode();
	}

	@Override
	public String toString() {
		StringBuffer results = new StringBuffer( uniqueId ).append( "'s dependencies include\n");
		if(parentArtifact!=null){
			results.append( parentArtifact.getUniqueId()  ).append( " [parent]\n");
		}

		for (Artifact a : dependenciesCompile) {
			results.append("\t- " ).append( a.getUniqueId() ).append( " [compile]\n");
		}
		for (Artifact a : dependenciesProvided) {
			results.append("\t- " ).append( a.getUniqueId() ).append( " [provided]\n");
		}
		for (Artifact a : dependenciesRuntime) {
			results.append("\t- " ).append( a.getUniqueId() ).append( " [runtime]\n");
		}
		for (Artifact a : dependenciesTest) {
			results.append("\t- " ).append( a.getUniqueId() ).append( " [test]\n");
		}
		for (Artifact a : dependenciesSystem) {
			results.append("\t- " ).append( a.getUniqueId() ).append( " [system]\n");
		}
		for (Artifact a : dependenciesImport) {
			results.append("\t- " ).append( a.getUniqueId() ).append( " [import]\n");
		}
		return results.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;

		if (! (other instanceof Artifact)) 
			return false;

		return this.toString().equals(((Artifact)other).toString());
	}

	public static Artifact parsePropertiesFromJson(JSONObject o ){
		Artifact a = new Artifact(
				o.get(ArtifactUtils.G).toString(),
				o.get(ArtifactUtils.A).toString(),
				o.get(ArtifactUtils.V).toString(),
				o.get(ArtifactUtils.P).toString(),
				o.get(ArtifactUtils.C).toString()
				);
		//TODO manage dependencies too?
		return a;
	}

}
