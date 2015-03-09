package qpo.processor;

import qpo.data.model.*;

public class PlanJoinNode extends PlanTableNode {
	public PlanJoinNode(){
		super();
		left = null;
		right = null;
		predicate = null;
	}
	public PlanTableNode left, right;
	public PlanPredicateNode predicate; 

	@Override
	public Table constructTable(){
		Table table = new Table();
		
		Table leftTable = left.getTable();
		Table rightTable = right.getTable();
	
		// TODO join logic merge tables
		
		return table;
	}
}
