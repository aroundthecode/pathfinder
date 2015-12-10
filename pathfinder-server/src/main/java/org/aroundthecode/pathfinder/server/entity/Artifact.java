package org.aroundthecode.pathfinder.server.entity;

import java.util.HashSet;
import java.util.Set;

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
	private String version = "";
	@Fetch
	private String type="jar";
	@Fetch
	private String classifier="";

	public Artifact() {
	}

	public Artifact(String uniqueId) {
		setUniqueId(uniqueId);
	}
	
	public void setUniqueId(String uniqueId) 
	{
		if(uniqueId!=null)
		{
			String[] tokens = uniqueId.split(":");
			if(tokens.length >= 4){
				setGroupId(tokens[0]);
				setArtifactId(tokens[1]);
				setVersion(tokens[2]);
				setType(tokens[3]);
			}
			if(tokens.length == 5){
				setClassifier(tokens[4]);

			} 
			this.uniqueId = getUniqueId();
		}
	}

	@RelatedTo(type="DEPENDENCY", direction=Direction.INCOMING)
	public @Fetch Set<Artifact> dependencies;

	public void dependsOn(Artifact a) {
		if (dependencies == null) {
			dependencies = new HashSet<Artifact>();
		}
		dependencies.add(a);
	}




	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getClassifier() {
		return classifier;
	}
	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}

	public String getUniqueId(){
		return getGroupId() + ":" +
				getArtifactId() + ":" + 
				getVersion() + ":" + 
				getType() + ":" + 
				getClassifier();
	}


	@Override
	public int hashCode() {
		return id == null ? System.identityHashCode(this) : id.hashCode();
	}

	@Override
	public String toString() {
		String results = uniqueId + "'s dependencies include\n";
		if (dependencies != null) {
			for (Artifact a : dependencies) {
				results += "\t- " + a.getUniqueId() + "\n";
			}
		}
		return results;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;

		if (! (other instanceof Artifact)) 
			return false;

		return this.toString().equals(((Artifact)other).toString());
	}



}
