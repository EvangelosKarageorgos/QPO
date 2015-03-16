package qpo.processor;

import java.util.List;

import qpo.data.model.Attribute;

public class PlanNegationNode extends PlanPredicateNode {
	public PlanNegationNode(){
		super();
		predicate = null;
	}

	public PlanNegationNode clone(){
		PlanNegationNode result = new PlanNegationNode();
		cloneValuesTo(result);
		return result;
	}

	protected void cloneValuesTo(PlanNegationNode node){
		super.cloneValuesTo(node);
		node.predicate = predicate==null?null:predicate.clone();
	}
	
	protected String toString(int padding){
		String ps = new String(new char[padding*2]).replace('\0', ' ');
		return "not "+predicate.toString();
	}
	
	protected void fillUniqueAttributes(List<Attribute> attributeList){
		if(predicate!=null)predicate.fillUniqueAttributes(attributeList);
	}
	protected void getAllAttributes(List<Attribute> attributes){
		predicate.getAllAttributes(attributes);
	}


	public PlanPredicateNode predicate;
}
