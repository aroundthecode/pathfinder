package org.aroundthecode.pathfinder.server.repository;

import org.aroundthecode.pathfinder.server.entity.Artifact;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "artifacts", path = "artifacts")
public interface ArtifactRepository extends CrudRepository<Artifact, String>, PagingAndSortingRepository<Artifact, String>  {

	Artifact findByUniqueId(@Param("0")String uniqueId);
	
    Iterable<Artifact> findByDependenciesUniqueId(String uniqueId);

}