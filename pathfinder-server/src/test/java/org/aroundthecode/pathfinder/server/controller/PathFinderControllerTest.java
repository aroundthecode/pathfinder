package org.aroundthecode.pathfinder.server.controller;

import static org.junit.Assert.*;

import java.io.IOException;

import org.aroundthecode.pathfinder.server.Application;
import org.aroundthecode.pathfinder.server.entity.ArtifactTest;
import org.aroundthecode.pathfinder.server.utils.RestUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class PathFinderControllerTest {

	JSONParser jparser = new JSONParser();


	@Test
	public void testPost() {

		JSONObject body = getJsonObject();

		assertNotNull(body);

		try {
			String response = RestUtils.sendPost("http://localhost:8080/artifacts", body);
			assertNotNull(response);
			System.out.println(response);

		} catch (IOException e) {
			fail(e.getMessage());
		}


	}

	@Test
	public void test() {

		try {
			String response = RestUtils.sendGet("http://localhost:8080/list/nodes?id="+ ArtifactTest.ID);
			assertNotNull(response);
			System.out.println(response);
			JSONObject o = (JSONObject) jparser.parse(response);
			assertNotNull(o);
		
		}
		catch (IOException | ParseException e) {
			fail(e.getMessage());
		}

	}

	@SuppressWarnings("unchecked")
	private JSONObject getJsonObject() {
		JSONObject body = new JSONObject();
		body.put("groupId", ArtifactTest.IDG);
		body.put("artifactId", ArtifactTest.IDA);
		body.put("version", ArtifactTest.IDV);
		body.put("type", ArtifactTest.IDT);
		body.put("classifier", ArtifactTest.IDC);
		return body;
	}

}
