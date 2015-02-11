package raven.ai.engine;

public class Shape {
	private int _sides;
	private boolean _verticalAxisSymmetry;
	private boolean _horizontalAxisSymmetry;
	private int _degreesRotationUntilSame;
	private ShapeRelation _relatedShape;
	
	public Shape(int sides, boolean vSymmetry, boolean hSymmetry, int degreesUntilSame) {
		this._sides = sides;
		this._verticalAxisSymmetry = vSymmetry;
		this._horizontalAxisSymmetry = hSymmetry;
		this._degreesRotationUntilSame = degreesUntilSame;
	}
	
	public Shape(int sides, boolean vSymmetry, boolean hSymmetry, int degreesUntilSame, ShapeRelation relatedShape) {
		this(sides, vSymmetry, hSymmetry, degreesUntilSame);
		this._relatedShape = relatedShape;
	}
	
	public int getSides() {
		return _sides;
	}
	
	public boolean hasVerticalAxisSymmetry() {
		return _verticalAxisSymmetry;
	}
	
	public boolean hasHorizontalAxisSymmetry() {
		return _horizontalAxisSymmetry;
	}
	
	public int getDegreesRotationUntilSame() {
		return _degreesRotationUntilSame;
	}
	
	public ShapeRelation getRelatedShape() {
		return _relatedShape;
	}
	
	public void setRelatedShape(ShapeRelation relation) {
		this._relatedShape = relation;
	}
	
	public class ShapeRelation {
		private Shape _relatedShape;
		private int _degreesUntilRelation;
		
		public ShapeRelation(Shape relatedShape, int degreesUntilRelation) {
			this._relatedShape = relatedShape;
			this._degreesUntilRelation = degreesUntilRelation;
		}
		
		public Shape getRelatedShape() {
			return _relatedShape;
		}
		
		public int getDegreesUntilRelation() {
			return _degreesUntilRelation;
		}
	}
}
