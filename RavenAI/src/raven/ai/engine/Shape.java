package raven.ai.engine;

import java.util.HashMap;

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
	
	public static HashMap<String,Shape> CreateShapeMap() {

		HashMap<String,Shape> shapes = new HashMap<String,Shape>();

		// Create shapes
		Shape circle = new Shape(1, true, true, 1);
		Shape square = new Shape(4, true, true, 90);
		Shape plus = new Shape(12, true, true, 90);
		Shape triangle = new Shape(3, false, true, 120);
		Shape pacman = new Shape(3, true, false, 360);
		Shape diamond = new Shape(4, true, true, 90);
		Shape arrow = new Shape(7, true, false, 360);
		Shape halfarrow = new Shape(5, false, false, 360);
		Shape rectangle = new Shape(4, true, true, 180);
		Shape pentagon = new Shape(5, false, true, 72);
		Shape hexagon = new Shape(6, true, true, 60);
		Shape heptagon = new Shape(7, false, true, 360);
		Shape septagon = new Shape(7, false, true, 360);
		Shape octogon = new Shape(8, true, true, 45);

		// Add shape relations
		square.setRelatedShape(square.new ShapeRelation(diamond, 45));
		diamond.setRelatedShape(diamond.new ShapeRelation(square, 45));			
		heptagon.setRelatedShape(heptagon.new ShapeRelation(septagon, 0));
		septagon.setRelatedShape(septagon.new ShapeRelation(heptagon, 0));

		// Add shapes to shape map
		shapes.put("circle", circle);
		shapes.put("square", square);
		shapes.put("plus", plus);
		shapes.put("triangle", triangle);
		shapes.put("Pac-Man", pacman);
		shapes.put("diamond", diamond);
		shapes.put("arrow", arrow);
		shapes.put("half-arrow", halfarrow);
		shapes.put("rectangle", rectangle);
		shapes.put("pentagon", pentagon);
		shapes.put("hexagon", hexagon);
		shapes.put("heptagon", heptagon);
		shapes.put("septagon", septagon);
		shapes.put("octogon", octogon);

		return shapes;
	}
	
}
