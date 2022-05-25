package math;

import java.nio.FloatBuffer;

import util.BufferUtils;

public class Matrix4f {

	public static final int SIZE = 16;
	
	public float[] _m = new float[SIZE];
	
	public Matrix4f (){}
	
	public Vector3f transform(Vector3f v){
		float vx = v._x;
		float vy = v._y;
		float vz = v._z;
		
		float rx = _m[0 + 0 * 4] * vx + _m[0 + 1 * 4] * vy + _m[0 + 2 * 4] * vz + _m[0 + 3 * 4];
		float ry = _m[1 + 0 * 4] * vx + _m[1 + 1 * 4] * vy + _m[1 + 2 * 4] * vz + _m[1 + 3 * 4];
		float rz = _m[2 + 0 * 4] * vx + _m[2 + 1 * 4] * vy + _m[2 + 2 * 4] * vz + _m[2 + 3 * 4];
		
		Vector3f result = new Vector3f(rx, ry, rz);
		
		return result;
	}
	
	
	public static Matrix4f identity(){
		//Matrix already default to zeros
		Matrix4f result = new Matrix4f();
		result._m[0 + 0 * 4] = 1.0f;
		result._m[1 + 1 * 4] = 1.0f;
		result._m[2 + 2 * 4] = 1.0f;
		result._m[3 + 3 * 4] = 1.0f;
		return result;
	}
	
	public static Matrix4f orthographic(float left, float right, float bottom, float top, float near, float far){
		Matrix4f result = identity();
		
		result._m[0 + 0 * 4] = 2.0f / (right  - left);
		result._m[1 + 1 * 4] = 2.0f / (top - bottom);
		result._m[2 + 2 * 4] = 2.0f / (near - far);
		result._m[0 + 3 * 4] = (left + right) / (left - right);
		result._m[1 + 3 * 4] = (bottom + top) / (bottom - top);
		result._m[2 + 3 * 4] = (far + near) / (far - near);
		
		return result;
	}
	
	public static Matrix4f translate(Vector3f vector){
		Matrix4f result = identity();
		result._m[0 + 3 * 4] = vector._x;
		result._m[1 + 3 * 4] = vector._y;
		result._m[2 + 3 * 4] = vector._z;
		
		return result;
	}
	
	public static Matrix4f scale(Vector3f scale){
		Matrix4f result = identity();
		result._m[0 + 0 * 4] = scale._x;
		result._m[1 + 1 * 4] = scale._y;
		result._m[2 + 2 * 4] = scale._z;
		
		return result;
	}
	
	public static Matrix4f scaleAll(float s){
		return scale(new Vector3f(s,s,s));
	}
	
	public static Matrix4f scaleUniform(float s){
		Matrix4f result = identity();
		result._m[3 + 3 * 4] = (1 / s);
		return result;
	}
	
	public static Matrix4f scaleXY(float s){
		return scale(new Vector3f(s,s,1));
	}
	
	public static Matrix4f reflectX(){
		Matrix4f result = identity();
		result._m[0 + 0 * 4] = -1;
		return result;
	}
	
	public static Matrix4f reflectY(){
		Matrix4f result = identity();
		result._m[1 + 1 * 4] = -1;
		return result;
	}
	
	public static Matrix4f reflectZ(){
		Matrix4f result = identity();
		result._m[2 + 2 * 4] = -1;
		return result;
	}
	
	public static Matrix4f rotateZ(float angle){
		Matrix4f result = identity();
		float r = (float) Math.toRadians(angle);
		float cos = (float) Math.cos(r);
		float sin = (float) Math.sin(r);
		
		result._m[0 + 0 * 4] = cos;
		result._m[0 + 1 * 4] = -sin;
		result._m[1 + 0 * 4] = sin;
		result._m[1 + 1 * 4] = cos;
		
		return result;
	}
	
	public float determinate(){
		float det = 0;
		
		float a = _m[0 + 0 * 4] * _m[1 + 1 * 4] - _m[0 + 1 * 4] * _m[1 + 0 * 4];
		float b = _m[0 + 0 * 4] * _m[1 + 2 * 4] - _m[0 + 2 * 4] * _m[1 + 0 * 4];
		float c = _m[0 + 0 * 4] * _m[1 + 3 * 4] - _m[0 + 3 * 4] * _m[1 + 0 * 4];
		float d = _m[0 + 1 * 4] * _m[1 + 2 * 4] - _m[0 + 2 * 4] * _m[1 + 1 * 4];
		float e = _m[0 + 1 * 4] * _m[1 + 3 * 4] - _m[0 + 3 * 4] * _m[1 + 1 * 4];
		float f = _m[0 + 2 * 4] * _m[1 + 3 * 4] - _m[0 + 3 * 4] * _m[1 + 2 * 4];
		float g = _m[2 + 0 * 4] * _m[3 + 1 * 4] - _m[2 + 1 * 4] * _m[3 + 0 * 4];
		float h = _m[2 + 0 * 4] * _m[3 + 2 * 4] - _m[2 + 2 * 4] * _m[3 + 0 * 4];
		float i = _m[2 + 0 * 4] * _m[3 + 3 * 4] - _m[2 + 3 * 4] * _m[3 + 0 * 4];
		float j = _m[2 + 1 * 4] * _m[3 + 2 * 4] - _m[2 + 2 * 4] * _m[3 + 1 * 4];
		float k = _m[2 + 1 * 4] * _m[3 + 3 * 4] - _m[2 + 3 * 4] * _m[3 + 1 * 4];
		float l = _m[2 + 2 * 4] * _m[3 + 3 * 4] - _m[2 + 3 * 4] * _m[3 + 2 * 4];
		
		det = a * l - b * k + c * j + d * i - e * h + f * g;

		return det;
	}
	
