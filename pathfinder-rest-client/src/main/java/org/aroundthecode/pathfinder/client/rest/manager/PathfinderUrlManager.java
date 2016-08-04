package org.aroundthecode.pathfinder.client.rest.manager;

import org.aroundthecode.pathfinder.client.rest.manager.configuration.PathfinderConnectionConfiguration;
import org.aroundthecode.tools.remote.api.AbstractUrlManager;

public class PathfinderUrlManager extends AbstractUrlManager {

	public PathfinderUrlManager(PathfinderConnectionConfiguration conf) {
		super(conf);
	}

	@Override
	public String getUrlPath() {
		return PathfinderConnectionConfiguration.BASE_URL;
	}

}
