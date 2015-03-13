package qpo.processor;

import java.util.List;

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
	public Table constructTable() throws Exception{
		Table table = new Table();
		
		Table leftTable = left.getTable();
		Table rightTable = right.getTable();
		
		List<Attribute> leftAttributes = leftTable.getAttributes();
		List<Attribute> rightAttributes = rightTable.getAttributes();
		
		TableStatistics leftStatistics = leftTable.getStatistics();
		TableStatistics rightStatistics = rightTable.getStatistics();
		
		for(Attribute a : leftAttributes)
			table.addAttribute(a.clone());
		for(Attribute a : rightAttributes)
			table.addAttribute(a.clone());
		table.getStatistics();
		table.getStatistics().setTupleSize(leftStatistics.getTupleSize() + rightStatistics.getTupleSize()); 

		//get block size
		int avgBlockSize = (leftStatistics.getTuplesPerBlock()*leftStatistics.getTupleSize()
				+ rightStatistics.getTuplesPerBlock()*rightStatistics.getTupleSize()) / 2;
		
		table.getStatistics().setTuplesPerBlock(avgBlockSize / table.getStatistics().getTupleSize());
		
		int leftCardinality = leftStatistics.getCardinality();
		int rightCardinality = rightStatistics.getCardinality();
		int finalCardinality = leftCardinality * rightCardinality;
		
		// TODO calculate final cardinality based on predicate
		
		table.getStatistics().setCardinality(finalCardinality);
	
		return table;
	}
}