	public Matrix4f inverse(){
		Matrix4f r = identity();
		
		float det = 0; 
		
		float a = _m[0 + 0 * 4] * _m[1 + 1 * 4] - _m[0 + 1 * 4] * _m[1 + 0 * 4];
		float b = _m[0 + 0 * 4] * _m[1 + 2 * 4] - _m[0 + 2 * 4] * _m[1 + 0 * 4];
		float c = _m[0 + 0 * 4] * _m[1 + 3 * 4] - _m[0 + 3 * 4] * _m[1 + 0 * 4];
		float d = _m[0 + 1 * 4] * _m[1 + 2 * 4] - _m[0 + 2 * 4] * _m[1 + 1 * 4];
		float e = _m[0 + 1 * 4] * _m[1 + 3 * 4] - _m[0 + 3 * 4] * _m[1 + 1 * 4];
		float f = _m[0 + 2 * 4] * _m[1 + 3 * 4] - _m[0 + 3 * 4] * _m[1 + 2 * 4];
		float g = _m[2 + 0 * 4] * _m[3 + 1 * 4] - _m[2 + 1 * 4] * _m[3 + 0 * 4];
		float h = _m[2 + 0 * 4] * _m[3 + 2 * 4] - _m[2 + 2 * 4] * _m[3 + 0 * 4];
		float i = _m[2 + 0 * 4] * _m[3 + 3 * 4] - _m[2 + 3 * 4] * _m[3 + 0 * 4];
		float j = _m[2 + 1 * 4] * _m[3 + 2 * 4] - _m[2 + 2 * 4] * _m[3 + 1 * 4];
		float k = _m[2 + 1 * 4] * _m[3 + 3 * 4] - _m[2 + 3 * 4] * _m[3 + 1 * 4];
		float l = _m[2 + 2 * 4] * _m[3 + 3 * 4] - _m[2 + 3 * 4] * _m[3 + 2 * 4];
		
		det = a * l - b * k + c * j + d * i - e * h + f * g;

		if( det != 0 ){
			det = 1.0f / det;
			r._m[0 + 0 * 4] = ( _m[1 + 1 * 4] * l - _m[1 + 2 * 4] * k + _m[1 + 3 * 4] * j) * det;
			r._m[0 + 1 * 4] = (-_m[0 + 1 * 4] * l + _m[0 + 2 * 4] * k - _m[0 + 3 * 4] * j) * det;
			r._m[0 + 2 * 4] = ( _m[3 + 1 * 4] * f - _m[3 + 2 * 4] * e + _m[3 + 3 * 4] * d) * det;
			r._m[0 + 3 * 4] = (-_m[2 + 1 * 4] * f + _m[2 + 2 * 4] * e - _m[2 + 3 * 4] * d) * det;
			r._m[1 + 0 * 4] = (-_m[1 + 0 * 4] * l + _m[1 + 2 * 4] * i - _m[1 + 3 * 4] * h) * det;
			r._m[1 + 1 * 4] = ( _m[0 + 0 * 4] * l - _m[0 + 2 * 4] * i + _m[0 + 3 * 4] * h) * det;
			r._m[1 + 2 * 4] = (-_m[3 + 0 * 4] * f + _m[3 + 2 * 4] * c - _m[3 + 3 * 4] * b) * det;
			r._m[1 + 3 * 4] = ( _m[2 + 0 * 4] * f - _m[2 + 2 * 4] * c + _m[2 + 3 * 4] * b) * det;
			r._m[2 + 0 * 4] = ( _m[1 + 0 * 4] * k - _m[1 + 1 * 4] * i + _m[1 + 3 * 4] * g) * det;
			r._m[2 + 1 * 4] = (-_m[0 + 0 * 4] * k + _m[0 + 1 * 4] * i - _m[0 + 3 * 4] * g) * det;
			r._m[2 + 2 * 4] = ( _m[3 + 0 * 4] * e - _m[3 + 1 * 4] * c + _m[3 + 3 * 4] * a) * det;
			r._m[2 + 3 * 4] = (-_m[2 + 0 * 4] * e + _m[2 + 1 * 4] * c - _m[2 + 3 * 4] * a) * det;
			r._m[3 + 0 * 4] = (-_m[1 + 0 * 4] * j + _m[1 + 1 * 4] * h - _m[1 + 2 * 4] * g) * det;
			r._m[3 + 1 * 4] = ( _m[0 + 0 * 4] * j - _m[0 + 1 * 4] * h + _m[0 + 2 * 4] * g) * det;
			r._m[3 + 2 * 4] = (-_m[3 + 0 * 4] * d + _m[3 + 1 * 4] * b - _m[3 + 2 * 4] * a) * det;
			r._m[3 + 3 * 4] = ( _m[2 + 0 * 4] * d - _m[2 + 1 * 4] * b + _m[2 + 2 * 4] * a) * det;
				
			return r;
		}else{
			return null;
		}
	}
	
	public Matrix4f multiply(Matrix4f matrix){
		Matrix4f result = new Matrix4f();
		for(int y = 0; y < 4; y++){
			for(int x = 0; x < 4; x++){
				float sum = 0.0f;
				for( int e = 0; e < 4; e++){
					sum += this._m[x + e * 4] * matrix._m[e + y * 4];
				}
				result._m[x + y * 4] = sum;
			}
		}
		return result;
	}
	
	public FloatBuffer toFloatBuffer(){
		return BufferUtils.createFloatBuffer(_m);
	}
	
	public String toString(){
		String s = "";
		
		for(int y = 0; y < 4; y++){
			s = s + "[" + _m[0 + y * 4] + " " + _m[1 + y * 4] + " " + _m[2 + y * 4] + " " + _m[3 + y * 4] + "]\n";
		}
		
		return s;
		
	}
	
	
}
