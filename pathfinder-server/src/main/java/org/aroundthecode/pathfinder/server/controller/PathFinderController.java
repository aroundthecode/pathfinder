package org.aroundthecode.pathfinder.server.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * PathFinderController wraps Neo4J and Spring data to expose end user REST api
 * @author msacchetti
 *
 */
@RestController
public class PathFinderController {

	@Autowired ArtifactRepository artifactRepository;

	@Autowired GraphDatabase graphDatabase;

	@Autowired
	GraphDatabaseService db;

	private static final Logger log = LogManager.getLogger(PathFinderController.class.getName());

	/**
	 * Execute cypher query passed in post method as a json object with "q" key
	 * @param body. {"q":"cypher query goes here"}
	 * @return a String object, each line representing  nodeId-relationType-nodeId
	 * @throws ParseException
	 */
	@RequestMapping(value="/cypher/query", method=RequestMethod.POST)
	public String doQuery(@RequestBody String body) throws ParseException 
	{
		JSONObject o = RestUtils.string2Json(body);
		String query = o.get("q").toString();

		StringBuilder rows = new StringBuilder("");

		try ( Transaction ignored = db.beginTx();
				Result result = db.execute( query ) )
				{
			while ( result.hasNext() )
			{
				Map<String,Object> row = result.next();

				Node n1 = (Node) row.get("n");
				Node n2 = (Node) row.get("n2");
				String r = (String) row.get("rel");
				rows.append(n1.getProperty("uniqueId"))
					.append(" - ").append( r )
					.append(" - ").append( n2.getProperty("uniqueId"))
					.append("\n");
			}
				}
		return rows.toString();
	}

	/**
	 * Return the full graph with given filter apply
	 * @param filterGN1 inner nodes groupId filter
	 * @param filterAN1 inner nodes artifacId filter
	 * @param filterPN1 inner nodes package filter
	 * @param filterCN1 inner nodes classifier filter
	 * @param filterVN1 inner nodes version filter
	 * @param filterGN2 outer nodes groupId filter
	 * @param filterAN2 outer nodes artifacId filter
	 * @param filterPN2 outer nodes package filter
	 * @param filterCN2 outer nodes classifier filter
	 * @param filterVN2 outer nodes version filter
	 * @return JsonArray representation of the whole graph in form of node1 - relation - node2
	 * @throws ParseException
	 */
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
		return doNodeRelationNodeQuery(query);
	}

	/**
	 * * Return a graph representing all artifact depending from the main one within a maximum of <i>depth</i> hops 

	 * @param depth masimum number of note to traverse duing the analysis
	 * @param groupId main artifact groupId
	 * @param artifactId main artifact artifactId
	 * @param packaging main artifact packaging
	 * @param classifier main artifact classifier
	 * @param version main artifact version
	 * @param filterGN1 inner nodes groupId filter
	 * @param filterAN1 inner nodes artifacId filter
	 * @param filterPN1 inner nodes package filter
	 * @param filterCN1 inner nodes classifier filter
	 * @param filterVN1 inner nodes version filter
	 * @param filterGN2 outer nodes groupId filter
	 * @param filterAN2 outer nodes artifacId filter
	 * @param filterPN2 outer nodes package filter
	 * @param filterCN2 outer nodes classifier filter
	 * @param filterVN2 outer nodes version filter
	 * @return JsonArray representation of the whole graph in form of node1 - relation - node2
	 * @throws ParseException
	 */
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

		return doNodeRelationNodeQuery(query);
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


	/**
	 * Return a single artifact item given its unique ID
	 * @param uniqueId String representing <b>uniqueId</b> attribute, pattern groupId:artifacId:packaging:classifier:version
	 * @return Json Artifact representation
	 */
	@RequestMapping(value="/node/get", method=RequestMethod.GET)
	public Artifact getArtifact(@RequestParam(value="id", defaultValue=ArtifactUtils.EMPTYID) String uniqueId) 
	{
		return artifactRepository.findByUniqueId(uniqueId);
	}

	/**
	 * Set a PARENT relation between two Artifacts
	 * @param body Json with <b>main</b> and <b>parent</b> keys representing given artifacts unique IDs
	 * @throws ParseException
	 */
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

	/**
	 * Set a scoped relation between two Artifacts
	 * @param body Json with <b>from</b>,<b>to</b> and <b>scope</b> keys representing given artifacts unique IDs and relationship scope type
	 * @throws ParseException
	 */
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

	/**
	 * Internal method to save artifact 
	 * @param a Artifact to be stored
	 */
	private void saveArtifact(Artifact a) {
		Transaction tx = graphDatabase.beginTx();
		try {
			artifactRepository.save(a);
			tx.success();
		} finally {
			tx.close();
		}
	}

	/**
	 * <p>Save given Artifact to database.</p>
	 * <b>Note:</b> this method will consider only uniqueId attributes to save the base artifact data, to populate dependencies or parent see <i>depends</i> and <i>parent</i> methods
	 * @param body json representation of the Artifact
	 * @return Artifact item of saved object
	 * @throws ParseException if json is not parsable
	 */
	@RequestMapping(value="/node/save", method=RequestMethod.POST)
	public void saveArtifact(@RequestBody String body) throws ParseException 
	{
		JSONObject o = RestUtils.string2Json(body);
		Artifact a = Artifact.parse(o);
		checkAndSaveArtifact(a);
	}

	/**
	 * Invoke pathfinder-mave-plugin crawl goal over the give artifact
	 * @param body JSON containing artifact uniqueId
	 * @return JsonoObject containing maven Invoker execution details
	 * @throws ParseException if JSON is nor parsable
	 * @throws UnsupportedEncodingException if UniqueId URLDecode fails
	 */
	@RequestMapping(value="/crawler/crawl", method=RequestMethod.POST)
	public JSONObject crawlArtifact(@RequestBody String body) throws ParseException, UnsupportedEncodingException 
	{
		String uid = URLDecoder.decode(body, "UTF-8");
		uid = uid.substring(0, uid.lastIndexOf('='));
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


	/**
	 * Generate a list of JSONObject for the whole Artifact database to be downloaded as a file
	 * @return download Json file as attachment
	 * @throws IOException
	 */
	@RequestMapping(value="/node/download", method=RequestMethod.GET )
	public HttpEntity<byte[]>  downloadNodes() throws IOException {

		Iterable<Artifact> all = null;

		StringBuilder sb = new StringBuilder("[");
		try(Transaction tx = graphDatabase.beginTx();) 
		{
			all = artifactRepository.findAll();
			tx.success();
			boolean skip=true;
			if(all==null){
				all = Collections.<Artifact> emptyList();
			}
			for (Artifact artifact : all) {
				if(skip){
					skip=false;
				}
				else{
					sb.append(",");
				}
				sb.append(artifact.toJSON());
			}
		} 
		sb.append("]");

		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "pdf"));
		header.set("Content-Disposition", "attachment; filename=pathfinder.json" );
		header.setContentLength(sb.length());

		return new HttpEntity<>(sb.toString().getBytes(),header);
	}



	private Artifact checkAndSaveArtifact(Artifact a) {
		Artifact out = null;
		try(Transaction tx = graphDatabase.beginTx();)
		{

			String uniqueId = a.getUniqueId();
			out = artifactRepository.findByUniqueId(uniqueId);

			if(out==null){
				out = artifactRepository.save( new Artifact(uniqueId));
			}

			tx.success();
		} 
		return out;
	}




}
