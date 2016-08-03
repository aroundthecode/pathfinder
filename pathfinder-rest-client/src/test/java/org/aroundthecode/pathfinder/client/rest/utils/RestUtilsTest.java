package org.aroundthecode.pathfinder.client.rest.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;

public class RestUtilsTest {

	@Test
	public void testUtilityClassWellDefined(){
		try {
			assertUtilityClassWellDefined(RestUtils.class);
		} catch (NoSuchMethodException | InvocationTargetException
				| InstantiationException | IllegalAccessException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testUtilityClassWellDefinedArtifactUtils(){
		try {
			assertUtilityClassWellDefined(ArtifactUtils.class);
		} catch (NoSuchMethodException | InvocationTargetException
				| InstantiationException | IllegalAccessException e) {
			fail(e.getMessage());
		}
	}


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



	/**
	 * Verifies that a utility class is well defined.
	 * 
	 * @param clazz utility class to verify.
	 */
	public static void assertUtilityClassWellDefined(final Class<?> clazz)
			throws NoSuchMethodException, InvocationTargetException,
			InstantiationException, IllegalAccessException {
		Assert.assertTrue("class must be final",
				Modifier.isFinal(clazz.getModifiers()));
		Assert.assertEquals("There must be only one constructor", 1,
				clazz.getDeclaredConstructors().length);
		final Constructor<?> constructor = clazz.getDeclaredConstructor();
		if (constructor.isAccessible() || 
				!Modifier.isPrivate(constructor.getModifiers())) {
			Assert.fail("constructor is not private");
		}
		constructor.setAccessible(true);
		constructor.newInstance();
		constructor.setAccessible(false);
		for (final Method method : clazz.getMethods()) {
			if (!Modifier.isStatic(method.getModifiers())
					&& method.getDeclaringClass().equals(clazz)) {
				Assert.fail("there exists a non-static method:" + method);
			}
		}
	}
}
