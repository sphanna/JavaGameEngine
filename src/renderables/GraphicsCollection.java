package renderables;

import java.util.Map.Entry;
import java.util.stream.Stream;

import math.Matrix4f;
import util.PR;

public class GraphicsCollection {

	static long hashVal = 0;
	
	int _layer;
	java.util.HashMap<Long, Renderable> _objs;
	
	public GraphicsCollection(int layer){
		_layer = layer;
		_objs = new java.util.HashMap<Long, Renderable>();
	}
	
	public void add(Renderable r){
		long hash = hashVal++;
		r.setHash(hash);
		_objs.put(hash, r);
	}
	
	public void remove(Renderable r){
		Renderable removed = _objs.remove(r.getHash());
		if(removed == null){
			PR.ln("nothing removed");
		}
	}
	
	public boolean contains(Renderable r){
		return _objs.containsKey(r.getHash());
	}
	
	public java.util.HashMap<Long, Renderable> getObjs(){
		return _objs;
	}
	
	public int getSize(){
		return _objs.size();
	}
	
	public Stream<Entry<Long,Renderable>> toStream(){
		return _objs.entrySet().stream();
	}
	
	public void renderAll(){
		for(Renderable r: _objs.values()){
			r.render();
		}
	}
	
	public void transformAll(String type, Matrix4f transform){
		_objs.entrySet().stream()
						.map((o)->o.getValue())
						.forEach((obj)->obj.applyTransform(type, transform));
	}
	
	public static long getCurHash(){
		return hashVal;
	}
}
