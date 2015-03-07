package qpo.processor;

import qpo.data.model.*;
public class PlanRelationNode extends PlanTableNode {
	public PlanRelationNode(){
		super();
		relation = null;
	}
	public Table relation;
}
