package org.aroundthecode.pathfinder.client.rest;

import static org.junit.Assert.*;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class RestUtilsTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testPost() {
		
		JSONObject body = new JSONObject();
		body.put("groupId", "g.r.o.u.p");
		body.put("artifactId", "a1");
		body.put("version", "0.0.1-SNAPSHOT");
		body.put("type", "war");
		body.put("classifier", "a");
		
		assertNotNull(body);
		
		try {
			String response = RestUtils.sendPost("http://localhost:8080/artifacts", body);
			assertNotNull(response);
			System.out.println(response);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		
	}

}
