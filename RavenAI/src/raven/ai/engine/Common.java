package raven.ai.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Common {

	public static class Weighting {

		public static final int Unchanged = 8;
		public static final int Angle_Unchanged = 2;
		public static final int Reflected = 6;
		public static final int Rotated = 2;
		public static final int Scaled = 2;
		public static final int Deleted = 1;
		public static final int Added = 1;
		public static final int Shape_Changed = 2;
		public static final int Moved = 1;
		public static final int Unknown = 1;
	}

	public static int GetTransformationScore(String name, String valueA, String valueB, String shape) {
		
		if(name.toLowerCase().equals("angle")) {
			if(valueA.toLowerCase().equals(valueB)) {
				return Weighting.Angle_Unchanged;
			}
			else {
				
				int difference = Math.abs(Integer.parseInt(valueB) - Integer.parseInt(valueA));
				
				// Shapes the same with 0 degree differences
				if(shape.toLowerCase().equals("circle")) {
					return Weighting.Angle_Unchanged;
				}
				
				// Shapes the same with 90 degree differences
				if(shape.toLowerCase().equals("square") || shape.toLowerCase().equals("diamond") || shape.toLowerCase().equals("plus")) {
					if(difference == 90 || difference == 180) {
						return Weighting.Angle_Unchanged;
					}
				}
				
				// Shapes the same with 180 degree differences
				if(shape.toLowerCase().equals("rectangle") || shape.toLowerCase().equals("pac-man") || shape.toLowerCase().equals("arrow")) {
					if(difference == 180) {
						return Weighting.Angle_Unchanged;
					}
				}
			}
		}
		
		if(valueA.toLowerCase().equals(valueB) && name.toLowerCase().equals("angle")) {
			return Weighting.Angle_Unchanged;
		}
		
		if(valueA.toLowerCase().equals(valueB)) {
			return Weighting.Unchanged;
		}					

		if(name.toLowerCase().equals("vertical-flip") && valueA.toLowerCase() != valueB.toLowerCase()) {
			return Weighting.Reflected;
		}

		if(name.toLowerCase().equals("horizontal-flip") && valueA.toLowerCase() != valueB.toLowerCase()) {
			return Weighting.Reflected;
		}

		if(name.toLowerCase().equals("angle") && valueA.toLowerCase() != valueB.toLowerCase()) {

			int difference = Math.abs(Integer.parseInt(valueA) - Integer.parseInt(valueB));

			if(difference == 45) {
				return Weighting.Rotated;
			}

			if(difference == 90) {
				return Weighting.Rotated;
			}

			if(difference == 180) {
				return Weighting.Reflected;
			}

			if(difference == 270) {
				return Weighting.Rotated;
			}
		}

		if(name.toLowerCase().equals("size") && valueA.toLowerCase() != valueB.toLowerCase()) {
			return Weighting.Scaled;
		}

		if(name.toLowerCase().equals("shape") && valueA.toLowerCase() != valueB.toLowerCase()) {
			return Weighting.Shape_Changed;
		}

		if(name.toLowerCase().equals("left-of") && valueA.toLowerCase() != valueB.toLowerCase()) {
			return Weighting.Moved;
		}

		if(name.toLowerCase().equals("right-of") && valueA.toLowerCase() != valueB.toLowerCase()) {
			return Weighting.Moved;
		}

		if(name.toLowerCase().equals("above") && valueA.toLowerCase() != valueB.toLowerCase()) {
			return Weighting.Moved;		
		}

		if(name.toLowerCase().equals("below") && valueA.toLowerCase() != valueB.toLowerCase()) {
			return Weighting.Moved;		
		}

		if(name.toLowerCase().equals("inside") && valueA.toLowerCase() != valueB.toLowerCase()) {
			return Weighting.Moved;		
		}

		if(name.toLowerCase().equals("overlaps") && valueA.toLowerCase() != valueB.toLowerCase()) {
			return Weighting.Moved;		
		}

		return Weighting.Unknown;
	}

	public static ProblemType GetProblemType(String problemType) {

		if(problemType.contains("2x1")){
			return ProblemType.TwoByOne;
		}
		else if(problemType.contains("2x2")){
			return ProblemType.TwoByTwo;
		}
		else if(problemType.contains("3x3")){
			return ProblemType.ThreeByThree;
		}
		else {
			return ProblemType.TwoByOne;
		}

	}

	public static int GetNodeSimilarityScore(Node node1, Node node2) {

		int score = 0;

		int matchCount = 0;

		for(Attribute node1Attribute : node1.Attributes) {
			for(Attribute node2Attribute : node2.Attributes) {
				if(node1Attribute.Name.toLowerCase().equals(node2Attribute.Name.toLowerCase())) {					
					score += GetTransformationScore(node1Attribute.Name, node1Attribute.Value, node2Attribute.Value, node2.getShape());

					if(node1Attribute.Value.toLowerCase().equals(node2Attribute.Value.toLowerCase())) {
						matchCount++;
					}
				}
			}
		}

		if(node1.getAttributeCount() == node2.getAttributeCount() && node1.getAttributeCount() == matchCount) {
			score += 30;
		}

		return score;
	}

	public static int GetNodeViableObjectSimilarityScore(ViableObject vObject, Node node2) {

		//int maxGroupScore = 0;
		int score = 0;
		int matchCount = 0;
		for(AttributeGroup group : vObject.AttributeGroups) {
			//int groupScore = 0;
			int maxAttributeScore = 0;
			for(Attribute vObjectAttribute : group.Attributes) {
				int attributeScore = 0;
				for(Attribute node2Attribute : node2.Attributes) {
					if(vObjectAttribute.Name.toLowerCase().equals(node2Attribute.Name.toLowerCase())) {					
						attributeScore += GetTransformationScore(vObjectAttribute.Name, vObjectAttribute.Value, node2Attribute.Value, node2.getShape());

						if(vObjectAttribute.Value.toLowerCase().equals(node2Attribute.Value.toLowerCase())) {
							matchCount++;
						}
					}
				}
				if(attributeScore >= maxAttributeScore) {
					maxAttributeScore = attributeScore;
				}
			}
			score += maxAttributeScore;
			//			if(groupScore >= maxGroupScore) {
			//				maxGroupScore = groupScore;
			//			}

		}



		//		if(vObject.getAttributeCountWithoutGroups() == node2.getAttributeCount()) {
		//			maxGroupScore += 1;
		//		}

		// Award points for perfect match
		if(vObject.getAttributeCountWithoutGroups() == node2.getAttributeCount() && vObject.getAttributeCountWithoutGroups() == matchCount) {
			score += 30;
		}

		return score;
	}

	public static Node GetNodeByName(List<Node> nodes, String name) {

		for(Node node : nodes) {
			if(node.Name.equals(name)) {
				return node;
			}
		}
		return null;
	}

	public static List<ViableAnswer> GetAnswerFigures(HashMap<String, Figure> allFigures) {

		List<ViableAnswer> answers = new ArrayList<ViableAnswer>();

		Iterator it = allFigures.entrySet().iterator();

		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();    		
			Figure f = new Figure();
			f.RFigure = ((Figure) pairs.getValue()).RFigure;

			if(f.RFigure.getName().matches("^\\d*")) {

				List<Node> newNodes = new ArrayList<Node>();    		    	    		
				for(RavensObject obj : f.RFigure.getObjects()) {
					Node newNode = new Node(obj);
					newNode.Name = obj.getName();
					//newNode.RObject = obj;
					newNodes.add(newNode);
				}

				f.Nodes = newNodes;

				ViableAnswer answer = new ViableAnswer(f, 0);

				answers.add(answer);
			}

		}

		return answers;
	}

	public static int GetAttributeCount(Figure figure) {

		int attributeCount = 0;

		for(RavensObject rObject : figure.RFigure.getObjects()) {
			for(RavensAttribute rAttribute : rObject.getAttributes()) {
				attributeCount += 1;
			}
		}

		return attributeCount;
	}

	public static int GetAttributeCount(Node node) {

		int attributeCount = 0;

		for(Attribute rAttribute : node.Attributes) {
			attributeCount += 1;
		}		

		return attributeCount;
	}

	public static int FormatAngle(int angle) {
		int outputAngle = angle % 360;

		if(outputAngle < 0) {
			outputAngle += 360;
		}
		return outputAngle;
	}

	public static AttributeType GetAttributeType(String attributeName) {

		switch(attributeName) {
		case "fill":
			return AttributeType.Additive;			
		case "shape":
			return AttributeType.Descriptive;
		case "angle":
			return AttributeType.Angular;
		case "size":
			return AttributeType.Descriptive;
		case "vertical-flip":
			return AttributeType.Descriptive;
		case "horizontal-flip":
			return AttributeType.Descriptive;
		case "inside":
			return AttributeType.Positional;
		case "outside":
			return AttributeType.Positional;
		case "above":
			return AttributeType.Positional;
		case "below":
			return AttributeType.Positional;
		case "left-of":
			return AttributeType.Positional;
		case "right-of":
			return AttributeType.Positional;
		case "overlaps":
			return AttributeType.Positional;

		default:
			return AttributeType.Unknown;
		}
	}

	public static String GenerateRandomLetter() {

		Random r = new Random();

		String alphabet = "ABCDEFGHIJKLMNOP";
		return Character.toString(alphabet.charAt(r.nextInt(alphabet.length())));    
	}

	public static String CombineLists(List<String> list1, List<String> list2) {

		List<String> combinedList = new ArrayList<String>();
		String output = "";

		for(String listItem : list1) {
			if(!combinedList.contains(listItem)) {
				combinedList.add(listItem);
			}
		}

		for(String listItem : list2) {
			if(!combinedList.contains(listItem)) {
				combinedList.add(listItem);
			}
		}

		for(String listItem : combinedList) {
			output = output.concat(listItem);
			output = output.concat(",");
		}

		output = output.substring(0, output.length()-1);

		return output;
	}

	public static boolean ListsEqual(List<String> list1, List<String> list2) {

		if(list1.size() != list2.size()) {
			return false;
		}

		for(String listItem : list1) {
			if(!list2.contains(listItem)) {
				return false;
			}
		}

		return true;
	}

}
