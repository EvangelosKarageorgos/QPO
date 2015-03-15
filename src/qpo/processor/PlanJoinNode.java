package qpo.processor;

import java.util.List;

import qpo.data.info.Catalog;
import qpo.data.info.CostEstimator;
import qpo.data.info.SizeEstimator;
import qpo.data.model.*;

public class PlanJoinNode extends PlanTableNode {
	
	public JoinTypeEnum joinType;
	
	public PlanJoinNode(){
		super();
		left = null;
		right = null;
		predicate = null;
		joinType = JoinTypeEnum.BlockLoopNested;
	}
	
	public PlanJoinNode clone(){
		PlanJoinNode result = new PlanJoinNode();
		cloneValuesTo(result);
		return result;
	}

	protected void cloneValuesTo(PlanJoinNode node){
		super.cloneValuesTo(node);
		node.left = left==null?null:left.clone();
		node.right = right==null?null:right.clone();
		if(node.left!=null)node.left.setParent(node);
		if(node.right!=null)node.right.setParent(node);
		node.predicate = predicate==null?null:predicate.clone();
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
		
		constructPredicate(predicate);
		
		table.getStatistics().setCardinality(SizeEstimator.getJoinEstimatedRecords(leftTable, rightTable, predicate));
	
		setJoinInfo( CostEstimator.getCheapestJoin(this) );
		
		return table;
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
			constructComparisonNode((PlanComparisonNode)pred);
			((PlanComparisonNode)pred).checkTypeConsistancy();
		}
		
	}
	
	private void constructComparisonNode(PlanComparisonNode comp) throws Exception{
		if((comp.left instanceof PlanAttributeValueNode) && (comp.right instanceof PlanAttributeValueNode)){
			PlanAttributeValueNode leftAtt = (PlanAttributeValueNode)comp.left;
			PlanAttributeValueNode rightAtt = (PlanAttributeValueNode)comp.right;
			if(leftAtt.tableName.length()>0 ^ rightAtt.tableName.length()>0){
				
				if(leftAtt.tableName.length()>0){
					boolean foundLeft = leftAtt.bindToTable(left);
					boolean foundRight = leftAtt.bindToTable(right);
					if(foundLeft&&foundRight)
						throw new Exception("Attribute ambiguity");
					if(foundLeft){
						if(!rightAtt.bindToTable(right))
							throw new Exception("Attribute not found");
					} else {
						if(!rightAtt.bindToTable(left))
							throw new Exception("Attribute not found");
					}
				} else {
					boolean foundLeft = rightAtt.bindToTable(left);
					boolean foundRight = rightAtt.bindToTable(right);
					if(foundLeft&&foundRight)
						throw new Exception("Attribute ambiguity");
					if(foundLeft){
						if(!leftAtt.bindToTable(right))
							throw new Exception("Attribute not found");
					} else {
						if(!leftAtt.bindToTable(left))
							throw new Exception("Attribute not found");
					}
				}
			} else {
				constructValueNode(comp.left);
				constructValueNode(comp.right);				
			}
		} else{
			constructValueNode(comp.left);
			constructValueNode(comp.right);
		}
			
	}
	private void constructValueNode(PlanValueNode value) throws Exception{
		if(value instanceof PlanAttributeValueNode){
			PlanAttributeValueNode pan = (PlanAttributeValueNode)value;
			boolean foundLeft = pan.bindToTable(left);
			boolean foundRight = pan.bindToTable(right);
			if(foundLeft&&foundRight)
				throw new Exception("Attribute ambiguity");
			if(!(foundLeft || foundRight))
				throw new Exception("Attribute not found");
		}
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
	
	public int getNumOfChildren(){
		return 2;
	}
	public PlanTableNode getChild(int index){
		if(index==0)
			return left;
		else if(index==1)
			return right;
		else return null;
	}
	
	public void setChild(int index, PlanTableNode node){
		if(index==0)
			left = node;
		else if(index==1)
			right = node;
	}

	
	public void setJoinInfo(JoinInfo joinInfo) {
		joinType = joinInfo.getJoinType();
		//setM_cost(joinInfo.getCostTime());
	}
	
	
	

}
