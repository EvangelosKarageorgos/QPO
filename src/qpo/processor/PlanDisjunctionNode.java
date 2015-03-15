package qpo.processor;

import java.util.ArrayList;
import java.util.List;

import qpo.data.model.Attribute;

public class PlanDisjunctionNode extends PlanPredicateNode {
	public PlanDisjunctionNode(){
		super();
		predicates = null;
	}
	
	public PlanDisjunctionNode clone(){
		PlanDisjunctionNode result = new PlanDisjunctionNode();
		cloneValuesTo(result);
		return result;
	}

	protected void cloneValuesTo(PlanDisjunctionNode node){
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
			output = output + (first?"":" or ")+p.toString();
			first = false;
		}
		
		return "("+output+")";
	}
	
	protected void fillUniqueAttributes(List<Attribute> attributeList){
		if(predicates!=null)
			for(PlanPredicateNode p : predicates)
				p.fillUniqueAttributes(attributeList);
	}

	public List<PlanPredicateNode> predicates;
}
