package org.aroundthecode.pathfinder.server.crawler;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.Test;
import org.junit.internal.ExactComparisonCriteria;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class CrawlerWrapperTest {

    /**
     * This is a canary test of CrawlerWrapper setup.
     * Fails if Maven installation is not found or there is a problem with dummy pom
     */
    @Test
    public void testMavenInstallation() {

        System.out.println("CrawlerWrapperTest. Starting the test");

        DefaultInvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(CrawlerWrapper.getFilePom());
        System.out.println("CrawlerWrapperTest. after setPomFile()");
        request.setGoals(Collections.singletonList("clean"));
        System.out.println("CrawlerWrapperTest. after setGoals()");

        try {
            InvocationResult result = CrawlerWrapper.getInvoker().execute(request);
            assertNull("Invocation resulted in exception. " + result.getExecutionException().toString(), result.getExecutionException());
            assertEquals("Maven invocation on dummy pom did not succeed", 0, result.getExitCode());
        } catch (MavenInvocationException e) {
            fail("CrawlerWrapper did not find Maven installation. " + e.toString());
        } catch (RuntimeException rte) {
            rte.printStackTrace();
        }

    }

}