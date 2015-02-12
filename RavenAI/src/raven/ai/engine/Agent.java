package raven.ai.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Your Agent for solving Raven's Progressive Matrices. You MUST modify this
 * file.
 * 
 * You may also create and submit new files in addition to modifying this file.
 * 
 * Make sure your file retains methods with the signatures:
 * public Agent()
 * public char Solve(RavensProblem problem)
 * 
 * These methods will be necessary for the project's main method to run.
 * 
 */
public class Agent {

	private static final boolean DEBUG = true;

	/**
	 * The default constructor for your Agent. Make sure to execute any
	 * processing necessary before your Agent starts solving problems here.
	 * 
	 * Do not add any variables to this signature; they will not be used by
	 * main().
	 * 
	 */
	public Agent() {

	}
	/**
	 * The primary method for solving incoming Raven's Progressive Matrices.
	 * For each problem, your Agent's Solve() method will be called. At the
	 * conclusion of Solve(), your Agent should return a String representing its
	 * answer to the question: "1", "2", "3", "4", "5", or "6". These Strings
	 * are also the Names of the individual RavensFigures, obtained through
	 * RavensFigure.getName().
	 * 
	 * In addition to returning your answer at the end of the method, your Agent
	 * may also call problem.checkAnswer(String givenAnswer). The parameter
	 * passed to checkAnswer should be your Agent's current guess for the
	 * problem; checkAnswer will return the correct answer to the problem. This
	 * allows your Agent to check its answer. Note, however, that after your
	 * agent has called checkAnswer, it will *not* be able to change its answer.
	 * checkAnswer is used to allow your Agent to learn from its incorrect
	 * answers; however, your Agent cannot change the answer to a question it
	 * has already answered.
	 * 
	 * If your Agent calls checkAnswer during execution of Solve, the answer it
	 * returns will be ignored; otherwise, the answer returned at the end of
	 * Solve will be taken as your Agent's answer to this problem.
	 * 
	 * @param problem the RavensProblem your agent should solve
	 * @return your Agent's answer to this problem
	 */
	public String Solve(RavensProblem problem) {

		if(DEBUG) {
			System.out.println("PROBLEM " + problem.getName());
			System.out.println("============================");
		}

		HashMap<String, Figure> figures = CreateFigures(problem.getFigures());

		HashMap<String, Shape> shapes = Shape.CreateShapeMap();

		if(Common.GetProblemType(problem.getProblemType()) == ProblemType.TwoByOne) {
			return SolveTwoByOne(figures, shapes);
		}

		return "1";
	}

