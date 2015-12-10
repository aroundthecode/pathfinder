package org.aroundthecode.pathfinder.server;

import java.io.File;
import java.util.Iterator;

import org.aroundthecode.pathfinder.server.dao.MavenArtifactDao;
import org.aroundthecode.pathfinder.server.entity.Artifact;
import org.aroundthecode.pathfinder.server.entity.MavenArtifact;
import org.aroundthecode.pathfinder.server.repository.ArtifactRepository;
import org.aroundthecode.pathfinder.server.repository.MavenArtifactRepository;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.io.fs.FileUtils;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.ServerConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.data.neo4j.template.Neo4jOperations;

@SuppressWarnings("deprecation")
@SpringBootApplication
public class Application extends Neo4jConfiguration implements CommandLineRunner {

	@Configuration
	@EnableNeo4jRepositories(basePackages = "org.aroundthecode.pathfinder.server")
	static class ApplicationConfig extends Neo4jConfiguration {

		public ApplicationConfig() {
			setBasePackage("org.aroundthecode.pathfinder.server");
		}

		@Bean(destroyMethod = "shutdown")
		GraphDatabaseService graphDatabaseService() {
			return new GraphDatabaseFactory().newEmbeddedDatabase("target/accessingdataneo4j.db");
		}
	}

	@Autowired MavenArtifactRepository mavenArtifactRepository;
	@Autowired ArtifactRepository artifactRepository;

	@Autowired GraphDatabase graphDatabase;
	
	@Autowired
	GraphDatabaseService db;
	
	@Autowired Neo4jOperations template;
	
	@Override
	public void run(String... args) throws Exception {

		// used for Neo4j browser
	    try {
			WrappingNeoServerBootstrapper neoServerBootstrapper;
	        GraphDatabaseAPI api = (GraphDatabaseAPI) db;

	        ServerConfigurator config = new ServerConfigurator(api);
	        config.configuration()
	            .addProperty(Configurator.WEBSERVER_ADDRESS_PROPERTY_KEY, "127.0.0.1");
	        config.configuration()
	            .addProperty(Configurator.WEBSERVER_PORT_PROPERTY_KEY, "8686");

	        neoServerBootstrapper = new WrappingNeoServerBootstrapper(api, config);
	        neoServerBootstrapper.start();
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	    // end of Neo4j browser config
		
		
		String uniqueIdA1 = "group1:artifact1:version1:jar:";
		String uniqueIdA2 = "group2:artifact2:version2:war:";
		String uniqueIdA3 = "group3:artifact3:version3:war:altro";
		
		Artifact a1 = new Artifact();
		a1.setUniqueId(uniqueIdA1);
		Artifact a2 = new Artifact();
		a2.setUniqueId(uniqueIdA2);
		Artifact a3 = new Artifact();
		a3.setUniqueId(uniqueIdA3);
		
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
			
//			queryAll(mad1, mad2, mad3);
			
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
			/*

			a2 = artifactRepository.findByUniqueId(uniqueIdA2);
			a2.dependsOn(a3);
//			// We already know that roy works with greg
			artifactRepository.save(a2);
//
//			// We already know craig works with roy and greg
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
		
		
/*		
		MavenArtifactDao mad1 = new MavenArtifactDao("group1","artifact1","version1");
		MavenArtifactDao mad2 = new MavenArtifactDao("group2","artifact2","version2");
		MavenArtifactDao mad3 = new MavenArtifactDao("group3","artifact3","version3");
		
		MavenArtifact art1 = new MavenArtifact( mad1 );
		MavenArtifact art2 = new MavenArtifact( mad2 );
		MavenArtifact art3 = new MavenArtifact( mad3 );

		System.out.println("Before linking up with Neo4j...");
		for (MavenArtifact a : new MavenArtifact[] { art1, art2, art3 }) {
			System.out.println(a);
		}

		Transaction tx = graphDatabase.beginTx();
		try {
			mavenArtifactRepository.save(art1);
			mavenArtifactRepository.save(art2);
			mavenArtifactRepository.save(art3);

			for (Iterator<MavenArtifact> i = mavenArtifactRepository.findAll().iterator(); i.hasNext();) {
				MavenArtifact a = i.next();
				if(a!=null){ 
					System.out.println("FOUND");
					template.fetch(a.getMavenArtifactDao());
				}
				if(a.getMavenArtifactDao()!=null){
				System.out.println(a);
				}
				else{
					System.out.println("but empty!");
				}
				
			}
			
			queryAll(mad1, mad2, mad3);
			
			art1 = mavenArtifactRepository.findByMavenArtifactDao(mad1);
			art1.dependsOn(art2);
			art1.dependsOn(art3);
			mavenArtifactRepository.save(art1);

			art2 = mavenArtifactRepository.findByMavenArtifactDao(mad2);
			art2.dependsOn(art3);
			// We already know that roy works with greg
			mavenArtifactRepository.save(art2);

			// We already know craig works with roy and greg

			queryAll(mad1, mad2, mad3);
			
			

//			System.out.println("Looking up who depends on art1...");
//			for (MavenArtifact a : mavenArtifactRepository.findByDependencyArtifact(art1)) {
//				System.out.println(a.getArtifact() + " depends on art1.");
//			}
			tx.success();
		} finally {
			tx.close();
		}
		*/
	}

	private void queryAll(MavenArtifactDao mad1, MavenArtifactDao mad2,	MavenArtifactDao mad3) {
		System.out.println("Lookup each MavenArtifact by id...");
		for (MavenArtifactDao mad : new MavenArtifactDao[] { mad1, mad2, mad3}) {
			System.out.println("looking for ["+mad.toString()+"]");
			MavenArtifact found = mavenArtifactRepository.findByMavenArtifactDao(mad);
			if(found!=null){
				System.out.println("FOUND:");
				if(found.getMavenArtifactDao()!=null){
					System.out.println("--"+found.getMavenArtifactDao().toString());
				}
				else{
					System.out.println("--but it's empty");
				}
			}
			else{System.out.println("NOT FOUND");
			}
			
		}
	}

	public static void main(String[] args) throws Exception {
		FileUtils.deleteRecursively(new File("target/accessingdataneo4j.db"));

		SpringApplication.run(Application.class, args);
	}

}
