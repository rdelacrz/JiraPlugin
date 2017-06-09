package it.plugin_suite;

import org.junit.Test;
import org.junit.runner.RunWith;

import plugin_suite.resources.TestRestResource;

import com.atlassian.plugins.osgi.test.AtlassianPluginsTestRunner;
import com.atlassian.sal.api.ApplicationProperties;

import static org.junit.Assert.assertEquals;

@RunWith(AtlassianPluginsTestRunner.class)
public class MyComponentWiredTest
{
    private final ApplicationProperties applicationProperties;
    private final TestRestResource myPluginComponent;

    public MyComponentWiredTest(ApplicationProperties applicationProperties,TestRestResource myPluginComponent)
    {
        this.applicationProperties = applicationProperties;
        this.myPluginComponent = myPluginComponent;
    }

    @Test
    public void testMyName()
    {
        assertEquals("names do not match!", "REST Component:" + applicationProperties.getDisplayName(),myPluginComponent.getName());
    }
}