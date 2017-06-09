package plugin_suite.resources;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.changehistory.ChangeHistory;
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager;
import com.atlassian.jira.issue.history.ChangeItemBean;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.Project;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.ofbiz.core.entity.GenericEntityException;

import plugin_suite.models.ErrorRestResourceModel;
import plugin_suite.models.HistoryRestResourceModel;

/**
 * A resource of change history.
 */
@Path("/changehistory")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class HistoryRestResource {
	
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getChangeHistory(@QueryParam("key") String key, @QueryParam("newStatusId") List<String> newStatusIds,
    		@QueryParam("excludeStatusId") List<String> excludeCurrStatusIds, @QueryParam("issueTypeId") List<String> issueTypeIds, 
    		@QueryParam("earliestDate") String earliestDate) {
        if(key != null)
        	return Response.ok(new HistoryRestResourceModel(getChangeHistoryForProject(
        				key, newStatusIds, issueTypeIds, earliestDate, excludeCurrStatusIds)).getChangeDataContainers()).build();
        else
            return Response.ok(new ErrorRestResourceModel("Invalid Key", "Please provide valid project key.")).build();
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{key}")
    public Response getChangeHistoryFromPath(@PathParam("key") String key) {
        return Response.ok(new HistoryRestResourceModel(getChangeHistoryForProject(key, null, null, null, null))
        		.getChangeDataContainers()).build();
    }
    
    /**
     * Obtains list of status changes in the form of a list of maps, using the 
     * project key and various ids as filters.
     * 
     * @param key - Project key.
     * @param newStatusIds - List of allowable new status ids from transitions (can be null).
     * @param issueTypeIds - List of ids for allowable issue types (can be null).
     * @param earliestDate - Earliest allowable date for created issues (can be null). 
     * Format of parameter must be: yyyy-mm-dd.
     * @param excludeCurrStatusIds - List of current status ids to exclude (can be null).
     * @return List of mappings with issue history parameters.
     */
	private List<Map<String, Object>> getChangeHistoryForProject(String key, List<String> newStatusIds, 
			List<String> issueTypeIds, String earliestDate, List<String> excludeCurrStatusIds) {
    	List<Map<String, Object>> changeList = new ArrayList<Map<String, Object>>();
    	
    	// Sets earliest date object, if any
    	Date date = null;
    	if (earliestDate != null && !earliestDate.equals("")) {
    		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    		try {
				date = dateFormat.parse(earliestDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
    	}
    	
    	// Gets project object for given key
    	ProjectManager projectManager = ComponentAccessor.getProjectManager();
    	Project project = projectManager.getProjectObjByKey(key);
    	
    	// Gets change history manager for later use
    	ChangeHistoryManager historyManager = ComponentAccessor.getChangeHistoryManager();
    	
    	// Gets current logged in user
    	User user = ComponentAccessor.getJiraAuthenticationContext().getUser().getDirectoryUser();
    	
    	// Gets all issues for given project and extracts their change histories
    	IssueManager issueManager = ComponentAccessor.getIssueManager();
    	try {
			Collection<Long> issueIds = issueManager.getIssueIdsForProject(project.getId());
			List<Issue> issueList = issueManager.getIssueObjects(issueIds);
			
			// Removes any issues outside of the allowable date and list of issue types
			for (Iterator<Issue> iterator = issueList.iterator(); iterator.hasNext();) {
				Issue issue = iterator.next();
				if ((date != null && issue.getUpdated().before(date)) 
						|| (issueTypeIds != null && !issueTypeIds.isEmpty() && !issueTypeIds.contains(issue.getIssueTypeId()))
						|| (excludeCurrStatusIds != null && !excludeCurrStatusIds.isEmpty() && excludeCurrStatusIds.contains(issue.getStatusId())))
					iterator.remove();
				
			}
			
			// Efficiently extracts change history data for given list of issues
			for (ChangeHistory history : historyManager.getChangeHistoriesForUser(issueList, user)) {
		    	Issue issue = history.getIssue();		// Gets corresponding issue for history
		    	updateDataMapList(changeList, issue, project, date, newStatusIds, history);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
    	
    	return changeList;
    }
	
	/**
	 * Updates given change history mapping list based on change history and 
	 * other parameters.
	 * 
	 * @param changeList - List of mappings for change history parameters, which 
	 * will be updated by this function.
	 * @param issue - Issue object associated with change history.
	 * @param project - Project object associated with change history.
	 * @param earliestDate - Earliest allowable transition.
	 * @param newStatusIds - Ids of new status associated with status transition. 
	 * If this parameter isn't null, the only transitions that will be mapped will 
	 * be the ones whose new status ids match the given newStatusId value.
	 * @param history - Change History object containing all the change history 
	 * data to extract.
	 */
	private void updateDataMapList(List<Map<String, Object>> changeList, Issue issue, Project project, 
			Date earliestDate, List<String> newStatusIds, ChangeHistory history) {
		// Iterates through the change history of the given issue
		for (ChangeItemBean changeItemBean : history.getChangeItemBeans()) {
			if (changeItemBean.getField().equals("status") && 
					(earliestDate == null || !changeItemBean.getCreated().before(earliestDate))  &&
					(newStatusIds == null || newStatusIds.isEmpty() || newStatusIds.contains(changeItemBean.getTo()))) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("changeDate", changeItemBean.getCreated());
				map.put("id", issue.getId());
				map.put("key", issue.getKey());
				map.put("projectId", project.getId());
	    		map.put("projectKey", project.getKey());
	   			map.put("projectName", project.getName());
	   			map.put("issueTypeId", issue.getIssueTypeId());
				map.put("issueTypeName", issue.getIssueTypeObject().getName());
	   			map.put("oldStatusId", changeItemBean.getFrom());
	   			map.put("oldStatus", changeItemBean.getFromString());
	   			map.put("newStatusId", changeItemBean.getTo() + " " + issue.getIssueTypeId());
	   			map.put("newStatus", changeItemBean.getToString());
    			changeList.add(map);
			}
		}
	}
}