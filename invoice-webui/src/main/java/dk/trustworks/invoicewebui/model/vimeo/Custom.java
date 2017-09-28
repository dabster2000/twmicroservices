package dk.trustworks.invoicewebui.model.vimeo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Generated;

@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("com.robohorse.robopojogenerator")
public class Custom{

	@JsonProperty("link")
	private Object link;

	@JsonProperty("sticky")
	private boolean sticky;

	@JsonProperty("active")
	private boolean active;

	public void setLink(Object link){
		this.link = link;
	}

	public Object getLink(){
		return link;
	}

	public void setSticky(boolean sticky){
		this.sticky = sticky;
	}

	public boolean isSticky(){
		return sticky;
	}

	public void setActive(boolean active){
		this.active = active;
	}

	public boolean isActive(){
		return active;
	}

	@Override
 	public String toString(){
		return 
			"Custom{" + 
			"link = '" + link + '\'' + 
			",sticky = '" + sticky + '\'' + 
			",active = '" + active + '\'' + 
			"}";
		}
}