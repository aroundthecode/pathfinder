package org.aroundthecode.pathfinder.server.crawler;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.Test;

public class CrawlerTest {

	@Test
	public void testCrawler() {

		try {
			CrawlerWrapper.crawl("", "", "", "", "");
		} catch (MavenInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
