package qpo.processor;

import qpo.data.model.*;
public class PlanRelationNode extends PlanTableNode {
	public PlanRelationNode(){
		super();
		relation = null;
	}
	
	public PlanRelationNode clone(){
		PlanRelationNode result = new PlanRelationNode();
		cloneValuesTo(result);
		return result;
	}

	protected void cloneValuesTo(PlanRelationNode node){
		super.cloneValuesTo(node);
		node.relation = relation;
	}

	@Override
	public Table constructTable(){
		return relation;
	}
	
	protected String toString(int padding){
		String ps = new String(new char[padding*2]).replace('\0', ' ');
		
		String output = ps+"Relation "+relation;
		
		return output;
	}
	
	public int getNumOfChildren(){
		return 0;
	}
	
	public Relation relation;
}
