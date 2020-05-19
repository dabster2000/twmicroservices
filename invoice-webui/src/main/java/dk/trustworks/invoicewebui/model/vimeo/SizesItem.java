package dk.trustworks.invoicewebui.model.vimeo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;

@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("com.robohorse.robopojogenerator")
public class SizesItem{

	@JsonProperty("link_with_play_button")
	private String linkWithPlayButton;

	@JsonProperty("width")
	private int width;

	@JsonProperty("link")
	private String link;

	@JsonProperty("height")
	private int height;

	public void setLinkWithPlayButton(String linkWithPlayButton){
		this.linkWithPlayButton = linkWithPlayButton;
	}

	public String getLinkWithPlayButton(){
		return linkWithPlayButton;
	}

	public void setWidth(int width){
		this.width = width;
	}

	public int getWidth(){
		return width;
	}

	public void setLink(String link){
		this.link = link;
	}

	public String getLink(){
		return link;
	}

	public void setHeight(int height){
		this.height = height;
	}

	public int getHeight(){
		return height;
	}

	@Override
 	public String toString(){
		return 
			"SizesItem{" + 
			"link_with_play_button = '" + linkWithPlayButton + '\'' + 
			",width = '" + width + '\'' + 
			",link = '" + link + '\'' + 
			",height = '" + height + '\'' + 
			"}";
		}
}