	private String SolveTwoByOne(HashMap<String, Figure> figures, HashMap<String, Shape> shapes) {			

		Figure figureA = figures.get("A");
		Figure figureB = figures.get("B");
		Figure figureC = figures.get("C");

		FigurePairMapping ABMapping = CreateFigurePairMapping(figureA, figureB);
		FigurePairMapping ACMapping = CreateFigurePairMapping(figureA, figureC);

		// DEBUG OUTPUT	
		if(DEBUG) {

			System.out.println("Node Mappings");			
			for(NodeMapping map : ABMapping.NodeMappings) {
				String node1Name = map.Node1 != null ? map.Node1.Name : "";
				String node2Name = map.Node2 != null ? map.Node2.Name : "";
				System.out.println(node1Name + " -> " + node2Name + " Score: " + map.Score);
			}
			System.out.println("----------------------------");
		}

		List<Edge> edges = new ArrayList<Edge>();	

		for(NodeMapping map : ABMapping.NodeMappings) {
			Edge edge = new Edge();
			edge.NodeA = map.Node1;
			edge.NodeB = map.Node2;
			edges.add(edge);
		}		

		List<ViableAnswer> allAnswers = Common.GetAnswerFigures(figures);	

		List<ViableObject> expectedObjects = new ArrayList<ViableObject>();

		for(Iterator<ViableAnswer> iterator = allAnswers.iterator(); iterator.hasNext();) {
			ViableAnswer currentAnswer = iterator.next();

			// Eliminate answers with wrong number of nodes
			int expectedNodeCount = 0;
			if(figureB.Nodes.size() == figureA.Nodes.size()) {
				expectedNodeCount = figureC.Nodes.size();
			}
			if(figureA.Nodes.size() == figureC.Nodes.size()) {
				expectedNodeCount = figureB.Nodes.size();
			}

			if(expectedNodeCount != 0 && expectedNodeCount != currentAnswer.AnswerFigure.Nodes.size()) {
				currentAnswer.Incompatible = true;
			}		

		}			

		for(Edge edge : edges) {

			ViableObject expectedObject = new ViableObject();			

			if(edge.NodeA != null && edge.NodeB != null) {							

				// Node wasn't deleted

				// Assign temporary name
				expectedObject.Name = edge.NodeA.Name;			

				List<Transformation> transformations = edge.GetTransformations();
				for(Transformation transformation : transformations) {
					if(transformation.BeforeAttributeValue != null && transformation.AfterAttributeValue != null) {
						if(!transformation.attributeChanged()) {

							// That means transformation is unchanged

							Node cNode = figureC.FindNode(edge.NodeA.Name);	
							if(cNode != null) {
								Attribute cNodeAttribute = cNode.findAttribute(transformation.AttributeName);
								
								if(cNodeAttribute != null) {
									expectedObject.addAttribute(transformation.AttributeName, cNodeAttribute.Value);
								}
							}
						}					
						else {
							Node cNode = figureC.FindNode(edge.NodeA.Name);								
							Node bNode = figureB.FindNode(cNode.Name);

							if(cNode != null && bNode != null) {

								Attribute cNodeAttribute = cNode.findAttribute(transformation.AttributeName);	
								Attribute bNodeAttribute = bNode.findAttribute(transformation.AttributeName);

								AttributeType attributeType = Common.GetAttributeType(transformation.AttributeName);
								if(attributeType == AttributeType.Additive) {
									List<String> cNodeAttributeValues = Arrays.asList(cNodeAttribute.Value.split(","));
									List<String> bNodeAttributeValues = Arrays.asList(bNodeAttribute.Value.split(","));																
									expectedObject.addAttribute(transformation.AttributeName, Common.CombineLists(cNodeAttributeValues, bNodeAttributeValues));
								}
								else if(attributeType == AttributeType.Positional) {
									List<String> cNodeAttributeValues = Arrays.asList(cNodeAttribute.Value.split(","));
									List<String> bNodeAttributeValues = Arrays.asList(bNodeAttribute.Value.split(","));																

									expectedObject.addAttribute(transformation.AttributeName, Common.CombineLists(cNodeAttributeValues, bNodeAttributeValues));
								}		
								else if(attributeType == AttributeType.Angular) {
									int firstAngle = Integer.parseInt(transformation.BeforeAttributeValue);
									int secondAngle = Integer.parseInt(transformation.AfterAttributeValue);
									int CAngle = Integer.parseInt(cNodeAttribute.Value);

									int difference = Common.FormatAngle(secondAngle - firstAngle);																									

									String angle1 = String.valueOf(Common.FormatAngle(CAngle + difference));																											
									expectedObject.addAttribute(transformation.AttributeName, angle1);

									int difference2 = Common.FormatAngle(secondAngle + firstAngle);																									

									String angle2 = String.valueOf(Common.FormatAngle(CAngle + difference2));
									expectedObject.addAttribute(transformation.AttributeName, angle2);
								}															

								expectedObject.addAttribute(transformation.AttributeName, bNodeAttribute.Value);	

							}
						}	
					}
					else if(transformation.BeforeAttributeValue == null) {

						// Attribute has been added
						expectedObject.addAttribute(transformation.AttributeName, transformation.AfterAttributeValue);
					}
					else if(transformation.AfterAttributeValue == null) {

						// Attribute has been deleted
					}
				}
			}
			else if(edge.NodeA != null && edge.NodeB == null) {

				// Node has been added
			}
			else if(edge.NodeA == null && edge.NodeB != null) {

				// Node has been deleted
			}

			// Check for any added attributes
			for(Node bNode : figureB.Nodes) {
				if(bNode.Name.equals(expectedObject.Name)) {
					if(bNode.getAttributeCount() > expectedObject.getAttributeCount()) {
						for(Attribute bAttribute : bNode.Attributes) {

							boolean attributeFound = false;

							for(Attribute myAttribute : expectedObject.Attributes) {
								if(myAttribute.Name.equals(bAttribute.Name)) {
									attributeFound = true;
								}
							}
							if(!attributeFound) {
								expectedObject.Attributes.add(bAttribute);
							}
						}
					}
				}
			}


			if(expectedObject.Name != null) {
				if(DEBUG) {
					System.out.println("Expected Object:");
					System.out.println(expectedObject.Name);
					for(AttributeGroup group : expectedObject.AttributeGroups) {
						for(Attribute att : group.Attributes) {
							System.out.println(att.Name + " : " + att.Value);
						}
					}
					System.out.println("----------------------------");
				}


				expectedObjects.add(expectedObject);
			}


		}	

		// Make sure proper ratio of objects is present (add extras if necessary)
		if(figureA.Nodes.size() == figureB.Nodes.size() && figureC.Nodes.size() != expectedObjects.size()) {
			int count = figureC.Nodes.size() - expectedObjects.size();
			for(int i = 0;i < count; i++) {
				ViableObject newObject = new ViableObject();
				newObject.Name = Common.GenerateRandomLetter();
				for(AttributeGroup group : expectedObjects.get(0).AttributeGroups) {
					for(Attribute att : group.Attributes) {
						newObject.addAttribute(att.Name, att.Value);					
					}
				}
				expectedObjects.add(newObject);
			}
		}

		for(ViableAnswer currentAnswer : allAnswers) {
			if(!currentAnswer.Incompatible) {		

				FigurePairMapping CNumMapping = CreateFigurePairMapping(figures.get("C"), figures.get(currentAnswer.AnswerFigure.getName()));

				// DEBUG OUTPUT		
				//				if(DEBUG) {
				//					System.out.println("C-Answer " + currentAnswer.AnswerFigure.getName() + " mappings");
				//					for(NodeMapping map : CNumMapping.NodeMappings) {
				//						String node1Name = map.Node1 != null ? map.Node1.Name : "";
				//						String node2Name = map.Node2 != null ? map.Node2.Name : "";
				//						System.out.println(node1Name + " -> " + node2Name + " Score: " + map.Score);
				//					}

				//System.out.println("Answer " + currentAnswer.AnswerFigure.getName() + " score: " + totalScore);							
				//				}

				List<NodeMapping> expectedObjectMappings = CreateNodeViableObjectMapping(expectedObjects, currentAnswer.AnswerFigure.Nodes);

				for(ViableObject myObject : expectedObjects) {

					Node currentNode = null;

					for(NodeMapping map : expectedObjectMappings) {
						if(map.Node1 != null) {
							if(map.Node1.Name.toLowerCase().equals(myObject.Name.toLowerCase())) {
								currentNode = map.Node2;
							}
						}
					}

					if(currentNode == null) {
						currentNode = myObject.getCorrespondingNode(currentAnswer.AnswerFigure);
					}

					// Make sure attribute ratios match
					if(Common.GetAttributeCount(figureA) + Common.GetAttributeCount(figureB) != Common.GetAttributeCount(figureC) + Common.GetAttributeCount(currentAnswer.AnswerFigure)) {
						currentAnswer.Score -= 4;
					}

					for(AttributeGroup group : myObject.AttributeGroups) {
						for(Attribute myAttribute : group.Attributes) {			
							Attribute currentAttribute = currentNode.findAttribute(myAttribute.Name);
							if(currentAttribute != null) {
								int score = 0;

								score = Common.GetTransformationScore(currentAttribute.Name, currentAttribute.Value, myAttribute.Value, myObject.getShape());

								List<String> myValues = Arrays.asList(myAttribute.Value.split(","));
								List<String> nodeValues = Arrays.asList(currentAttribute.Value.split(","));

								if(Common.GetAttributeType(myAttribute.Name) == AttributeType.Positional) {
									for(int i = 0; i < myValues.size(); i++) {
										myValues.set(i, CNumMapping.GetCorrespondingNode2Name(myValues.get(i)));											
									}
								}

								if(Common.ListsEqual(myValues, nodeValues)) {
									currentAnswer.Score += myValues.size();
								}

								if(currentAttribute.Value.equals(myAttribute.Value)) {
									currentAnswer.Score += score;									
								}
							}
						}
					}
				}											
			}		
		}

		// Sort answers by score
		Collections.sort(allAnswers);

		if(DEBUG) {
			System.out.println("POSSIBLE ANSWERS");
			for(ViableAnswer pAnswer : allAnswers) {
				if(!pAnswer.Incompatible) {
					System.out.println(pAnswer.AnswerFigure.getName() + " : " + pAnswer.Score);
				}
			}

			System.out.println("");
		}

		// Return GUESS if guessing during debug
		if(DEBUG && allAnswers.size() > 1 && allAnswers.get(0).Score == allAnswers.get(1).Score) {
			System.out.println("Guessing...");
			return "GUESS";
		}

		return allAnswers.get(0).AnswerFigure.getName();
	}

