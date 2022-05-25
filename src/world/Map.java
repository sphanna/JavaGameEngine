package world;


import events.Event;
import events.EventManager;
import math.Vec2Int;
import math.Vector3f;
import renderables.TileMap;
import util.PR;

public class Map {

	public static EventManager eventManager;
	
	Vector3f _pos; //the bottom left corner of the map
	int _level = 0;
	Vec2Int _mapDim;
	float _size;
	
	Zone _superZone = null;
	float _subZoneSize;
	Zone _zones[][];
	Zone _currZone;
	
	TileMap mesh;
	float texID;
	
	public Map(Vec2Int dim){
		_mapDim = dim;
	}
	
	public Map(Vec2Int dim, Zone superZ){
		_mapDim = dim;
		_superZone = superZ;
		_pos = superZ.getPos();
		_level = superZ.getLevel();
		texID = superZ.texID;
		_size = superZ.getSize();
		_subZoneSize = _size / dim.x();
		
		mesh = new TileMap(this);
		initializeZones();
		
	}
	
	private void initializeZones(){
		int width = _mapDim.x();
		int height = _mapDim.y();
		_zones = new Zone[width][height];
				
		for(int i = 0; i < width; i++ ){
			for( int j = 0; j < height; j++ ){
				_zones[i][j] = new Zone(i,j, this);
			}
		}
	}

	
	public Zone loadZoneFromWorldPosition(int level, Vector3f worldPos){
		Vector3f localPos = worldPos.subtractn(_pos);
		Vector3f zonePos = localPos.scalarMultN(1 / _subZoneSize);
		Vec2Int zoneLocation = new Vec2Int((int)Math.floor(zonePos._x), (int)Math.floor(zonePos._y));
		Zone z = validZone(zoneLocation);
		
		
		//TODO so many if statements...what to do?  Too many state checks.
		
		if(z == null){ //invalid position (off the entire map)
			return null; 
		}
		
		if(!z.hasSubMap() && _level < World.LEVEL_LOCAL){
			z.loadMap(World.SUB_MAP_DIM);
		}
		
		if(_level == level || _level == World.LEVEL_LOCAL){
			return z;
		}
		else{
			//recursive: go into the submap and try again
			return z.getSubMap().loadZoneFromWorldPosition(level, worldPos);	
		}
	}
	
	public Zone validZone(Vec2Int loc){
		int x = loc.x();
		int y = loc.y();
		
		if(validLocation(loc))
			return _zones[x][y];
		else
			return null;
	}
	
	public boolean validLocation(Vec2Int loc){
		int x = loc.x();
		int y = loc.y();
		
		return x >= 0 && y >= 0 && x < _mapDim.x() && y < _mapDim.y();
	}
	
	public void setLoc(){
		int width = _mapDim.x();
		int height = _mapDim.y();
		
		for(int i = 0; i < width; i++ ){
			for( int j = 0; j < height; j++ ){
				_zones[i][j].setLoc(i,j);
			}
		}
	}
	
	public void setTexID(float ID){
		texID = ID;
		for(int i = 0; i < _mapDim.x(); i++ ){
			for( int j = 0; j < _mapDim.y(); j++ ){
				_zones[i][j].setTexID(ID);
			}
		}
			
			//updateMesh();
	}
	
	//assumes textures have already been changed with setTexID
	public void updateMesh(){
		TileMap oldMesh = mesh;
		TileMap newMesh = new TileMap(this);
		
		TileMap[] mapChange = {oldMesh, newMesh};
		
		eventManager.dispatch(new Event("MAP_TEX_CHANGE", mapChange));
	}
	
	public static void registerEventManager(EventManager e){
		eventManager = e;
	}
	
	public void setSize(float s){
		_size = s;
		setLoc();
	}
	
	
	public void attachZone(Zone z){
		_superZone = z;
	}
	
	public int getLevel(){
		return _level;
	}
	
	public void setLevel(int l){
		_level = l;
	}
	
	public Zone getSuperZone(){
		return _superZone;
	}
	
	public Vec2Int getSuperZoneLoc(){
		return _superZone.getLoc();
	}
	
	public float getSubZoneSize(){
		return _subZoneSize;
	}
	
	public Vec2Int getMapDimensions(){
		return _mapDim.clone();
	}
	
	public Zone getZone(int x, int y){
		return _zones[x][y];
	}
	
	public Zone[][] getZones(){
		return _zones;
	}
	
	public float getSize(){
		return _size;
	}
	
	public Vector3f getPos(){
		return _pos;
	}
	
	public TileMap getMesh(){
		return mesh;
	}
	
	public float getTexID(){
		return texID;
	}
	
	
		
}