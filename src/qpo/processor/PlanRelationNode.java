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

	public Relation relation;
}
