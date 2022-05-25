package math;

public class Transform3 {

	double[][] _t = new double[3][3];
	
	public Transform3(){
		reset();
	}
	
	public void reset(){
		_t[0][0] = 1;_t[0][1] = 0;_t[0][2] = 0;
		_t[1][0] = 0;_t[1][1] = 1;_t[1][2] = 0;
		_t[2][0] = 0;_t[2][1] = 0;_t[2][2] = 1;
	}
	

	public void setTranslate(Vector2f dr){
		_t[0][2] = dr.x();
		_t[1][2] = dr.y();
	}
	
	public void setScale(double s){
		_t[0][0] = s;
		_t[1][1] = s;
	}
	
	//input vector gets normalized
	public void setRotation(Vector2f r){
		r.normalize();
		_t[0][0] = r.y();_t[0][1] = r.x();
		_t[1][0] = -r.x();_t[1][1] = r.y();		
	}
	
	/* Scale of 1 = pi/4 rotation.  It is not a linear scale.
	 * Higher scale means larger rotation.
	 */
	public void setRotationScale(double scale){
		Vector2f rot = new Vector2f(scale,1);
		setRotation(rot);
	}
	
	public void xReflection(){
		_t[0][0] = -1;
	}
	
	public void yReflection(){
		_t[1][1] = -1;
	}
	
	public Vector2f getDisplacement(){
		return new Vector2f(_t[0][2], _t[1][2]);
	}
	
	public double getValue(int i, int j){
		return _t[i][j];
	}
	
	public double determinant(){
		double det = _t[0][0]*(_t[1][1]*_t[2][2] - _t[1][2]*_t[2][1]) -
					 _t[0][1]*(_t[1][0]*_t[2][2] - _t[2][0]*_t[1][2]) +
					 _t[0][2]*(_t[1][0]*_t[2][1] - _t[2][0]*_t[1][1]);
		return det;
	}
	
	public Transform3 inverse(){
		Transform3 newT = new Transform3();
		double invDet = 1 / this.determinant();
		
		newT._t[0][0] = invDet * (_t[1][1]*_t[2][2] - _t[2][1]*_t[1][2]);
		newT._t[0][1] = invDet * (_t[0][2]*_t[2][1] - _t[2][2]*_t[0][1]);
		newT._t[0][2] = invDet * (_t[0][1]*_t[1][2] - _t[1][1]*_t[0][2]);
		newT._t[1][0] = invDet * (_t[1][2]*_t[2][0] - _t[2][2]*_t[1][0]);
		newT._t[1][1] = invDet * (_t[0][0]*_t[2][2] - _t[2][0]*_t[0][2]);
		newT._t[1][2] = invDet * (_t[0][2]*_t[1][0] - _t[1][2]*_t[0][0]);
		newT._t[2][0] = invDet * (_t[1][0]*_t[2][1] - _t[2][0]*_t[1][1]);
		newT._t[2][1] = invDet * (_t[0][1]*_t[2][0] - _t[2][1]*_t[0][0]);
		newT._t[2][2] = invDet * (_t[0][0]*_t[1][1] - _t[1][0]*_t[0][1]);
		
		return newT;
	}
	
	public Vector2f transform(Vector2f v){
		double newX = (v.x() * _t[0][0]) + (v.y() * _t[0][1]) + _t[0][2];
		double newY = (v.x() * _t[1][0]) + (v.y() * _t[1][1]) + _t[1][2];
		return new Vector2f(newX, newY);
	}
	
	public Transform3 Mmultiply(Transform3 t){
		Transform3 newT = new Transform3();
	
		newT._t[0][0] = (_t[0][0] * t._t[0][0]) + (_t[0][1] * t._t[1][0]) + (_t[0][2] * t._t[2][0]);
		newT._t[0][1] = (_t[0][0] * t._t[0][1]) + (_t[0][1] * t._t[1][1]) + (_t[0][2] * t._t[2][1]);
		newT._t[0][2] = (_t[0][0] * t._t[0][2]) + (_t[0][1] * t._t[1][2]) + (_t[0][2] * t._t[2][2]);
		newT._t[1][0] = (_t[1][0] * t._t[0][0]) + (_t[1][1] * t._t[1][0]) + (_t[1][2] * t._t[2][0]);
		newT._t[1][1] = (_t[1][0] * t._t[0][1]) + (_t[1][1] * t._t[1][1]) + (_t[1][2] * t._t[2][1]);
		newT._t[1][2] = (_t[1][0] * t._t[0][2]) + (_t[1][1] * t._t[1][2]) + (_t[1][2] * t._t[2][2]);
		newT._t[2][0] = (_t[2][0] * t._t[0][0]) + (_t[2][1] * t._t[1][0]) + (_t[2][2] * t._t[2][0]);
		newT._t[2][1] = (_t[2][0] * t._t[0][1]) + (_t[2][1] * t._t[1][1]) + (_t[2][2] * t._t[2][1]);
		newT._t[2][2] = (_t[2][0] * t._t[0][2]) + (_t[2][1] * t._t[1][2]) + (_t[2][2] * t._t[2][2]);

		return newT;
	}
	
	public Transform3 Smultiply(double s){
		Transform3 newT = new Transform3();
		
		newT._t[0][0] = s*_t[0][0];
		newT._t[0][1] = s*_t[0][1];
		newT._t[0][2] = s*_t[0][2];
		newT._t[1][0] = s*_t[1][0];
		newT._t[1][1] = s*_t[1][1];
		newT._t[1][2] = s*_t[1][2];
		newT._t[2][0] = s*_t[2][0];
		newT._t[2][1] = s*_t[2][1];
		newT._t[2][2] = s*_t[2][2];

		return newT;
		
	}
	
	public String toString(){
		
		String s = "[" + _t[0][0] + " " + _t[0][1] + " " + _t[0][2] + "]\n" +
				   "[" + _t[1][0] + " " + _t[1][1] + " " + _t[1][2] + "]\n" +
				   "[" + _t[2][0] + " " + _t[2][1] + " " + _t[2][2] + "]\n";
		return s;
	}
}
