package tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import raven.ai.engine.Common;
import raven.ai.engine.Figure;
import raven.ai.engine.Node;
import raven.ai.engine.RavensAttribute;
import raven.ai.engine.RavensFigure;
import raven.ai.engine.RavensObject;
import raven.ai.engine.ViableObject;


public class AgentTests {

	@Test
	public void testNodeMapping() {

		try {

			// Arrange
			Figure figureA = new Figure();
			figureA.RFigure = new RavensFigure("A");			

			Figure figureB = new Figure();
			figureB.RFigure = new RavensFigure("B");

			Figure figureC = new Figure();
			figureC.RFigure = new RavensFigure("C");

			Figure figureTest1 = new Figure();
			figureTest1.RFigure = new RavensFigure("1");

			Figure figureTest2 = new Figure();
			figureTest2.RFigure = new RavensFigure("2");

			RavensObject objectX = new RavensObject("X");
			RavensObject objectY = new RavensObject("Y");
			RavensObject objectZ = new RavensObject("Z");

			objectX.getAttributes().add(new RavensAttribute("shape", "triangle"));
			objectX.getAttributes().add(new RavensAttribute("fill", "no"));
			//objectX.getAttributes().add(new RavensAttribute("angle", "0"));
			objectY.getAttributes().add(new RavensAttribute("shape", "circle"));
			objectZ.getAttributes().add(new RavensAttribute("shape", "square"));

			RavensObject objectX2 = new RavensObject("Z");
			RavensObject objectY2 = new RavensObject("Y");
			RavensObject objectZ2 = new RavensObject("X");

			objectX2.getAttributes().add(new RavensAttribute("shape", "triangle"));
			objectX2.getAttributes().add(new RavensAttribute("fill", "no"));
			//objectX2.getAttributes().add(new RavensAttribute("angle", "0"));
			objectY2.getAttributes().add(new RavensAttribute("shape", "circle"));
			objectZ2.getAttributes().add(new RavensAttribute("shape", "square"));

			figureA.RFigure.getObjects().add(objectX);
			figureA.RFigure.getObjects().add(objectY);
			figureA.RFigure.getObjects().add(objectZ);
			figureB.RFigure.getObjects().add(objectX2);
			figureB.RFigure.getObjects().add(objectY2);
			figureB.RFigure.getObjects().add(objectZ2);

			// Act
			List<Node> newNodes = new ArrayList<Node>();    		    	    		
			for(RavensObject obj : figureA.RFigure.getObjects()) {
				Node newNode = new Node(obj);
				newNode.Name = obj.getName();
				//newNode.RObject = obj;
				newNodes.add(newNode);
			}

			figureA.Nodes = newNodes;    
			newNodes = new ArrayList<Node>();

			for(RavensObject obj : figureB.RFigure.getObjects()) {
				Node newNode = new Node(obj);
				newNode.Name = obj.getName();
				//newNode.RObject = obj;
				newNodes.add(newNode);
			}

			figureB.Nodes = newNodes;    

			// Assert
			for(Node node : figureA.Nodes) {				
				System.out.println("Figure A Node " + node.Name + " corresponds to Figure B Node " + node.getCorrespondingNode(figureB).Name);
				Assert.assertTrue(node.getCorrespondingNode(figureB).Name.equals(node.Name));				
			}


		} catch(Exception ex) {
			ex.printStackTrace();
		}


	}

	@Test
	public void testAttributeGroups() {

		try {

			// Arrange
			ViableObject vObject = new ViableObject();

			// Act
			vObject.addAttribute("fill", "top-right");
			vObject.addAttribute("shape", "circle");
			vObject.addAttribute("fill", "bottom-right,top-right");

			// Assert
			Assert.assertTrue(vObject.AttributeGroups.size() == 2);

		} catch(Exception ex) {
			ex.printStackTrace();
		}

	}

	@Test
	public void testAngleOver360() {

		// Arrange
		int angle1 = 270;
		int angle2 = 180;

		try {

			// Act
			int result = Common.FormatAngle(angle1 + angle2);

			// Assert
			Assert.assertEquals(result, 90);

		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Test
	public void testAngleUnder0() {

		// Arrange
		int angle1 = 90;
		int angle2 = 180;

		try {

			// Act
			int result = Common.FormatAngle(angle1 - angle2);

			// Assert
			Assert.assertEquals(result, 270);

		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Test
	public void testRandomAngleOperationRange() {

		// Arrange
		int max = 360;
		int min = 0;

		try {

			Random rand = new Random();
			int randomAngle1 = rand.nextInt((max - min) + 1) + min;
			int randomAngle2 = rand.nextInt((max - min) + 1) + min;

			// Act
			int additionResult = Common.FormatAngle(randomAngle1 + randomAngle2);
			int subtractionResult = Common.FormatAngle(randomAngle1 - randomAngle2);

			// Assert
			Assert.assertTrue(additionResult < max);
			Assert.assertTrue(subtractionResult >= min);

		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Test
	public void testRelatedShape() {

		// Arrange
		Figure figureA = new Figure();
		figureA.RFigure = new RavensFigure("A");			

		Figure figureB = new Figure();
		figureB.RFigure = new RavensFigure("B");

		try {
			
			RavensObject objectX = new RavensObject("X");
			RavensObject objectY = new RavensObject("Y");
			RavensObject objectZ = new RavensObject("Z");

			objectX.getAttributes().add(new RavensAttribute("shape", "triangle"));
			objectX.getAttributes().add(new RavensAttribute("fill", "no"));
			objectX.getAttributes().add(new RavensAttribute("angle", "0"));
			objectY.getAttributes().add(new RavensAttribute("shape", "circle"));
			objectZ.getAttributes().add(new RavensAttribute("shape", "square"));

			RavensObject objectX2 = new RavensObject("Z");
			RavensObject objectY2 = new RavensObject("Y");
			RavensObject objectZ2 = new RavensObject("X");

			objectX2.getAttributes().add(new RavensAttribute("shape", "triangle"));
			objectX2.getAttributes().add(new RavensAttribute("fill", "no"));
			objectX2.getAttributes().add(new RavensAttribute("angle", "0"));
			objectY2.getAttributes().add(new RavensAttribute("shape", "circle"));
			objectZ2.getAttributes().add(new RavensAttribute("shape", "square"));

			figureA.RFigure.getObjects().add(objectX);
			figureA.RFigure.getObjects().add(objectY);
			figureA.RFigure.getObjects().add(objectZ);
			figureB.RFigure.getObjects().add(objectX2);
			figureB.RFigure.getObjects().add(objectY2);
			figureB.RFigure.getObjects().add(objectZ2);
			
			

		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Test
	public void testRotationSymmetry() {

		try {


		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Test
	public void testRandomLetterGenerator() {
		
		String newLetter = "";
		String exceptions = "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O";
		List<String> items = Arrays.asList(exceptions.split("\\s*,\\s*"));
		
		try {
				
			newLetter = Common.GenerateRandomLetter(items);
			
			Assert.assertTrue(newLetter.equalsIgnoreCase("P"));
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
