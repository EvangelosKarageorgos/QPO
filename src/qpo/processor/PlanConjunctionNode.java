package qpo.processor;

import java.util.List;

public class PlanConjunctionNode extends PlanPredicateNode {
	public PlanConjunctionNode(){
		super();
		predicates = null;
	}	
	public List<PlanPredicateNode> predicates;
}
