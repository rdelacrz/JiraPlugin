package plugin_suite.models;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "test")
@XmlAccessorType(XmlAccessType.FIELD)
public class TestRestResourceModel {

    @XmlElement(name = "value")
    private String message;
    
    @XmlAttribute
    private String key;

    public TestRestResourceModel() {
    }

    public TestRestResourceModel(String message) {
        this.message = message;
    }

    public TestRestResourceModel(String key, String message) {
        this.key = key;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
