package dk.trustworks.invoicewebui.model.vimeo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Generated;

@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("com.robohorse.robopojogenerator")
public class Preferences{

	@JsonProperty("videos")
	private Videos videos;

	public void setVideos(Videos videos){
		this.videos = videos;
	}

	public Videos getVideos(){
		return videos;
	}

	@Override
 	public String toString(){
		return 
			"Preferences{" + 
			"videos = '" + videos + '\'' + 
			"}";
		}
}