package org.aroundthecode.pathfinder.server.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.aroundthecode.pathfinder.client.rest.items.FilterItem;
import org.junit.Assert;
import org.junit.Test;

public class QueryUtilsTest {


	private final String TEST_CYPHER_FILTERALL ="MATCH n1-[r]->n2 WHERE n1.groupId =~ \".*\" AND n1.artifactId =~ \".*\" AND n1.packaging =~ \".*\" AND n1.classifier =~ \".*\" AND n1.version =~ \".*\" AND n2.groupId =~ \".*\" AND n2.artifactId =~ \".*\" AND n2.packaging =~ \".*\" AND n2.classifier =~ \".*\" AND n2.version =~ \".*\" RETURN n1 as node1,type(r) as rel ,n2 as node2";
	private final String TEST_CYPHER_IMPACT ="MATCH (n1:Artifact { groupId:'org.sample' ,artifactId:'mypackage' ,packaging:'jar' ,version:'1.0.0' ,classifier: '' })-[r1]->(n2) WHERE n1.groupId =~ \".*\" AND n1.artifactId =~ \".*\" AND n1.packaging =~ \".*\" AND n1.classifier =~ \".*\" AND n1.version =~ \".*\" AND n2.groupId =~ \".*\" AND n2.artifactId =~ \".*\" AND n2.packaging =~ \".*\" AND n2.classifier =~ \".*\" AND n2.version =~ \".*\" RETURN n1 as node1,type(r1) as rel ,n2 as node2 UNION MATCH (n1:Artifact { groupId:'org.sample' ,artifactId:'mypackage' ,packaging:'jar' ,version:'1.0.0' ,classifier: '' })-[r1]->(n2)-[r2]->(n3) WHERE n2.groupId =~ \".*\" AND n2.artifactId =~ \".*\" AND n2.packaging =~ \".*\" AND n2.classifier =~ \".*\" AND n2.version =~ \".*\" AND n3.groupId =~ \".*\" AND n3.artifactId =~ \".*\" AND n3.packaging =~ \".*\" AND n3.classifier =~ \".*\" AND n3.version =~ \".*\" RETURN n3 as node1,type(r2) as rel ,n3 as node2";

	@Test
	public void testUtilityClassWellDefined(){
		try {
			assertUtilityClassWellDefined(QueryUtils.class);
		} catch (NoSuchMethodException | InvocationTargetException
				| InstantiationException | IllegalAccessException e) {
			fail(e.getMessage());
		}
	}


	@Test
	public void testFilterAllQuery() {

		FilterItem f = new FilterItem();
		assertNotNull(f);

		String query = QueryUtils.getFilterAllQuery(f);
		assertNotNull(query);
		assertEquals(TEST_CYPHER_FILTERALL, query);
	}

	@Test
	public void testImpactQuery() {

		FilterItem f = new FilterItem();
		assertNotNull(f);

		String query = QueryUtils.getImpactQuery(2, "org.sample", "mypackage", "jar", "", "1.0.0", f );
		assertNotNull(query);
		assertEquals(TEST_CYPHER_IMPACT, query);
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
