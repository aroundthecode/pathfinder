package org.aroundthecode.pathfinder.server.repository;

import org.aroundthecode.pathfinder.server.entity.Artifact;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository class to retrieve Artifact invormation via Spring Data
 * @author msacchetti
 *
 */
@RepositoryRestResource(collectionResourceRel = "artifacts", path = "artifacts")
public interface ArtifactRepository extends CrudRepository<Artifact, String>, PagingAndSortingRepository<Artifact, String>  {

	/**
	 * Get Artifact by its unique identifier groupId:artifacId:packaging:classifier:version
	 * @param uniqueId artifact unique ID
	 * @return
	 */
	Artifact findByUniqueId(@Param("0")String uniqueId);
	
	
	/**
	 * Get list of Artifact which have given artifact as COMPILE dependency
	 * @param uniqueId artifact unique ID groupId:artifacId:packaging:classifier:version
	 * @return
	 */
	Iterable<Artifact> findByDependenciesCompileUniqueId(String uniqueId);
	
	/**
	 * Get list of Artifact which have given artifact as PROVIDED dependency
	 * @param uniqueId artifact unique ID groupId:artifacId:packaging:classifier:version
	 * @return
	 */
	Iterable<Artifact> findByDependenciesProvidedUniqueId(String uniqueId);
	
	/**
	 * Get list of Artifact which have given artifact as RUNTIME dependency
	 * @param uniqueId artifact unique ID groupId:artifacId:packaging:classifier:version	 
	 * @return
	 */
	Iterable<Artifact> findByDependenciesRuntimeUniqueId(String uniqueId);
	
	/**
	  * Get list of Artifact which have given artifact as TEST dependency
	 * @param uniqueId artifact unique ID groupId:artifacId:packaging:classifier:version
	 * @return
	 */
	Iterable<Artifact> findByDependenciesTestUniqueId(String uniqueId);
	
	/**
	  * Get list of Artifact which have given artifact as SYSTEM dependency
	 * @param uniqueId artifact unique ID groupId:artifacId:packaging:classifier:version
	 * @return
	 */
	Iterable<Artifact> findByDependenciesSystemUniqueId(String uniqueId);
	
	/**
	  * Get list of Artifact which have given artifact as IMPORT dependency
	 * @param uniqueId artifact unique ID groupId:artifacId:packaging:classifier:version
	 * @return
	 */
	Iterable<Artifact> findByDependenciesImportUniqueId(String uniqueId);
	

}