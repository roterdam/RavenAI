package raven.ai.engine;


public class Rotation extends Transformation {

	public int Degrees;
	
	public Rotation() {
		
	}
	
	public Rotation(Transformation transform) {
		this.BeforeAttributeValue = transform.BeforeAttributeValue;
		this.AfterAttributeValue = transform.AfterAttributeValue;
		this.AttributeName = transform.AttributeName;	
		this.Degrees = Math.abs(Integer.parseInt(transform.AfterAttributeValue) - Integer.parseInt(transform.BeforeAttributeValue));
	}
}
