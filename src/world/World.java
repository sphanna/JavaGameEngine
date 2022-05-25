package world;


import java.util.Arrays;

import events.Event;
import events.EventManager;
import game.GameSystem;
import math.Vec2Int;
import math.Vector3f;
import renderables.Quad;
import util.PR;

public class World extends GameSystem{

	static final int WORLD_MAP_SIZE = 128;
	static final Vec2Int WORLD_MAP_DIM = new Vec2Int(WORLD_MAP_SIZE,WORLD_MAP_SIZE);
	static final int SUB_MAP_SIZE = 64;
	static final Vec2Int SUB_MAP_DIM = new Vec2Int(SUB_MAP_SIZE,SUB_MAP_SIZE);
	static final int WORLDZONESIZE = WORLD_MAP_SIZE*SUB_MAP_SIZE*SUB_MAP_SIZE; //makes each character level zone 1x1
	
	static final int NUMMAPLEVELS = 4;
	public static final int LEVEL_WORLD = 0;
	public static final int LEVEL_REGION = 1;
	public static final int LEVEL_LOCAL = 2;
	public static final int LEVEL_CHARACTER = 3;
	
	Zone worldZone;
	MapCollection _loadedMaps[];
	int zoomLevel = 0;
	
	public World(EventManager eventManager){
		super(eventManager);
		Zone.registerEventManager(eventManager);
		Map.registerEventManager(eventManager);
		subscribeEvents();
		init();
	}
	
	private void subscribeEvents(){
		eventMap.put("MAP_CURSOR_CLICK", this::onMapClicked);
		eventMap.put("ZOOM_CHANGE", this::onZoomChange);
		eventMap.put("MAP_CREATED", this::onMapCreated);
		eventMap.put("VIEWPORT_CHANGE", this::onViewPortChange);
		
		eventMap.entrySet().stream()
		   .forEach(e->eventManager.subscribe(this, e.getKey()));
	}
	
	private void onMapClicked(Object data){
		Vector3f worldPos = (Vector3f)data;
		
		//PR.ln("num maps loaded: " + this.numMapsLoaded());
		PR.ln("world Position: " + worldPos);

		Zone z = loadZone(zoomLevel,worldPos);
		if(z != null){
			z.setTexID(1);
			z.updateTexture();
		}	
	}
	
	private void onViewPortChange(Object data){
		Quad viewPort = (Quad)data;
		
		if(zoomLevel > 0)
			loadAllMapsWithin(zoomLevel - 1, viewPort);
	}
	
	private void onMapCreated(Object data){
		Map map = (Map)data;
		Zone z = map._superZone;
		int level = map._level;
		
		//PR.ln("zone loaded: " + z + " size: " + z._size +  " pos: " + z.getPos());
		
		if(!_loadedMaps[level].contains(z._loc)){
			Map removed = _loadedMaps[level].add(map);
			eventManager.dispatch(new Event("MAP_ADDED", map.mesh));
			if(removed != null){
				eventManager.dispatch(new Event("MAP_REMOVED", removed.mesh));
			}
		}
	}
	
	private void onZoomChange(Object data){
		Quad viewPort = (Quad)data;
		float viewWidth = viewPort.getWidth();
		
		int temp = zoomLevel;
		if(viewWidth <= SUB_MAP_SIZE * 2){
			zoomLevel = 2;
		} else
		if(viewWidth > SUB_MAP_SIZE * 2 && viewWidth < (SUB_MAP_SIZE*SUB_MAP_SIZE*2)){
			zoomLevel = 1;
		} else
		if(viewWidth >= (SUB_MAP_SIZE*SUB_MAP_SIZE*2))
			zoomLevel = 0;
		
		if(temp!=zoomLevel){
			eventManager.dispatch(new Event("ZOOM_STATE_CHANGE", zoomLevel));
			loadAllMapsWithin(zoomLevel - 1, viewPort);
		}
	}
	
	public void init(){
		_loadedMaps = new MapCollection[NUMMAPLEVELS];
		_loadedMaps[0] = new MapCollection();  //this will remain empty
		_loadedMaps[1] = new MapCollection();
		_loadedMaps[2] = new MapCollection();
		_loadedMaps[3] = new MapCollection();
	}
	
	//Assumption is Quad q is in world coordinates
	public void loadAllMapsWithin(int level, Quad q){
		Vector3f bottomLeft = q.getBottomLeftPos(); //world coordinate
		Vector3f topRight = q.getTopRightPos();
	
		float sizeOfZone = (float)(WORLDZONESIZE / (WORLD_MAP_SIZE*Math.pow(SUB_MAP_SIZE, level)));
		
		Vector3f loc = new Vector3f();
		
		//make a 2d grid based on zoneSize around the camera viewport.  For each grid point, attempt to load a zone there.
		//TODO make this more efficient.  The max number of zones that we should be able to load is 9, so we shouldn't have to
		//load more than that.
		for(float x = bottomLeft._x; x <= (topRight._x + sizeOfZone); x+=sizeOfZone){
			for(float y = bottomLeft._y; y <= (topRight._y + sizeOfZone); y+=sizeOfZone){
				loc.set(x, y, 0);
				loadZone(level, loc);
			}
		}
		
		//float fidelity = 1; //the number of checks for a given width/height.  Turns out not to be needed if
		//we know the size of the zone we are checking for.
		
		//float xStart = bottomLeft._x;
				//float yStart = bottomLeft._y;
				
				//float width = q.getWidth();
				//float height = q.getHeight();
		
		//PR.ln("size of zone: " + sizeOfZone);
		
				//sizeOfZone /= fidelity;
				//int numZonesWidth = (int)Math.floor((width / sizeOfZone));
				//int numZonesHeight = (int)Math.floor((height / sizeOfZone));
				
				//PR.ln("num zones width: " + numZonesWidth);
				
				
				//float widthChange = width / fidelity;
				//float heightChange = height / fidelity;
		
		//Algorithm 1 - FPS drop even with just the corners checked
		//Vector3f[] viewPortCorners = q.getFourCorners();
		//Arrays.stream(viewPortCorners)
		//	  .forEach((loc)->loadZone(level - 1, loc));
		
		//Algorithm 2 - FPS drop
		/*float xpos = 0;
		float ypos = 0;
		for(int x = 0; x < fidelity + 1; x++){
			for(int y = 0; y < fidelity + 1; y++){
				xpos = xStart + x*widthChange;
				ypos = yStart + y*heightChange;
				loc.set(xpos, ypos, 0);
				loadZone(level, loc);
			}
		}*/
	}
	
	public int numMapsLoaded(){
		int num = 0;
		for(int i = 0; i < 4; i++){
			num += _loadedMaps[i].getSize();
		}
		
		return num;
	}
	
	
	public void createOverviewMap(){
		int worldStartLoc = (int)(-WORLDZONESIZE / 2);
		
		worldZone = new Zone(worldStartLoc,worldStartLoc,null);
		worldZone.setSize(WORLDZONESIZE);
		worldZone.loadMap(WORLD_MAP_DIM);

	}
	
	
	public Zone loadZone(int level, Vector3f worldPosition){
		return worldZone.getSubMap().loadZoneFromWorldPosition(level, worldPosition);
	}

}
