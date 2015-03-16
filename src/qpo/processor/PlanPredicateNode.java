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
			res.predicates.add(predicate);
		}
		return res;
	}

	public PlanPredicateNode mergeWith(PlanPredicateNode predicate, Table table1, Table table2){
		PlanPredicateNode res = mergeWith(predicate);
		List<Attribute> attributes = new ArrayList<Attribute>();
		getAllAttributes(attributes);
		for(Attribute a : attributes){
			if(table1!=null){
				for(Attribute ta : table1.getAttributes()){
					if((ta.getRelationName().length()==0||a.getRelationName().length()==0||ta.getRelationName().equalsIgnoreCase(a.getRelationName())) && ta.getName().equalsIgnoreCase(a.getName())){
						a.setTable(ta.getTable());
					}
				}
			}
			if(table2!=null){
				for(Attribute ta : table2.getAttributes()){
					if((ta.getRelationName().length()==0||a.getRelationName().length()==0||ta.getRelationName().equalsIgnoreCase(a.getRelationName())) && ta.getName().equalsIgnoreCase(a.getName())){
						a.setTable(ta.getTable());
					}
				}
			}
		}
		return res;
	}
	
	protected void getAllAttributes(List<Attribute> attributes){
		
	}

	protected void fillUniqueAttributes(List<Attribute> attributeList){
		
	}
}
