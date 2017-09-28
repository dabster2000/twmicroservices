package dk.trustworks.invoicewebui.model.vimeo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Generated;

@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("com.robohorse.robopojogenerator")
public class TagsItem{

	@JsonProperty("metadata")
	private Metadata metadata;

	@JsonProperty("resource_key")
	private String resourceKey;

	@JsonProperty("name")
	private String name;

	@JsonProperty("tag")
	private String tag;

	@JsonProperty("canonical")
	private String canonical;

	@JsonProperty("uri")
	private String uri;

	public void setMetadata(Metadata metadata){
		this.metadata = metadata;
	}

	public Metadata getMetadata(){
		return metadata;
	}

	public void setResourceKey(String resourceKey){
		this.resourceKey = resourceKey;
	}

	public String getResourceKey(){
		return resourceKey;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setTag(String tag){
		this.tag = tag;
	}

	public String getTag(){
		return tag;
	}

	public void setCanonical(String canonical){
		this.canonical = canonical;
	}

	public String getCanonical(){
		return canonical;
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
			"TagsItem{" + 
			"metadata = '" + metadata + '\'' + 
			",resource_key = '" + resourceKey + '\'' + 
			",name = '" + name + '\'' + 
			",tag = '" + tag + '\'' + 
			",canonical = '" + canonical + '\'' + 
			",uri = '" + uri + '\'' + 
			"}";
		}
}