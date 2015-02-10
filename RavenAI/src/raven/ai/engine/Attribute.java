package raven.ai.engine;

public class Attribute {
	public String Name;
    public String Value;
    public AttributeType Type;
    
    public Attribute() {
    	this.Type = AttributeType.Unknown;
    }
    
    public Attribute(RavensAttribute rAttribute) {
    	this.Name = rAttribute.getName();
    	this.Value = rAttribute.getValue();
    	this.Type = Common.GetAttributeType(this.Name);
    }  
    
    public Attribute(String name, String value) {
    	this.Name = name;
    	this.Value = value;
    	this.Type = Common.GetAttributeType(this.Name);
    }
    
}
