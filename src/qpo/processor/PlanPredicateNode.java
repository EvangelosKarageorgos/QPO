package qpo.processor;

import java.util.ArrayList;

public class PlanPredicateNode extends PlanNode {
	public PlanPredicateNode(){
		super();
	}
	
	public PlanPredicateNode clone(){
		PlanPredicateNode result = new PlanPredicateNode();
		cloneValuesTo(result);
		return result;
	}

	protected void cloneValuesTo(PlanPredicateNode node){
		super.cloneValuesTo(node);
	}
}
