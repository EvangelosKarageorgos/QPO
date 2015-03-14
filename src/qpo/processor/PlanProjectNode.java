package qpo.processor;

import java.util.List;

import qpo.data.info.Catalog;
import qpo.data.info.SizeEstimator;
import qpo.data.model.*;

public class PlanProjectNode extends PlanTableNode {
	public PlanProjectNode(){
		super();
		table = null;
		projectedAttributes = null;
	}
	
	@Override
	public Table constructTable() throws Exception{
		Table t = new Table();

		Table mainTable = table.getTable();
		for(PlanAttributeNode pan : projectedAttributes){
			Boolean found = false;
			for(Attribute a : mainTable.getAttributes()){
				if(a.getName().equalsIgnoreCase(pan.attributeName) && (pan.tableName.length()>0 ? a.getRelationName().equalsIgnoreCase(pan.tableName) : true)){
					if(found)
						throw new Exception("Attribute ambiguity");
					found = true;
					t.addAttribute(a.clone());
				}
			}
		}
		
		t.getStatistics().setCardinality(mainTable.getStatistics().getCardinality());
		t.getStatistics().setTupleSize(SizeEstimator.getTupleSize(t.getAttributes()));
		t.getStatistics().setTuplesPerBlock(Catalog.getSystemProperties().getPageSize() / t.getStatistics().getTupleSize());
	
		// TODO project logic construct table

		return t;
	}

	protected String toString(int padding){
		String ps = new String(new char[padding*2]).replace('\0', ' ');
		
		String output = "";
		try{
			output = ps+"Projection "+getTable().toString(padding);
		}catch(Exception ex){}
		
		output = output + "{\n";
		output = output + table.toString(padding+1);
		output = output + "\n"+ps+"}";
		
		return output;
	}
	
	public PlanTableNode table;
	public List<PlanAttributeNode> projectedAttributes;
}
