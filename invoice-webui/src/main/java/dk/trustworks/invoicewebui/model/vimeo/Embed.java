package dk.trustworks.invoicewebui.model.vimeo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;

@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("com.robohorse.robopojogenerator")
public class Embed{

	@JsonProperty("html")
	private String html;

	public void setHtml(String html){
		this.html = html;
	}

	public String getHtml(){
		return html;
	}

	@Override
 	public String toString(){
		return 
			"Embed{" + 
			"html = '" + html + '\'' + 
			"}";
		}
}