package qpo.processor;

import qpo.data.model.*;
public class PlanRelationNode extends PlanTableNode {
	public PlanRelationNode(){
		super();
		relation = null;
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
	
	public Relation relation;
}
