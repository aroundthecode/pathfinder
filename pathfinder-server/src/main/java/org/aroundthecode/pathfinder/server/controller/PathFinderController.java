package org.aroundthecode.pathfinder.server.controller;

import org.aroundthecode.pathfinder.server.entity.Artifact;
import org.aroundthecode.pathfinder.server.repository.ArtifactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PathFinderController {

	@Autowired ArtifactRepository artifactRepository;

	@Autowired GraphDatabase graphDatabase;
	
	@RequestMapping(value="/list/nodes", method=RequestMethod.GET)
    public Artifact getArtifact(@RequestParam(value="id", defaultValue=Artifact.EMPTYID) String uniqueId) 
	{
		System.out.println("here we go:["+uniqueId+"]");
        return artifactRepository.findByUniqueId(uniqueId);
    }
	
	
	
	
}
