package qpo.processor;

import java.util.ArrayList;
import java.util.List;

public class PlanConjunctionNode extends PlanPredicateNode {
	public PlanConjunctionNode(){
		super();
		predicates = null;
	}
	
	public PlanConjunctionNode clone(){
		PlanConjunctionNode result = new PlanConjunctionNode();
		cloneValuesTo(result);
		return result;
	}

	protected void cloneValuesTo(PlanConjunctionNode node){
		super.cloneValuesTo(node);
		node.predicates = null;
		if(predicates!=null){
			node.predicates = new ArrayList<PlanPredicateNode>();
			for(PlanPredicateNode p : predicates)
				node.predicates.add(p.clone());
		}
	}
	
	protected String toString(int padding){
		String ps = new String(new char[padding*2]).replace('\0', ' ');
		String output = "";
		boolean first = true;
		for(PlanPredicateNode p : predicates){
			output = output + (first?"":" and ")+p.toString();
			first = false;
		}
		
		return "("+output+")";
	}
	
	public List<PlanPredicateNode> predicates;
}
