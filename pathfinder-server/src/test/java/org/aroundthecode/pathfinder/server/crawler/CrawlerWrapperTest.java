package org.aroundthecode.pathfinder.server.crawler;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CrawlerWrapperTest {

    /**
     * This is a canary test of CrawlerWrapper setup.
     * Fails if Maven installation is not found or there is a problem with dummy pom
     */
    @Test
    public void testMavenInstallation() {

        DefaultInvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(CrawlerWrapper.getFilePom());
        request.setGoals(Collections.singletonList("clean"));

        try {
            InvocationResult result = CrawlerWrapper.getInvoker().execute(request);
            if (result.getExecutionException() != null) {
                fail("Invocation resulted in exception. " + result.getExecutionException());
            }
            assertEquals("Maven invocation on dummy pom did not succeed", 0, result.getExitCode());
        } catch (MavenInvocationException e) {
            fail("CrawlerWrapper did not find Maven installation. " + e);
        }

    }

}