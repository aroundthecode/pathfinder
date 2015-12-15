package org.aroundthecode.pathfinder.server.entity;

import static org.junit.Assert.*;

import org.junit.Test;

public class ArtifactTest {

	
	public static final String IDG = "group";
	public static final String IDA = "artifact";
	public static final String IDP = "packaging";
	public static final String IDC = "classifier";
	public static final String IDV = "version";
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
		
		assertEquals(Artifact.EMPTYID, a.getUniqueId());
		
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

}
