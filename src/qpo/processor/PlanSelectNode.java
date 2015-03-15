package qpo.processor;

import java.util.List;

import qpo.data.info.CostEstimator;
import qpo.data.info.SizeEstimator;
import qpo.data.model.Attribute;
import qpo.data.model.Table;

public class PlanSelectNode extends PlanTableNode {
	public PlanSelectNode(){
		super();
		table = null;
		predicate = null;
	}
	
	public PlanSelectNode clone(){
		PlanSelectNode result = new PlanSelectNode();
		cloneValuesTo(result);
		return result;
	}

	protected void cloneValuesTo(PlanSelectNode node){
		super.cloneValuesTo(node);
		node.table = table==null?null:table.clone();
		if(node.table!=null)node.table.setParent(node);
		node.predicate = predicate==null?null:predicate.clone();
	}
	
	@Override
	public Table constructTable() throws Exception{
		Table t = table.getTable().clone();

		//Table mainTable = table.getTable().clone();
		//t = mainTable;
	
		// TODO evaluate predicate to reduce cardinality
		constructPredicate(predicate);
		t.getStatistics().setCardinality(SizeEstimator.getEstimatedRecords(table.getTable(), predicate));

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
	
	public int getNumOfChildren(){
		return 1;
	}
	public PlanTableNode getChild(int index){
		if(index==0)
			return table;
		else return null;
	}
	
	public void setChild(int index, PlanTableNode node){
		if(index==0)
			table = node;
	}

	
	public boolean moveDownwards(){
		if(table.getNumOfChildren()==1){
			if(table instanceof PlanSelectNode){
				predicate = predicate.mergeWith(((PlanSelectNode)table).predicate);
				table.getChild(0).setParent(this);
				table = table.getChild(0);
				invalidate();
				return moveDownwards();
			}
			moveDownwardsOperation(0, 0);
			return moveDownwards();
		}
		if(table instanceof PlanUnionNode){
			PlanSelectNode newSelect = new PlanSelectNode();
			newSelect.table = table;
			newSelect.predicate = predicate.clone();
			table.setParent(newSelect);
			table = newSelect;
			newSelect.setParent(this);
			newSelect.moveDownwardsOperation(0, 1);
			newSelect.invalidate();
			newSelect.moveDownwards();
			moveDownwardsOperation(0, 0);
			invalidate();
			return moveDownwards();
		}
		if(table instanceof PlanDiffNode){
			moveDownwardsOperation(0, 0);
			invalidate();
			return moveDownwards();
		}
		if(table instanceof PlanJoinNode){
			((PlanJoinNode)table).predicate = ((PlanJoinNode)table).predicate.mergeWith(predicate); 
			
			
			//moveDownwardsOperation(0, 0);
			//invalidate();
			return false;
			//return moveDownwards();
		}

		if(table.getNumOfChildren()!=1)
			return false;
		return false;
	}
	
	public PlanTableNode table;
	public PlanPredicateNode predicate; 
	
	
	@Override
	public Integer getCost() throws Exception{
		return getChild(0).getCost() + getMyCost();
	}
	
	
	private Integer getMyCost() throws Exception {
		
		if (!(getChild(0) instanceof PlanRelationNode)) return 0;
		
		if( SizeEstimator.getEstimatedRecords(getChild(0).getTable(), predicate) > 0.35*(getChild(0).getTable().getStatistics().getCardinality())  )
			return CostEstimator.getCostOfLinear( getChild(0).getTable().getStatistics().getBlocksOnDisk() );
		
		
		return CostEstimator.getSelectionEstimatedCost( getChild(0).getTable(), predicate);
	}

	
	
}
