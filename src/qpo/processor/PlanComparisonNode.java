package qpo.processor;

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
	
	public PlanValueNode left;
	public PlanValueNode right;
	public ComparisonOperatorType operator;
}
