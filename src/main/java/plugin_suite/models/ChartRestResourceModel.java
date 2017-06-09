package plugin_suite.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Resource model for charts.
 */
@XmlRootElement
public class ChartRestResourceModel {
	@XmlElement
	private String location;
	
	@XmlElement
	private String imageMap;

    @XmlElement
    private String imageMapName;
    
    @XmlElement
    private int width;

    @XmlElement
    private int height;

    @XmlElement
    private String base64Image;
     
    // Empty constructor required for XML, but doesn't need to be implemented
    public ChartRestResourceModel() {}
     
    public ChartRestResourceModel(String location, String imageMap, String imageMapName, int width, int height, 
    		 String base64Image) {
    	this.location = location;
    	this.imageMap = imageMap;
    	this.imageMapName = imageMapName;
    	this.width = width;
    	this.height = height;
    	this.base64Image = base64Image;
    }
}
