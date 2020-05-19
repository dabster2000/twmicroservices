package dk.trustworks.invoicewebui.model.vimeo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;

@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("com.robohorse.robopojogenerator")
public class Logos{

	@JsonProperty("vimeo")
	private boolean vimeo;

	@JsonProperty("custom")
	private Custom custom;

	public void setVimeo(boolean vimeo){
		this.vimeo = vimeo;
	}

	public boolean isVimeo(){
		return vimeo;
	}

	public void setCustom(Custom custom){
		this.custom = custom;
	}

	public Custom getCustom(){
		return custom;
	}

	@Override
 	public String toString(){
		return 
			"Logos{" + 
			"vimeo = '" + vimeo + '\'' + 
			",custom = '" + custom + '\'' + 
			"}";
		}
}