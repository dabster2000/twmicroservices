package dk.trustworks.invoicewebui.model.vimeo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Generated;

@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("com.robohorse.robopojogenerator")
public class Paging{

	@JsonProperty("next")
	private String next;

	@JsonProperty("previous")
	private Object previous;

	@JsonProperty("last")
	private String last;

	@JsonProperty("first")
	private String first;

	public void setNext(String next){
		this.next = next;
	}

	public String getNext(){
		return next;
	}

	public void setPrevious(Object previous){
		this.previous = previous;
	}

	public Object getPrevious(){
		return previous;
	}

	public void setLast(String last){
		this.last = last;
	}

	public String getLast(){
		return last;
	}

	public void setFirst(String first){
		this.first = first;
	}

	public String getFirst(){
		return first;
	}

	@Override
 	public String toString(){
		return 
			"Paging{" + 
			"next = '" + next + '\'' + 
			",previous = '" + previous + '\'' + 
			",last = '" + last + '\'' + 
			",first = '" + first + '\'' + 
			"}";
		}
}