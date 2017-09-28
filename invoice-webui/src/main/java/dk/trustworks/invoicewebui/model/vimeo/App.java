package dk.trustworks.invoicewebui.model.vimeo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Generated;

@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("com.robohorse.robopojogenerator")
public class App{

	@JsonProperty("name")
	private String name;

	@JsonProperty("uri")
	private String uri;

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
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
			"App{" + 
			"name = '" + name + '\'' + 
			",uri = '" + uri + '\'' + 
			"}";
		}
}