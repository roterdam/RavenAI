package raven.ai.engine;

import java.util.ArrayList;
import java.util.List;

public class Edge {

	public Node NodeA;
	public Node NodeB;

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
}
