package org.aroundthecode.pathfinder.server.entity;

import static org.junit.Assert.*;

import org.junit.Test;

public class ArtifactTest {

	
	String ID = "group:artifact:version:war:classifier";
	String IDG = "group";
	String IDA = "artifact";
	String IDV = "version";
	String IDT = "war";
	String IDC = "classifier";
	
	@Test
	public void testEmptyArtifact() {
		Artifact a = new Artifact();
		assertNotNull(a);
		assertEquals("", a.getGroupId());
		assertEquals("", a.getArtifactId());
		assertEquals("", a.getVersion());
		assertEquals("jar", a.getType());
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
		assertEquals(IDT, a.getType());
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
		a.setType(IDT);
		a.setClassifier(IDC);
		
		Artifact b = new Artifact(ID);
		assertNotNull(b);

		assertTrue(a.equals(b));
		
		a.setClassifier("different");
		assertFalse(a.equals(b));
		
	}

}
