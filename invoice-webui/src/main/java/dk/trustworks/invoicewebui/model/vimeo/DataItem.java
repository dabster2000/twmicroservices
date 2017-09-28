package dk.trustworks.invoicewebui.model.vimeo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("com.robohorse.robopojogenerator")
public class DataItem{

	@JsonProperty("metadata")
	private Metadata metadata;

	@JsonProperty("link")
	private String link;

	@JsonProperty("description")
	private String description;

	@JsonProperty("privacy")
	private Privacy privacy;

	@JsonProperty("language")
	private String language;

	@JsonProperty("review_link")
	private String reviewLink;

	@JsonProperty("pictures")
	private Pictures pictures;

	@JsonProperty("duration")
	private int duration;

	@JsonProperty("modified_time")
	private String modifiedTime;

	@JsonProperty("stats")
	private Stats stats;

	@JsonProperty("content_rating")
	private List<String> contentRating;

	@JsonProperty("embed")
	private Embed embed;

	@JsonProperty("height")
	private int height;

	@JsonProperty("release_time")
	private String releaseTime;

	@JsonProperty("app")
	private App app;

	@JsonProperty("created_time")
	private String createdTime;

	@JsonProperty("embed_presets")
	private Object embedPresets;

	@JsonProperty("uri")
	private String uri;

	@JsonProperty("tags")
	private List<TagsItem> tags;

	@JsonProperty("license")
	private Object license;

	@JsonProperty("resource_key")
	private String resourceKey;

	@JsonProperty("name")
	private String name;

	@JsonProperty("width")
	private int width;

	@JsonProperty("user")
	private User user;

	@JsonProperty("status")
	private String status;

	public void setMetadata(Metadata metadata){
		this.metadata = metadata;
	}

	public Metadata getMetadata(){
		return metadata;
	}

	public void setLink(String link){
		this.link = link;
	}

	public String getLink(){
		return link;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setPrivacy(Privacy privacy){
		this.privacy = privacy;
	}

	public Privacy getPrivacy(){
		return privacy;
	}

	public void setLanguage(String language){
		this.language = language;
	}

	public String getLanguage(){
		return language;
	}

	public void setReviewLink(String reviewLink){
		this.reviewLink = reviewLink;
	}

	public String getReviewLink(){
		return reviewLink;
	}

	public void setPictures(Pictures pictures){
		this.pictures = pictures;
	}

	public Pictures getPictures(){
		return pictures;
	}

	public void setDuration(int duration){
		this.duration = duration;
	}

	public int getDuration(){
		return duration;
	}

	public void setModifiedTime(String modifiedTime){
		this.modifiedTime = modifiedTime;
	}

	public String getModifiedTime(){
		return modifiedTime;
	}

	public void setStats(Stats stats){
		this.stats = stats;
	}

	public Stats getStats(){
		return stats;
	}

	public void setContentRating(List<String> contentRating){
		this.contentRating = contentRating;
	}

	public List<String> getContentRating(){
		return contentRating;
	}

	public void setEmbed(Embed embed){
		this.embed = embed;
	}

	public Embed getEmbed(){
		return embed;
	}

	public void setHeight(int height){
		this.height = height;
	}

	public int getHeight(){
		return height;
	}

	public void setReleaseTime(String releaseTime){
		this.releaseTime = releaseTime;
	}

	public String getReleaseTime(){
		return releaseTime;
	}

	public void setApp(App app){
		this.app = app;
	}

	public App getApp(){
		return app;
	}

	public void setCreatedTime(String createdTime){
		this.createdTime = createdTime;
	}

	public String getCreatedTime(){
		return createdTime;
	}

	public void setEmbedPresets(Object embedPresets){
		this.embedPresets = embedPresets;
	}

	public Object getEmbedPresets(){
		return embedPresets;
	}

	public void setUri(String uri){
		this.uri = uri;
	}

	public String getUri(){
		return uri;
	}

	public void setTags(List<TagsItem> tags){
		this.tags = tags;
	}

	public List<TagsItem> getTags(){
		return tags;
	}

	public void setLicense(Object license){
		this.license = license;
	}

	public Object getLicense(){
		return license;
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

	public void setWidth(int width){
		this.width = width;
	}

	public int getWidth(){
		return width;
	}

	public void setUser(User user){
		this.user = user;
	}

	public User getUser(){
		return user;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"DataItem{" + 
			"metadata = '" + metadata + '\'' + 
			",link = '" + link + '\'' + 
			",description = '" + description + '\'' + 
			",privacy = '" + privacy + '\'' + 
			",language = '" + language + '\'' + 
			",review_link = '" + reviewLink + '\'' + 
			",pictures = '" + pictures + '\'' + 
			",duration = '" + duration + '\'' + 
			",modified_time = '" + modifiedTime + '\'' + 
			",stats = '" + stats + '\'' + 
			",content_rating = '" + contentRating + '\'' + 
			",embed = '" + embed + '\'' + 
			",height = '" + height + '\'' + 
			",release_time = '" + releaseTime + '\'' + 
			",app = '" + app + '\'' + 
			",created_time = '" + createdTime + '\'' + 
			",embed_presets = '" + embedPresets + '\'' + 
			",uri = '" + uri + '\'' + 
			",tags = '" + tags + '\'' + 
			",license = '" + license + '\'' + 
			",resource_key = '" + resourceKey + '\'' + 
			",name = '" + name + '\'' + 
			",width = '" + width + '\'' + 
			",user = '" + user + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}