package raven.ai.engine;

public class Translation extends Transformation {

	public String BeforeTargetName;
	public String AfterTargetName;
	
	public Translation() {
		
	}
	
	public Translation(Transformation transform) {
		this.BeforeAttributeValue = transform.BeforeAttributeValue;
		this.AfterAttributeValue = transform.AfterAttributeValue;
		this.AttributeName = transform.AttributeName;			
	}
}
