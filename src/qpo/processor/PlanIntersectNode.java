package qpo.processor;

import qpo.data.model.Attribute;
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
	
		if(leftTable.getAttributes().size()!=rightTable.getAttributes().size())
			throw new Exception("Table attributes mismatch");
		for(Attribute a : leftTable.getAttributes()){
			boolean found = false;
			for(Attribute ra : rightTable.getAttributes())
				if(ra.getName().equalsIgnoreCase(a.getName()) && ra.getType()==a.getType() && ra.getSize()==a.getSize()){
					found = true;
					break;
				}
			if(!found)
				throw new Exception("Table attributes mismatch");
			table.addAttribute(a.clone());
		}
		
		table.getStatistics().setTupleSize(leftTable.getStatistics().getTupleSize());
		table.getStatistics().setTuplesPerBlock(leftTable.getStatistics().getTuplesPerBlock());
		table.getStatistics().setCardinality(Math.min(leftTable.getStatistics().getCardinality(),rightTable.getStatistics().getCardinality()));

		// TODO intersect logic merge tables

		return table;
	}

	public PlanTableNode left, right;

}
