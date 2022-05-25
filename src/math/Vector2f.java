package math;

import java.text.DecimalFormat;

public class Vector2f {

	private double _x;
	private double _y;
	
	public Vector2f(){}
	public Vector2f(double x, double y){_x=x;_y=y;}
	
	public double x(){return _x;}
	public double y(){return _y;}
	
	public void set(int x, int y){_x=x;_y=y;}
	public void set(double x, double y){_x=x;_y=y;}
	public void set(Vector2f v){_x = v.x(); _y = v.y();}
	
	public Vector2f addn(Vector2f v){
		return new Vector2f(_x + v._x, _y + v._y);
	}
	public void add(Vector2f v){
		_x = _x + v._x;
		_y = _y + v._y;
	}
	public Vector2f subtractn(Vector2f v){
		return new Vector2f(_x - v._x, _y - v._y);
	}
	public void subtract(Vector2f v){
		_x = _x - v._x;
		_y = _y - v._y;
	}
	public Vector2f multiplyn(double s){
		return new Vector2f(_x * s, _y * s);
	}
	public void multiply(double s){
		_x *= s; _y *= s;
	}
	public void vMultiply(Vector2f v){
		_x *= v.x(); _y *= v.y();
	}
	public Vector2f vMultiplyn(Vector2f v){
		return new Vector2f(_x*v.x(), _y*v.y());
	}
	public Vector2f dividen(double s){
		return new Vector2f(_x / s, _y / s);
	}
	public void divide(double s){
		_x /= s; _y /= s;
	}
	
	public double magnitude(){
		return Math.sqrt(magnitudeSquared());
	}
	
	public double magnitudeSquared(){
		return _x*_x + _y*_y;
	}
	
	
	public Vector2f normalized(){
		return this.multiplyn( 1/this.magnitude() );
	}
	public void normalize(){
		this.multiply( 1/this.magnitude() );
	}
	public void reflect(){
		_x = -_x;
		_y = -_y;	
	}
	public Vector2f reflectn(){
		return new Vector2f(-_x,-_y);	
	}
	
	public Vector2f clone(){
		return new Vector2f(_x,_y);
	}
	
	public Vec2Int toInt(){
		return new Vec2Int((int)_x,(int)_y);
	}
	
	public boolean equals(Vector2f v){
		if(_x == v._x && _y == v._y )
			return true;
		else
			return false;
	}
	
	public Vector3f toVector3f(){
		return new Vector3f( (float)_x, (float)_y, 0.0f);
	}
	
	public String toString(){
		//return "(" + _x + "," + _y + ")";
		
		DecimalFormat d = new DecimalFormat("#.#");
		double x2f = Double.valueOf(d.format(_x));
		double y2f = Double.valueOf(d.format(_y));
	
		return "(" + x2f + "," + y2f + ")";
		

	}
}
