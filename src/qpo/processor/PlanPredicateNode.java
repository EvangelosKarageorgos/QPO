package qpo.processor;

import java.util.ArrayList;
import java.util.List;

import qpo.data.model.*;

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
	
	public List<Attribute> getUniqueAttributes(){
		List<Attribute> result = new ArrayList<Attribute>();
		fillUniqueAttributes(result);
		return result;
	}
	
	public PlanPredicateNode mergeWith(PlanPredicateNode predicate){
		PlanConjunctionNode res = null;
		if(predicate==null)
			return this;
		if(predicate instanceof PlanConjunctionNode){
			res = (PlanConjunctionNode) predicate.clone();
			if(res.predicates==null)res.predicates = new ArrayList<PlanPredicateNode>();
			res.predicates.add(this);
		} else{
			res = new PlanConjunctionNode();
			res.predicates = new ArrayList<PlanPredicateNode>();
			res.predicates.add(this);
		}
		return res;
	}
	
	protected void fillUniqueAttributes(List<Attribute> attributeList){
		
	}
}
