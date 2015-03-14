package qpo.processor;

import java.util.List;

import qpo.data.info.Catalog;
import qpo.data.info.SizeEstimator;
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
		table.getStatistics().setTupleSize(leftStatistics.getTupleSize() + rightStatistics.getTupleSize()); 

		//get block size
		int blockSize = Catalog.getSystemProperties().getPageSize();
		
		table.getStatistics().setTuplesPerBlock(blockSize / table.getStatistics().getTupleSize());
		
		constructPredicates();
		
		table.getStatistics().setCardinality(SizeEstimator.getJoinEstimatedRecords(leftTable, rightTable, predicate));
	
		return table;
	}
	
	private void constructPredicates(){
		
	}
	
	protected String toString(int padding){
		String ps = new String(new char[padding*2]).replace('\0', ' ');
		
		String output = "";
		try{
			output = ps+"Join "+getTable().toString(padding)+" {";	
		}catch(Exception ex){}

		output = output + "\n"+left.toString(padding+1);
		output = output + "\n"+right.toString(padding+1);
		output = output + "\n"+ps+"  on "+predicate;
		output = output + "\n"+ps+"}";
		
		return output;
	}
	
}
