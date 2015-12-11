package org.aroundthecode.pathfinder.server.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;

import org.aroundthecode.pathfinder.server.Application;
import org.aroundthecode.pathfinder.server.entity.Artifact;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Transactional
@Ignore
public class ArtifactRepositoryTest {

	@Autowired ArtifactRepository artifactRepository;

	@Autowired GraphDatabase graphDatabase;
	
	@Test
	public void test() {
		String uniqueIdA1 = "group1:artifact1:version1:jar:";
		String uniqueIdA2 = "group2:artifact2:version2:war:";
		String uniqueIdA3 = "group3:artifact3:version3:war:altro";

		Artifact a1 = new Artifact(uniqueIdA1);
		assertNotNull(a1);
		assertEquals(uniqueIdA1, a1.getUniqueId());
		
		Artifact a2 = new Artifact(uniqueIdA2);
		assertNotNull(a2);
		assertEquals(uniqueIdA2, a2.getUniqueId());
		
		Artifact a3 = new Artifact(uniqueIdA3);
		assertNotNull(a3);
		assertEquals(uniqueIdA3, a3.getUniqueId());

		System.out.println("Before linking up with Neo4j...");
		for (Artifact a : new Artifact[] { a1, a2, a3 }) {
			System.out.println(a);
		}

		Transaction tx = graphDatabase.beginTx();
		try {
			artifactRepository.save(a1);
			artifactRepository.save(a2);
			artifactRepository.save(a3);

			for (Iterator<Artifact> i = artifactRepository.findAll().iterator(); i.hasNext();) {
				Artifact a = i.next();
				if(a!=null){ 
					System.out.println("FOUND");
				}
				if(a.getUniqueId()!=null){
					System.out.println(a);
				}
				else{
					System.out.println("but empty!");
				}

			}

			a1 = artifactRepository.findByUniqueId(uniqueIdA1);
			if(a1!=null){ 
				System.out.println("A1 FOUND");
			}
			if(a1.getUniqueId()!=null){
				System.out.println(a1);
			}
			else{
				System.out.println("but empty!");
			}
			a1.dependsOn(a2);
			a1.dependsOn(a3);
			artifactRepository.save(a1);

			a2 = artifactRepository.findByUniqueId(uniqueIdA2);
			a2.dependsOn(a3);
			artifactRepository.save(a2);
//
			/*
//
			for (Iterator<Artifact> i = artifactRepository.findAll().iterator(); i.hasNext();) {
				Artifact a = i.next();
				if(a!=null){ 
					System.out.println("FOUND");
				}
				if(a.getUniqueId()!=null){
				System.out.println(a);
				}
				else{
					System.out.println("but empty!");
				}

			}
//			
			 */

			//			System.out.println("Looking up who depends on art1...");
			//			for (MavenArtifact a : mavenArtifactRepository.findByDependencyArtifact(art1)) {
			//				System.out.println(a.getArtifact() + " depends on art1.");
			//			}
			tx.success();
		} finally {
			tx.close();
		}
	}

}
