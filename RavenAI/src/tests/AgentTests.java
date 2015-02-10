package tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

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

			// Create nodes
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
			
			ViableObject vObject = new ViableObject();
			
			vObject.addAttribute("fill", "top-right");
			vObject.addAttribute("shape", "circle");
			vObject.addAttribute("fill", "bottom-right,top-right");
			
			Assert.assertTrue(vObject.AttributeGroups.size() == 2);
			
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

}
