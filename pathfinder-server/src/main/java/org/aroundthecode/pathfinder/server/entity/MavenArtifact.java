package org.aroundthecode.pathfinder.server.entity;

import java.util.HashSet;
import java.util.Set;

import org.aroundthecode.pathfinder.server.dao.MavenArtifactDao;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;


@NodeEntity
public class MavenArtifact {
	
	@GraphId Long id;
	
	@GraphProperty
	@Fetch
	public MavenArtifactDao mavenArtifactDao;

	public MavenArtifact() {
//		System.out.println("MavenArtifact() INVOKED!");
	}

	public MavenArtifact(MavenArtifactDao a) {
		this.setMavenArtifactDao(new MavenArtifactDao(a.getGroupId(),a.getArtifactId(),a.getVersion(),a.getType(),a.getClassifier()));
	}

	@RelatedTo(type="DEPENDENCY", direction=Direction.INCOMING)
	public @Fetch Set<MavenArtifact> dependencies;

	public void dependsOn(MavenArtifact a) {
		if (dependencies == null) {
			dependencies = new HashSet<MavenArtifact>();
		}
		dependencies.add(a);
	}

	@Override
	public boolean equals(Object other) {
		return getMavenArtifactDao().equals(other);
	}
	
	@Override
	public int hashCode() {
	      return id == null ? System.identityHashCode(this) : id.hashCode();
	   }
	
	@Override
	public String toString() {
		String results = getMavenArtifactDao().toString() + "'s dependencies include\n";
		if (dependencies != null) {
			for (MavenArtifact a : dependencies) {
				results += "\t- " + a.getMavenArtifactDao().toString() + "\n";
			}
		}
		return results;
	}

	public MavenArtifactDao getMavenArtifactDao() {
		return mavenArtifactDao;
	}

	public void setMavenArtifactDao(MavenArtifactDao mavenArtifactDao) {
		this.mavenArtifactDao = new MavenArtifactDao(mavenArtifactDao.getGroupId(),mavenArtifactDao.getArtifactId(),mavenArtifactDao.getVersion(),mavenArtifactDao.getType(),mavenArtifactDao.getClassifier());
	}

}
