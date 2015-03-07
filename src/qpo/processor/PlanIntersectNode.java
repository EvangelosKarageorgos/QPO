package qpo.processor;

public class PlanIntersectNode extends PlanTableNode {
	public PlanIntersectNode(){
		super();
		left = null;
		right = null;
	}
	public PlanTableNode left, right;

}
