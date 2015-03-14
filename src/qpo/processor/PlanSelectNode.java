package qpo.processor;

import qpo.data.info.SizeEstimator;
import qpo.data.model.Attribute;
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
		constructPredicate(predicate);
		t.getStatistics().setCardinality(SizeEstimator.getEstimatedRecords(t, predicate));

		return t;
	}
	
	private void constructPredicate(PlanPredicateNode pred) throws Exception{
		if(pred instanceof PlanConjunctionNode){
			for(PlanPredicateNode p : ((PlanConjunctionNode)pred).predicates)
				constructPredicate(p);
		} else if(pred instanceof PlanDisjunctionNode){
			for(PlanPredicateNode p : ((PlanDisjunctionNode)pred).predicates)
				constructPredicate(p);
		} else if(pred instanceof PlanNegationNode){
			constructPredicate(((PlanNegationNode)pred).predicate);
		} else if(pred instanceof PlanComparisonNode){
			constructValueNode(((PlanComparisonNode)pred).left);
			constructValueNode(((PlanComparisonNode)pred).right);
			((PlanComparisonNode)pred).checkTypeConsistancy();
		}
		
	}
	private void constructValueNode(PlanValueNode value) throws Exception{
		if(value instanceof PlanAttributeValueNode){
			PlanAttributeValueNode pan = (PlanAttributeValueNode)value;
			if(!pan.bindToTable(table))
				throw new Exception("Attribute not found");
		}
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
