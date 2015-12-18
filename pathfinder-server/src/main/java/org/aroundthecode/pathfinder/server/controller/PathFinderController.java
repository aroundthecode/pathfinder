package org.aroundthecode.pathfinder.server.controller;

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
	public Artifact getArtifact(@RequestParam(value="id", defaultValue=Artifact.EMPTYID) String uniqueId) 
	{
		return artifactRepository.findByUniqueId(uniqueId);
	}

	@RequestMapping(value="/node/depends", method=RequestMethod.POST)
	public void depends(@RequestParam("uid_from") String uidFrom,@RequestParam("uid_to") String uidTo) throws ParseException 
	{
		//for dependency uid last token is dependency type
		String type = uidTo.substring(uidTo.lastIndexOf(":")+1);
		uidTo = uidTo.substring(0,uidTo.lastIndexOf(":"));
		
		Artifact aFrom = new Artifact(uidFrom);
		Artifact aTo = new Artifact(uidTo);
		checkAndSaveArtifact(aFrom);
		checkAndSaveArtifact(aTo);
		aFrom.dependsOn(aTo,type);
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
		Artifact a = new Artifact(
				o.get(Artifact.G).toString(),
				o.get(Artifact.A).toString(),
				o.get(Artifact.V).toString(),
				o.get(Artifact.P).toString(),
				o.get(Artifact.C).toString()
				);

		return checkAndSaveArtifact(a);
	}

	private Artifact checkAndSaveArtifact(Artifact a) {
		Transaction tx = graphDatabase.beginTx();
		try {

			String uniqueId = a.getUniqueId();
			a = artifactRepository.findByUniqueId(uniqueId);

			if(a==null){
				a = new Artifact(uniqueId);
				artifactRepository.save(a);
			}

			tx.success();
		} finally {
			tx.close();
		}
		return a;
	}




}
