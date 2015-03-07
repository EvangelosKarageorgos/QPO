package qpo.processor;

public class PlanComparisonNode extends PlanPredicateNode{
	public PlanComparisonNode(){
		super();
		left = null;
		right = null;
		operator = ComparisonOperatorType.equals;
	}	
	public PlanValueNode left;
	public PlanValueNode right;
	public ComparisonOperatorType operator;
}
