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
		if(uniqueId!=null)
		{
			String[] tokens = uniqueId.split(":");
			if(tokens.length >= 4){
				setGroupId(tokens[0]);
				setArtifactId(tokens[1]);
				setPackaging(tokens[2]);
				setVersion(tokens[3]);
			}
			if(tokens.length == 5){
				setClassifier(tokens[3]);
				setVersion(tokens[4]);

			} 
			this.uniqueId = getUniqueId();
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
		//https://maven.apache.org/pom.html
		//groupId:artifactId:packaging:classifier:version
		return getGroupId() + ":" +
		getArtifactId() + ":" + 
		getPackaging() + ":" + 
		getClassifier() + ":" +
		getVersion() ;
	}

	@Override
	public int hashCode() {
		return id == null ? System.identityHashCode(this) : id.hashCode();
	}

	@Override
	public String toString() {
		String results = uniqueId + "'s dependencies include\n";
		for (Artifact a : dependenciesCompile) {
			results += "\t- " + a.getUniqueId() + " [compile]\n";
		}
		for (Artifact a : dependenciesProvided) {
			results += "\t- " + a.getUniqueId() + " [provided]\n";
		}
		for (Artifact a : dependenciesRuntime) {
			results += "\t- " + a.getUniqueId() + " [runtime]\n";
		}
		for (Artifact a : dependenciesTest) {
			results += "\t- " + a.getUniqueId() + " [test]\n";
		}
		for (Artifact a : dependenciesSystem) {
			results += "\t- " + a.getUniqueId() + " [system]\n";
		}
		for (Artifact a : dependenciesImport) {
			results += "\t- " + a.getUniqueId() + " [import]\n";
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
