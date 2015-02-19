package raven.ai.engine;

import java.util.List;

public class Figure {
	public RavensFigure RFigure;
	public List<Node> Nodes;
	
	public String getName() {
		return RFigure.getName();
	}
		
	public Node FindNode(String nodeName) {
		if(Nodes == null || nodeName == null) {
			return null;
		}
		for(Node node : Nodes) {
			if(node.Name.toLowerCase().equals(nodeName.toLowerCase())) {
				return node;
			}
		}
		return null;
	}
}
