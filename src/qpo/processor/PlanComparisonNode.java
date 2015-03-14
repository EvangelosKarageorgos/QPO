package qpo.processor;

import qpo.data.model.AttributeTypeEnum;

public class PlanComparisonNode extends PlanPredicateNode{
	public PlanComparisonNode(){
		super();
		left = null;
		right = null;
		operator = ComparisonOperatorType.equals;
	}
	
	protected String toString(int padding){
		String ps = new String(new char[padding*2]).replace('\0', ' ');
		String op = "";
		switch(operator){
		case equals:
			op = " = ";
			break;
		case notEqual:
			op = " <> ";
			break;
		case greaterThan:
			op = " > ";
			break;
		case lessThan:
			op = " < ";
			break;
		case like:
			op = " like ";
			break;
		default:
			break;
		}
		return left.toString()+op+right.toString();
	}
	
	public void checkTypeConsistancy() throws Exception{
		AttributeTypeEnum leftType = left.getType();
		AttributeTypeEnum rightType = right.getType();
		boolean leftStrict = left instanceof PlanAttributeValueNode;
		boolean rightStrict = right instanceof PlanAttributeValueNode;
		if(leftStrict && rightStrict && leftType!=rightType)
			throw new Exception("Type Mismatch");
		if(AttributeTypeEnum.getTypeClass(leftType)!=AttributeTypeEnum.getTypeClass(rightType))
			throw new Exception("Type Mismatch");
	}
	
	public PlanValueNode left;
	public PlanValueNode right;
	public ComparisonOperatorType operator;
}
