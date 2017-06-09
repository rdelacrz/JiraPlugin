package plugin_suite.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.*;

/**
 * Resource model for issue data.
 */
@XmlRootElement(name = "issue")
@XmlAccessorType(XmlAccessType.FIELD)
public class IssueRestResourceModel {
	
	// List of issue data containers, sorted by creation date
	@XmlElement(name = "issues")
	private List<IssueDataContainer> issueList;
	
	/**
	 * Constructor that simply initializes an empty list of mappings.
	 */
	public IssueRestResourceModel() {
		this.issueList = new ArrayList<IssueDataContainer>();
	}
	
	/**
	 * Constructor that accepts a list of mappings, with each mapping
	 * linking field names to values for a single issue.
	 * 
	 * @param issueList - List of field > value mappings for every issue.
	 */
	public IssueRestResourceModel(List<Map<String, Object>> issueList) {
		this.issueList = new ArrayList<IssueDataContainer>();
		
		// Goes through list of mappings to get issue data
		for(Map<String, Object> issueData : issueList) {
			this.issueList.add(new IssueDataContainer(issueData));
		}
		
		// Sorts the issues
		Collections.sort(this.issueList);
    }
	
	/**
	 * Adds a Issue Data Container to the current list.
	 * 
	 * @param issueData - Issue Data Container with issue data.
	 */
	public void addIssueData(IssueDataContainer issueData) {
		issueList.add(issueData);
	}
	
	/**
	 * Get list of Issue Data Container objects, with each individual one 
	 * containing issue data for a given issue at a given date.
	 * 
	 * @return List of IssueDataContainer objects with issue data.
	 */
	public List<IssueDataContainer> getIssueDataContainers() {
		return issueList;
	}
	
	
	/**
	 * Inner class that encapsulates the data for a single issue.
	 */
	@XmlRootElement(name = "issue")
	public static class IssueDataContainer implements Comparable<IssueDataContainer> {
		@XmlElement
		private Long id;
		
		@XmlElement
		private String key;
		
		@XmlElement
		private Date createdDate;
		
		@XmlElement
		private Long projectId;
		
		@XmlElement
		private String projectKey;
		
		@XmlElement
		private String projectName;
		
		@XmlElement
		private String issueTypeId;
		
		@XmlElement
		private String issueTypeName;
		
		@XmlElement
		private Map<String, Object> fields;
		
		public IssueDataContainer() {
		}
		
		/**
		 * Constructor that accepts a mapping of field names to values and uses 
		 * it to populate the container's parameters.
		 * 
		 * @param dataMap - Mapping of field names > values.
		 */
		@SuppressWarnings("unchecked")
		public IssueDataContainer(Map<String, Object> dataMap) {
			this.id = (Long) dataMap.get("id");
			this.key = (String) dataMap.get("key");
			this.createdDate = (Date) dataMap.get("createdDate");
			this.projectId = (Long) dataMap.get("projectId");
			this.projectKey = (String) dataMap.get("projectKey");
			this.projectName = (String) dataMap.get("projectName");
			this.issueTypeId = (String) dataMap.get("issueTypeId");
			this.issueTypeName = (String) dataMap.get("issueTypeName");
			this.fields = (Map<String, Object>) dataMap.get("fields");
		}

		public int compareTo(IssueDataContainer container) {
			return this.createdDate.compareTo(container.createdDate);
		}
	}
}
