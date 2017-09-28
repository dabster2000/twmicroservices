package dk.trustworks.invoicewebui.model.vimeo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("com.robohorse.robopojogenerator")
public class Pictures{

	@JsonProperty("sizes")
	private List<SizesItem> sizes;

	@JsonProperty("resource_key")
	private String resourceKey;

	@JsonProperty("active")
	private boolean active;

	@JsonProperty("type")
	private String type;

	@JsonProperty("uri")
	private String uri;

	public void setSizes(List<SizesItem> sizes){
		this.sizes = sizes;
	}

	public List<SizesItem> getSizes(){
		return sizes;
	}

	public void setResourceKey(String resourceKey){
		this.resourceKey = resourceKey;
	}

	public String getResourceKey(){
		return resourceKey;
	}

	public void setActive(boolean active){
		this.active = active;
	}

	public boolean isActive(){
		return active;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
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
			"Pictures{" + 
			"sizes = '" + sizes + '\'' + 
			",resource_key = '" + resourceKey + '\'' + 
			",active = '" + active + '\'' + 
			",type = '" + type + '\'' + 
			",uri = '" + uri + '\'' + 
			"}";
		}
}