package plugin_suite.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectCategory;
import com.atlassian.jira.project.ProjectManager;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import plugin_suite.models.ProjectRestResourceModel;

/**
 * A resource for projects.
 */
@Path("/projects")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class ProjectRestResource {
	
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getProject(@QueryParam("key") String key) {
        if(key != null)
            return Response.ok(new ProjectRestResourceModel(getProjectParamFromKey(key)).getProjects()).build();
        else
            return Response.ok(new ProjectRestResourceModel(getAllProjectParams()).getProjects()).build();
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{key}")
    public Response getProjectFromPath(@PathParam("key") String key) {
        return Response.ok(new ProjectRestResourceModel(getProjectParamFromKey(key)).getProjects()).build();
    }
    
    /**
     * Gets parameters for every project.
     * 
     * @return List of mappings with project parameters.
     */
    private List<Map<String, Object>> getAllProjectParams() {
    	ProjectManager manager = ComponentAccessor.getProjectManager();
    	
    	// Initializes list of project parameter mappings
    	List<Map<String, Object>> paramList = new ArrayList<Map<String, Object>>();
    	
    	// Gets mappings for every project in Jira
    	for (Project project : manager.getProjectObjects())
    		paramList.add(getProjectParamFromKey(project.getKey()));
    	
    	return paramList;
    }
    
    /**
     * Obtains project parameters from Jira and returns the necessary ones.
     * 
     * @param key - Project key.
     * @return Map with project parameters.
     */
    private Map<String, Object> getProjectParamFromKey(String key) {
    	ProjectManager manager = ComponentAccessor.getProjectManager();
		Project project = manager.getProjectObjByKey(key);
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		// Sets project parameters
		if (project != null) {
			map.put("key", project.getKey());
			map.put("id", project.getId());
			map.put("name", project.getName());
			map.put("projectLead", project.getLeadUserName());
			
			// Gets project category data
			ProjectCategory category = project.getProjectCategoryObject();
			if (category != null)
				map.put("category", category.getName());
			else
				map.put("category", "N/A");
		}
    	
    	return map;
    }
}