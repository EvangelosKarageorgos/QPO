package qpo.processor;

import java.util.ArrayList;
import java.util.List;

import qpo.data.model.Attribute;

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
	
	public PlanPredicateNode mergeWith(PlanPredicateNode predicate){
		if(predicate==null)
			return this;
		if(predicate instanceof PlanConjunctionNode){
			for(PlanPredicateNode p : ((PlanConjunctionNode)predicate).predicates)
				predicates.add(p);
		} else{
			predicates.add(predicate);
		}
		return this;
	}
	protected void fillUniqueAttributes(List<Attribute> attributeList){
		if(predicates!=null)
			for(PlanPredicateNode p : predicates)
				p.fillUniqueAttributes(attributeList);
	}
	
	protected void getAllAttributes(List<Attribute> attributes){
		for(PlanPredicateNode p : predicates)
			p.getAllAttributes(attributes);
	}

	public List<PlanPredicateNode> predicates;
}
