package plugin_suite.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.issue.search.SearchService.ParseResult;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.web.bean.PagerFilter;

import plugin_suite.models.ErrorRestResourceModel;
import plugin_suite.models.IssueRestResourceModel;

/**
 * A resource of issue data.
 */
@Path("/issues")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class IssueRestResource {
	@GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getIssueData(@QueryParam("key") String key, 
    		@QueryParam("issueTypeId") List<String> issueTypeIds,
    		@QueryParam("dateFieldId") String dateFieldId,
    		@QueryParam("earliestDate") String earliestDate) {
        if(key != null)
        	return Response.ok(new IssueRestResourceModel(
        			getIssueDataFromProject(key, issueTypeIds, dateFieldId, earliestDate)
        	).getIssueDataContainers()).build();
        else
            return Response.ok(new ErrorRestResourceModel("Invalid Key", "Please provide valid project key.")).build();
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{key}")
    public Response getIssueDataFromPath(@PathParam("key") String key) {
    	return Response.ok(new IssueRestResourceModel(
    			getIssueDataFromProject(key, null, null, null)
    	).getIssueDataContainers()).build();
    }
    
    /**
     * Gets parameters for issues within a given project, and returns a list of mappings for each 
     * significant field within the issue.
     * 
     * @param key - Project key associated with the desired issue(s).
     * @param issueTypeIds - Ids for acceptable issue types (null/empty value means all issue types 
     * are acceptable and no issues will be filtered out based on issue type).
     * @param dateFieldId - Id for date field containing the desired data. A null or empty value
     * means every custom field will be queried for each issue.
     * @param earliestDate - Date string (formatted as yyyy-MM-dd) representing the earliest 
     * acceptable data (based on the field associated with the dateFieldId). A null or empty 
     * value means data will not be filtered based on this date. Note that if the dateFieldId is null 
     * or empty, this field will be ignored.
     * @return List of field mappings for every issue within a given project.
     */
    private List<Map<String, Object>> getIssueDataFromProject(String key, List<String> issueTypeIds, 
    		String dateFieldId, String earliestDate) {
    	// Initializes issue data mapping
    	List<Map<String, Object>> issueDataMap = new ArrayList<Map<String, Object>>();
    	
    	// Obtains top level parameters responsible for querying certain important data points
    	ProjectManager projectManager = ComponentAccessor.getProjectManager();
		CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
		User user = ComponentAccessor.getJiraAuthenticationContext().getUser().getDirectoryUser();
		SearchService searchService = ComponentAccessor.getComponent(SearchService.class);
		
		// Gets desired project based on the key passed
		Project project = projectManager.getProjectObjByKey(key);
		
		// Constructs the issue type portion of the JQL query
		Collection<IssueType> issueTypes = ComponentAccessor.getConstantsManager().getAllIssueTypeObjects();
		String issueTypeStr = "";
		for (IssueType issueType : issueTypes) {
			if (issueTypeIds.contains(issueType.getId())) {
				if (issueTypeStr.equals(""))
					issueTypeStr = getQueryStr(issueType.getName());
				else
					issueTypeStr += ", " + getQueryStr(issueType.getName());
			}
		}
		
		// Gets custom field associated with given date field id
    	CustomField field = customFieldManager.getCustomFieldObject(dateFieldId);
    	
    	// Constructs JQL accordingly, starting with mandatory project key
		String jqlQuery = "project = " + getQueryStr(project.getName());
		
		// Adds issue type(s) to query, if any
		if (!issueTypeStr.isEmpty())
			jqlQuery += " AND type IN (" + issueTypeStr + ")";
			
		// Adds field and earliest date to query, if any
		if (field != null && earliestDate != null && !earliestDate.isEmpty())
			jqlQuery +=  " AND " + getQueryStr(field.getFieldName()) + " >= " + earliestDate;
		
		// Gets the results of parsing the JQL query
		ParseResult parseResult = searchService.parseQuery(user, jqlQuery);
		
		try {
			// Searches for issues that meet given criteria
			SearchResults searchResults = searchService.search(user,  parseResult.getQuery(), 
					PagerFilter.getUnlimitedFilter());
			List<Issue> issueList = searchResults.getIssues();
	    	
	    	for (Issue issue : issueList)
				updateDataMapList(issueDataMap, issue, project, field);
		} catch (SearchException e) {
			e.printStackTrace();
		}
		
		return issueDataMap;
    }
    
    /**
     * Constructs query string based on passed value. The surrounding 
     * quotation marks are added according.
     * 
     * @param initialStr - Initial string without surrounding quotation marks.
     * @return Initial string with surrounding quotation marks added.
     */
    private String getQueryStr(String initialStr) {
    	return "\"" + initialStr + "\"";
    }
    
    /**
	 * Updates given issue data mapping list based on given parameters.
	 * 
	 * @param issueList - List of mappings for issue parameters, which 
	 * will be updated by this function.
	 * @param issue - Issue object containing desirable data to be extracted.
	 * @param project - Project object associated with issue.
	 * @param dateField - Custom date field to be extracted (if null, all custom field 
	 * data for given issue will be extracted).
	 */
	private void updateDataMapList(List<Map<String, Object>> issueList, Issue issue, Project project, 
			CustomField dateField) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", issue.getId());
		map.put("key", issue.getKey());
		map.put("createdDate", issue.getCreated());
		map.put("projectId", project.getId());
		map.put("projectKey", project.getKey());
		map.put("projectName", project.getName());
		map.put("issueTypeId", issue.getIssueTypeId());
		map.put("issueTypeName", issue.getIssueTypeObject().getName());
		
		// Maps given custom field only, or all relevant custom field data if null
		Map<String, Object> fields = new HashMap<String, Object>();
		if (dateField != null) {
			fields.put(dateField.getId(), issue.getCustomFieldValue(dateField));
		} else {
			List<CustomField> fieldList = ComponentAccessor.getCustomFieldManager()
												.getCustomFieldObjects(issue);
			Object value;
			for (CustomField customField : fieldList) {
				value = issue.getCustomFieldValue(customField);
				
				// Only maps values that aren't null or empty
				if (value != null && value != "")
					fields.put(customField.getId(), value);
			}
		}
		map.put("fields", fields);
		
		issueList.add(map);
	}
}
