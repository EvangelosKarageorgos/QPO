package qpo.processor;

import java.util.List;

public class PlanDisjunctionNode extends PlanPredicateNode {
	public PlanDisjunctionNode(){
		super();
		predicates = null;
	}
	public List<PlanPredicateNode> predicates;
}
