package qpo.processor;

import qpo.data.info.SizeEstimator;
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

		Table mainTable = table.getTable().clone();
		t = mainTable;
	
		// TODO evaluate predicate to reduce cardinality
		constructPredicates();
		t.getStatistics().setCardinality(SizeEstimator.getEstimatedRecords(t, predicate));

		return t;
	}
	
	private void constructPredicates(){
		
	}
	

	protected String toString(int padding){
		String ps = new String(new char[padding*2]).replace('\0', ' ');
		String output = "";
		try{
			output = ps+"Select "+getTable().toString(padding)+" {";	
		}catch(Exception ex){}
		output = output + "\n"+table.toString(padding+1);
		output = output + "\n"+ps+"  where "+predicate.toString();
		output = output + "\n"+ps+"}";
		
		return output;
	}
	
	public PlanTableNode table;
	public PlanPredicateNode predicate; 
}
