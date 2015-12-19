package org.aroundthecode.pathfinder.server.controller;

import org.aroundthecode.pathfinder.client.rest.utils.ArtifactUtils;
import org.aroundthecode.pathfinder.client.rest.utils.RestUtils;
import org.aroundthecode.pathfinder.server.entity.Artifact;
import org.aroundthecode.pathfinder.server.repository.ArtifactRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PathFinderController {

	@Autowired ArtifactRepository artifactRepository;

	@Autowired GraphDatabase graphDatabase;

	@RequestMapping(value="/node/get", method=RequestMethod.GET)
	public Artifact getArtifact(@RequestParam(value="id", defaultValue=ArtifactUtils.EMPTYID) String uniqueId) 
	{
		return artifactRepository.findByUniqueId(uniqueId);
	}

	@RequestMapping(value="/node/depends", method=RequestMethod.POST)
	public void depends(@RequestBody String body) throws ParseException 
	{
		JSONObject o = RestUtils.string2Json(body);
		Artifact aFrom = new Artifact(o.get("from").toString());
		Artifact aTo = new Artifact(o.get("to").toString());
		aFrom = checkAndSaveArtifact(aFrom);
		aTo = checkAndSaveArtifact(aTo);
		aFrom.dependsOn(aTo,o.get("scope").toString());
		Transaction tx = graphDatabase.beginTx();
		try {
			artifactRepository.save(aFrom);
			tx.success();
		} finally {
			tx.close();
		}

	}

	@RequestMapping(value="/node/save", method=RequestMethod.POST)
	public Artifact saveArtifact(@RequestBody String body) throws ParseException 
	{
		JSONObject o = RestUtils.string2Json(body);
		Artifact a = Artifact.parsePropertiesFromJson(o);
		return checkAndSaveArtifact(a);
	}

	private Artifact checkAndSaveArtifact(Artifact a) {
		Transaction tx = graphDatabase.beginTx();
		try {

			String uniqueId = a.getUniqueId();
			a = artifactRepository.findByUniqueId(uniqueId);

			if(a==null){
				a = artifactRepository.save( new Artifact(uniqueId));
			}

			tx.success();
		} finally {
			tx.close();
		}
		return a;
	}




}
