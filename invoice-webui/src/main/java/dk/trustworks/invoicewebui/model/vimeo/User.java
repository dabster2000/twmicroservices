package dk.trustworks.invoicewebui.model.vimeo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("com.robohorse.robopojogenerator")
public class User{

	@JsonProperty("created_time")
	private String createdTime;

	@JsonProperty("metadata")
	private Metadata metadata;

	@JsonProperty("preferences")
	private Preferences preferences;

	@JsonProperty("resource_key")
	private String resourceKey;

	@JsonProperty("name")
	private String name;

	@JsonProperty("link")
	private String link;

	@JsonProperty("bio")
	private String bio;

	@JsonProperty("location")
	private String location;

	@JsonProperty("websites")
	private List<WebsitesItem> websites;

	@JsonProperty("uri")
	private String uri;

	@JsonProperty("account")
	private String account;

	@JsonProperty("pictures")
	private Pictures pictures;

	public void setCreatedTime(String createdTime){
		this.createdTime = createdTime;
	}

	public String getCreatedTime(){
		return createdTime;
	}

	public void setMetadata(Metadata metadata){
		this.metadata = metadata;
	}

	public Metadata getMetadata(){
		return metadata;
	}

	public void setPreferences(Preferences preferences){
		this.preferences = preferences;
	}

	public Preferences getPreferences(){
		return preferences;
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

	public void setLink(String link){
		this.link = link;
	}

	public String getLink(){
		return link;
	}

	public void setBio(String bio){
		this.bio = bio;
	}

	public String getBio(){
		return bio;
	}

	public void setLocation(String location){
		this.location = location;
	}

	public String getLocation(){
		return location;
	}

	public void setWebsites(List<WebsitesItem> websites){
		this.websites = websites;
	}

	public List<WebsitesItem> getWebsites(){
		return websites;
	}

	public void setUri(String uri){
		this.uri = uri;
	}

	public String getUri(){
		return uri;
	}

	public void setAccount(String account){
		this.account = account;
	}

	public String getAccount(){
		return account;
	}

	public void setPictures(Pictures pictures){
		this.pictures = pictures;
	}

	public Pictures getPictures(){
		return pictures;
	}

	@Override
 	public String toString(){
		return 
			"User{" + 
			"created_time = '" + createdTime + '\'' + 
			",metadata = '" + metadata + '\'' + 
			",preferences = '" + preferences + '\'' + 
			",resource_key = '" + resourceKey + '\'' + 
			",name = '" + name + '\'' + 
			",link = '" + link + '\'' + 
			",bio = '" + bio + '\'' + 
			",location = '" + location + '\'' + 
			",websites = '" + websites + '\'' + 
			",uri = '" + uri + '\'' + 
			",account = '" + account + '\'' + 
			",pictures = '" + pictures + '\'' + 
			"}";
		}
}