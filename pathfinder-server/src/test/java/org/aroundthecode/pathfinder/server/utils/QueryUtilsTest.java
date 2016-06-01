package org.aroundthecode.pathfinder.server.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class QueryUtilsTest {

	
	private final String TEST_CYPHER_FILTERALL ="MATCH n1-[r]->n2 WHERE n1.groupId =~ \".*\" AND n1.artifactId =~ \".*\" AND n1.packaging =~ \".*\" AND n1.classifier =~ \".*\" AND n1.version =~ \".*\" AND n2.groupId =~ \".*\" AND n2.artifactId =~ \".*\" AND n2.packaging =~ \".*\" AND n2.classifier =~ \".*\" AND n2.version =~ \".*\" RETURN n1,type(r) as rel ,n2";
	private final String TEST_CYPHER_IMPACT ="MATCH (n1:Artifact { groupId:'org.sample' ,artifactId:'mypackage' ,packaging:'jar' ,version:'1.0.0' ,classifier: '' })-[r1]->(n2) with n1 as node, [type(r1), n2] as relative WHERE n1.groupId =~ \".*\" AND n1.artifactId =~ \".*\" AND n1.packaging =~ \".*\" AND n1.classifier =~ \".*\" AND n1.version =~ \".*\" AND n2.groupId =~ \".*\" AND n2.artifactId =~ \".*\" AND n2.packaging =~ \".*\" AND n2.classifier =~ \".*\" AND n2.version =~ \".*\" RETURN { root: node, relatives: collect(relative) } UNION MATCH (n1:Artifact { groupId:'org.sample' ,artifactId:'mypackage' ,packaging:'jar' ,version:'1.0.0' ,classifier: '' })-[r1]->(n2)-[r2]->(n3) with n2 as node, [type(r2), n3] as relative WHERE n2.groupId =~ \".*\" AND n2.artifactId =~ \".*\" AND n2.packaging =~ \".*\" AND n2.classifier =~ \".*\" AND n2.version =~ \".*\" AND n3.groupId =~ \".*\" AND n3.artifactId =~ \".*\" AND n3.packaging =~ \".*\" AND n3.classifier =~ \".*\" AND n3.version =~ \".*\" RETURN { root: node, relatives: collect(relative) }";
	
	@Test
	public void testFilterAllQuery() {
		
		FilterItem f = new FilterItem();
		assertNotNull(f);
		
		String query = QueryUtils.getFilterAllQuery(f);
		assertNotNull(query);
		System.out.println(query);
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

}
