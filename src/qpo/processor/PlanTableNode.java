package qpo.processor;

import qpo.data.model.*;

public class PlanTableNode extends PlanNode {
	public PlanTableNode(){
		m_table = null;
		m_parent = null;
	}
	
	public PlanTableNode clone(){
		PlanTableNode result = new PlanTableNode();
		cloneValuesTo(result);
		return result;
	}

	protected void cloneValuesTo(PlanTableNode node){
		super.cloneValuesTo(node);
		node.m_table = m_table==null?null:m_table.clone();
	}

	public Table constructTable() throws Exception{
		return new Table();
	}
	
	public PlanTableNode getParent(){
		return m_parent;
	}
	
	public void setParent(PlanTableNode node){
		m_parent = node;
	}
	
	public Table getTable() throws Exception{
		if(m_table==null || isValid==false){
			m_table = constructTable();
			if(getParent()!=null)
				getParent().invalidate();
		}
		return m_table;
	}
	
	protected String toString(int padding){
		String ps = new String(new char[padding*2]).replace('\0', ' ');

		String output = "";

		try{
			output= ps+getTable().toString(padding);
		}
		catch(Exception ex){}
		
		return output;
	}

	public int getNumOfChildren(){
		return 0;
	}
	
	public PlanTableNode getChild(int index){
		return null;
	}
	
	public void setChild(int index, PlanTableNode node){
		
	}
	public void invalidate(){
		isValid = false;
		if(getParent()!=null)
			getParent().invalidate();
	}
	protected void moveDownwardsOperation(int index, int childIndex){
		PlanTableNode child = getChild(index);
		if(child!=null){
			PlanTableNode parent = getParent();
			if(parent!=null){
				if(parent.getChild(0)==this){
					parent.setChild(0, child);
				} else if(parent.getChild(1)==this){
					parent.setChild(1, child);
				}
				parent.invalidate();
			}
			child.setParent(parent);
			child.invalidate();
			setParent(child);
			setChild(index, child.getChild(childIndex));
			child.setChild(childIndex, this);
			invalidate();
		}
	}
	
	
	private boolean isValid;
	private PlanTableNode m_parent;
	private Table m_table;
	private int m_cost=0;
	
	

	
	
}
