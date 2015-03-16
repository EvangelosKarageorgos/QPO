package qpo.processor;

import java.util.ArrayList;
import java.util.LinkedList;
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
			output = ps+joinType+" Join "+getTable().toString(padding)+" "+getCost()+" cost"+" {";	
		}catch(Exception ex){}

		output = output + "\n"+left.toString(padding+1);
		output = output + "\n"+right.toString(padding+1);
		if(predicate!=null)output = output + "\n"+ps+"  on "+predicate;
		output = output + "\n"+ps+"}";
		
		return output;
	}
	
	public PlanTableNode moveSelectsDown(PlanTableNode root) throws Exception{
		extractSelectOperations();
		root = getRootNode();
		//System.out.println(root);
		//root = super.moveSelectsDown(root);
		return root;
	}
	
	public PlanTableNode optimizeJoinsOrientation() throws Exception{
		super.optimizeJoinsOrientation();
		PlanTableNode temp;
		PlanJoinNode tmp = clone();
		
		temp = tmp.left;
		tmp.left = tmp.right;
		tmp.right = temp;
		tmp.setJoinInfo( CostEstimator.getCheapestJoin(tmp) );
		if(tmp.getCost()<getCost()){
			temp = left;
			left = right;
			right = temp;
			invalidate();
		}
		return this;
	}
	public void extractSelectOperations() throws Exception{

		if(predicate instanceof PlanComparisonNode){
			List<PlanPredicateNode> leftLst = new ArrayList<PlanPredicateNode>();
			List<PlanPredicateNode> rightLst = new ArrayList<PlanPredicateNode>();
			//for(PlanPredicateNode p : ((PlanConjunctionNode)predicate).predicates){
			PlanPredicateNode p = predicate;

			List<Attribute> attrList = p.getUniqueAttributes();
			List<Attribute> leftAttrList = new ArrayList<Attribute>();
			List<Attribute> rightAttrList = new ArrayList<Attribute>();
			boolean isFromLeft = false;
			boolean isFromRight = false;
			boolean utilizeLeft = false;
			boolean utilizeRight = false;
			for(Attribute a : attrList){
				if(a.getTable().getUid().equals(left.getTable().getUid()))
					isFromLeft = true;
				if(a.getTable().getUid().equals(right.getTable().getUid()))
					isFromRight = true;
			}
			if((!isFromLeft && !isFromRight) || (isFromLeft && !isFromRight))
				leftLst.add(p.clone());
			if((!isFromLeft && !isFromRight) || (!isFromLeft && isFromRight))
				rightLst.add(p.clone());
			if((isFromLeft ^ isFromRight) || (!isFromLeft && !isFromRight))
				predicate = null;
			if(leftLst.size()>0){
				PlanSelectNode node = new PlanSelectNode();
				if(leftLst.size()==1){
					node.predicate = leftLst.get(0);
				} else {
					PlanConjunctionNode pred = new PlanConjunctionNode();
					pred.predicates = leftLst;
					node.predicate = pred;
				}
				node.table = left;
				left.setParent(node);
				node.setParent(this);
				left = node;
				node.moveDownwards();
			}
			if(rightLst.size()>0){
				PlanSelectNode node = new PlanSelectNode();
				if(leftLst.size()==1){
					node.predicate = leftLst.get(0);
				} else {
					PlanConjunctionNode pred = new PlanConjunctionNode();
					pred.predicates = leftLst;
					node.predicate = pred;
				}
				node.table = right;
				right.setParent(node);
				node.setParent(this);
				right = node;
				node.moveDownwards();
			}
		} else if(predicate instanceof PlanConjunctionNode){
			List<PlanPredicateNode> leftLst = new ArrayList<PlanPredicateNode>();
			List<PlanPredicateNode> rightLst = new ArrayList<PlanPredicateNode>();
			List<PlanPredicateNode> toRemove = new LinkedList<PlanPredicateNode>();
			for(PlanPredicateNode p : ((PlanConjunctionNode)predicate).predicates){
				List<Attribute> attrList = p.getUniqueAttributes();
				List<Attribute> leftAttrList = new ArrayList<Attribute>();
				List<Attribute> rightAttrList = new ArrayList<Attribute>();
				boolean isFromLeft = false;
				boolean isFromRight = false;
				boolean utilizeLeft = false;
				boolean utilizeRight = false;
				for(Attribute a : attrList){
					if(a.getTable().getUid().equals(left.getTable().getUid()))
						isFromLeft = true;
					if(a.getTable().getUid().equals(right.getTable().getUid()))
						isFromRight = true;
				}
				if((!isFromLeft && !isFromRight) || (isFromLeft && !isFromRight))
					leftLst.add(p.clone());
				if((!isFromLeft && !isFromRight) || (!isFromLeft && isFromRight))
					rightLst.add(p.clone());
				if((isFromLeft ^ isFromRight) || (!isFromLeft && !isFromRight))
					toRemove.add(p);
			}
			for(PlanPredicateNode p : toRemove)
				((PlanConjunctionNode)predicate).predicates.remove(p);
			if(((PlanConjunctionNode)predicate).predicates.size()==0)
				predicate = null;
			if(leftLst.size()>0){
				PlanSelectNode node = new PlanSelectNode();
				if(leftLst.size()==1){
					node.predicate = leftLst.get(0);
				} else {
					PlanConjunctionNode pred = new PlanConjunctionNode();
					pred.predicates = leftLst;
					node.predicate = pred;
				}
				node.table = left;
				left.setParent(node);
				node.setParent(this);
				left = node;
				node.invalidate();
				node.constructTable();
				node.moveDownwards();
			}
			if(rightLst.size()>0){
				PlanSelectNode node = new PlanSelectNode();
				if(rightLst.size()==1){
					node.predicate = rightLst.get(0);
				} else {
					PlanConjunctionNode pred = new PlanConjunctionNode();
					pred.predicates = rightLst;
					node.predicate = pred;
				}
				node.table = right;
				right.setParent(node);
				node.setParent(this);
				right = node;
				node.invalidate();
				node.constructTable();
				node.moveDownwards();
			}
			predicate.distributeAttributeReferences(left.getTable(), right.getTable());
			
			//invalidate();
		}
	}
	
	private void createChildSelectNode(int child, List<PlanPredicateNode> predicates, int utilization){
		
	}
	
	private int classifyComparisonNodeUtilization(PlanComparisonNode cnode, List<Attribute> leftAttrList, List<Attribute> rightAttrList){
		boolean isFromLeft = false;
		boolean isFromRight = false;
		boolean utilizeLeft = false;
		boolean utilizeRight = false;
		if(cnode.left instanceof PlanAttributeValueNode){
			PlanAttributeValueNode pan = (PlanAttributeValueNode)cnode.left;
			for(Attribute a : leftAttrList){
				if((pan.tableName.length()==0||a.getRelationName().length()==0||pan.tableName.equalsIgnoreCase(a.getRelationName())) && pan.attributeName.equalsIgnoreCase(a.getName())){
					isFromLeft = true;
					break;
				}
			}
			for(Attribute a : rightAttrList){
				if((pan.tableName.length()==0||a.getRelationName().length()==0||pan.tableName.equalsIgnoreCase(a.getRelationName())) && pan.attributeName.equalsIgnoreCase(a.getName())){
					isFromRight = true;
					break;
				}
			}
		}
		if(cnode.right instanceof PlanAttributeValueNode){
			PlanAttributeValueNode pan = (PlanAttributeValueNode)cnode.right;
			for(Attribute a : leftAttrList){
				if((pan.tableName.length()==0||a.getRelationName().length()==0||pan.tableName.equalsIgnoreCase(a.getRelationName())) && pan.attributeName.equalsIgnoreCase(a.getName())){
					isFromLeft = true;
					break;
				}
			}
			for(Attribute a : rightAttrList){
				if((pan.tableName.length()==0||a.getRelationName().length()==0||pan.tableName.equalsIgnoreCase(a.getRelationName())) && pan.attributeName.equalsIgnoreCase(a.getName())){
					isFromRight = true;
					break;
				}
			}
		}
		if((isFromLeft && isFromRight) || (!isFromLeft && !isFromRight)){
			utilizeLeft = true;
			utilizeRight = false;
			return 3;
		}
		if(isFromLeft)
			return 1;
		if(isFromRight)
			return 2;
		return 0;
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

	
	private Integer myCost;

	public void setJoinInfo(JoinInfo joinInfo) {
		joinType = joinInfo.getJoinType();
		myCost = joinInfo.getCostTime();
	}
	
	
	@Override
	public Integer getCost() throws Exception{
		return getChild(0).getCost() + getChild(1).getCost() + getMyCost();
	}
	
	
	private Integer getMyCost() throws Exception {
		return myCost; 
	}
	

}
