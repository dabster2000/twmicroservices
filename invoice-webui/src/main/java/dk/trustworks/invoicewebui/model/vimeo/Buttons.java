package dk.trustworks.invoicewebui.model.vimeo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Generated;

@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("com.robohorse.robopojogenerator")
public class Buttons{

	@JsonProperty("scaling")
	private boolean scaling;

	@JsonProperty("fullscreen")
	private boolean fullscreen;

	@JsonProperty("like")
	private boolean like;

	@JsonProperty("watchlater")
	private boolean watchlater;

	@JsonProperty("share")
	private boolean share;

	@JsonProperty("embed")
	private boolean embed;

	@JsonProperty("hd")
	private boolean hd;

	public void setScaling(boolean scaling){
		this.scaling = scaling;
	}

	public boolean isScaling(){
		return scaling;
	}

	public void setFullscreen(boolean fullscreen){
		this.fullscreen = fullscreen;
	}

	public boolean isFullscreen(){
		return fullscreen;
	}

	public void setLike(boolean like){
		this.like = like;
	}

	public boolean isLike(){
		return like;
	}

	public void setWatchlater(boolean watchlater){
		this.watchlater = watchlater;
	}

	public boolean isWatchlater(){
		return watchlater;
	}

	public void setShare(boolean share){
		this.share = share;
	}

	public boolean isShare(){
		return share;
	}

	public void setEmbed(boolean embed){
		this.embed = embed;
	}

	public boolean isEmbed(){
		return embed;
	}

	public void setHd(boolean hd){
		this.hd = hd;
	}

	public boolean isHd(){
		return hd;
	}

	@Override
 	public String toString(){
		return 
			"Buttons{" + 
			"scaling = '" + scaling + '\'' + 
			",fullscreen = '" + fullscreen + '\'' + 
			",like = '" + like + '\'' + 
			",watchlater = '" + watchlater + '\'' + 
			",share = '" + share + '\'' + 
			",embed = '" + embed + '\'' + 
			",hd = '" + hd + '\'' + 
			"}";
		}
}