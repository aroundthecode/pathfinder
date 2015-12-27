package org.aroundthecode.pathfinder.server.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.aroundthecode.pathfinder.client.rest.PathfinderClient;
import org.aroundthecode.pathfinder.client.rest.utils.ArtifactUtils;
import org.aroundthecode.pathfinder.client.rest.utils.ArtifactUtils.Dependency;
import org.aroundthecode.pathfinder.server.Application;
import org.aroundthecode.pathfinder.server.entity.ArtifactTest;
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

	private static final String NEO4J_SERVER_PROTOCOL = "http";
	private static final String NEO4J_SERVER_HOST = "localhost";
	private static final int    NEO4J_SERVER_PORT = 8080;
	private static final String NEO4J_SERVER_PATH = "/";

	@Test
	public void test_10_Client() throws IOException {
		PathfinderClient client = new PathfinderClient(NEO4J_SERVER_PROTOCOL,NEO4J_SERVER_HOST,NEO4J_SERVER_PORT,NEO4J_SERVER_PATH);
		assertNotNull(client);
	}



	@Test
	public void test_20_Write() throws IOException {

		JSONObject body = getJsonObject();
		assertNotNull(body);
		PathfinderClient client = new PathfinderClient(NEO4J_SERVER_PROTOCOL,NEO4J_SERVER_HOST,NEO4J_SERVER_PORT,NEO4J_SERVER_PATH);
		try {
			String response = client.saveArtifact(ArtifactTest.ID);
			assertNotNull(response);
			System.out.println(response);

		} catch (IOException e) {
			fail(e.getMessage());
		}

	}

	@Test
	public void test_30_Dependencies() throws IOException {

		PathfinderClient client = new PathfinderClient(NEO4J_SERVER_PROTOCOL,NEO4J_SERVER_HOST,NEO4J_SERVER_PORT,NEO4J_SERVER_PATH);

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

	}
	
	@Test
	public void test_40_Parent() throws IOException {

		PathfinderClient client = new PathfinderClient(NEO4J_SERVER_PROTOCOL,NEO4J_SERVER_HOST,NEO4J_SERVER_PORT,NEO4J_SERVER_PATH);

		//main artifact creation
		JSONObject obj = getJsonObject();
		assertNotNull(obj);
		JSONObject o =  getJsonObject("parent");
		assertNotNull(o);
		client.saveArtifact( o.get(ArtifactUtils.U).toString());
		
		client.addParent(obj.get(ArtifactUtils.U).toString(), o.get(ArtifactUtils.U).toString() );

	}


	@Test
	public void test_50_Read() throws IOException {
		PathfinderClient client = new PathfinderClient(NEO4J_SERVER_PROTOCOL,NEO4J_SERVER_HOST,NEO4J_SERVER_PORT,NEO4J_SERVER_PATH);
		try {
			JSONObject o = client.getArtifact(ArtifactTest.ID);
			assertNotNull(o);
			System.out.println(o.toString());
		}
		catch (IOException | ParseException e) {
			fail(e.getMessage());
		}

	}

	private JSONObject getJsonObject() {
		return getJsonObject("");
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
		return body;
	}

}
