package qpo.processor;

import qpo.data.model.Table;

public class PlanIntersectNode extends PlanTableNode {
	public PlanIntersectNode(){
		super();
		left = null;
		right = null;
	}
	
	@Override
	public Table constructTable() throws Exception{
		Table table = new Table();
		
		Table leftTable = left.getTable();
		Table rightTable = right.getTable();
	
		// TODO intersect logic merge tables

		return table;
	}

	public PlanTableNode left, right;

}
