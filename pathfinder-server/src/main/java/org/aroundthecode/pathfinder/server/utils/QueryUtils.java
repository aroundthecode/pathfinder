package org.aroundthecode.pathfinder.server.utils;

public class QueryUtils {

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
			+ "RETURN n1,type(r) as rel ,n2";

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
	    .append(" WHERE n"+idx+".groupId =~ \"" + f.getFilterGN1() + "\"")
		.append(" AND n"+idx+".artifactId =~ \"" + f.getFilterAN1() + "\"")
		.append(" AND n"+idx+".packaging =~ \"" + f.getFilterPN1() + "\"")
		.append(" AND n"+idx+".classifier =~ \"" + f.getFilterCN1() + "\"")
		.append(" AND n"+idx+".version =~ \"" + f.getFilterVN1() + "\"");
	    
	    idx++;
	    
	    whereclause
	    .append(" AND n"+idx+".groupId =~ \"" + f.getFilterGN2() + "\"")
		.append(" AND n"+idx+".artifactId =~ \"" + f.getFilterAN2() + "\"")
		.append(" AND n"+idx+".packaging =~ \"" + f.getFilterPN2() + "\"")
		.append(" AND n"+idx+".classifier =~ \"" + f.getFilterCN2() + "\"")
		.append(" AND n"+idx+".version =~ \"" + f.getFilterVN2() + "\"");
	    
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
		.append( "classifier: '" + classifier + "'" );
		
		query.append(fixedQuery)
	    .append(" })"+chain+" with n1 as node, [type(r1), n2] as relative")
	    .append(getSearchWhereClause(1,f) )
		.append(" RETURN { root: node, relatives: collect(relative) }");
		
		 for(int i = 2 ; i <= depth; i++){
		        chain += "-[r"+i+"]->(n"+(i+1)+")";

		        query.append(" UNION ")
		        .append(fixedQuery)
		        .append(" })"+chain+" with n"+i+" as node, [type(r"+i+"), n"+(i+1)+"] as relative")
		        .append( getSearchWhereClause(2,f) )
		        .append(" RETURN { root: node, relatives: collect(relative) }");
		    }
		
		return query.toString();
	}

}
