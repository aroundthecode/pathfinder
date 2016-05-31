package org.aroundthecode.pathfinder.client.rest.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.aroundthecode.pathfinder.client.rest.utils.RestUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Test;

public class RestUtilsTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testPost() {
		
		JSONObject body = new JSONObject();
		body.put("key", "value");
		
		assertNotNull(body);
		
		try {
			String response = RestUtils.sendPost("http://httpbin.org/post", body);
			assertNotNull(response);
			JSONObject o = RestUtils.string2Json(response);
			assertEquals(((JSONObject)o.get("headers")).get("User-Agent").toString(), RestUtils.USER_AGENT);
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (ParseException e) {
			fail(e.getMessage());

		}
		
		
	}
	
	@Test
	public void testGet() {
		
		try {
			String response = RestUtils.sendGet("http://httpbin.org/get?a=b");
			assertNotNull(response);
			JSONObject o = RestUtils.string2Json(response);
			assertEquals(((JSONObject)o.get("headers")).get("User-Agent").toString(), RestUtils.USER_AGENT);
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (ParseException e) {
			fail(e.getMessage());

		}
		
		
	}
	

}
