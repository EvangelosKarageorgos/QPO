package qpo.processor;

import qpo.data.model.AttributeTypeEnum;

public class PlanNumberLiteralValueNode extends PlanLiteralValueNode {
	public PlanNumberLiteralValueNode(){
		super();
		value = 0;
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
