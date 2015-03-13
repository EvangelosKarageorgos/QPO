package qpo.processor;

import qpo.data.model.*;

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
		table.getStatistics().setCardinality(leftTable.getStatistics().getCardinality() + rightTable.getStatistics().getCardinality());
	
		// TODO union logic merge tables

		return table;
	}

	public PlanTableNode left, right;

}
