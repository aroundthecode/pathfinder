# Pathfinder

Pathfinder is a develper / release manager assistant which will help you:
  - Trace all you projects dependencies and cross-dependencies
  - Create a "release path" to optimize your delivery time
  - Know every project your current code is actually included

## Invoking pathfinder 
Pathfinder dependency analyser is available as an extension of [Maven Dependency Plugin] which will analize dependency hierarchy in the very same wat dependency:tree goas does, but it will store output into a Neo4J Graph database for later retrieval

### Configure plugin in your maven hierarchy
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
			<id>test-store-tree</id>
			<phase>test</phase>
			<goals>
				<goal>store-tree</goal>
			</goals>
		</execution>
	</executions>
</plugin>
```

###  Invoke plugin directly from command line
run it directly from your project 'pom.xml' path
```sh
mvn org.aroundthecode.pathfinder:pathfinder-maven-plugin:0.1.0-SNAPSHOT:store-tree -Dneo4j.protocol=http -Dneo4j.host=localhost -Dneo4j.port=8080 -Dneo4j.path=/
```
#### Author
[Michele Sacchetti]

### Version
0.1.0-SNAPSHOT (Yes, still work in progress!)

### License
Apache 2

**Free Software, Hell Yeah!**

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)


   [Maven Dependency Plugin]: <https://maven.apache.org/plugins/maven-dependency-plugin/>
   [Michele Sacchetti]: <http://aroundthecode.org>
   



