package org.aroundthecode.pathfinder.server.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.aroundthecode.pathfinder.client.rest.PathfinderClient;
import org.aroundthecode.pathfinder.server.Application;
import org.aroundthecode.pathfinder.server.entity.Artifact;
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

	private static final String HOST = "http://localhost:8080";
	
	@Test
	public void test_00_Client() {
		PathfinderClient client = new PathfinderClient(HOST);
		assertNotNull(client);
	}

	
	
	@Test
	public void test_01_Write() {

		JSONObject body = getJsonObject();
		assertNotNull(body);
		PathfinderClient client = new PathfinderClient(HOST);
		try {
			String response = client.saveArtifact(ArtifactTest.IDG,ArtifactTest.IDA,ArtifactTest.IDP,ArtifactTest.IDC,ArtifactTest.IDV);
			//, artifactId, packaging, classifier, version)RestUtils.sendPost(HOST+"/node/save", body);
			assertNotNull(response);
			System.out.println(response);

		} catch (IOException e) {
			fail(e.getMessage());
		}

	}

	@Test
	public void test_02_Read() {
		PathfinderClient client = new PathfinderClient(HOST);
		try {
			JSONObject o = client.getArtifact(ArtifactTest.ID);
			assertNotNull(o);
			System.out.println(o.toString());
		}
		catch (IOException | ParseException e) {
			fail(e.getMessage());
		}

	}

	@SuppressWarnings("unchecked")
	private JSONObject getJsonObject() {
		JSONObject body = new JSONObject();
		body.put(Artifact.G, ArtifactTest.IDG);
		body.put(Artifact.A, ArtifactTest.IDA);
		body.put(Artifact.P, ArtifactTest.IDP);
		body.put(Artifact.C, ArtifactTest.IDC);
		body.put(Artifact.V, ArtifactTest.IDV);
		return body;
	}

}
