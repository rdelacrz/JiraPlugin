package plugin_suite.resources;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import plugin_suite.models.TestRestResourceModel;


/**
 * A resource of test.
 */
@Path("/test")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class TestRestResource {
	private String name;
	private String defaultMsg;
	
	public TestRestResource() {
		name = "REST Component";
		defaultMsg = "Please input a valid key...";
	}
	
	public String getName() {
		return name;
	}
	
    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getMessage(@QueryParam("key") String key)
    {
        if(key!=null)
            return Response.ok(new TestRestResourceModel(key, getMessageFromKey(key))).build();
        else
            return Response.ok(new TestRestResourceModel("default", defaultMsg)).build();
    }
    
    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/{key}")
    public Response getMessageFromPath(@PathParam("key") String key)
    {
        return Response.ok(new TestRestResourceModel(key, getMessageFromKey(key))).build();
    }
    
    private String getMessageFromKey(String key) {
    	   // In reality, this data would come from a database or some component
    	   // within the hosting application, for demonstration purposes I will
    	   // just return the key
    	   return key;
    }
}