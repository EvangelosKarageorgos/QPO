package qpo.processor;

public class PlanDiffNode extends PlanTableNode {
	public PlanDiffNode(){
		super();
		left = null;
		right = null;
	}	
	public PlanTableNode left, right;

}
