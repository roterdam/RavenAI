package raven.ai.engine;

import java.util.List;

public class FigurePairMapping {

	public Figure Figure1;
	public Figure Figure2;
	List<NodeMapping> NodeMappings;
	
	public String GetCorrespondingNode1Name(String node2Name) {
		for(NodeMapping map : NodeMappings) {
			if(map.Node2.Name.toLowerCase().equals(node2Name.toLowerCase())) {
				return map.Node1.Name;
			}
		}
		return null;
	}
	
	public String GetCorrespondingNode2Name(String node1Name) {
		for(NodeMapping map : NodeMappings) {
			if(map.Node1.Name.toLowerCase().equals(node1Name.toLowerCase())) {
				return map.Node2.Name;
			}
		}
		return null;
	}
}
