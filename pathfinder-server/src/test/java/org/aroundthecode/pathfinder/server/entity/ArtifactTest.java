package org.aroundthecode.pathfinder.server.entity;

import static org.junit.Assert.*;

import org.aroundthecode.pathfinder.client.rest.utils.ArtifactUtils;
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

}
