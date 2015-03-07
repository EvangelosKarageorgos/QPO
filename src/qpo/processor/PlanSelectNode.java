package qpo.processor;

public class PlanSelectNode extends PlanTableNode {
	public PlanSelectNode(){
		super();
		table = null;
		predicate = null;
	}
	public PlanTableNode table;
	public PlanPredicateNode predicate; 
}
