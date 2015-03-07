package qpo.processor;

public class PlanUnionNode extends PlanTableNode {
	public PlanUnionNode(){
		super();
		left = null;
		right = null;
	}
	public PlanTableNode left, right;

}
