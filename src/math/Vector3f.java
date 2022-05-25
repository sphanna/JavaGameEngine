package math;

import java.text.DecimalFormat;

public class Vector3f {

	public float _x,_y,_z;
	
	public Vector3f(){
		_x = 0.0f;
		_y = 0.0f;
		_z = 0.0f;
	}
	
	public Vector3f(float x, float y, float z){
		_x = x;
		_y = y;
		_z = z;
	}
	
	public void set(float x, float y , float z){
		_x = x;
		_y = y;
		_z = z;
	}
	
	public Vector3f flipn(){
		return new Vector3f(-_x,-_y,-_z);
	}
	
	public Vector3f addn(Vector3f v){
		return new Vector3f(_x+v._x, _y+v._y, _z+v._z);
	}

	public Vector3f subtractn(Vector3f v){
		return new Vector3f(_x-v._x, _y-v._y, _z-v._z);
	}
	
	public Vector3f scalarMultN(float scalar){
		return new Vector3f(_x*scalar, _y*scalar, _z*scalar);
	}
	
	public Vector3f scalarDivideN(float scalar){
		return scalarMultN(1 / scalar);
	}
	
	public float distSquared(){
		return (_x*_x + _y*_y + _z*_z);
	}
	
	public Vector3f clone(){
		return new Vector3f(_x,_y,_z);
	}
	
	public void add(Vector3f v){
		_x+=v._x;
		_y+=v._y;
		_z+=v._z;
	}

	public String toString(){
		DecimalFormat d = new DecimalFormat("#.#");
		double x2f = Double.valueOf(d.format(_x));
		double y2f = Double.valueOf(d.format(_y));
		double z2f = Double.valueOf(d.format(_z));
	
		return "(" + x2f + "," + y2f + "," + z2f + ")";
	}
}
