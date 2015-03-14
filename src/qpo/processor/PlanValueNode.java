package qpo.processor;

import qpo.data.model.*;

public class PlanValueNode extends PlanNode {
	public PlanValueNode(){
		super();
	}
	
	public AttributeTypeEnum getType(){
		return AttributeTypeEnum.Integer;
	}

	public PlanValueNode clone(){
		PlanValueNode result = new PlanValueNode();
		cloneValuesTo(result);
		return result;
	}

	protected void cloneValuesTo(PlanValueNode node){
		super.cloneValuesTo(node);
	}

}
