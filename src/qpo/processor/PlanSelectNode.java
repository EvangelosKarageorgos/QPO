package qpo.processor;

import qpo.data.model.Table;

public class PlanSelectNode extends PlanTableNode {
	public PlanSelectNode(){
		super();
		table = null;
		predicate = null;
	}
	
	@Override
	public Table constructTable() throws Exception{
		Table t = new Table();

		Table mainTable = table.getTable();
	
		// TODO select logic construct table

		return t;
	}

	public PlanTableNode table;
	public PlanPredicateNode predicate; 
}
