package raven.ai.engine;

import java.util.ArrayList;
import java.util.List;


public class Node {
	
	public String Name;
	private RavensObject RObject;
	public List<Attribute> Attributes;
	
	public Node(RavensObject rObj) {
		this.RObject = rObj;
		this.Name = rObj.getName();
		this.Attributes = new ArrayList<Attribute>();
		for(RavensAttribute att : rObj.getAttributes()) {
			this.Attributes.add(new Attribute(att.getName(), att.getValue()));
		}
	}
	
	public Node() {
		this.Attributes = new ArrayList<Attribute>();
	}
	
	public int getAttributeCount() {
		return this.Attributes.size();
	}
	
	public String getShape() {
		for(Attribute att : Attributes) {
			if(att.Name.toLowerCase().equals("shape")) {
				return att.Value;
			}
		}
		return null;
	}
	
	public Node getCorrespondingNode(Figure figure) {
		
		int maxScore = 0;
		Node bestNode = null;
					
		for(Node node : figure.Nodes) {
			
			int currentScore = 0;
			
			// Heavy emphasis temporarily on matching object names
			if(node.Name.equals(this.Name)) {
				currentScore += 4;
			}
			
			if(this.Attributes.size() == node.Attributes.size()) {
				currentScore += 1;
			}
			
			for(Attribute attribute : node.Attributes) {
	        	if(this.containsAttribute(attribute.Name)) {
	        		currentScore += 1;
	        	}
	        }
			
			if(currentScore >= maxScore) {
				maxScore = currentScore;
				bestNode = node;
			}
		}
		
		return bestNode;
	}
	
	public boolean containsAttribute(String attributeName) {
		
		for(Attribute attribute : this.Attributes) {
			if(attribute.Name.equals(attributeName))
				return true;
		}
		return false;
	}
}
