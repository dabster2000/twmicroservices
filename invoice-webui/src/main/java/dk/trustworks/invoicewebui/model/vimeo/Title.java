package dk.trustworks.invoicewebui.model.vimeo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;

@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("com.robohorse.robopojogenerator")
public class Title{

	@JsonProperty("owner")
	private String owner;

	@JsonProperty("name")
	private String name;

	@JsonProperty("portrait")
	private String portrait;

	public void setOwner(String owner){
		this.owner = owner;
	}

	public String getOwner(){
		return owner;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setPortrait(String portrait){
		this.portrait = portrait;
	}

	public String getPortrait(){
		return portrait;
	}

	@Override
 	public String toString(){
		return 
			"Title{" + 
			"owner = '" + owner + '\'' + 
			",name = '" + name + '\'' + 
			",portrait = '" + portrait + '\'' + 
			"}";
		}
}