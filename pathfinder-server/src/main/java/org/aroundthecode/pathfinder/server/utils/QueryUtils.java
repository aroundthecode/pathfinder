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

	public static String getFilterAllQuery(String filterGN1, String filterAN1, String filterPN1, String filterCN1, String filterVN1, String filterGN2, String filterAN2, String filterPN2, String filterCN2, String filterVN2) {
		return String.format(FILTERALL, filterGN1,filterAN1,filterPN1,filterCN1,filterVN1,filterGN2,filterAN2,filterPN2,filterCN2,filterVN2);
	}



}
