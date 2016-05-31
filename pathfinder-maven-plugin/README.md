# Pathfinder-maven-plugin

Pathfinder dependency analyzer is available as an extension of [Maven Dependency Plugin] which will analyze dependency hierarchy in the very same way dependency:tree goals does, but it will store output into a Neo4J Graph database for later retrieval.

This helps you integrating Pathfinder standard goal into your Continuous Integration chain to have all project dependencies immediately available and up-to date

### Configure plugin in your maven hierarchy

the easiest way to integrate the plugin is to configur it into your [pom hierarchy](http://aroundthecode.org/2013/08/27/maven-organizzazione-gerarchica-dei-progetti/) to have it automatcally execute upon every artifact deploy.

Here is a sample for such configuration, you can change parameters in order to match the proper address where you installed [pathfinder-server](../pathfinder-server).

```xml
<plugin>
	<groupId>org.aroundthecode.pathfinder</groupId>
	<artifactId>pathfinder-maven-plugin</artifactId>
	<version>0.1.0-SNAPSHOT</version>
	<configuration>
		<neo4jProtocol>http</neo4jProtocol>
		<neo4jHost>127.0.0.1</neo4jHost>
		<neo4jPort>8080</neo4jPort>
		<neo4jPath>/</neo4jPath>
	</configuration>
	<executions>
		<execution>
			<id>store-tree</id>
			<phase>deploy</phase>
			<goals>
				<goal>store-tree</goal>
			</goals>
		</execution>
	</executions>
</plugin>
```

###  Invoke plugin directly from command line
If you want to test Pathfinder system before full integration you can run it directly from your project 'pom.xml' path
```sh
mvn org.aroundthecode.pathfinder:pathfinder-maven-plugin:0.1.0-SNAPSHOT:store-tree -Dneo4j.protocol=http -Dneo4j.host=localhost -Dneo4j.port=8080 -Dneo4j.path=/
```
Expected output is similar to:

```sh
mvn org.aroundthecode.pathfinder:pathfinder-maven-plugin:0.1.0-SNAPSHOT:store-tree 
[INFO] Scanning for projects...
[INFO]                                                                         
[INFO] ------------------------------------------------------------------------
[INFO] Building Pathfinder REST Client  - org.aroundthecode.pathfinder:pathfinder-rest-client:jar 0.1.0-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO] 
[INFO] --- pathfinder-maven-plugin:0.1.0-SNAPSHOT:store-tree (default-cli) @ pathfinder-rest-client ---
testing connection [http://localhost:8080/]...OK
[INFO] Project is:[org.aroundthecode.pathfinder:pathfinder-rest-client:jar:0.1.0-SNAPSHOT]
[INFO] Parent project is:[org.aroundthecode.pathfinder:pathfinder:pom::0.1.0-SNAPSHOT]
[INFO] org.aroundthecode.pathfinder:pathfinder-rest-client:jar::0.1.0-SNAPSHOT --(compile)--> com.googlecode.json-simple:json-simple:jar::1.1.1
[INFO] org.aroundthecode.pathfinder:pathfinder-rest-client:jar::0.1.0-SNAPSHOT --(compile)--> org.apache.logging.log4j:log4j-api:jar::2.5
[INFO] org.aroundthecode.pathfinder:pathfinder-rest-client:jar::0.1.0-SNAPSHOT --(compile)--> org.apache.logging.log4j:log4j-core:jar::2.5
[INFO] org.aroundthecode.pathfinder:pathfinder-rest-client:jar::0.1.0-SNAPSHOT --(test)--> junit:junit:jar::4.12
[INFO] junit:junit:jar::4.12 --(test)--> org.hamcrest:hamcrest-core:jar::1.3
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 1.242 s
[INFO] Finished at: 2016-01-03T23:50:53+01:00
[INFO] Final Memory: 12M/245M
[INFO] ------------------------------------------------------------------------
```

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)


   [Maven Dependency Plugin]: <https://maven.apache.org/plugins/maven-dependency-plugin/>
   [Michele Sacchetti]: <http://aroundthecode.org>
   [pathfinder-server]: <../pathfinder-server>
   [pathfinder-maven-plugin]: <../pathfinder-maven-plugin>
   [pathfinder-web]: <../pathfinder-web>


