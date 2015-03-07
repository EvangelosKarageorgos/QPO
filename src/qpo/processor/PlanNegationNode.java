package qpo.processor;

public class PlanNegationNode extends PlanPredicateNode {
	public PlanNegationNode(){
		super();
		predicate = null;
	}
	public PlanPredicateNode predicate;
}
