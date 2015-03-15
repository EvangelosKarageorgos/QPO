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
		return new ArrayList<Attribute>();
	}
	
	protected void fillUniqueAttributes(List<Attribute> attributeList){
		
	}
}
