package entities;

public class EntityList {

	private static final int MAX_ENTITIES = 10000;
	
	public static Entity[] entities = new Entity[MAX_ENTITIES];
	
	public EntityList(){}
	
	public Entity get(int ID){
		assert entities[ID] != null : "no entity: " + ID;
		
		return entities[ID];
	}
	
	public void put(Entity e){
		int ID = e.ID;
		assert entities[ID] == null : "entity " + ID + " already exists";
		assert ID < 10000 : "ID is greater than max entities";
		
		entities[ID] = e;
	}
	
	public void remove(int ID){
		entities[ID] = null;
	}
	
}
