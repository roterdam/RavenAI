package raven.ai.engine;

import java.util.List;

public class Figure {
	public RavensFigure RFigure;
	public List<Node> Nodes;
	
	public String getName() {
		return RFigure.getName();
	}
		
	public Node FindNode(String nodeName) {
		for(Node node : Nodes) {
			if(node.Name.toLowerCase().equals(nodeName.toLowerCase())) {
				return node;
			}
		}
		return null;
	}
}
