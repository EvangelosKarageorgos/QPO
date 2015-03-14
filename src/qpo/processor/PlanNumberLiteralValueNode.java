package qpo.processor;

import qpo.data.model.AttributeTypeEnum;

public class PlanNumberLiteralValueNode extends PlanLiteralValueNode {
	public PlanNumberLiteralValueNode(){
		super();
		value = 0;
	}
	
	
	public PlanNumberLiteralValueNode clone(){
		PlanNumberLiteralValueNode result = new PlanNumberLiteralValueNode();
		cloneValuesTo(result);
		return result;
	}

	protected void cloneValuesTo(PlanNumberLiteralValueNode node){
		super.cloneValuesTo(node);
		node.value = value;
	}
	
	protected String toString(int padding){
		String ps = new String(new char[padding*2]).replace('\0', ' ');
		return value.toString();
	}
	public Object getValue(){
		return value;
	}
	
	public AttributeTypeEnum getType(){
		return AttributeTypeEnum.Integer;
	}
	
	public Number value;

}
