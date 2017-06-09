package plugin_suite.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.*;

/**
 * Resource model for projects.
 */
@XmlRootElement(name = "projectData")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectRestResourceModel {
	@XmlElement
	private List<ProjectDataContainer> projects;
	
	public ProjectRestResourceModel() {
		this.projects = new ArrayList<ProjectDataContainer>();
	}
	
	/**
	 * Constructor that produces a single Project Data Container and adds it to
	 * the list.
	 * 
	 * @param parameters - Mapping of project data parameters.
	 */
	public ProjectRestResourceModel(Map<String, Object> parameters) {
		this.projects = new ArrayList<ProjectDataContainer>();
		this.projects.add(new ProjectDataContainer(parameters));
    }
	
	/**
	 * Constructor that produces a list of Project Data Containers.
	 * 
	 * @param parameterList - List of mappings of project data parameters.
	 */
	public ProjectRestResourceModel(List<Map<String, Object>> parameterList) {
		this.projects = new ArrayList<ProjectDataContainer>();
		
		for (Map<String, Object> parameters : parameterList) {
			if (parameters != null && !parameters.isEmpty())
				this.projects.add(new ProjectDataContainer(parameters));
		}
    }
	
	/**
	 * Get list of Project Data Containers containing data for the various projects.
	 * 
	 * @return List of project data containers.
	 */
	public List<ProjectDataContainer> getProjects() {
		return projects;
	}
	
	
	/**
	 * Inner class that encapsulates the parameters of a single project.
	 */
	@XmlRootElement(name = "project")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ProjectDataContainer implements Comparable<ProjectDataContainer> {
		@XmlElement
		private String key;
		
		@XmlElement
		private long id;
		
		@XmlElement
		private String name;
		
		@XmlElement
		private String projectLead;
		
		@XmlElement
		private String category;
		
		public ProjectDataContainer() {
		}
		
		public ProjectDataContainer(Map<String, Object> parameters) {
			this.key = (String) parameters.get("key");
			this.id = (Long) parameters.get("id");
			this.name = (String) parameters.get("name");
			this.projectLead = (String) parameters.get("projectLead");
			this.category = (String) parameters.get("category");
	    }
		
		public ProjectDataContainer(String key, long id, String name, 
				String projectLead, String category) {
			this.key = key;
			this.id = id;
			this.name = name;
			this.projectLead = projectLead;
			this.category = category;
	    }
		
		public int compareTo(ProjectDataContainer container) {
			return name.compareTo(container.name);
		}
	}
}