	private HashMap<String, Figure> CreateFigures(HashMap<String, RavensFigure> figures) {

		HashMap<String, Figure> myFigures = new HashMap<String, Figure>();

		Iterator it = figures.entrySet().iterator();

		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			Figure f = new Figure();
			f.RFigure = (RavensFigure) pairs.getValue();

			// Create nodes
			List<Node> newNodes = new ArrayList<Node>();    		    	    		
			for(RavensObject obj : f.RFigure.getObjects()) {
				Node newNode = new Node(obj);
				newNode.Name = obj.getName();
				newNodes.add(newNode);
			}

			f.Nodes = newNodes;    		    	

			myFigures.put((String)pairs.getKey(), f);

		}

		return myFigures;
	}

	

	private List<NodeMapping> CreateNodePairMapping(List<Node> nodeList1, List<Node> nodeList2) {

		List<NodeMapping> mappings = new ArrayList<NodeMapping>();

		int nodeList1Count = nodeList1.size();
		int nodeList2Count = nodeList2.size();

		int[][] mapMatrix = new int[nodeList1Count][nodeList2Count];

		for(int i = 0; i < nodeList1Count; i++) {
			for(int j = 0; j < nodeList2Count; j++) {
				Node node1 = nodeList1.get(i);
				Node node2 = nodeList2.get(j);

				mapMatrix[i][j] = Common.GetNodeSimilarityScore(node1, node2);
			}
		}

		// Determine number of times to loop through mapping matrix
		int loopCount = nodeList1Count >= nodeList2Count ? nodeList2Count : nodeList1Count;

		int maxScore = 1;
		int maxX = 0;
		int maxY = 0;

		for(int l = 0; l < loopCount; l++) {			
			maxScore = 0;
			maxX = 0;
			maxY = 0;						

			for(int i = 0; i < nodeList1Count; i++) {
				for(int j = 0; j < nodeList2Count; j++) {

					if(mapMatrix[i][j] > maxScore) {
						maxScore = mapMatrix[i][j];
						maxX = i;
						maxY = j;					
					}

				}
			}

			NodeMapping map = new NodeMapping();
			map.Node1 = nodeList1.get(maxX);
			map.Node2 = nodeList2.get(maxY);
			map.Score = maxScore;
			mappings.add(map);

			// Zero out row and column to prevent duplicate pairings
			for(int y = 0; y < nodeList1Count; y++) {
				mapMatrix[y][maxY] = 0;
			}

			for(int z = 0; z < nodeList2Count; z++) {
				mapMatrix[maxX][z] = 0;
			}

		}

		// Handle removed nodes
		if(nodeList1Count > nodeList2Count) {
			for(Node strandedNode : nodeList1) {
				boolean nodeFound = false;
				for(NodeMapping map : mappings) {
					if(strandedNode.Name.toLowerCase().equals(map.Node1.Name.toLowerCase())) {
						nodeFound = true;
					}
				}
				if(!nodeFound) {
					NodeMapping map = new NodeMapping();
					map.Node1 = strandedNode;
					map.Node2 = null;						
					mappings.add(map);
				}
			}
		}

		// Handle added nodes
		if(nodeList2Count > nodeList1Count) {
			for(Node strandedNode : nodeList2) {
				boolean nodeFound = false;
				for(NodeMapping map : mappings) {
					if(strandedNode.Name.toLowerCase().equals(map.Node2.Name.toLowerCase())) {
						nodeFound = true;
					}
				}
				if(!nodeFound) {
					NodeMapping map = new NodeMapping();
					map.Node1 = null;
					map.Node2 = strandedNode;								
					mappings.add(map);
				}
			}
		}

		return mappings;
	}

	private List<NodeMapping> CreateNodeViableObjectMapping(List<ViableObject> vObjectList, List<Node> nodeList) {

		List<NodeMapping> mappings = new ArrayList<NodeMapping>();

		int nodeList1Count = vObjectList.size();
		int nodeList2Count = nodeList.size();

		int[][] mapMatrix = new int[nodeList1Count][nodeList2Count];

		for(int i = 0; i < nodeList1Count; i++) {
			for(int j = 0; j < nodeList2Count; j++) {
				ViableObject node1 = vObjectList.get(i);
				Node node2 = nodeList.get(j);

				mapMatrix[i][j] = Common.GetNodeViableObjectSimilarityScore(node1, node2);
			}
		}

		// Determine number of times to loop through mapping matrix
		int loopCount = nodeList1Count >= nodeList2Count ? nodeList2Count : nodeList1Count;

		int maxScore = 1;
		int maxX = 0;
		int maxY = 0;

		for(int l = 0; l < loopCount; l++) {			
			maxScore = 0;
			maxX = 0;
			maxY = 0;						

			for(int i = 0; i < nodeList1Count; i++) {
				for(int j = 0; j < nodeList2Count; j++) {

					if(mapMatrix[i][j] > maxScore) {
						maxScore = mapMatrix[i][j];
						maxX = i;
						maxY = j;					
					}

				}
			}

			NodeMapping map = new NodeMapping();
			map.Node1 = vObjectList.get(maxX);
			map.Node2 = nodeList.get(maxY);
			map.Score = maxScore;
			mappings.add(map);

			// Zero out row and column to prevent duplicate pairings
			for(int y = 0; y < nodeList1Count; y++) {
				mapMatrix[y][maxY] = 0;
			}

			for(int z = 0; z < nodeList2Count; z++) {
				mapMatrix[maxX][z] = 0;
			}

		}

		// Handle removed nodes
		if(nodeList1Count > nodeList2Count) {
			for(Node strandedNode : vObjectList) {
				boolean nodeFound = false;
				for(NodeMapping map : mappings) {
					if(strandedNode.Name.toLowerCase().equals(map.Node1.Name.toLowerCase())) {
						nodeFound = true;
					}
				}
				if(!nodeFound) {
					NodeMapping map = new NodeMapping();
					map.Node1 = strandedNode;
					map.Node2 = null;						
					mappings.add(map);
				}
			}
		}

		// Handle added nodes
		if(nodeList2Count > nodeList1Count) {
			for(Node strandedNode : nodeList) {
				boolean nodeFound = false;
				for(NodeMapping map : mappings) {
					if(strandedNode.Name.toLowerCase().equals(map.Node2.Name.toLowerCase())) {
						nodeFound = true;
					}
				}
				if(!nodeFound) {
					NodeMapping map = new NodeMapping();
					map.Node1 = null;
					map.Node2 = strandedNode;								
					mappings.add(map);
				}
			}
		}

		return mappings;
	}

	private FigurePairMapping CreateFigurePairMapping(Figure figure1, Figure figure2) {

		FigurePairMapping figureMapping = new FigurePairMapping();
		figureMapping.Figure1 = figure1;
		figureMapping.Figure2 = figure2;
		figureMapping.NodeMappings = CreateNodePairMapping(figure1.Nodes, figure2.Nodes);	

		return figureMapping;

	}
}
