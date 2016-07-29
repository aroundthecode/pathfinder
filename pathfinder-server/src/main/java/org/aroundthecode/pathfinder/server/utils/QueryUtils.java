package org.aroundthecode.pathfinder.server.utils;

import org.aroundthecode.pathfinder.client.rest.items.FilterItem;

/**
 * Utility class to manage Cypher queries
 * @author msacchetti
 *
 */
public class QueryUtils {

	private QueryUtils() {
		throw new IllegalAccessError("Utility class");
	}
	
	private static final String FILTERALL = 
			"MATCH n1-[r]->n2 WHERE "
			+ "n1.groupId =~ \"%s\" AND "
			+ "n1.artifactId =~ \"%s\" AND "
			+ "n1.packaging =~ \"%s\" AND "
			+ "n1.classifier =~ \"%s\" AND "
			+ "n1.version =~ \"%s\" AND "
			+ "n2.groupId =~ \"%s\" AND "
			+ "n2.artifactId =~ \"%s\" AND "
			+ "n2.packaging =~ \"%s\" AND "
			+ "n2.classifier =~ \"%s\" AND "
			+ "n2.version =~ \"%s\" "
			+ "RETURN n1 as node1,type(r) as rel ,n2 as node2";

	public static String getFilterAllQuery(FilterItem f) {
		return String.format(FILTERALL, f.getFilterGN1(),f.getFilterAN1(),f.getFilterPN1(),f.getFilterCN1(),f.getFilterVN1(),f.getFilterGN2(),f.getFilterAN2(),f.getFilterPN2(),f.getFilterCN2(),f.getFilterVN2());
	}

	
	private static String getSearchValue(String key , String val) {
	    String s = "";
		if (val != null && val.length() > 0) {
	    	s = key + ":'" + val + "' ,";
	    }
	    return s;
	}
	
	private static String getSearchWhereClause(int idx, FilterItem f){

	    StringBuffer whereclause = new StringBuffer("");

	    whereclause
	    .append(" WHERE n").append(idx).append(".groupId =~ \"").append(f.getFilterGN1()).append("\"")
		.append(" AND n").append(idx).append(".artifactId =~ \"").append(f.getFilterAN1()).append("\"")
		.append(" AND n").append(idx).append(".packaging =~ \"").append(f.getFilterPN1()).append("\"")
		.append(" AND n").append(idx).append(".classifier =~ \"").append(f.getFilterCN1()).append("\"")
		.append(" AND n").append(idx).append(".version =~ \"").append(f.getFilterVN1()).append("\"");
	    
	    idx++;
	    
	    whereclause
	    .append(" AND n").append(idx).append(".groupId =~ \"").append(f.getFilterGN2()).append("\"")
		.append(" AND n").append(idx).append(".artifactId =~ \"").append(f.getFilterAN2()).append("\"")
		.append(" AND n").append(idx).append(".packaging =~ \"").append(f.getFilterPN2()).append("\"")
		.append(" AND n").append(idx).append(".classifier =~ \"").append(f.getFilterCN2()).append("\"")
		.append(" AND n").append(idx).append(".version =~ \"").append(f.getFilterVN2()).append("\"");
	    
	    return whereclause.toString();
	}
	
	public static String getImpactQuery(int depth, String groupId,String artifactId,String packaging,String classifier,String version, FilterItem f){
		
		StringBuffer query = new StringBuffer();
		String chain = "-[r1]->(n2)";
		
		StringBuffer fixedQuery = new StringBuffer();
		fixedQuery.append("MATCH (n1:Artifact { ")
		.append( getSearchValue("groupId", groupId) )
		.append( getSearchValue("artifactId", artifactId) )
		.append( getSearchValue("packaging", packaging) )
		.append( getSearchValue("version", version) )
		.append( "classifier: '").append(classifier).append("'" );
		
		query.append(fixedQuery)
	    .append(" })")
	    .append(chain)
	    .append(getSearchWhereClause(1,f) )
		.append(" RETURN n1 as node1,type(r1) as rel ,n2 as node2");
		
		 for(int i = 2 ; i <= depth; i++){
		        chain += "-[r"+i+"]->(n"+(i+1)+")";

		        query.append(" UNION ")
		        .append(fixedQuery)
		        .append(" })")
		        .append(chain)
		        .append( getSearchWhereClause(2,f) )
		        .append(" RETURN n"+(i+1)+" as node1,type(r"+i+") as rel ,n"+(i+1)+" as node2" );
		    }
		
		return query.toString();
	}

}
