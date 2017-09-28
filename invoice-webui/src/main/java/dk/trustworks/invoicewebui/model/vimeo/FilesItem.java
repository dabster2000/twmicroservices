package dk.trustworks.invoicewebui.model.vimeo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Generated;

@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("com.robohorse.robopojogenerator")
public class FilesItem{

	@JsonProperty("created_time")
	private String createdTime;

	@JsonProperty("size")
	private int size;

	@JsonProperty("link_secure")
	private String linkSecure;

	@JsonProperty("width")
	private int width;

	@JsonProperty("link")
	private String link;

	@JsonProperty("fps")
	private int fps;

	@JsonProperty("type")
	private String type;

	@JsonProperty("quality")
	private String quality;

	@JsonProperty("height")
	private int height;

	@JsonProperty("md5")
	private String md5;

	public void setCreatedTime(String createdTime){
		this.createdTime = createdTime;
	}

	public String getCreatedTime(){
		return createdTime;
	}

	public void setSize(int size){
		this.size = size;
	}

	public int getSize(){
		return size;
	}

	public void setLinkSecure(String linkSecure){
		this.linkSecure = linkSecure;
	}

	public String getLinkSecure(){
		return linkSecure;
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

	public void setFps(int fps){
		this.fps = fps;
	}

	public int getFps(){
		return fps;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setQuality(String quality){
		this.quality = quality;
	}

	public String getQuality(){
		return quality;
	}

	public void setHeight(int height){
		this.height = height;
	}

	public int getHeight(){
		return height;
	}

	public void setMd5(String md5){
		this.md5 = md5;
	}

	public String getMd5(){
		return md5;
	}

	@Override
 	public String toString(){
		return 
			"FilesItem{" + 
			"created_time = '" + createdTime + '\'' + 
			",size = '" + size + '\'' + 
			",link_secure = '" + linkSecure + '\'' + 
			",width = '" + width + '\'' + 
			",link = '" + link + '\'' + 
			",fps = '" + fps + '\'' + 
			",type = '" + type + '\'' + 
			",quality = '" + quality + '\'' + 
			",height = '" + height + '\'' + 
			",md5 = '" + md5 + '\'' + 
			"}";
		}
}