package qpo.processor;

import java.util.ArrayList;
import java.util.List;

import qpo.data.model.Attribute;
import qpo.data.model.AttributeTypeEnum;

public class PlanComparisonNode extends PlanPredicateNode{
	public PlanComparisonNode(){
		super();
		left = null;
		right = null;
		operator = ComparisonOperatorType.equals;
	}
	
	public PlanComparisonNode clone(){
		PlanComparisonNode result = new PlanComparisonNode();
		cloneValuesTo(result);
		return result;
	}

	protected void cloneValuesTo(PlanComparisonNode node){
		super.cloneValuesTo(node);
		node.left = left==null?null:left.clone();
		node.right = right==null?null:right.clone();
		node.operator = operator;
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
	
	protected void fillUniqueAttributes(List<Attribute> attributeList){
		if(left!=null && (left instanceof PlanAttributeValueNode))((PlanAttributeValueNode)left).fillUniqueAttributes(attributeList);
		if(right!=null && (right instanceof PlanAttributeValueNode))((PlanAttributeValueNode)right).fillUniqueAttributes(attributeList);
	}
	
	protected void getAllAttributes(List<Attribute> attributes){
		if(left instanceof PlanAttributeValueNode)
			attributes.add(((PlanAttributeValueNode)left).attribute);
		if(left instanceof PlanAttributeValueNode)
			attributes.add(((PlanAttributeValueNode)left).attribute);
	}
	
	public PlanValueNode left;
	public PlanValueNode right;
	public ComparisonOperatorType operator;
}
