package qpo.processor;

import qpo.data.info.CostEstimator;
import qpo.data.model.Attribute;
import qpo.data.model.JoinInfo;
import qpo.data.model.JoinTypeEnum;
import qpo.data.model.Table;

public class PlanIntersectNode extends PlanTableNode {
	public PlanIntersectNode(){
		super();
		left = null;
		right = null;
	}
	
	public PlanIntersectNode clone(){
		PlanIntersectNode result = new PlanIntersectNode();
		cloneValuesTo(result);
		return result;
	}

	protected void cloneValuesTo(PlanIntersectNode node){
		super.cloneValuesTo(node);
		node.left = left==null?null:left.clone();
		node.right = right==null?null:right.clone();
		if(node.left!=null)node.left.setParent(node);
		if(node.right!=null)node.right.setParent(node);

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
		table.getStatistics().setCardinality(Math.min(leftTable.getStatistics().getCardinality(),rightTable.getStatistics().getCardinality())/2);

		// TODO intersect logic merge tables
		
		setJoinInfo( CostEstimator.getCheapestIntersection(this) );
		

		return table;
	}

	protected String toString(int padding){
		String ps = new String(new char[padding*2]).replace('\0', ' ');
		
		String output = "";
		try{
			output = ps+"Intersect "+getTable().toString(padding)+" "+getCost()+" cost"+" {";	
		}catch(Exception ex){}

		output = output + "\n"+left.toString(padding+1);
		output = output + "\n"+right.toString(padding+1);
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

	public PlanTableNode left, right;
	
	
	
	private JoinTypeEnum joinType;
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
