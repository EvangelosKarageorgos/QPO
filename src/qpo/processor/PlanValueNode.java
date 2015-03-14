package qpo.processor;

import qpo.data.model.*;

public class PlanValueNode extends PlanNode {
	public PlanValueNode(){
		super();
	}
	
	public AttributeTypeEnum getType(){
		return AttributeTypeEnum.Integer;
	}
}
