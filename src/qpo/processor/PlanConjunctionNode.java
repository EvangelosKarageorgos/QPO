package qpo.processor;

import java.util.List;

public class PlanConjunctionNode extends PlanPredicateNode {
	public PlanConjunctionNode(){
		super();
		predicates = null;
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
