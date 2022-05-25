package entities;

import events.EventManager;

public class Component {
	
	int entityID;
	Object data;
	String type;
	
	public Component(String type, int ID){
		setID(ID);
		this.type = type;
		data = null;
	}
	
	public Component(String type, int ID, Object data){
		setID(ID);
		this.type = type;
		this.data = data;
	}
	
	public String getType(){
		return type;
	}
	
	public int ID(){
		return entityID;
	}
	
	public void setID(int ID){
		entityID = ID;
	}
	
	public Object getData(){
		return data;
	}
	
	public void set(Object o){
		data = o;
	}
	
	public Entity getMyEntity(){
		return EntityList.entities[entityID];
	}
	
}
