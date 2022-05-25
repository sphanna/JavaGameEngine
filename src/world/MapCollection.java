package world;

import math.Vec2Int;
import math.Vector3f;

public class MapCollection {

	static final int MAXCOLLECTIONSIZE = 9;
	
	java.util.LinkedHashMap<Vec2Int, Map> _maps;
	
	public MapCollection(){
		_maps = new java.util.LinkedHashMap<Vec2Int, Map>();
	}
	
	public Map add(Map m){ //returns a removed map if any
		Vec2Int loc = m.getSuperZoneLoc();
		Vector3f pos = m.getPos();
		_maps.put(loc, m);
		
		Map farthest = null;
		
		//if we have gone over max size, remove the map farthest from our current location
		if(_maps.size() > MAXCOLLECTIONSIZE){
			//first = _maps.entrySet().iterator().next().getValue();  //trick to get first map
			//first.getSuperZone().unloadMap();
			farthest = getFarthestFrom(pos);
			farthest.getSuperZone().unloadMap();
			remove(farthest);
		}
		
		return farthest;
	}
	
	public void remove(Map m){
		_maps.remove(m.getSuperZoneLoc());
	}
	
	public Map getFarthestFrom(Vector3f pos){
		Map farthestMap = null;
		float dist = 0.0f;
		for(Map m: _maps.values()){
			float d = pos.subtractn(m.getPos()).distSquared();
			if(d > dist){
				dist = d;
				farthestMap = m;
			}
		}
		return farthestMap;
	}
	
	public boolean contains(Map m){
		Vec2Int loc = m.getSuperZoneLoc();
		return _maps.containsKey(loc);
	}
	
	public boolean contains(Vec2Int loc){
		return _maps.containsKey(loc);
	}
	
	public int getSize(){
		return _maps.size();
	}
		
}
