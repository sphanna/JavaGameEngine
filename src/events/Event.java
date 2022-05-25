package events;

public class Event {

	public String type;
	public Object data;
	
	public Event(String type, Object data){
		this.type = type;
		this.data = data;
	}
	
	public String getType(){
		return type;
	}
	
	public Object getData(){
		return data;
	}

}
