package dk.trustworks.invoicewebui.model.vimeo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;

@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("com.robohorse.robopojogenerator")
public class Connections{

	@JsonProperty("shared")
	private Shared shared;

	@JsonProperty("albums")
	private Albums albums;

	@JsonProperty("moderated_channels")
	private ModeratedChannels moderatedChannels;

	@JsonProperty("portfolios")
	private Portfolios portfolios;

	@JsonProperty("groups")
	private Groups groups;

	@JsonProperty("videos")
	private Videos videos;

	@JsonProperty("pictures")
	private Pictures pictures;

	@JsonProperty("appearances")
	private Appearances appearances;

	@JsonProperty("feed")
	private Feed feed;

	@JsonProperty("followers")
	private Followers followers;

	@JsonProperty("channels")
	private Channels channels;

	@JsonProperty("activities")
	private Activities activities;

	@JsonProperty("following")
	private Following following;

	@JsonProperty("likes")
	private Likes likes;

	@JsonProperty("comments")
	private Comments comments;

	@JsonProperty("related")
	private Related related;

	public void setShared(Shared shared){
		this.shared = shared;
	}

	public Shared getShared(){
		return shared;
	}

	public void setAlbums(Albums albums){
		this.albums = albums;
	}

	public Albums getAlbums(){
		return albums;
	}

	public void setModeratedChannels(ModeratedChannels moderatedChannels){
		this.moderatedChannels = moderatedChannels;
	}

	public ModeratedChannels getModeratedChannels(){
		return moderatedChannels;
	}

	public void setPortfolios(Portfolios portfolios){
		this.portfolios = portfolios;
	}

	public Portfolios getPortfolios(){
		return portfolios;
	}

	public void setGroups(Groups groups){
		this.groups = groups;
	}

	public Groups getGroups(){
		return groups;
	}

	public void setVideos(Videos videos){
		this.videos = videos;
	}

	public Videos getVideos(){
		return videos;
	}

	public void setPictures(Pictures pictures){
		this.pictures = pictures;
	}

	public Pictures getPictures(){
		return pictures;
	}

	public void setAppearances(Appearances appearances){
		this.appearances = appearances;
	}

	public Appearances getAppearances(){
		return appearances;
	}

	public void setFeed(Feed feed){
		this.feed = feed;
	}

	public Feed getFeed(){
		return feed;
	}

	public void setFollowers(Followers followers){
		this.followers = followers;
	}

	public Followers getFollowers(){
		return followers;
	}

	public void setChannels(Channels channels){
		this.channels = channels;
	}

	public Channels getChannels(){
		return channels;
	}

	public void setActivities(Activities activities){
		this.activities = activities;
	}

	public Activities getActivities(){
		return activities;
	}

	public void setFollowing(Following following){
		this.following = following;
	}

	public Following getFollowing(){
		return following;
	}

	public void setLikes(Likes likes){
		this.likes = likes;
	}

	public Likes getLikes(){
		return likes;
	}

	public Comments getComments() {
		return comments;
	}

	public void setComments(Comments comments) {
		this.comments = comments;
	}

	public Related getRelated() {
		return related;
	}

	public void setRelated(Related related) {
		this.related = related;
	}

	@Override
 	public String toString(){
		return 
			"Connections{" + 
			"shared = '" + shared + '\'' + 
			",albums = '" + albums + '\'' + 
			",moderated_channels = '" + moderatedChannels + '\'' + 
			",portfolios = '" + portfolios + '\'' + 
			",groups = '" + groups + '\'' + 
			",videos = '" + videos + '\'' + 
			",pictures = '" + pictures + '\'' + 
			",appearances = '" + appearances + '\'' + 
			",feed = '" + feed + '\'' + 
			",followers = '" + followers + '\'' + 
			",channels = '" + channels + '\'' + 
			",activities = '" + activities + '\'' + 
			",following = '" + following + '\'' + 
			",likes = '" + likes + '\'' + 
			"}";
		}
}