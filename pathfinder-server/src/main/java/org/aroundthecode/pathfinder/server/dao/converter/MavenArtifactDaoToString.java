package org.aroundthecode.pathfinder.server.dao.converter;

import org.aroundthecode.pathfinder.server.dao.MavenArtifactDao;
import org.springframework.core.convert.converter.Converter;

/**
 * Converts from a {@link MavenArtifactDao} to a {@link String}
 */
public class MavenArtifactDaoToString implements Converter<MavenArtifactDao, String> {


	@Override
	public String convert(MavenArtifactDao a) {
		return a.toString();
	}

}