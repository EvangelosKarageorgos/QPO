package qpo.processor;

import java.util.List;

import qpo.data.model.*;

public class PlanProjectNode extends PlanTableNode {
	public PlanProjectNode(){
		super();
		table = null;
		projectedAttributes = null;
	}
	
	@Override
	public Table constructTable(){
		Table t = new Table();

		Table mainTable = table.getTable();
	
		// TODO project logic construct table

		return t;
	}

	
	public PlanTableNode table;
	public List<PlanAttributeNode> projectedAttributes;
}
