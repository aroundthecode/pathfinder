package org.aroundthecode.pathfinder.server.dao;

import java.io.Serializable;


public class MavenArtifactDao implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7845235035697519592L;

	private String groupId;
	private String artifactId;
	private String version;
	private String type;
	private String classifier;

	public MavenArtifactDao(String groupId,String artifactId,String version,String type,String classifier) { 
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.type = type;
		this.classifier = classifier;
	}

	public MavenArtifactDao(String groupId,String artifactId,String version) { 
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.type = "jar";
		this.classifier = "";
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

	@Override
	public String toString(){
    	return getGroupId() + ":" +
    			getArtifactId() + ":" + 
    			getVersion() + ":" + 
    			getType() + (getClassifier().length()!=0?(":" + 
    			getClassifier()):"");
    }
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
	         return true;
		
		if (! (other instanceof MavenArtifactDao)) 
	         return false;
		
		return this.toString().equals(((MavenArtifactDao)other).toString());
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
