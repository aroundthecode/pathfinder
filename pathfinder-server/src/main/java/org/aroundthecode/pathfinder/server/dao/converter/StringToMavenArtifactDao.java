package org.aroundthecode.pathfinder.server.dao.converter;

import org.aroundthecode.pathfinder.server.dao.MavenArtifactDao;
import org.springframework.core.convert.converter.Converter;

/**
 * Converts from a {@link String} to a {@link MavenArtifactDao}
 */
public class StringToMavenArtifactDao implements Converter<String, MavenArtifactDao> {


	@Override
	public MavenArtifactDao convert(String artifact) {

		MavenArtifactDao out = null;
		if(artifact!=null){
			String[] tokens = artifact.split(":");
			if(tokens.length == 3){
				out = new MavenArtifactDao(tokens[0], tokens[1], tokens[2]);
			}

			if (tokens.length == 5) {
				out.setType(tokens[3]);
				out.setClassifier(tokens[4]);

			} 
		}
		return out;
	}

}