package org.aroundthecode.pathfinder.server.repository;

import org.aroundthecode.pathfinder.server.dao.MavenArtifactDao;
import org.aroundthecode.pathfinder.server.entity.MavenArtifact;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MavenArtifactRepository extends CrudRepository<MavenArtifact, MavenArtifactDao>, PagingAndSortingRepository<MavenArtifact, MavenArtifactDao>  {

	MavenArtifact findByMavenArtifactDao(MavenArtifactDao mavenArtifactDao);
	
    Iterable<MavenArtifact> findByDependenciesMavenArtifactDao(MavenArtifactDao mavenArtifactDao);

 // TODO: Replace with @Param("name") when Spring Data Neo4j supports names vs. positional arguments
//    List<MavenArtifact> findByLastName(@Param("0") String name);
    
}