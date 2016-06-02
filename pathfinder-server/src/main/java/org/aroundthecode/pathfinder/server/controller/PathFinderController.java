package org.aroundthecode.pathfinder.server.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aroundthecode.pathfinder.client.rest.items.FilterItem;
import org.aroundthecode.pathfinder.client.rest.utils.ArtifactUtils;
import org.aroundthecode.pathfinder.client.rest.utils.RestUtils;
import org.aroundthecode.pathfinder.server.crawler.CrawlerWrapper;
import org.aroundthecode.pathfinder.server.entity.Artifact;
import org.aroundthecode.pathfinder.server.repository.ArtifactRepository;
import org.aroundthecode.pathfinder.server.utils.QueryUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
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

	@Autowired
	GraphDatabaseService db;

	private static final Logger log = LogManager.getLogger(PathFinderController.class.getName());

	@RequestMapping(value="/cypher/query", method=RequestMethod.POST)
	public String doQuery(@RequestBody String body) throws ParseException 
	{
		JSONObject o = RestUtils.string2Json(body);
		String query = o.get("q").toString();

		String rows = "";

		try ( Transaction ignored = db.beginTx();
				Result result = db.execute( query ) )
				{
			while ( result.hasNext() )
			{
				Map<String,Object> row = result.next();

				Node n1 = (Node) row.get("n");
				Node n2 = (Node) row.get("n2");
				String r = (String) row.get("rel");
				rows += n1.getProperty("uniqueId") + " - " + r + " - " + n2.getProperty("uniqueId") + "\n";
			}
				}
		return rows;
	}

	@RequestMapping(value="/query/filterall", method=RequestMethod.GET)
	public JSONArray doFilterAll(
			@RequestParam(value="gn1", defaultValue=".*") String filterGN1,
			@RequestParam(value="an1", defaultValue=".*") String filterAN1,
			@RequestParam(value="pn1", defaultValue=".*") String filterPN1,
			@RequestParam(value="cn1", defaultValue=".*") String filterCN1,
			@RequestParam(value="vn1", defaultValue=".*") String filterVN1,
			@RequestParam(value="gn2", defaultValue=".*") String filterGN2,
			@RequestParam(value="an2", defaultValue=".*") String filterAN2,
			@RequestParam(value="pn2", defaultValue=".*") String filterPN2,
			@RequestParam(value="cn2", defaultValue=".*") String filterCN2,
			@RequestParam(value="vn2", defaultValue=".*") String filterVN2
			) throws ParseException 
	{
		FilterItem f = new FilterItem(filterGN1, filterAN1, filterPN1, filterCN1, filterVN1, filterGN2, filterAN2, filterPN2, filterCN2, filterVN2);
		String query = QueryUtils.getFilterAllQuery(f);

		JSONArray out = doNodeRelationNodeQuery(query);
		return out;
	}

	@RequestMapping(value="/query/impact", method=RequestMethod.GET)
	public JSONArray doImpact(
			@RequestParam(value="d", defaultValue="2") int depth, 
			@RequestParam(value="g") String groupId, 
			@RequestParam(value="a") String artifactId, 
			@RequestParam(value="p") String packaging, 
			@RequestParam(value="c") String classifier, 
			@RequestParam(value="v") String version,
			@RequestParam(value="gn1", defaultValue=".*") String filterGN1,
			@RequestParam(value="an1", defaultValue=".*") String filterAN1,
			@RequestParam(value="pn1", defaultValue=".*") String filterPN1,
			@RequestParam(value="cn1", defaultValue=".*") String filterCN1,
			@RequestParam(value="vn1", defaultValue=".*") String filterVN1,
			@RequestParam(value="gn2", defaultValue=".*") String filterGN2,
			@RequestParam(value="an2", defaultValue=".*") String filterAN2,
			@RequestParam(value="pn2", defaultValue=".*") String filterPN2,
			@RequestParam(value="cn2", defaultValue=".*") String filterCN2,
			@RequestParam(value="vn2", defaultValue=".*") String filterVN2
			) throws ParseException 
	{
		FilterItem f = new FilterItem(filterGN1, filterAN1, filterPN1, filterCN1, filterVN1, filterGN2, filterAN2, filterPN2, filterCN2, filterVN2);
		String query = QueryUtils.getImpactQuery(depth, groupId, artifactId, packaging, classifier, version, f);

		JSONArray out = doNodeRelationNodeQuery(query);
		return out;
	}

	/**
	 * execute a Cypher query expecting a node-relation-node columns result to map to JSONArray
	 * @param query Cypher query 
	 * @return JSONArray with query result
	 */
	@SuppressWarnings("unchecked")
	private JSONArray doNodeRelationNodeQuery(String query) {
		JSONArray out = new JSONArray();
		log.info("QUERY: [{}]",query);
		try ( Transaction ignored = db.beginTx();
				Result result = db.execute( query ) )
				{
			while ( result.hasNext() )
			{
				JSONObject o = new JSONObject();
				Map<String,Object> row = result.next();

				Artifact a1 = new Artifact((Node) row.get("node1"));
				Artifact a2 = new Artifact((Node) row.get("node2"));

				o.put("r", (String) row.get("rel"));
				o.put("n1", a1.toJSON());
				o.put("n2", a2.toJSON());

				out.add(o);
			}
				}
		return out;
	}


	@RequestMapping(value="/node/get", method=RequestMethod.GET)
	public Artifact getArtifact(@RequestParam(value="id", defaultValue=ArtifactUtils.EMPTYID) String uniqueId) 
	{
		return artifactRepository.findByUniqueId(uniqueId);
	}

	@RequestMapping(value="/node/parent", method=RequestMethod.POST)
	public void parent(@RequestBody String body) throws ParseException 
	{
		JSONObject o = RestUtils.string2Json(body);
		Artifact main = new Artifact(o.get("main").toString());
		Artifact parent = new Artifact(o.get("parent").toString());
		main = checkAndSaveArtifact(main);
		parent = checkAndSaveArtifact(parent);
		main.hasParent(parent);
		saveArtifact(main);
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
		saveArtifact(aFrom);

	}

	private void saveArtifact(Artifact a) {
		Transaction tx = graphDatabase.beginTx();
		try {
			artifactRepository.save(a);
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

	@RequestMapping(value="/crawler/crawl", method=RequestMethod.POST)
	public JSONObject crawlArtifact(@RequestBody String body) throws ParseException, UnsupportedEncodingException 
	{
		String uid = URLDecoder.decode(body, "UTF-8");
		uid = uid.substring(0, uid.lastIndexOf("="));
		log.debug("Request body:[{}]",uid);

		Map<String, String> map = ArtifactUtils.splitUniqueId(uid);
		return CrawlerWrapper.crawl(
				map.get(ArtifactUtils.G),
				map.get(ArtifactUtils.A), 
				map.get(ArtifactUtils.P), 
				map.get(ArtifactUtils.C), 
				map.get(ArtifactUtils.V)
				);
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
