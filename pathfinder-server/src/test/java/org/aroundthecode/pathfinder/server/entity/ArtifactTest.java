package org.aroundthecode.pathfinder.server.entity;

import static org.junit.Assert.*;

import org.aroundthecode.pathfinder.client.rest.utils.ArtifactUtils;
import org.aroundthecode.pathfinder.client.rest.utils.ArtifactUtils.Dependency;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

public class ArtifactTest {


	public static final String IDG = "my.group";
	public static final String IDA = "test";
	public static final String IDP = "jar";
	public static final String IDC = "none";
	public static final String IDV = "1.0.0";
	public static final String ID = IDG+":"+IDA+":"+IDP+":"+IDC+":"+IDV;

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
	public void testArtifactJson() {
		Artifact a = new Artifact(ID);

		Artifact p = new Artifact(ID);
		p.setArtifactId("parent");
		a.hasParent(p);
		
		int count=1;
		for (Dependency dd : Dependency.values()) {
			for (int i = 0; i < count; i++) {
				Artifact d2 = new Artifact(ID);
				d2.setArtifactId(dd.name()+"-"+count);
				a.dependsOn(d2, dd.name());
			}
			count++;
		}

		JSONObject o = a.toJSON();
		assertNotNull(o);

		assertEquals(o.get(ArtifactUtils.U), a.getUniqueId());
		assertEquals(o.get(ArtifactUtils.G), a.getGroupId());
		assertEquals(o.get(ArtifactUtils.A), a.getArtifactId());
		assertEquals(o.get(ArtifactUtils.P), a.getPackaging());
		assertEquals(o.get(ArtifactUtils.C), a.getClassifier());
		assertEquals(o.get(ArtifactUtils.V), a.getVersion());
		assertEquals(o.get(ArtifactUtils.PN), a.getParent().getUniqueId() );
		
		assertEquals(((JSONArray)((JSONObject)o.get(ArtifactUtils.D)).get(Dependency.COMPILE)).size(), a.dependenciesCompile.size() );
		assertEquals(((JSONArray)((JSONObject)o.get(ArtifactUtils.D)).get(Dependency.PROVIDED)).size(), a.dependenciesProvided.size() );
		assertEquals(((JSONArray)((JSONObject)o.get(ArtifactUtils.D)).get(Dependency.RUNTIME)).size(), a.dependenciesRuntime.size() );
		assertEquals(((JSONArray)((JSONObject)o.get(ArtifactUtils.D)).get(Dependency.TEST)).size(), a.dependenciesTest.size() );
		assertEquals(((JSONArray)((JSONObject)o.get(ArtifactUtils.D)).get(Dependency.SYSTEM)).size(), a.dependenciesSystem.size() );
		assertEquals(((JSONArray)((JSONObject)o.get(ArtifactUtils.D)).get(Dependency.IMPORT)).size(), a.dependenciesImport.size() );
		
		
		
	}
}
