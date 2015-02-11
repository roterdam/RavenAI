package raven.ai.engine;

public class Transformation {

	public String AttributeName;
	public String BeforeAttributeValue;
	public String AfterAttributeValue;
	public int Weight;
	
	public boolean attributeChanged() {
		return !BeforeAttributeValue.equals(AfterAttributeValue);
	}
}
