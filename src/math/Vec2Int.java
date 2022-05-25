package math;

//import java.text.DecimalFormat;

public class Vec2Int{

	private int _x;
	private int _y;
	
	public Vec2Int(){}
	public Vec2Int(int x, int y){_x=x;_y=y;}
	
	public int x(){return _x;}
	public int y(){return _y;}
	
	public void set(int x, int y){_x=x;_y=y;}
	
	public Vec2Int addn(Vec2Int v){
		return new Vec2Int(_x + v._x, _y + v._y);
	}
	public Vec2Int subtractn(Vec2Int v){
		return new Vec2Int(_x - v._x, _y - v._y);
	}
	
	public int distSquared(){
		return _x*_x + _y*_y;
	}

	
	public Vec2Int multiplyn(int s){
		return new Vec2Int(_x * s, _y * s);
	}
	public void multiply(int s){
		_x *= s; _y *= s;
	}
	public Vec2Int dividen(int s){
		return new Vec2Int(_x / s, _y / s);
	}
	
	public Vector2f toDouble(){
		return new Vector2f((double)_x, (double)_y);
	}
	
	public Vec2Int clone(){
		return new Vec2Int(_x,_y);
	}
	
	public boolean equals(Vec2Int v){
		if(_x == v._x && _y == v._y )
			return true;
		else
			return false;
	}
	
	public String toString(){
		return "(" + _x + "," + _y + ")";
	}

}
