package dk.trustworks.invoicewebui.model.vimeo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;

@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("com.robohorse.robopojogenerator")
public class Stats{

	@JsonProperty("plays")
	private int plays;

	public void setPlays(int plays){
		this.plays = plays;
	}

	public int getPlays(){
		return plays;
	}

	@Override
 	public String toString(){
		return 
			"Stats{" + 
			"plays = '" + plays + '\'' + 
			"}";
		}
}