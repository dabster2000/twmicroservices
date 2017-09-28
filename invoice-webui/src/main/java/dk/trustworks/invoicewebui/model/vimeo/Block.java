package dk.trustworks.invoicewebui.model.vimeo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("com.robohorse.robopojogenerator")
public class Block{

	@JsonProperty("added_time")
	private Object addedTime;

	@JsonProperty("added")
	private boolean added;

	@JsonProperty("options")
	private List<String> options;

	@JsonProperty("uri")
	private String uri;

	public void setAddedTime(Object addedTime){
		this.addedTime = addedTime;
	}

	public Object getAddedTime(){
		return addedTime;
	}

	public void setAdded(boolean added){
		this.added = added;
	}

	public boolean isAdded(){
		return added;
	}

	public void setOptions(List<String> options){
		this.options = options;
	}

	public List<String> getOptions(){
		return options;
	}

	public void setUri(String uri){
		this.uri = uri;
	}

	public String getUri(){
		return uri;
	}

	@Override
 	public String toString(){
		return 
			"Block{" + 
			"added_time = '" + addedTime + '\'' + 
			",added = '" + added + '\'' + 
			",options = '" + options + '\'' + 
			",uri = '" + uri + '\'' + 
			"}";
		}
}