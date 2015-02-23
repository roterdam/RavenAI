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

	private static final boolean DEBUG = false;

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

		if(Common.GetProblemType(problem.getProblemType()) == ProblemType.TwoByOne) {
			return SolveTwoByOne(figures);
		}
		else if(Common.GetProblemType(problem.getProblemType()) == ProblemType.TwoByTwo) {
			return SolveTwoByTwo(figures);
		}

		return "1";
	}

	private List<ViableObject> GenerateViableObjects(Figure figureA, Figure figureB, Figure figureC) {

		List<ViableObject> expectedObjects = new ArrayList<ViableObject>();

		FigurePairMapping ABMapping = CreateFigurePairMapping(figureA, figureB, true);
		FigurePairMapping ACMapping = CreateFigurePairMapping(figureA, figureC, false);

		// DEBUG OUTPUT	
		if(DEBUG) {

			ABMapping.PrintMappingInfo();
			ACMapping.PrintMappingInfo();

			System.out.println("----------------------------");
		}

		List<Edge> edges = ABMapping.ToEdges();

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

							Node cNode = figureC.FindNode(ACMapping.GetCorrespondingNode2Name(edge.NodeA.Name));
							//Node cNode = figureC.FindNode(edge.NodeA.Name);	
							if(cNode != null) {
								Attribute cNodeAttribute = cNode.findAttribute(transformation.AttributeName);

								if(cNodeAttribute != null) {
									expectedObject.addAttribute(transformation.AttributeName, cNodeAttribute.Value);
								}
								else {
									//									if(transformation.AttributeName.equalsIgnoreCase("left-of") || transformation.AttributeName.equalsIgnoreCase("right-of") ||
									//											transformation.AttributeName.equalsIgnoreCase("above") || transformation.AttributeName.equalsIgnoreCase("behind") ||
									//											transformation.AttributeName.equalsIgnoreCase("below") || transformation.AttributeName.equalsIgnoreCase("inside") ||
									//											transformation.AttributeName.equalsIgnoreCase("overlaps")) {
									if(Common.GetAttributeType(transformation.AttributeName) == AttributeType.Positional) {

										for(Attribute cAttribute : cNode.Attributes) {
											if(Common.GetAttributeType(cAttribute.Name) == AttributeType.Positional) {
												expectedObject.addAttribute(cAttribute.Name, cAttribute.Value);
											}
										}

									}
								}
							}
						}					
						else {
							Node cNode = figureC.FindNode(ACMapping.GetCorrespondingNode2Name(edge.NodeA.Name));	
							Node bNode = figureB.FindNode(ABMapping.GetCorrespondingNode2Name(edge.NodeA.Name));
							//Node bNode = figureB.FindNode(cNode.Name);

							if(cNode != null && bNode != null) {

								Attribute cNodeAttribute = cNode.findAttribute(transformation.AttributeName);	
								Attribute bNodeAttribute = bNode.findAttribute(transformation.AttributeName);

								expectedObject.addAttribute(transformation.AttributeName, bNodeAttribute.Value);	


								if(cNodeAttribute != null && bNodeAttribute != null) {

									AttributeType attributeType = Common.GetAttributeType(transformation.AttributeName);
									if(attributeType == AttributeType.Additive) {
										List<String> cNodeAttributeValues = Arrays.asList(cNodeAttribute.Value.split(","));
										List<String> bNodeAttributeValues = Arrays.asList(bNodeAttribute.Value.split(","));																
										expectedObject.addAttribute(transformation.AttributeName, Common.CombineLists(cNodeAttributeValues, bNodeAttributeValues));
									}
									else if(attributeType == AttributeType.Positional) {							
										if(cNodeAttribute.Value != null && cNodeAttribute.Value != null) {
											List<String> cNodeAttributeValues = Arrays.asList(cNodeAttribute.Value.split(","));
											List<String> bNodeAttributeValues = Arrays.asList(bNodeAttribute.Value.split(","));	
											expectedObject.addAttribute(transformation.AttributeName, Common.CombineLists(cNodeAttributeValues, bNodeAttributeValues));
										}

									}		
									else if(attributeType == AttributeType.Angular) {
										int firstAngle = Integer.parseInt(transformation.BeforeAttributeValue);
										int secondAngle = Integer.parseInt(transformation.AfterAttributeValue);
										int CAngle = Integer.parseInt(cNodeAttribute.Value);

										if(bNode.getShape().equalsIgnoreCase(cNode.getShape())) {
											Shape shape = Common.Shapes.get(bNode.getShape());
											int difference = Common.FormatAngle(secondAngle - firstAngle);	
											if(difference == 180) {
												if(firstAngle == 0 || firstAngle == 180) {
													if(shape.hasVerticalAxisSymmetry()) {

														// Nothing has changed, create appropriate vobjects
														expectedObject.addAttribute(transformation.AttributeName, transformation.BeforeAttributeValue);

													}
													if(shape.hasHorizontalAxisSymmetry()) {

														// Reflected across vertical axis
														expectedObject.addAttribute("horizontal-flip", "yes");
														expectedObject.addAttribute("angle", transformation.BeforeAttributeValue);
													}

												}
												else if(firstAngle == 90 || firstAngle == 270) {
													if(shape.hasVerticalAxisSymmetry()) {

														// Reflected across vertical axis
														expectedObject.addAttribute("horizontal-flip", "yes");
														expectedObject.addAttribute("angle", cNodeAttribute.Value);
													}
													if(shape.hasHorizontalAxisSymmetry()) {

														// Nothing has changed
														expectedObject.addAttribute(transformation.AttributeName, transformation.BeforeAttributeValue);
													}

												}
											}
											else if(difference == 90) {
												if(firstAngle == 45) {

													// Reflected across vertical axis
													expectedObject.addAttribute("horizontal-flip", "yes");
													expectedObject.addAttribute("angle", transformation.BeforeAttributeValue);

												}
											}
										}

										int difference = Common.FormatAngle(secondAngle - firstAngle);																									

										String angle1 = String.valueOf(Common.FormatAngle(CAngle + difference));																											
										expectedObject.addAttribute(transformation.AttributeName, angle1);

										int difference2 = Common.FormatAngle(secondAngle + firstAngle);																									

										String angle2 = String.valueOf(Common.FormatAngle(CAngle + difference2));
										expectedObject.addAttribute(transformation.AttributeName, angle2);
									}															

									//expectedObject.addAttribute(transformation.AttributeName, bNodeAttribute.Value);	
								}
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
					if(expectedObject.AttributeGroups != null) {
						for(AttributeGroup group : expectedObject.AttributeGroups) {
							for(Attribute att : group.Attributes) {
								System.out.println(att.Name + " : " + att.Value);
							}
						}
					}
					System.out.println("----------------------------");
				}


				expectedObjects.add(expectedObject);
			}


		}

		return expectedObjects;
	}

	private String SolveTwoByOne(HashMap<String, Figure> figures) {			

		Figure figureA = figures.get("A");
		Figure figureB = figures.get("B");
		Figure figureC = figures.get("C");

		List<ViableAnswer> allAnswers = Common.GetAnswerFigures(figures);	

		List<ViableObject> expectedObjects = GenerateViableObjects(figureA, figureB, figureC);

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

		// Make sure proper ratio of objects is present (add extras if necessary)
		if(figureA.Nodes.size() == figureB.Nodes.size() && figureC.Nodes.size() != expectedObjects.size()) {
			int count = figureC.Nodes.size() - expectedObjects.size();
			for(int i = 0;i < count; i++) {
				ViableObject newObject = new ViableObject();
				List<String> usedNodeNames = new ArrayList<String>();
				for(ViableObject object : expectedObjects) {
					usedNodeNames.add(object.Name);
				}

				// Generate new node name for object
				newObject.Name = Common.GenerateRandomLetter(usedNodeNames);

				for(AttributeGroup group : expectedObjects.get(0).AttributeGroups) {
					for(Attribute att : group.Attributes) {
						newObject.addAttribute(att.Name, att.Value);					
					}
				}
				expectedObjects.add(newObject);
			}
		}

		allAnswers = CheckAnswers(expectedObjects, allAnswers, figures);

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
			//System.out.println("Breaking tie...");

			//			List<ViableAnswer> topAnswers = Common.GetTopTiedAnswers(allAnswers, allAnswers.get(0).Score);
			//			for(ViableAnswer answer : topAnswers) {
			//				answer.Score += answer.CMatchScore;
			//			}
			//			
			//			Collections.sort(topAnswers);
			//			
			//			System.out.println("NEW POSSIBLE ANSWERS");
			//			for(ViableAnswer pAnswer : topAnswers) {
			//				if(!pAnswer.Incompatible) {
			//					System.out.println(pAnswer.AnswerFigure.getName() + " : " + pAnswer.Score);
			//				}
			//			}
			//			
			//			return topAnswers.get(0).AnswerFigure.getName();
			return "GUESS";
		}

		return allAnswers.get(0).AnswerFigure.getName();
	}

	private List<ViableAnswer> CheckAnswers(List<ViableObject> expectedObjects, List<ViableAnswer> allAnswers, HashMap<String, Figure> figures) {

		for(ViableAnswer currentAnswer : allAnswers) {
			if(!currentAnswer.Incompatible) {	

				Figure figureA = figures.get("A");
				Figure figureB = figures.get("B");
				Figure figureC = figures.get("C");

				FigurePairMapping ABMapping = CreateFigurePairMapping(figureA, figureB, true);
				FigurePairMapping ACMapping = CreateFigurePairMapping(figureA, figureC, false);
				FigurePairMapping CNumMapping = CreateFigurePairMapping(figureC, figures.get(currentAnswer.AnswerFigure.getName()), true);

				if(CNumMapping != null) {

					//					for(NodeMapping map : CNumMapping.NodeMappings) {
					//						if(map.Node1 != null && map.Node2 != null) {
					//							if(Common.GetAttributeCount(map.Node1) == Common.GetAttributeCount(map.Node2)) {
					//								//currentAnswer.Score += 1;
					//							}
					//						}
					//						//currentAnswer.CMatchScore += map.Score;
					//					}


					// DEBUG OUTPUT		
					//					if(DEBUG) {
					//						System.out.println("C-Answer " + currentAnswer.AnswerFigure.getName() + " mappings");
					//						for(NodeMapping map : CNumMapping.NodeMappings) {							
					//							String node1Name = map.Node1 != null ? map.Node1.Name : "";
					//							String node2Name = map.Node2 != null ? map.Node2.Name : "";
					//							System.out.println(node1Name + " -> " + node2Name + " Score: " + map.Score);
					//						}
					//
					//						//System.out.println("Answer " + currentAnswer.AnswerFigure.getName() + " score: " + totalScore);							
					//					}

					List<NodeMapping> expectedObjectMappings = CreateNodeViableObjectMapping(expectedObjects, currentAnswer.AnswerFigure.Nodes);

					// Make sure attribute ratios match
					if(Common.GetAttributeCount(figureA) / Common.GetAttributeCount(figureB) != Common.GetAttributeCount(figureC) / Common.GetAttributeCount(currentAnswer.AnswerFigure)) {
						currentAnswer.Score -= 4;
					}

					// Award points for symmetric answers
					for(Node aNode : figureA.Nodes) {
						Node bNode = figureB.FindNode(ABMapping.GetCorrespondingNode2Name(aNode.Name));
						if(bNode != null) {
							Node cNode = figureC.FindNode(ACMapping.GetCorrespondingNode2Name(aNode.Name));
							if(cNode != null) {
								if(aNode.containsAttribute("angle") && bNode.containsAttribute("angle") && cNode.containsAttribute("angle")) {
									List<Integer> angles = new ArrayList<Integer>();
									angles.add(Integer.parseInt(aNode.findAttribute("angle").Value));
									angles.add(Integer.parseInt(bNode.findAttribute("angle").Value));
									angles.add(Integer.parseInt(cNode.findAttribute("angle").Value));

									// Make sure angles are all different
									if(angles.get(0) != angles.get(1) && angles.get(0) != angles.get(2) && angles.get(1) != angles.get(2)) {

										int minAngle = 360;
										int symmetricAngle = -1;									

										// minAngle should be either 0 or 45
										for(Integer angle : angles) {											

											if(angle < minAngle) {
												minAngle = angle;
											}
										}

										// If smallest angle is 90, then we should be looking for an angle of 0
										if(minAngle == 90) {
											symmetricAngle = 0;
										}
										else {

											// Calculate symmetric angle needed
											for(int i = 0; i < 4; i++) {
												if(!angles.contains(minAngle + (i*90))) {
													symmetricAngle = minAngle + (i*90);
												}
											}
										}

										angles.add(symmetricAngle);

										// Make sure angles are all in 90 degree increments
										if((angles.contains(0) && angles.contains(90) && angles.contains(180) && angles.contains(270)) ||
												(angles.contains(45) && angles.contains(135) && angles.contains(225) && angles.contains(315))) {

											String dNodeName = CNumMapping.GetCorrespondingNode2Name(cNode.Name);

											for(Node n : currentAnswer.AnswerFigure.Nodes) {
												if(n.Name.equalsIgnoreCase(dNodeName)) {
													if(n.containsAttribute("angle") && n.findAttribute("angle").Value.equalsIgnoreCase(String.valueOf(symmetricAngle))) {
														currentAnswer.Score += 7;
														if(DEBUG) {
															System.out.println("SYMMETRIC BONUS GIVEN FOR ANSWER " + currentAnswer.AnswerFigure.getName());
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}

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




						//						for(Node aNode : figureA.Nodes) {
						//							Node bNode = figureB.FindNode(ABMapping.GetCorrespondingNode2Name(aNode.Name));
						//							if(bNode != null) {
						//								if(Common.GetAttributeCount(bNode) != 0) {
						//									double ABRatio = Common.GetAttributeCount(aNode) / Common.GetAttributeCount(bNode);
						//									Node cNode = figureC.FindNode(ACMapping.GetCorrespondingNode2Name(aNode.Name));
						//									for(NodeMapping map : expectedObjectMappings) {
						//										if(cNode.Name.equalsIgnoreCase(map.Node1.Name)) {
						//											if(Common.GetAttributeCount(map.Node1) != 0) {
						//												double CDRatio = Common.GetAttributeCount(cNode) / Common.GetAttributeCount(map.Node1);
						//												if(ABRatio == CDRatio) {
						//													currentAnswer.Score += 1;
						//												}
						//											}
						//										}
						//									}
						//								}
						//							}
						//						}

						if(myObject.AttributeGroups != null) {
							for(AttributeGroup group : myObject.AttributeGroups) {
								for(Attribute myAttribute : group.Attributes) {			
									Attribute currentAttribute = currentNode.findAttribute(myAttribute.Name);
									if(currentAttribute != null) {
										int score = 0;

										score = Common.GetTransformationScore(currentAttribute.Name, currentAttribute.Value, myAttribute.Value, currentNode.getShape(), myObject.getShape());

										List<String> myValues = Arrays.asList(myAttribute.Value.split(","));
										List<String> nodeValues = Arrays.asList(currentAttribute.Value.split(","));

										if(Common.GetAttributeType(myAttribute.Name) == AttributeType.Positional) {
											for(int i = 0; i < myValues.size(); i++) {
												String tempNodeName = CNumMapping.GetCorrespondingNode2Name(myValues.get(i));
												if(tempNodeName != null) {
													myValues.set(i, tempNodeName);	
												}
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
			}		
		}
		return allAnswers;
	}

	private String SolveTwoByTwo(HashMap<String, Figure> figures) {		

		Figure figureA = figures.get("A");
		Figure figureB = figures.get("B");
		Figure figureC = figures.get("C");

		List<ViableAnswer> allAnswersHorizontal = Common.GetAnswerFigures(figures);	
		List<ViableAnswer> allAnswersVertical = Common.GetAnswerFigures(figures);	

		List<ViableObject> expectedObjectsHorizontal = GenerateViableObjects(figureA, figureB, figureC);
		List<ViableObject> expectedObjectsVertical = GenerateViableObjects(figureA, figureC, figureB);

		for(Iterator<ViableAnswer> iterator = allAnswersHorizontal.iterator(); iterator.hasNext();) {
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

		for(Iterator<ViableAnswer> iterator = allAnswersHorizontal.iterator(); iterator.hasNext();) {
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

		// Make sure proper ratio of objects is present (add extras if necessary)
		//		if(figureA.Nodes.size() == figureB.Nodes.size() && figureC.Nodes.size() != expectedObjects.size()) {
		//			int count = figureC.Nodes.size() - expectedObjects.size();
		//			for(int i = 0;i < count; i++) {
		//				ViableObject newObject = new ViableObject();
		//				List<String> usedNodeNames = new ArrayList<String>();
		//				for(ViableObject object : expectedObjects) {
		//					usedNodeNames.add(object.Name);
		//				}
		//
		//				// Generate new node name for object
		//				newObject.Name = Common.GenerateRandomLetter(usedNodeNames);
		//
		//				for(AttributeGroup group : expectedObjects.get(0).AttributeGroups) {
		//					for(Attribute att : group.Attributes) {
		//						newObject.addAttribute(att.Name, att.Value);					
		//					}
		//				}
		//				expectedObjects.add(newObject);
		//			}
		//		}

		allAnswersHorizontal = CheckAnswers(expectedObjectsHorizontal, allAnswersHorizontal, figures);
		allAnswersVertical = CheckAnswers(expectedObjectsVertical, allAnswersVertical, figures);

		// Sort answers by score
		//Collections.sort(allAnswersHorizontal);
		//Collections.sort(allAnswersVertical);

		HashMap<String, Integer> scoreList = new HashMap<String, Integer>();

		for(ViableAnswer answer : allAnswersHorizontal) {
			scoreList.put(answer.AnswerFigure.getName(), answer.Score);
		}

		for(ViableAnswer answer : allAnswersVertical) {
			if(scoreList.containsKey(answer.AnswerFigure.getName())) {

				// Add scores together
				int newScore = answer.Score;
				newScore += scoreList.get(answer.AnswerFigure.getName());
				scoreList.put(answer.AnswerFigure.getName(), newScore);
			}
			else {

				// Append new score
				scoreList.put(answer.AnswerFigure.getName(), answer.Score);
			}
		}

		int maxScore = 0;
		String bestAnswer = "-1";

		Iterator it = scoreList.entrySet().iterator();

		if(DEBUG) {
			System.out.println("POSSIBLE ANSWERS");
		}

		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			int score = (Integer)pairs.getValue();

			if(score >= maxScore) {
				maxScore = score;
				bestAnswer = (String)pairs.getKey();
			}

			if(DEBUG) {							
				System.out.println((String)pairs.getKey() + " : " + (Integer)pairs.getValue());					
			}

		}

		if(DEBUG) {
			System.out.println("");
		}


		//		if(DEBUG) {
		//			System.out.println("POSSIBLE ANSWERS");
		//			for(ViableAnswer pAnswer : allAnswers) {
		//				if(!pAnswer.Incompatible) {
		//					System.out.println(pAnswer.AnswerFigure.getName() + " : " + pAnswer.Score);
		//				}
		//			}
		//
		//			System.out.println("");
		//		}
		//
		//		// Return GUESS if guessing during debug
		//		if(DEBUG && allAnswers.size() > 1 && allAnswers.get(0).Score == allAnswers.get(1).Score) {
		//			System.out.println("Guessing...");
		//			return "GUESS";
		//		}

		return bestAnswer;
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



	private List<NodeMapping> CreateNodePairMapping(List<Node> nodeList1, List<Node> nodeList2, boolean isTransformation) {

		List<NodeMapping> mappings = new ArrayList<NodeMapping>();

		int nodeList1Count = nodeList1.size();
		int nodeList2Count = nodeList2.size();

		int[][] mapMatrix = new int[nodeList1Count][nodeList2Count];

		for(int i = 0; i < nodeList1Count; i++) {
			for(int j = 0; j < nodeList2Count; j++) {
				Node node1 = nodeList1.get(i);
				Node node2 = nodeList2.get(j);

				mapMatrix[i][j] = Common.GetNodeSimilarityScore(node1, node2, isTransformation);
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

					if(mapMatrix[i][j] >= maxScore) {
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

			// Cancel out row and column to prevent duplicate pairings
			for(int y = 0; y < nodeList1Count; y++) {
				mapMatrix[y][maxY] = -1;
			}

			for(int z = 0; z < nodeList2Count; z++) {
				mapMatrix[maxX][z] = -1;
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

	private FigurePairMapping CreateFigurePairMapping(Figure figure1, Figure figure2, boolean isTransformation) {

		FigurePairMapping figureMapping = new FigurePairMapping();
		figureMapping.Figure1 = figure1;
		figureMapping.Figure2 = figure2;
		if(figure2 == null || figure2.Nodes.size() == 0) {
			return null;
		}
		figureMapping.NodeMappings = CreateNodePairMapping(figure1.Nodes, figure2.Nodes, isTransformation);	

		return figureMapping;

	}
}
