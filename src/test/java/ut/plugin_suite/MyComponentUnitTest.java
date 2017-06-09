package ut.plugin_suite;

import org.junit.Test;

import plugin_suite.models.TestRestResourceModel;
import plugin_suite.resources.TestRestResource;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        TestRestResource component = new TestRestResource();
        assertEquals("names do not match!", "REST Component", component.getName());
    }
    
    @Test
    public void testGetProject()
    {
        //RESTComponent component = new RESTComponentImpl(null);
        //System.out.println(component.getProjectName(""));
        assertTrue(true);
    }
    
    @Test
    public void messageIsValid() {
        TestRestResource resource = new TestRestResource();

        Response response = resource.getMessage("Hello World");
        final TestRestResourceModel message = (TestRestResourceModel) response.getEntity();

        assertEquals("Wrong message!", "Hello World", message.getMessage());
    }
    
    @Test
    public void messageIsValid2() {
        TestRestResource resource = new TestRestResource();

        Response response = resource.getMessage(null);
        final TestRestResourceModel message = (TestRestResourceModel) response.getEntity();

        assertEquals("Wrong message!", "Please input a valid key...", message.getMessage());
    }
}