package dk.trustworks.invoicewebui.model.vimeo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Generated;

@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("com.robohorse.robopojogenerator")
public class Interactions{

	@JsonProperty("block")
	private Block block;

	@JsonProperty("follow")
	private Follow follow;

	public void setBlock(Block block){
		this.block = block;
	}

	public Block getBlock(){
		return block;
	}

	public void setFollow(Follow follow){
		this.follow = follow;
	}

	public Follow getFollow(){
		return follow;
	}

	@Override
 	public String toString(){
		return 
			"Interactions{" + 
			"block = '" + block + '\'' + 
			",follow = '" + follow + '\'' + 
			"}";
		}
}