package raven.ai.engine;

import java.util.ArrayList;
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
	
	public void PrintMappingInfo() {
		System.out.println(Figure1.getName() + " --> " + Figure2.getName() + " Node Mappings");
		for(NodeMapping map : NodeMappings) {
			String node1Name = map.Node1 != null ? map.Node1.Name : "";
			String node2Name = map.Node2 != null ? map.Node2.Name : "";
			System.out.println(node1Name + " -> " + node2Name + " Score: " + map.Score);
		}
	}

	public List<Edge> ToEdges() {

		List<Edge> edges = new ArrayList<Edge>();

		for(NodeMapping map : NodeMappings) {
			Edge edge = new Edge();
			edge.NodeA = map.Node1;
			edge.NodeB = map.Node2;
			edges.add(edge);
		}
		
		return edges;
	}
}
