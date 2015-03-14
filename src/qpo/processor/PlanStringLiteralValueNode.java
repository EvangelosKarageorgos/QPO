package qpo.processor;

import qpo.data.model.AttributeTypeEnum;

public class PlanStringLiteralValueNode extends PlanLiteralValueNode {
	public PlanStringLiteralValueNode(){
		super();
		value = null;
	}
	
	public PlanStringLiteralValueNode clone(){
		PlanStringLiteralValueNode result = new PlanStringLiteralValueNode();
		cloneValuesTo(result);
		return result;
	}

	protected void cloneValuesTo(PlanStringLiteralValueNode node){
		super.cloneValuesTo(node);
		node.value = value;
	}

	protected String toString(int padding){
		String ps = new String(new char[padding*2]).replace('\0', ' ');
		return "'"+value+"'";
	}
	
	public AttributeTypeEnum getType(){
		return AttributeTypeEnum.Character;
	}
	
	public Object getValue(){
		return value;
	}
	public String value;
}
