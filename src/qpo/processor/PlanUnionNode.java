package qpo.processor;

import qpo.data.model.Table;

public class PlanUnionNode extends PlanTableNode {
	public PlanUnionNode(){
		super();
		left = null;
		right = null;
	}
	
	@Override
	public Table constructTable() throws Exception{
		Table table = new Table();
		
		Table leftTable = left.getTable();
		Table rightTable = right.getTable();
	
		// TODO union logic merge tables

		return table;
	}

	public PlanTableNode left, right;

}
