package dk.trustworks.invoicewebui.model.vimeo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("com.robohorse.robopojogenerator")
public class Activities{

	@JsonProperty("options")
	private List<String> options;

	@JsonProperty("uri")
	private String uri;

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
			"Activities{" + 
			"options = '" + options + '\'' + 
			",uri = '" + uri + '\'' + 
			"}";
		}
}