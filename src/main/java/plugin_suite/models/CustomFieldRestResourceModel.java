package plugin_suite.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.*;

/**
 * Resource model for custom fields.
 */
@XmlRootElement(name = "customFieldData")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomFieldRestResourceModel {
	@XmlElement
	private List<CustomFieldDataContainer> customFields;
	
	public CustomFieldRestResourceModel() {
		this.customFields = new ArrayList<CustomFieldDataContainer>();
	}
	
	/**
	 * Constructor that produces a single Custom Field Container and adds it to
	 * the list.
	 * 
	 * @param parameters - Mapping of custom field data parameters.
	 */
	public CustomFieldRestResourceModel(Map<String, Object> parameters) {
		this.customFields = new ArrayList<CustomFieldDataContainer>();
		this.customFields.add(new CustomFieldDataContainer(parameters));
    }
	
	/**
	 * Constructor that produces a list of Custom Field Data Containers.
	 * 
	 * @param parameterList - List of mappings of custom field data parameters.
	 */
	public CustomFieldRestResourceModel(List<Map<String, Object>> parameterList) {
		this.customFields = new ArrayList<CustomFieldDataContainer>();
		
		for (Map<String, Object> parameters : parameterList) {
			this.customFields.add(new CustomFieldDataContainer(parameters));
		}
    }
	
	/**
	 * Get list of Custom Field Data Containers containing data for the various custom fields.
	 * 
	 * @return List of custom field data containers.
	 */
	public List<CustomFieldDataContainer> getFields() {
		return customFields;
	}
	
	
	/**
	 * Inner class that encapsulates the parameters of a single custom field.
	 */
	@XmlRootElement(name = "customField")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class CustomFieldDataContainer implements Comparable<CustomFieldDataContainer> {
		@XmlElement
		private String id;
		
		@XmlElement
		private String name;
		
		@XmlElement
		private String typeKey;
		
		@XmlElement
		private String typeName;
		
		public CustomFieldDataContainer() {
		}
		
		public CustomFieldDataContainer(Map<String, Object> parameters) {
			this.id = (String) parameters.get("id");
			this.name = (String) parameters.get("name");
			this.typeKey = (String) parameters.get("typeKey");
			this.typeName = (String) parameters.get("typeName");
	    }
		
		public CustomFieldDataContainer(String id, String key, String name, 
				String typeKey, String typeName, List<String> valueList) {
			this.id = id;
			this.name = name;
			this.typeKey = typeKey;
			this.typeName = typeName;
	    }
		
		public int compareTo(CustomFieldDataContainer container) {
			return name.compareTo(container.name);
		}
	}
}
