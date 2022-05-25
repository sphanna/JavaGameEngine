package world;

import events.Event;
import events.EventManager;
import math.Vec2Int;
import math.Vector3f;
import util.BlockTimer;
import util.PR;

public class Zone {

	private static EventManager eventManager;
	
	Vec2Int _loc;
	Vector3f _pos; //bottom left corner
	int _level = 0;
	
	Map _superMap = null;
	Map _subMap = null;
	
	float _size;
	
	float texID = 0;
	
	boolean _highlight = false;
		
	public Zone(int x, int y, Map m){
		_superMap = m;

		if(_superMap != null){
			_size = _superMap.getSubZoneSize();
			_level = _superMap.getLevel() + 1;
			texID = _superMap.getTexID();
		}

		setLoc(x,y);
	}
	
	public Map loadMap(Vec2Int mapDimensions){
		setHighlight(true);
		return createSubMap(mapDimensions);
	}
	
	public Map createSubMap(Vec2Int mapDimensions){
		_subMap = new Map(mapDimensions, this);
		eventManager.dispatch(new Event("MAP_CREATED", _subMap));
		return _subMap;
	}
	
	public Map unloadMap(){
		setHighlight(false);
		Map temp = _subMap;
		_subMap = null;
		return temp;
	}
	
	public boolean hasSubMap(){
		if(_subMap == null){
			return false;
		}else{return true;}
	}
	
	public void setLoc(int x, int y){
		_loc = new Vec2Int(x,y);
		setPos();		
	}
	
	public void setPos(){
		
		float xpos;
		float ypos;
		
		if(_superMap != null){
			xpos = (float)_loc.x()*_size;
			ypos = (float)_loc.y()*_size;
			_pos = new Vector3f(xpos, ypos, 0);
			_pos.add(_superMap._pos);
		}else{
			xpos = (float)_loc.x();
			ypos = (float)_loc.y();
			_pos = new Vector3f(xpos, ypos, 0);
		}
		
		if(_subMap != null){
			_subMap.setLoc(); //this will recursively call setPos for all subZones of the subMap
		}
	}
	
	
	public void setSize(float s){
		_size = s;
		
		if(_subMap != null){
			_subMap.setSize(_size);
		}
		
		setPos();
	}
	
	//dir is the direction of the neighbor with respect to this zone.  It can be one of eight possible directions.
	//returns null if this is the overviewMap or the neighbor location is off of the entire world map.
	
	//if the neighbor is in a map that is adjacent and not yet loaded, it will load the entire map in order to get that zone.
	//This shouldn't need to happen since we are loading all adjacent maps anyway.
	public Zone getNeighbor(Vec2Int dir){
		Zone neighbor = null;
		Vec2Int neighborLoc = _loc.addn(dir);
		Vector3f neighborPos = _pos.addn(new Vector3f(dir.x(), dir.y(), 0));
		
		if(this.hasSuperMap()){
			neighbor = _superMap.validZone(neighborLoc);
			if(neighbor == null){
				neighbor = _superMap.loadZoneFromWorldPosition(this._level, neighborPos);
			}
		}
		
		return neighbor;
	}
	
	//starts bottom left and goes clockwise.
	public Zone[] getNeighbors(){
		Zone[] zones = new Zone[8];
		
		zones[0] = getNeighbor(new Vec2Int(-1,-1));
		zones[1] = getNeighbor(new Vec2Int(-1,0));
		zones[2] = getNeighbor(new Vec2Int(-1,1));
		zones[3] = getNeighbor(new Vec2Int(0,1));
		zones[4] = getNeighbor(new Vec2Int(1,1));
		zones[5] = getNeighbor(new Vec2Int(1,0));
		zones[6] = getNeighbor(new Vec2Int(1,-1));
		zones[7] = getNeighbor(new Vec2Int(0,-1));
		
		return zones;
	}
		
	public boolean hasSuperMap(){
		return _superMap != null;
	}
	public Map getSubMap(){
		return _subMap;
	}
	
	public int getLevel(){
		return _level;
	}
	
	public Vector3f getPos(){
		return _pos;
	}
	
	public Vec2Int getLoc(){
		return _loc;
	}
	
	public float getSize(){
		return _size;
	}
	
	public boolean highlighted(){
		return _highlight;
	}
	
	public void toggleHighlight(){
		_highlight = !_highlight;
	}
	
	public void setHighlight(boolean val){
		_highlight = val;
	}
	
	public String toString(){
		return _level + ":" + getLoc();
	}
	
	public float getTexID(){
		return this.texID;
	}
	
	public void setTexID(float ID){
		if(texID != ID){
			this.texID = ID;
			//updateTexture();
			if(_subMap != null)
				_subMap.setTexID(ID);
		}
	}
	
	public void updateTexture(){ //assumes the new texture was already set with setTexID
		if(_superMap != null)
			_superMap.getMesh().updateTileTexture(_loc.x(), _loc.y(), this.texID);
	}
	
	public static void registerEventManager(EventManager e){
		eventManager = e;
	}
}
