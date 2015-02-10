package raven.ai.engine;

import java.util.ArrayList;
import java.util.List;

public class ViableObject extends Node {

	public ViableObject(RavensObject rObj) {
		super(rObj);

	}

	public ViableObject() {
		super();
	}

	public List<AttributeGroup> AttributeGroups;	

	@Override
	public int getAttributeCount() {
		int attributeCount = 0;

		for(AttributeGroup group : AttributeGroups) {
			for (Attribute att : group.Attributes) {
				attributeCount += 1;
			}
		}
		return attributeCount;
	}

	@Override
	public String getShape() {
		for(AttributeGroup group : AttributeGroups) {
			for(Attribute att : group.Attributes) {
				if(att.Name.toLowerCase().equals("shape")) {
					return att.Value;
				}
			}
		}
		return null;
	}

	public int getAttributeCountWithoutGroups() {
		return AttributeGroups.size();
	}

	public void addAttribute(String name, String value) {
		if(AttributeGroups == null) {
			AttributeGroups = new ArrayList<AttributeGroup>();
			AttributeGroup newGroup = new AttributeGroup();
			newGroup.Attributes = new ArrayList<Attribute>();
			newGroup.Attributes.add(new Attribute(name, value));
			AttributeGroups.add(newGroup);
		}
		else {
			boolean groupFound = false;
			for(AttributeGroup group : AttributeGroups) {
				if(group.Attributes == null) {
					group.Attributes = new ArrayList<Attribute>();
					group.Attributes.add(new Attribute(name, value));
				}
				else {
					if(group.Attributes.size() == 0) {
						group.Attributes.add(new Attribute(name, value));
						groupFound = true;
					}
					else {
						if(group.Attributes.get(0).Name.toLowerCase().equals(name.toLowerCase())) {							
							group.Attributes.add(new Attribute(name, value));	
							groupFound = true;
						}
					}

				}
			}
			if(!groupFound) {
				AttributeGroup newGroup = new AttributeGroup();
				newGroup.Attributes = new ArrayList<Attribute>();
				newGroup.Attributes.add(new Attribute(name, value));
				AttributeGroups.add(newGroup);
			}
		}
	}
}
