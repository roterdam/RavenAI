package raven.ai.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Edge {

	public Node NodeA;
	public Node NodeB;
	
	public Edge() {
		
	}
	
	public Edge(Node nodeA, Node nodeB) {
		this.NodeA = nodeA;
		this.NodeB = nodeB;
	}
	
	public int calculateDegreeChange() {
		
		if(NodeA == null || NodeB == null) return -1;
		
		Attribute angle1 = NodeA.findAttribute("angle");
		Attribute angle2 = NodeB.findAttribute("angle");
		int change = 0;
		
		// Return -1 if either node has no angle
		if(angle1 == null || angle2 == null) return -1;
		
		change = Common.FormatAngle(Math.abs(Integer.parseInt(angle1.Value) - Integer.parseInt(angle2.Value)));
	
		return change;
	}

	public List<Transformation> GetTransformations() {

		List<Transformation> Transformations = new ArrayList<Transformation>();

		for(Attribute att : NodeA.Attributes) {

			Transformation t = new Transformation();
			t.AttributeName = att.Name;
			t.BeforeAttributeValue = att.Value;
			for(Attribute attB : NodeB.Attributes) {
				if(attB.Name.equals(att.Name)) {
					t.AfterAttributeValue = attB.Value;
				}
			}
			Transformations.add(t);
		}

		for(Attribute att : NodeB.Attributes) {

			boolean attributeExists = false;
			for(Transformation transform : Transformations) {
				if(transform.AttributeName.toLowerCase().equals(att.Name.toLowerCase())) {
					attributeExists = true;
				}
			}

			if(!attributeExists) {
				Transformation t = new Transformation();
				t.AttributeName = att.Name;
				t.AfterAttributeValue = att.Value;
				for(Attribute attA : NodeA.Attributes) {
					if(attA.Name.equals(att.Name)) {
						t.BeforeAttributeValue = attA.Value;
					}
				}
				Transformations.add(t);
			}
		}
		
		List<Transformation> detailedTransforms = new ArrayList<Transformation>();
		
		for(Transformation transform : Transformations) {
			if(transform.AttributeName.toLowerCase().equals("angle")) {
				Rotation r = new Rotation(transform);
				detailedTransforms.add(r);				
			}
			else if(transform.AttributeName.toLowerCase().equals("left-of")) {
				Additive a = new Additive(transform);
				detailedTransforms.add(a);
			}
			else if(transform.AttributeName.toLowerCase().equals("right-of")) {
				Additive a = new Additive(transform);
				detailedTransforms.add(a);
			}
			else if(transform.AttributeName.toLowerCase().equals("above")) {
				Additive a = new Additive(transform);
				detailedTransforms.add(a);
			}
			else if(transform.AttributeName.toLowerCase().equals("below")) {
				Additive a = new Additive(transform);
				detailedTransforms.add(a);
			}
			else if(transform.AttributeName.toLowerCase().equals("inside")) {
				Additive a = new Additive(transform);
				detailedTransforms.add(a);
			}
			else if(transform.AttributeName.toLowerCase().equals("overlaps")) {
				Additive a = new Additive(transform);
				detailedTransforms.add(a);
			}
			else if(transform.AttributeName.toLowerCase().equals("fill")) {
				Additive a = new Additive(transform);
				detailedTransforms.add(a);
			}
			else {
				detailedTransforms.add(transform);
			}
		}
		
		return detailedTransforms;

	}
	
//	public ViableObject GenerateViableObject() {
//
//		ViableObject expectedObject = new ViableObject();			
//
//		if(NodeA != null && NodeB != null) {							
//
//			// Node wasn't deleted
//
//			// Assign temporary name
//			expectedObject.Name = NodeA.Name;			
//
//			List<Transformation> transformations = GetTransformations();
//			for(Transformation transformation : transformations) {
//				if(transformation.BeforeAttributeValue != null && transformation.AfterAttributeValue != null) {
//					if(!transformation.attributeChanged()) {
//
//						// That means transformation is unchanged
//
//						Node cNode = figureC.FindNode(ACMapping.GetCorrespondingNode2Name(NodeA.Name));
//						//Node cNode = figureC.FindNode(NodeA.Name);	
//						if(cNode != null) {
//							Attribute cNodeAttribute = cNode.findAttribute(transformation.AttributeName);
//
//							if(cNodeAttribute != null) {
//								expectedObject.addAttribute(transformation.AttributeName, cNodeAttribute.Value);
//							}
//						}
//					}					
//					else {
//						Node cNode = figureC.FindNode(NodeA.Name);	
//						Node bNode = figureB.FindNode(ABMapping.GetCorrespondingNode2Name(NodeA.Name));
//						//Node bNode = figureB.FindNode(cNode.Name);
//
//						if(cNode != null && bNode != null) {
//
//							Attribute cNodeAttribute = cNode.findAttribute(transformation.AttributeName);	
//							Attribute bNodeAttribute = bNode.findAttribute(transformation.AttributeName);
//
//							expectedObject.addAttribute(transformation.AttributeName, bNodeAttribute.Value);	
//
//
//							if(cNodeAttribute != null && bNodeAttribute != null) {
//
//								AttributeType attributeType = Common.GetAttributeType(transformation.AttributeName);
//								if(attributeType == AttributeType.Additive) {
//									List<String> cNodeAttributeValues = Arrays.asList(cNodeAttribute.Value.split(","));
//									List<String> bNodeAttributeValues = Arrays.asList(bNodeAttribute.Value.split(","));																
//									expectedObject.addAttribute(transformation.AttributeName, Common.CombineLists(cNodeAttributeValues, bNodeAttributeValues));
//								}
//								else if(attributeType == AttributeType.Positional) {							
//									if(cNodeAttribute.Value != null && cNodeAttribute.Value != null) {
//										List<String> cNodeAttributeValues = Arrays.asList(cNodeAttribute.Value.split(","));
//										List<String> bNodeAttributeValues = Arrays.asList(bNodeAttribute.Value.split(","));	
//										expectedObject.addAttribute(transformation.AttributeName, Common.CombineLists(cNodeAttributeValues, bNodeAttributeValues));
//									}
//
//								}		
//								else if(attributeType == AttributeType.Angular) {
//									int firstAngle = Integer.parseInt(transformation.BeforeAttributeValue);
//									int secondAngle = Integer.parseInt(transformation.AfterAttributeValue);
//									int CAngle = Integer.parseInt(cNodeAttribute.Value);
//
//									if(bNode.getShape().equalsIgnoreCase(cNode.getShape())) {
//										Shape shape = Common.Shapes.get(bNode.getShape());
//										int difference = Common.FormatAngle(secondAngle - firstAngle);	
//										if(difference == 180) {
//											if(firstAngle == 0 || firstAngle == 180) {
//												if(shape.hasHorizontalAxisSymmetry()) {
//
//													// Nothing has changed, create appropriate vobjects
//													expectedObject.addAttribute(transformation.AttributeName, transformation.BeforeAttributeValue);
//
//												}
//												else if(shape.hasVerticalAxisSymmetry()) {
//
//													// Reflected across vertical axis
//													expectedObject.addAttribute("horizontal-flip", "yes");
//													expectedObject.addAttribute("angle", transformation.BeforeAttributeValue);
//												}
//
//											}
//											else if(firstAngle == 90 || firstAngle == 270) {
//												if(shape.hasHorizontalAxisSymmetry()) {
//
//													// Reflected across vertical axis
//													expectedObject.addAttribute("horizontal-flip", "yes");
//													expectedObject.addAttribute("angle", cNodeAttribute.Value);
//												}
//												else if(shape.hasVerticalAxisSymmetry()) {
//
//													// Nothing has changed
//													expectedObject.addAttribute(transformation.AttributeName, transformation.BeforeAttributeValue);
//												}
//
//											}
//										}
//									}
//
//									int difference = Common.FormatAngle(secondAngle - firstAngle);																									
//
//									String angle1 = String.valueOf(Common.FormatAngle(CAngle + difference));																											
//									expectedObject.addAttribute(transformation.AttributeName, angle1);
//
//									int difference2 = Common.FormatAngle(secondAngle + firstAngle);																									
//
//									String angle2 = String.valueOf(Common.FormatAngle(CAngle + difference2));
//									expectedObject.addAttribute(transformation.AttributeName, angle2);
//								}															
//
//								//expectedObject.addAttribute(transformation.AttributeName, bNodeAttribute.Value);	
//							}
//						}
//					}	
//				}
//				else if(transformation.BeforeAttributeValue == null) {
//
//					// Attribute has been added
//					expectedObject.addAttribute(transformation.AttributeName, transformation.AfterAttributeValue);
//				}
//				else if(transformation.AfterAttributeValue == null) {
//
//					// Attribute has been deleted
//				}
//			}
//		}
//		else if(NodeA != null && NodeB == null) {
//
//			// Node has been added
//		}
//		else if(NodeA == null && NodeB != null) {
//
//			// Node has been deleted
//		}
//
//		// Check for any added attributes
//		for(Node bNode : figureB.Nodes) {
//			if(bNode.Name.equals(expectedObject.Name)) {
//				if(bNode.getAttributeCount() > expectedObject.getAttributeCount()) {
//					for(Attribute bAttribute : bNode.Attributes) {
//
//						boolean attributeFound = false;
//
//						for(Attribute myAttribute : expectedObject.Attributes) {
//							if(myAttribute.Name.equals(bAttribute.Name)) {
//								attributeFound = true;
//							}
//						}
//						if(!attributeFound) {
//							expectedObject.Attributes.add(bAttribute);
//						}
//					}
//				}
//			}
//		}
//
//
//		if(expectedObject.Name != null) {
//			if(DEBUG) {
//				System.out.println("Expected Object:");
//				System.out.println(expectedObject.Name);
//				for(AttributeGroup group : expectedObject.AttributeGroups) {
//					for(Attribute att : group.Attributes) {
//						System.out.println(att.Name + " : " + att.Value);
//					}
//				}
//				System.out.println("----------------------------");
//			}
//
//
//			return expectedObject;
//		}
//
//	}
}
