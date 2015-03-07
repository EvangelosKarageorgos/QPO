package qpo.processor;

public class PlanJoinNode extends PlanTableNode {
	public PlanJoinNode(){
		super();
		left = null;
		right = null;
		predicate = null;
	}
	public PlanTableNode left, right;
	public PlanPredicateNode predicate; 
}
