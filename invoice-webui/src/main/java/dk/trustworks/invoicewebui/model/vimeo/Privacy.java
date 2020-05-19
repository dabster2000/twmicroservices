package dk.trustworks.invoicewebui.model.vimeo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;

@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("com.robohorse.robopojogenerator")
public class Privacy{

	@JsonProperty("add")
	private boolean add;

	@JsonProperty("view")
	private String view;

	@JsonProperty("download")
	private boolean download;

	@JsonProperty("comments")
	private String comments;

	@JsonProperty("embed")
	private String embed;

	public void setAdd(boolean add){
		this.add = add;
	}

	public boolean isAdd(){
		return add;
	}

	public void setView(String view){
		this.view = view;
	}

	public String getView(){
		return view;
	}

	public void setDownload(boolean download){
		this.download = download;
	}

	public boolean isDownload(){
		return download;
	}

	public void setComments(String comments){
		this.comments = comments;
	}

	public String getComments(){
		return comments;
	}

	public void setEmbed(String embed){
		this.embed = embed;
	}

	public String getEmbed(){
		return embed;
	}

	@Override
 	public String toString(){
		return 
			"Privacy{" + 
			"add = '" + add + '\'' + 
			",view = '" + view + '\'' + 
			",download = '" + download + '\'' + 
			",comments = '" + comments + '\'' + 
			",embed = '" + embed + '\'' + 
			"}";
		}
}