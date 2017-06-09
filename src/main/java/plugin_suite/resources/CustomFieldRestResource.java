package plugin_suite.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;

import plugin_suite.models.CustomFieldRestResourceModel;

/**
 * A resource for custom fields.
 */
@Path("/customfields")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class CustomFieldRestResource {
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getField(@QueryParam("id") Long id) {
        if(id != null)
            return Response.ok(
            	new CustomFieldRestResourceModel(getCustomFieldParamsById(id)).getFields()
            ).build();
        else
            return Response.ok(
            	new CustomFieldRestResourceModel(getCustomFieldParams(null)).getFields()
            ).build();
    }
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/{id}")
	public Response getFieldFromPath(@PathParam("id") Long id) {
		return Response.ok(
			new CustomFieldRestResourceModel(getCustomFieldParamsById(id)).getFields()
		).build();
    }
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/datefields")
	public Response getDateFields() {
		return Response.ok(
			new CustomFieldRestResourceModel(getCustomFieldParams(".*Date.*")).getFields()
		).build();
    }
	
	/**
     * Gets parameters for every custom field of the given type (or all if the list of
     * field types is null or empty).
     * 
     * @param fieldTypeRegex - Regular expression for acceptable field type(s).
     * @return List of mappings with custom field parameters.
     */
    private List<Map<String, Object>> getCustomFieldParams(String fieldTypeRegex) {
    	CustomFieldManager manager = ComponentAccessor.getCustomFieldManager();
    	
    	// Initializes list of custom field parameter mappings
    	List<Map<String, Object>> paramList = new ArrayList<Map<String, Object>>();
    	
    	// Initializes pattern for regex operations
    	Pattern p = null;
    	if (fieldTypeRegex != null)
    		p = Pattern.compile(fieldTypeRegex);
    	
    	// Gets mappings for the desired custom fields in Jira
    	for (CustomField field : manager.getCustomFieldObjects())
    		if (p == null)
    			paramList.add(getCustomFieldParamHelper(field));
    		else {
    			Matcher m = p.matcher(field.getCustomFieldType().getName());
    			if (m.matches())
    				paramList.add(getCustomFieldParamHelper(field));
    		}
    	
    	return paramList;
    }
    
    /**
     * Gets parameters for a custom field with the given id.
     * 
     * @param id - Id of custom field.
     * @return Mappings with the given custom field's parameters.
     */
    private Map<String, Object> getCustomFieldParamsById(Long id) {
    	CustomFieldManager manager = ComponentAccessor.getCustomFieldManager();
    	
    	// Gets mappings for given custom field
    	return getCustomFieldParamHelper(manager.getCustomFieldObject(id));
    }
    
    /**
     * Obtains parameters of given custom field from Jira and returns the necessary ones.
     * 
     * @param field - Custom field object.
     * @return Map with custom field parameters.
     */
    private Map<String, Object> getCustomFieldParamHelper(CustomField field) {
    	// Initializes map of custom field parameter mappings
    	Map<String, Object> map = new HashMap<String, Object>();
    	
    	// Sets custom field parameters
    	if (field != null) {
    		map.put("id", field.getId());
    		map.put("name", field.getFieldName());
    		map.put("typeKey", field.getCustomFieldType().getKey());
    		map.put("typeName", field.getCustomFieldType().getName());
    	}
    	
		return map;
    }
}
