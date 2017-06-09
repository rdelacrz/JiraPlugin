package plugin_suite.models;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "error")
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorRestResourceModel {

	@XmlAttribute
    private String type;
	
    @XmlElement(name = "value")
    private String message;

    public ErrorRestResourceModel() {
    }

    public ErrorRestResourceModel(String message) {
        this.message = message;
    }

    public ErrorRestResourceModel(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorType() {
        return type;
    }

    public void setErrorType(String type) {
        this.type = type;
    }
}
