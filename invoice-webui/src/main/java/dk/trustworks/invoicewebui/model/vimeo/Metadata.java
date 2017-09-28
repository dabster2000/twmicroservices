package dk.trustworks.invoicewebui.model.vimeo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Generated;

@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("com.robohorse.robopojogenerator")
public class Metadata{

	@JsonProperty("connections")
	private Connections connections;

	@JsonProperty("interactions")
	private Interactions interactions;

	public void setConnections(Connections connections){
		this.connections = connections;
	}

	public Connections getConnections(){
		return connections;
	}

	public void setInteractions(Interactions interactions){
		this.interactions = interactions;
	}

	public Interactions getInteractions(){
		return interactions;
	}

	@Override
 	public String toString(){
		return 
			"Metadata{" + 
			"connections = '" + connections + '\'' + 
			",interactions = '" + interactions + '\'' + 
			"}";
		}
}