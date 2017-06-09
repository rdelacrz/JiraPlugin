package plugin_suite.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.*;

/**
 * Resource model for issue change history.
 */
@XmlRootElement(name = "changehistory")
@XmlAccessorType(XmlAccessType.FIELD)
public class HistoryRestResourceModel {
	
	// List of issue history containers, sorted by change date
	@XmlElement(name = "issues")
	private List<ChangeDataContainer> changeList;
	
	/**
	 * Constructor that simply initializes an empty list of mappings.
	 */
	public HistoryRestResourceModel() {
		this.changeList = new ArrayList<ChangeDataContainer>();
	}
	
	/**
	 * Constructor that accepts a list of mappings, with each mapping
	 * linking field names to values from a single change.
	 * 
	 * @param issueList - List of field > value mappings for every issue change.
	 */
	public HistoryRestResourceModel(List<Map<String, Object>> issueList) {
		this.changeList = new ArrayList<ChangeDataContainer>();
		
		// Goes through list of mappings to get change data
		for(Map<String, Object> issueHistory : issueList) {
			this.changeList.add(new ChangeDataContainer(issueHistory));
		}
		
		// Sorts the change list items
		Collections.sort(this.changeList);
    }
	
	/**
	 * Adds a Change Data Container to the current list.
	 * 
	 * @param changeData - Change Data Container with one instance of change 
	 * history data.
	 */
	public void addChangeData(ChangeDataContainer changeData) {
		changeList.add(changeData);
	}
	
	/**
	 * Get list of Change Data Container objects, with each individual one 
	 * containing change history data for a given issue at a given date.
	 * 
	 * @return List of ChangeDataContainer objects with change history data.
	 */
	public List<ChangeDataContainer> getChangeDataContainers() {
		return changeList;
	}
	
	
	/**
	 * Inner class that encapsulates the change history data for a single issue.
	 */
	@XmlRootElement(name = "issue")
	public static class ChangeDataContainer implements Comparable<ChangeDataContainer> {
		@XmlElement
		private Long id;
		
		@XmlElement
		private String key;
		
		@XmlElement
		private Date changeDate;
		
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
		private String oldStatusId;
		
		@XmlElement
		private String oldStatus;
		
		@XmlElement
		private String newStatusId;
		
		@XmlElement
		private String newStatus;
		
		public ChangeDataContainer() {
		}
		
		/**
		 * Constructor that accepts a mapping of field names to values (the 
		 * data points of a single change) and uses it to populate the 
		 * container's parameters.
		 * 
		 * @param dataMap - Mapping of field names > values.
		 */
		public ChangeDataContainer(Map<String, Object> dataMap) {
			this.changeDate = (Date) dataMap.get("changeDate");
			this.id = (Long) dataMap.get("id");
			this.key = (String) dataMap.get("key");
			this.projectId = (Long) dataMap.get("projectId");
			this.projectKey = (String) dataMap.get("projectKey");
			this.projectName = (String) dataMap.get("projectName");
			this.issueTypeId = (String) dataMap.get("issueTypeId");
			this.issueTypeName = (String) dataMap.get("issueTypeName");
			this.oldStatusId = (String) dataMap.get("oldStatusId");
			this.oldStatus = (String) dataMap.get("oldStatus");
			this.newStatusId = (String) dataMap.get("newStatusId");
			this.newStatus = (String) dataMap.get("newStatus");
		}

		public int compareTo(ChangeDataContainer container) {
			return this.changeDate.compareTo(container.changeDate);
		}
	}
}
