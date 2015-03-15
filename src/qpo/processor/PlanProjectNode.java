package qpo.processor;

import java.util.ArrayList;
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

	public PlanProjectNode clone(){
		PlanProjectNode result = new PlanProjectNode();
		cloneValuesTo(result);
		return result;
	}

	protected void cloneValuesTo(PlanProjectNode node){
		super.cloneValuesTo(node);
		node.table = table==null?null:table.clone();
		if(node.table!=null)node.table.setParent(node);
		node.projectedAttributes = null;
		if(projectedAttributes!=null){
			node.projectedAttributes = new ArrayList<PlanAttributeNode>();
			for(PlanAttributeNode a : projectedAttributes)
				node.projectedAttributes.add(a.clone());
		}
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
			boolean canMove = true;
			if(table instanceof PlanSelectNode){
				if(table.getChild(0) instanceof PlanRelationNode)
					return false;
				List<Attribute> attrList = ((PlanSelectNode)table).predicate.getUniqueAttributes();
				for(Attribute a : attrList){
					boolean found = false;
					for(PlanAttributeNode pan : projectedAttributes){
						if((pan.tableName.length()==0||pan.tableName.equalsIgnoreCase(a.getRelationName())) && pan.attributeName.equalsIgnoreCase(a.getName())){
							found = true;
							break;
						}
					}
					if(!found){
						canMove = false;
						break;
					}
				}
			}
			if(canMove){
				moveDownwardsOperation(0, 0);
				return true;
			} else
				return false;
		}
		if(table.getNumOfChildren()!=1)
			return false;
		return false;
	}

	public PlanTableNode table;
	public List<PlanAttributeNode> projectedAttributes;
}
