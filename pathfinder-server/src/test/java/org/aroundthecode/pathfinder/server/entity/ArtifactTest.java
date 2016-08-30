package org.aroundthecode.pathfinder.server.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.aroundthecode.pathfinder.client.rest.utils.ArtifactUtils;
import org.aroundthecode.pathfinder.client.rest.utils.ArtifactUtils.Dependency;
import org.aroundthecode.pathfinder.client.rest.utils.RestUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Test;

public class ArtifactTest {


	public static final String IDG = "my.group";
	public static final String IDA = "test";
	public static final String IDP = "jar";
	public static final String IDC = "none";
	public static final String IDV = "1.0.0";
	public static final String ID = IDG+":"+IDA+":"+IDP+":"+IDC+":"+IDV;
	
	public static final String PARSEJSON = "{\"timestamp\":1000,\"groupId\":\"my.group\",\"dependencies\":{\"RUNTIME\":[\"my.group:RUNTIME-3:jar:none:1.0.0\",\"my.group:RUNTIME-3:jar:none:1.0.0\",\"my.group:RUNTIME-3:jar:none:1.0.0\"],\"TEST\":[\"my.group:TEST-4:jar:none:1.0.0\",\"my.group:TEST-4:jar:none:1.0.0\",\"my.group:TEST-4:jar:none:1.0.0\",\"my.group:TEST-4:jar:none:1.0.0\"],\"COMPILE\":[\"my.group:COMPILE-1:jar:none:1.0.0\"],\"SYSTEM\":[\"my.group:SYSTEM-5:jar:none:1.0.0\",\"my.group:SYSTEM-5:jar:none:1.0.0\",\"my.group:SYSTEM-5:jar:none:1.0.0\",\"my.group:SYSTEM-5:jar:none:1.0.0\",\"my.group:SYSTEM-5:jar:none:1.0.0\"],\"PROVIDED\":[\"my.group:PROVIDED-2:jar:none:1.0.0\",\"my.group:PROVIDED-2:jar:none:1.0.0\"],\"IMPORT\":[\"my.group:IMPORT-6:jar:none:1.0.0\",\"my.group:IMPORT-6:jar:none:1.0.0\",\"my.group:IMPORT-6:jar:none:1.0.0\",\"my.group:IMPORT-6:jar:none:1.0.0\",\"my.group:IMPORT-6:jar:none:1.0.0\",\"my.group:IMPORT-6:jar:none:1.0.0\"]},\"parentNode\":\"my.group:parent:jar:none:1.0.0\",\"packaging\":\"jar\",\"classifier\":\"none\",\"artifactId\":\"test\",\"version\":\"1.0.0\",\"uniqueId\":\"my.group:test:jar:none:1.0.0\"}";

	

	@Test
	public void testEmptyArtifact() {
		Artifact a = new Artifact();
		assertNotNull(a);
		assertEquals("", a.getGroupId());
		assertEquals("", a.getArtifactId());
		assertEquals("", a.getVersion());
		assertEquals("jar", a.getPackaging());
		assertEquals("", a.getClassifier());
		assertEquals(ArtifactUtils.EMPTYID, a.getUniqueId());
	}

	@Test
	public void testArtifact() {
		Artifact a = new Artifact(ID);
		assertNotNull(a);
		assertEquals(IDG, a.getGroupId());
		assertEquals(IDA, a.getArtifactId());
		assertEquals(IDV, a.getVersion());
		assertEquals(IDP, a.getPackaging());
		assertEquals(IDC, a.getClassifier());

		assertEquals(ID, a.getUniqueId());
		
		Long now = System.currentTimeMillis();
		a.setTimestamp( now );
		assertEquals(now, a.getTimestamp());

	}

	@Test
	public void testArtifactCompare() {
		Artifact a = new Artifact();
		assertNotNull(a);
		a.setGroupId(IDG);
		a.setArtifactId(IDA);
		a.setVersion(IDV);
		a.setPackaging(IDP);
		a.setClassifier(IDC);

		Artifact b = new Artifact(ID);
		assertNotNull(b);

		assertTrue(a.equals(b));

		a.setClassifier("different");
		assertFalse(a.equals(b));

	}

	@Test
	public void testArtifactToJson() {
		
		Artifact a = getTestArtifact();
		Long now = System.currentTimeMillis();
		a.setTimestamp( now );

		JSONObject o = a.toJSON();
		assertNotNull(o);

		assertEquals(o.get(ArtifactUtils.U), a.getUniqueId());
		assertEquals(o.get(ArtifactUtils.G), a.getGroupId());
		assertEquals(o.get(ArtifactUtils.A), a.getArtifactId());
		assertEquals(o.get(ArtifactUtils.P), a.getPackaging());
		assertEquals(o.get(ArtifactUtils.C), a.getClassifier());
		assertEquals(o.get(ArtifactUtils.V), a.getVersion());
		assertEquals(o.get(ArtifactUtils.PN), a.getParent().getUniqueId() );
		assertEquals(o.get(ArtifactUtils.T), a.getTimestamp().toString() );
		
		assertEquals(((JSONArray)((JSONObject)o.get(ArtifactUtils.D)).get(Dependency.COMPILE.toString())).size(), a.dependenciesCompile.size() );
		assertEquals(((JSONArray)((JSONObject)o.get(ArtifactUtils.D)).get(Dependency.PROVIDED.toString())).size(), a.dependenciesProvided.size() );
		assertEquals(((JSONArray)((JSONObject)o.get(ArtifactUtils.D)).get(Dependency.RUNTIME.toString())).size(), a.dependenciesRuntime.size() );
		assertEquals(((JSONArray)((JSONObject)o.get(ArtifactUtils.D)).get(Dependency.TEST.toString())).size(), a.dependenciesTest.size() );
		assertEquals(((JSONArray)((JSONObject)o.get(ArtifactUtils.D)).get(Dependency.SYSTEM.toString())).size(), a.dependenciesSystem.size() );
		assertEquals(((JSONArray)((JSONObject)o.get(ArtifactUtils.D)).get(Dependency.IMPORT.toString())).size(), a.dependenciesImport.size() );
		
		System.out.println(o.toJSONString());
		
	}

	@Test
	public void testArtifactFromJson() throws ParseException {
		Artifact a = getTestArtifact();

		JSONObject o = a.toJSON();
		assertNotNull(o);
		
		Artifact a2 = Artifact.parse(o);
		assertEquals(a, a2);
		
		JSONObject o2 = RestUtils.string2Json(PARSEJSON);
		Artifact a3 = Artifact.parse(o2);
		assertEquals(a, a3);
		
		//removing parent to check parsing works as well
		o2.remove(ArtifactUtils.PN);
		Artifact a4 = Artifact.parse(o2);
		assertEquals(a.getUniqueId(), a4.getUniqueId());
		
	}
	
	/**
	 * Utility method to generate artifact for json tests
	 * @return Artifact test object
	 */
	private Artifact getTestArtifact() {
		Artifact a = new Artifact(ID);

		Artifact p = new Artifact(ID);
		p.setArtifactId("parent");
		a.hasParent(p);
		
		int count=1;
		for (Dependency dd : Dependency.values()) {
			for (int i = 0; i < count; i++) {
				Artifact d2 = new Artifact(ID);
				d2.setArtifactId(dd.name()+"-"+i);
				a.dependsOn(d2, dd.name());
			}
			count++;
		}
		return a;
	}
	
	
}
