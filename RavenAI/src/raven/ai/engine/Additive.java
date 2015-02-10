package raven.ai.engine;

import java.util.Arrays;
import java.util.List;

public class Additive extends Transformation {
	public List<String> BeforeValues;
	public List<String> AfterValues;

	public Additive() {

	}

	public Additive(Transformation transform) {
		if(transform != null) {
			this.BeforeAttributeValue = transform.BeforeAttributeValue;
			this.AfterAttributeValue = transform.AfterAttributeValue;
			this.AttributeName = transform.AttributeName;	
			if(transform.BeforeAttributeValue != null) {
				this.BeforeValues = Arrays.asList(transform.BeforeAttributeValue.split(","));
			}
			if(transform.AfterAttributeValue != null) {
				this.AfterValues = Arrays.asList(transform.AfterAttributeValue.split(","));
			}
		}
	}
}
