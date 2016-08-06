package org.aroundthecode.pathfinder.server.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.aroundthecode.pathfinder.client.rest.PathfinderClient;
import org.aroundthecode.pathfinder.client.rest.items.FilterItem;
import org.aroundthecode.pathfinder.client.rest.utils.ArtifactUtils;
import org.aroundthecode.pathfinder.client.rest.utils.ArtifactUtils.Dependency;
import org.aroundthecode.pathfinder.client.rest.utils.RestUtils;
import org.aroundthecode.pathfinder.server.Application;
import org.aroundthecode.pathfinder.server.configuration.ConfigurationManager;
import org.aroundthecode.pathfinder.server.crawler.CrawlerWrapper;
import org.aroundthecode.pathfinder.server.entity.ArtifactTest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
@FixMethodOrder( MethodSorters.NAME_ASCENDING)
public class PathFinderControllerTest {

	private static final String PF_SERVER_PROTOCOL = ConfigurationManager.getPathfinderProtocol();
	private static final String PF_SERVER_HOST = ConfigurationManager.getPathfinderHost();
	private static final int    PF_SERVER_PORT = ConfigurationManager.getPathfinderPort();
	private static final String PF_SERVER_PATH = ConfigurationManager.getPathfinderPath();

	private static PathfinderClient client = null;
	
	private static final String FILTERALL = 
			"MATCH n-[r]->n2 RETURN n,type(r) as rel ,n2";
	
	
	@Test
	public void test10Client()  {
		try {
			client = new PathfinderClient(PF_SERVER_PROTOCOL,PF_SERVER_HOST,PF_SERVER_PORT,PF_SERVER_PATH);
			assertNotNull(client);
		} catch (IOException e) {
			fail(e.getMessage());
			}
	}

	@Test
	public void test20Write()  {

		JSONObject body = getJsonObject();
		assertNotNull(body);
		try {
			JSONObject response = client.saveArtifact(ArtifactTest.ID);
			assertNotNull(response);
			System.out.println(response);

		} catch (IOException e) {
			fail(e.getMessage());
		}

	}

	@Test
	public void test30Dependencies()  {

		try {
			//main artifact creation
			JSONObject obj = getJsonObject();
			assertNotNull(obj);
			client.saveArtifact(ArtifactTest.ID);

			//dependency artifacts, one per scope
			//create and link
			for ( Dependency dir : Dependency.values()) {
				JSONObject o =  getJsonObject(dir.toString());
				//client.saveArtifact(o.get(ArtifactUtils.U).toString());
				client.createDependency(obj.get(ArtifactUtils.U).toString(), o.get(ArtifactUtils.U).toString(), dir.toString() );
			}
		} catch (IOException e) {
			fail(e.getMessage());
		}

	}

	@Test
	public void test40Parent() {

		try {
			//main artifact creation
			JSONObject obj = getJsonObject();
			assertNotNull(obj);
			JSONObject o =  getJsonObject("parent");
			assertNotNull(o);
			client.saveArtifact( o.get(ArtifactUtils.U).toString());

			client.addParent(obj.get(ArtifactUtils.U).toString(), o.get(ArtifactUtils.U).toString() );
		} catch (IOException e) {
			fail(e.getMessage());
			}

	}


	@Test
	public void test50Read(){

		try {
			JSONObject o = client.getArtifact(ArtifactTest.ID);
			assertNotNull(o);
			System.out.println(o.toString());
		}
		catch (IOException e) {
			fail(e.getMessage());
		}

	}

	@Test
	public void test60Crawler() {

		JSONObject obj = CrawlerWrapper.crawl("org.apache.maven.shared", "maven-invoker", "jar", "", "2.2");
		assertNotNull(obj);
		System.out.println(obj);
		assertEquals(0,obj.get("return"));

	}

	private JSONObject getJsonObject() {
		return getJsonObject("");
	}
	
	@Test
	public void test70Query() {
		
		try {
			String response = client.query(FILTERALL);
			assertNotNull(response);
			System.out.println(response);
			
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
	}

	@Test
	public void test71Filter() {

		try {
			FilterItem f = new FilterItem();
			JSONArray response = client.filterAll(f);
			assertNotNull(response);
			System.out.println(response);

		} catch (IOException e) {
			fail(e.getMessage());
		}

	}
	
	@Test
	public void test72Impact() {

		try {
			FilterItem f = new FilterItem();
			JSONArray response = client.impact(2, "org.aroundthecode.pathfinder", "pathfinder-client", "jar", "", "0.1.0-SNAPSHOT", f);
			assertNotNull(response);
			System.out.println(response);

		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void test73Download() {

		try {
			File f = client.downloadProject();
			assertNotNull(f);
			assertTrue(f.exists());
			
			FileReader fr = new FileReader(f);
			JSONArray obj = RestUtils.string2JSONArray(fr);
			assertNotNull(obj);

		} catch (IOException | ParseException e) {
			fail(e.getMessage());
		}
	}
	

	@SuppressWarnings("unchecked")
	private JSONObject getJsonObject(String prefix) {
		JSONObject body = new JSONObject();
		String artifactId = prefix + ArtifactTest.IDA;
		body.put(ArtifactUtils.G, ArtifactTest.IDG);
		body.put(ArtifactUtils.A, artifactId);
		body.put(ArtifactUtils.P, ArtifactTest.IDP);
		body.put(ArtifactUtils.C, ArtifactTest.IDC);
		body.put(ArtifactUtils.V, ArtifactTest.IDV);
		body.put(ArtifactUtils.U, ArtifactUtils.getUniqueId(ArtifactTest.IDG, artifactId, ArtifactTest.IDP, ArtifactTest.IDC, ArtifactTest.IDV));
		body.put(ArtifactUtils.T, System.currentTimeMillis());
		return body;
	}

}
