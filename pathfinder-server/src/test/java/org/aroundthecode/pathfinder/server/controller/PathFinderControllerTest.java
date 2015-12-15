package org.aroundthecode.pathfinder.server.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.aroundthecode.pathfinder.server.Application;
import org.aroundthecode.pathfinder.server.entity.Artifact;
import org.aroundthecode.pathfinder.server.entity.ArtifactTest;
import org.aroundthecode.pathfinder.server.utils.RestUtils;
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

	@Test
	public void test_01_Post() {

		JSONObject body = getJsonObject();
		assertNotNull(body);

		try {
			String response = RestUtils.sendPost("http://localhost:8080/node/save", body);
			assertNotNull(response);
			System.out.println(response);

		} catch (IOException e) {
			fail(e.getMessage());
		}

	}

	@Test
	public void test_02_Get() {

		try {
			String response = RestUtils.sendGet("http://localhost:8080/node/get?id="+ ArtifactTest.ID);
			assertNotNull(response);
			System.out.println(response);
			JSONObject o = RestUtils.string2Json(response);
			assertNotNull(o);
		
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
