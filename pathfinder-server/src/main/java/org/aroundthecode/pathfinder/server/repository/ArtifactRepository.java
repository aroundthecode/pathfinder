package org.aroundthecode.pathfinder.server.repository;

import java.util.Map;

import org.aroundthecode.pathfinder.server.entity.Artifact;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "artifacts", path = "artifacts")
public interface ArtifactRepository extends CrudRepository<Artifact, String>, PagingAndSortingRepository<Artifact, String>  {

	Artifact findByUniqueId(@Param("0")String uniqueId);
	
	Iterable<Artifact> findByDependenciesCompileUniqueId(String uniqueId);
	Iterable<Artifact> findByDependenciesProvidedUniqueId(String uniqueId);
	Iterable<Artifact> findByDependenciesRuntimeUniqueId(String uniqueId);
	Iterable<Artifact> findByDependenciesTestUniqueId(String uniqueId);
	Iterable<Artifact> findByDependenciesSystemUniqueId(String uniqueId);
	Iterable<Artifact> findByDependenciesImportUniqueId(String uniqueId);
	
	 // returns the nodes which have a title according to the movieTitle parameter
    @Query("MATCH n-[r]->n2 with n, [type(r), n2] as relative WHERE "
    		+ "n2.groupId =~ \".*\" AND "
    		+ "n2.artifactId =~ \".*\" AND "
    		+ "n2.packaging =~ \".*\" AND "
    		+ "n2.classifier =~ \".*\" AND "
    		+ "n2.version =~ \".*\" AND "
    		+ "n.groupId =~ \".*\" AND "
    		+ "n.artifactId =~ \".*\" AND "
    		+ "n.packaging =~ \".*\" AND "
    		+ "n.classifier =~ \".*\" AND "
    		+ "n.version =~ \".*\" "
    		+ "return { root: n, relatives: collect(relative) }")
    Iterable<Map<String,Object>> getFilteredRelations();

}